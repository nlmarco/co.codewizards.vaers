package co.codewizards.vaers.imp;

import static java.util.Objects.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public abstract class VaersCsvLineImporterBase implements VaersCsvLineImporter {

	protected Connection connection;
	protected PreparedStatement insertStatement;
	protected String csvFileName;
	protected long csvLineIndex;
	protected String[] csvLine;
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	@Override
	public Connection getConnection() {
		return connection;
	}

	@Override
	public void setConnection(Connection connection) throws SQLException {
		this.connection = connection;
		if (connection != null) {
			initPreparedStatements();
		}
	}

	@Override
	public String getCsvFileName() {
		return csvFileName;
	}
	@Override
	public void setCsvFileName(String csvFileName) {
		this.csvFileName = csvFileName;
	}

	@Override
	public long getCsvLineIndex() {
		return csvLineIndex;
	}
	@Override
	public void setCsvLineIndex(long csvLineIndex) {
		this.csvLineIndex = csvLineIndex;
	}

	@Override
	public String[] getCsvLine() {
		return csvLine;
	}

	@Override
	public void setCsvLine(String[] csvLine) {
		this.csvLine = csvLine;
	}

	protected void initPreparedStatements() throws SQLException {
		insertStatement = createInsertStatement();
	}

	protected final PreparedStatement createInsertStatement() throws SQLException {
		String sql = "insert into " + getTableName() + " ( \n"
				+ "  " + String.join(", ", getColumnNames()) + " \n"
				+ ") values ( \n"
				+ "  " + getInsertQuestionMarks() + " \n"
				+ ")";

		return connection.prepareStatement(sql);
	}

	protected abstract String getTableName();

	protected String getInsertQuestionMarks() {
		StringBuilder sb = new StringBuilder();
		for (@SuppressWarnings("unused") String columnName : getColumnNames()) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append("?");
		}
		return sb.toString();
	}

	protected abstract String[] getColumnNames();

	protected String getCsvCellAsString(int index) {
		String[] l = requireNonNull(csvLine, "csvLine");
		if (index >= l.length) {
			return null;
		}
		return l[index];
	}

	protected Long getCsvCellAsLong(int index) {
		String columnName = getColumnNames()[index];
		String string = getCsvCellAsString(index);
		if (string == null) {
			return null;
		}
		string = string.trim();
		if (string.isEmpty()) {
			return null;
		}
		try {
			return Long.parseLong(string);
		} catch (NumberFormatException x) {
			throw new IllegalArgumentException(String.format("file='%s', lineIndex=%d, columnName='%s': The value '%s' cannot be parsed as long integer!",
					getCsvFileName(), getCsvLineIndex(), columnName, string));
		}
	}

	protected Integer getCsvCellAsInt(int index) {
		String columnName = getColumnNames()[index];
		String string = getCsvCellAsString(index);
		if (string == null) {
			return null;
		}
		string = string.trim();
		if (string.isEmpty()) {
			return null;
		}
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException x) {
			throw new IllegalArgumentException(String.format("file='%s', lineIndex=%d, columnName='%s': The value '%s' cannot be parsed as integer!",
					getCsvFileName(), getCsvLineIndex(), columnName, string));
		}
	}

	protected Date getCsvCellAsDate(int index) {
		String columnName = getColumnNames()[index];
		String string = getCsvCellAsString(index);
		if (string == null) {
			return null;
		}
		string = string.trim();
		if (string.isEmpty()) {
			return null;
		}
		try {
			java.util.Date jud = dateFormat.parse(string);
			return new Date(jud.getTime());
		} catch (ParseException x) {
			throw new IllegalArgumentException(String.format("file='%s', lineIndex=%d, columnName='%s': The value '%s' cannot be parsed as date!",
					getCsvFileName(), getCsvLineIndex(), columnName, string));
		}
	}

	protected BigDecimal getCsvCellAsBigDecimal(int index) {
		String columnName = getColumnNames()[index];
		String string = getCsvCellAsString(index);
		if (string == null) {
			return null;
		}
		string = string.trim();
		if (string.isEmpty()) {
			return null;
		}
		try {
			return new BigDecimal(string);
		} catch (NumberFormatException x) {
			throw new IllegalArgumentException(String.format("file='%s', lineIndex=%d, columnName='%s': The value '%s' cannot be parsed as long integer!",
					getCsvFileName(), getCsvLineIndex(), columnName, string));
		}
	}

	protected void checkHeaderLine() {
		checkHeaderLine(getColumnNames());
	}

	protected void checkHeaderLine(String[] columnNames) {
		if (getCsvLineIndex() != 0) {
			throw new IllegalStateException("Current line is not the header-line!");
		}
		String[] csvLine = getCsvLine();
		if (columnNames.length > csvLine.length) { // We allow more columns in the file to allow for newer versions with additional columns
			throw new IllegalArgumentException(String.format("file='%s': The number of expected columns %d exceeds the number of columns in the file %d!",
					getCsvFileName(), columnNames.length, csvLine.length));
		}
		for (int i = 0; i < columnNames.length; i++) {
			if (!columnNames[i].equalsIgnoreCase(csvLine[i])) {
				throw new IllegalArgumentException(String.format("file='%s': Expected column '%s' at index %d, but found '%s'!",
						getCsvFileName(), columnNames[i], i, csvLine[i]));
			}
		}
	}
}
