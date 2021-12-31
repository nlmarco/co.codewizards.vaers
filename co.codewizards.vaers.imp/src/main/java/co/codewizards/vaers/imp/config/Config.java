package co.codewizards.vaers.imp.config;

import static java.util.Objects.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Properties;

public class Config extends Properties {

	private static final long serialVersionUID = 1L;

	public static final String CONFIG_DIR_NAME = ".vaers";
	public static final String CONFIG_FILE_NAME = "vaers.properties";
	public static final String CONFIG_TEMPLATE_RESOURCE_PATH = "template/" + CONFIG_FILE_NAME;

	public <V> V getValue(ConfigKey<V> key) throws ParseException {
		V value = getRawValue(key);
		if (value == null) {
			if (key.isMandatory()) {
				throw new IllegalStateException("Config is incomplete! Missing key: " + key.getName());
			}
			return key.getDefaultValue();
		}
		return value;
	}

	protected <V> V getRawValue(ConfigKey<V> key) throws ParseException {
		String stringValue = getProperty(requireNonNull(key, "key").getName());
		if (stringValue == null) {
			return null;
		}
		V value = key.parseString(stringValue);
		return value;
	}

	public static Config readConfig() throws IOException {
		File configFile = getConfigFile();
		try (InputStream in = new FileInputStream(configFile)) {
			Config config = new Config();
			config.load(in);
			return config;
		}
	}

	private static File getConfigFile() throws IOException {
		File configFile = new File(getConfigDir(), CONFIG_FILE_NAME);
		if (configFile.isFile()) {
			return configFile;
		}
		OutputStream out = null;
		InputStream in = Config.class.getClassLoader().getResourceAsStream(CONFIG_TEMPLATE_RESOURCE_PATH);
		if (in == null) {
			throw new IOException("Resource not found: " + CONFIG_TEMPLATE_RESOURCE_PATH);
		}
		try {
			out = new FileOutputStream(configFile);
			in.transferTo(out);
		} finally {
			in.close();
			if (out != null) {
				out.close();
			}
		}
		return configFile;
	}

	private static File getConfigDir() throws IOException {
		String userHomeString = System.getProperty("user.home");
		if (userHomeString == null || userHomeString.isEmpty()) {
			throw new IllegalStateException("System-property >>user.home<< not set!");
		}
		File userHome = new File(userHomeString);
		File configDir = new File(userHome, CONFIG_DIR_NAME);
		if (configDir.isDirectory()) {
			return configDir;
		}
		if (! userHome.isDirectory()) {
			throw new IOException("User-home-directory does not exist: " + userHomeString);
		}
		configDir.mkdirs();
		if (configDir.isDirectory()) {
			return configDir;
		}
		if (configDir.exists()) {
			throw new IOException("Config-directory exists, but is not a directory: " + configDir.getAbsolutePath());
		}
		throw new IOException("Config-directory does not exist and could not be created: " + configDir.getAbsolutePath());
	}
}
