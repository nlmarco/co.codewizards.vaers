package co.codewizards.vaers.imp;

import static java.util.Objects.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import co.codewizards.vaers.imp.config.Config;
import co.codewizards.vaers.imp.config.ConfigKeyConst;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

public class VaersImport {

	protected final static String CSV_FILE_NAME_SUFFIX = ".csv";

	private final Config config;
	private Connection connection;
	private File vaersSourceDir;
	private List<File> vaersCsvFiles;
	private File vaersCsvFile;
	private CSVParser csvParser = new CSVParserBuilder().withSeparator(',').withQuoteChar('"').withEscapeChar((char)0).build();
	private final Map<VaersTable, VaersCsvLineImporter> vaersTable2VaersCsvLineImporter = new HashMap<>();

	public static void main(String[] args) throws Exception {
		new VaersImport(args).run();
	}

	public VaersImport(String[] args) throws Exception {
		config = Config.readConfig();
		if (args.length == 0) {
			throw new IllegalArgumentException("Source-directory must be passed as first argument!");
		}
		vaersSourceDir = new File(args[0]);
	}

	public void run() throws Exception {
		initDatabase();
		try {
			collectVaersCsvFiles();
			for (File f : vaersCsvFiles) {
				vaersCsvFile = f;
				importVaersCsvFile();
			}
		} finally {
			closeConnection();
		}
		System.out.println("All successfully done.");
	}

	protected void importVaersCsvFile() throws Exception {
		System.out.println(String.format("Importing file '%s'...", requireNonNull(vaersCsvFile, "vaersCsvFile").getName()));
		openConnection();
		VaersTable vaersTable = getVaersTable();
		if (vaersTable == null) {
			return; // skip unknown zip-entry.
		}
		long lineIndex = 0;
		try (InputStream in = new FileInputStream(requireNonNull(vaersCsvFile, "vaersCsvFile"))) {
			try (InputStreamReader r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
				CSVReader csvReader = new CSVReaderBuilder(r).withCSVParser(csvParser).build();
				String[] line;
				try {
					while ((line = csvReader.readNext()) != null) {
						importVaersCsvLine(vaersTable, lineIndex, line);
						++lineIndex;
					}
				} catch (Exception x) {
					throw new IOException(String.format("Importing file '%s' failed at (0-based) lineIndex=%d: %s", vaersCsvFile.getName(), lineIndex, x.toString()), x);
				}
			}
		}
	}

	protected void importVaersCsvLine(VaersTable vaersTable, long lineIndex, String[] line) throws SQLException {
		VaersCsvLineImporter importer = getVaersCsvLineImporter(vaersTable);
		importer.setCsvFileName(requireNonNull(vaersCsvFile, "vaersCsvFile").getName());
		importer.setCsvLineIndex(lineIndex);
		importer.setCsvLine(line);
		importer.importCsvLine();
	}

	protected VaersCsvLineImporter getVaersCsvLineImporter(VaersTable vaersTable) {
		return vaersTable2VaersCsvLineImporter.computeIfAbsent(
				requireNonNull(vaersTable, "vaersTable"), k -> initVaersCsvLineImporter(createVaersCsvLineImporter(vaersTable)));
	}

	protected VaersCsvLineImporter createVaersCsvLineImporter(VaersTable vaersTable) {
		switch (requireNonNull(vaersTable, "vaersTable")) {
			case VAERSDATA:
				return new VaersDataCsvLineImporter();
			case VAERSSYMPTOMS:
				return new VaersSymptomsCsvLineImporter();
			case VAERSVAX:
				return new VaersVaxCsvLineImporter();
			default:
				throw new IllegalStateException("Unknown vaersTable: " + vaersTable);
		}
	}

	protected VaersCsvLineImporter initVaersCsvLineImporter(VaersCsvLineImporter importer) {
		try {
			requireNonNull(importer, "importer").setConnection(requireNonNull(connection, "connection"));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return importer;
	}

	protected VaersTable getVaersTable() {
		String csvFileName = requireNonNull(vaersCsvFile, "vaersCsvFile").getName();
		if (!csvFileName.endsWith(CSV_FILE_NAME_SUFFIX)) {
			throw new IllegalStateException("File does not end with suffix '" + CSV_FILE_NAME_SUFFIX + "': " + csvFileName);
		}
		String nameWithoutExt = csvFileName.substring(0, csvFileName.length() - CSV_FILE_NAME_SUFFIX.length());
		for (VaersTable vaersTable : VaersTable.values()) {
			if (nameWithoutExt.toUpperCase(Locale.US).endsWith(vaersTable.name())) {
				return vaersTable;
			}
		}
		System.out.println("File does not indicate any VAERS-table-name: " + csvFileName);
		return null;
	}

	protected void collectVaersCsvFiles() throws IOException {
		List<File> csvFiles = new ArrayList<>();
		collectVaersCsvFiles(csvFiles, requireNonNull(vaersSourceDir, "vaersSourceDir"));
		Collections.sort(csvFiles);
		this.vaersCsvFiles = csvFiles;
	}

	protected void collectVaersCsvFiles(List<File> csvFiles, File directory) throws IOException {
		requireNonNull(csvFiles, "csvFiles");
		requireNonNull(directory, "directory");
		File[] children = directory.listFiles();
		if (children == null) {
			return;
		}
		for (File child : children) {
			if (child.isDirectory())
				collectVaersCsvFiles(csvFiles, child);
			else if (child.getName().endsWith(CSV_FILE_NAME_SUFFIX))
				csvFiles.add(child);
		}
	}

	protected void initDatabase() throws Exception {
		initLiquibase();
		openConnection();
		truncateTables();
	}

	protected void truncateTables() throws Exception {
		truncateTable("VAERSSYMPTOMS");
		truncateTable("VAERSVAX");
		truncateTable("VAERSDATA");
	}

	protected void truncateTable(String tableName) throws Exception {
		requireNonNull(tableName, "tableName");
		Connection connection = openConnection();
		String sql = String.format("truncate table %s restart identity", tableName);
		Statement statement = connection.createStatement();
		statement.executeUpdate(sql);
	}

	protected void closeConnection() throws SQLException {
		vaersTable2VaersCsvLineImporter.clear();
		if (connection != null) {
			if (!connection.isClosed()) {
				if (!connection.getAutoCommit()) {
					connection.rollback();
				}
				connection.close();
			}
			connection = null;
		}
	}

	protected Connection openConnection() throws SQLException, ParseException {
		if (connection == null || connection.isClosed()) {
			vaersTable2VaersCsvLineImporter.clear();
			connection = DriverManager.getConnection(config.getValue(ConfigKeyConst.DB_URL), config.getValue(ConfigKeyConst.DB_USER), config.getValue(ConfigKeyConst.DB_PASSWORD));
		}
		connection.setAutoCommit(true);
		return connection;
	}

	protected void initLiquibase() throws LiquibaseException, SQLException, ParseException {
		Connection connection = openConnection();
		try (Liquibase liquibase = new Liquibase("liquibase/dbchangelog.xml", new ClassLoaderResourceAccessor(), new JdbcConnection(connection))) {
			liquibase.update((Contexts) null);
		}
		closeConnection();
	}
}
