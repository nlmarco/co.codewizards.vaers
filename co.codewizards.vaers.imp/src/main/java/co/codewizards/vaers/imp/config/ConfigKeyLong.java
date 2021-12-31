package co.codewizards.vaers.imp.config;

import static java.util.Objects.*;

import java.text.ParseException;

public class ConfigKeyLong extends ConfigKeyBase<Long> {

	public ConfigKeyLong(String name, boolean mandatory, Long defaultValue) {
		super(name, mandatory, defaultValue);
	}

	public ConfigKeyLong(String name, boolean mandatory) {
		super(name, mandatory);
	}

	public ConfigKeyLong(String name, Long defaultValue) {
		super(name, defaultValue);
	}

	@Override
	public Long parseString(String string) throws ParseException {
		requireNonNull(string, "string");
		string = string.trim();
		if (string.isEmpty()) {
			return null;
		}
		try {
			return Long.valueOf(string);
		} catch (NumberFormatException x) {
			ParseException y = new ParseException(string, 0);
			y.initCause(x);
			throw y;
		}
	}
}
