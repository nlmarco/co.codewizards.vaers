package co.codewizards.vaers.imp.config;

import static java.util.Objects.*;

import java.text.ParseException;

public class ConfigKeyNonEmptyTrimmedString extends ConfigKeyBase<String> {

	public ConfigKeyNonEmptyTrimmedString(String name, boolean mandatory, String defaultValue) {
		super(name, mandatory, defaultValue);
	}

	public ConfigKeyNonEmptyTrimmedString(String name, boolean mandatory) {
		super(name, mandatory);
	}

	public ConfigKeyNonEmptyTrimmedString(String name, String defaultValue) {
		super(name, defaultValue);
	}

	@Override
	public String parseString(String string) throws ParseException {
		string = requireNonNull(string, "string").trim();
		if (string.isEmpty()) {
			return null;
		}
		return string;
	}
}
