package co.codewizards.vaers.imp;

import java.sql.SQLException;

public class VaersSymptomsCsvLineImporter extends VaersCsvLineImporterBase {

	public static final String[] COLUMN_NAMES = {
			"VAERS_ID", "SYMPTOM1", "SYMPTOMVERSION1", "SYMPTOM2", "SYMPTOMVERSION2", "SYMPTOM3", "SYMPTOMVERSION3",
			"SYMPTOM4", "SYMPTOMVERSION4", "SYMPTOM5", "SYMPTOMVERSION5"
	};

	@Override
	protected String getTableName() {
		return "VAERSSYMPTOMS";
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
		insertStatement.setObject(index + 1, getCsvCellAsLong(index));
		++index;

		// SYMPTOM1
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		// SYMPTOMVERSION1
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		// SYMPTOM2
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		// SYMPTOMVERSION2
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		// SYMPTOM3
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		// SYMPTOMVERSION3
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		// SYMPTOM4
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		// SYMPTOMVERSION4
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		// SYMPTOM5
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		// SYMPTOMVERSION5
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		insertStatement.executeUpdate();
	}
}
