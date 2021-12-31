package co.codewizards.vaers.imp;

import java.sql.SQLException;

public class VaersDataCsvLineImporter extends VaersCsvLineImporterBase {

	public static final String[] COLUMN_NAMES = {
			"VAERS_ID", "RECVDATE", "STATE", "AGE_YRS", "CAGE_YR", "CAGE_MO", "SEX",
			"RPT_DATE", "SYMPTOM_TEXT", "DIED", "DATEDIED", "L_THREAT", "ER_VISIT", "HOSPITAL", "HOSPDAYS",
			"X_STAY", "DISABLE", "RECOVD", "VAX_DATE", "ONSET_DATE", "NUMDAYS", "LAB_DATA", "V_ADMINBY",
			"V_FUNDBY", "OTHER_MEDS", "CUR_ILL", "HISTORY", "PRIOR_VAX", "SPLTTYPE", "FORM_VERS",
			"TODAYS_DATE", "BIRTH_DEFECT", "OFC_VISIT", "ER_ED_VISIT", "ALLERGIES"
	};

	@Override
	protected String getTableName() {
		return "VAERSDATA";
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

		// RECVDATE
		insertStatement.setObject(index + 1, getCsvCellAsDate(index));
		++index;

		// STATE
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		// AGE_YRS
		insertStatement.setObject(index + 1, getCsvCellAsBigDecimal(index));
		++index;

		// CAGE_YR
		insertStatement.setObject(index + 1, getCsvCellAsLong(index));
		++index;

		// CAGE_MO
		insertStatement.setObject(index + 1, getCsvCellAsBigDecimal(index));
		++index;

		// SEX
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		// RPT_DATE
		insertStatement.setObject(index + 1, getCsvCellAsDate(index));
		++index;

		// SYMPTOM_TEXT
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		// DIED
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		// DATEDIED
		insertStatement.setObject(index + 1, getCsvCellAsDate(index));
		++index;

		// L_THREAT
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		// ER_VISIT
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		// HOSPITAL
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		// HOSPDAYS
		insertStatement.setObject(index + 1, getCsvCellAsInt(index));
		++index;

		// X_STAY
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		// DISABLE
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		// RECOVD
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		// VAX_DATE
		insertStatement.setObject(index + 1, getCsvCellAsDate(index));
		++index;

		// ONSET_DATE
		insertStatement.setObject(index + 1, getCsvCellAsDate(index));
		++index;

		// NUMDAYS
		insertStatement.setObject(index + 1, getCsvCellAsInt(index));
		++index;

		// LAB_DATA
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		// V_ADMINBY
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		// V_FUNDBY
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		// OTHER_MEDS
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		// CUR_ILL
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		// HISTORY
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		// PRIOR_VAX
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		// SPLTTYPE
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		// FORM_VERS
		insertStatement.setObject(index + 1, getCsvCellAsInt(index));
		++index;

		// TODAYS_DATE
		insertStatement.setObject(index + 1, getCsvCellAsDate(index));
		++index;

		// BIRTH_DEFECT
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		// OFC_VISIT
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		// ER_ED_VISIT
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		// ALLERGIES
		insertStatement.setObject(index + 1, getCsvCellAsString(index));
		++index;

		insertStatement.executeUpdate();
	}
}
