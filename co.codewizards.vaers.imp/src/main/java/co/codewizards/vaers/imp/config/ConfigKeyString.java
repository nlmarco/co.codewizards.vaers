package co.codewizards.vaers.imp.config;

import static java.util.Objects.*;

import java.text.ParseException;

public class ConfigKeyString extends ConfigKeyBase<String> {

	public ConfigKeyString(String name, boolean mandatory, String defaultValue) {
		super(name, mandatory, defaultValue);
	}

	public ConfigKeyString(String name, boolean mandatory) {
		super(name, mandatory);
	}

	public ConfigKeyString(String name, String defaultValue) {
		super(name, defaultValue);
	}

	@Override
	public String parseString(String string) throws ParseException {
		return requireNonNull(string, "string");
	}
}
