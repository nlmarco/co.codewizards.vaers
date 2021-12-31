package co.codewizards.vaers.imp;

import java.sql.SQLException;

public class VaersVaxCsvLineImporter extends VaersCsvLineImporterBase {

	public static final String[] COLUMN_NAMES = { "VAERS_ID", "VAX_TYPE", "VAX_MANU", "VAX_LOT", "VAX_DOSE_SERIES",
			"VAX_ROUTE", "VAX_SITE", "VAX_NAME" };

	@Override
	protected String getTableName() {
		return "VAERSVAX";
	}

	@Override
	protected String[] getColumnNames() {
		return COLUMN_NAMES;
	}

	@Override
	public void importCsvLine() throws SQLException {
		if (getCsvLineIndex() == 0) {
			checkHeaderLine();
			return;
		}

		connection.setAutoCommit(true);
		int index = 0;

		// VAERS_ID
		insertStatement.setLong(index + 1, getCsvCellAsLong(index));
		++index;

		// VAX_TYPE
		insertStatement.setString(index + 1, getCsvCellAsString(index));
		++index;

		// VAX_MANU
		insertStatement.setString(index + 1, getCsvCellAsString(index));
		++index;

		// VAX_LOT
		insertStatement.setString(index + 1, getCsvCellAsString(index));
		++index;

		// VAX_DOSE_SERIES
		insertStatement.setString(index + 1, getCsvCellAsString(index));
		++index;

		// VAX_ROUTE
		insertStatement.setString(index + 1, getCsvCellAsString(index));
		++index;

		// VAX_SITE
		insertStatement.setString(index + 1, getCsvCellAsString(index));
		++index;

		// VAX_NAME
		insertStatement.setString(index + 1, getCsvCellAsString(index));
		++index;

		insertStatement.executeUpdate();
	}
}
