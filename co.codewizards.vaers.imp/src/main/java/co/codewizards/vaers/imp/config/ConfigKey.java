package co.codewizards.vaers.imp.config;

import java.text.ParseException;

public interface ConfigKey<V> {

	String getName();

	boolean isMandatory();

	V getDefaultValue();

	V parseString(String string) throws ParseException;
}
