package co.codewizards.vaers.imp.config;

import static java.util.Objects.*;

public abstract class ConfigKeyBase<V> implements ConfigKey<V> {

	private final String name;
	private final boolean mandatory;
	private final V defaultValue;

	public ConfigKeyBase(String name, boolean mandatory, V defaultValue) {
		this.name = requireNonNull(name, "name");
		this.mandatory = mandatory;
		this.defaultValue = defaultValue;
	}

	public ConfigKeyBase(String name, boolean mandatory) {
		this(name, mandatory, null);
	}

	public ConfigKeyBase(String name, V defaultValue) {
		this(name, false, defaultValue);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isMandatory() {
		return mandatory;
	}

	@Override
	public V getDefaultValue() {
		return defaultValue;
	}
}
