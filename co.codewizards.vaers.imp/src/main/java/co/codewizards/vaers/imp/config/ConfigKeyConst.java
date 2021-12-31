package co.codewizards.vaers.imp.config;

public interface ConfigKeyConst {

	ConfigKeyNonEmptyTrimmedString DB_URL = new ConfigKeyNonEmptyTrimmedString("db.url", true);
	ConfigKeyNonEmptyTrimmedString DB_USER = new ConfigKeyNonEmptyTrimmedString("db.user", false);
	ConfigKeyNonEmptyTrimmedString DB_PASSWORD = new ConfigKeyNonEmptyTrimmedString("db.password", false);

}
