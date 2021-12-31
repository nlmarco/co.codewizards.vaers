package co.codewizards.vaers.imp;

import java.sql.Connection;
import java.sql.SQLException;

public interface VaersCsvLineImporter {

	Connection getConnection();

	void setConnection(Connection connection) throws SQLException;

	String getCsvFileName();

	void setCsvFileName(String csvFileName);

	long getCsvLineIndex();

	void setCsvLineIndex(long csvLineIndex);

	void setCsvLine(String[] csvLine);

	String[] getCsvLine();

	void importCsvLine() throws SQLException;
}
