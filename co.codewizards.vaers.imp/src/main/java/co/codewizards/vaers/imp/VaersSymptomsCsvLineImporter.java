package co.codewizards.vaers.imp;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VaersSymptomsCsvLineImporter extends VaersCsvLineImporterBase {

	public static final String[] COLUMN_NAMES = {
			"VAERS_ID", "SYMPTOM", "SYMPTOMVERSION"
	};

	// The CSV has 5 symptoms per line, but in our DB we map this to 1 symptom per line.
	public static final String[] CSV_COLUMN_NAMES = {
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
			checkHeaderLine(CSV_COLUMN_NAMES);
			return;
		}

		List<Symptom> symptoms = new ArrayList<>();

		int index = 0;
		long vaersId = getCsvCellAsLong(index);

		for (int symptomIndex = 1; symptomIndex <= 5; ++ symptomIndex) {
			// SYMPTOM1 + SYMPTOMVERSION1 ... SYMPTOM5 + SYMPTOMVERSION5
			Symptom symptom = new Symptom(getCsvCellAsString(++index), getCsvCellAsString(++index));
			if (symptom.symptom != null && !symptom.symptom.isEmpty()) {
				symptoms.add(symptom);
			}
		}

		connection.setAutoCommit(true);

		for (Symptom symptom : symptoms) {
			index = 0;

			// VAERS_ID
			insertStatement.setObject(index + 1, vaersId);
			++index;

			// SYMPTOM
			insertStatement.setObject(index + 1, symptom.symptom);
			++index;

			// SYMPTOMVERSION
			insertStatement.setObject(index + 1, symptom.symptomVersion);
			++index;

			insertStatement.executeUpdate();
		}
	}

	private static class Symptom {
		public final String symptom;
		public final String symptomVersion;

		public Symptom(String symptom, String symptomVersion) {
			this.symptom = symptom;
			this.symptomVersion = symptomVersion;
		}
	}
}
