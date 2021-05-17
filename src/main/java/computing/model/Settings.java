package computing.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.concurrent.ThreadSafe;

import computing.exception.BootException;
import computing.utils.StringUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@ThreadSafe
@Slf4j
public class Settings {

	private static final String PROP_PATH = "/application.properties";

	@Getter
	private Properties props = null;

	private static volatile Settings instance = null;

	private Settings() {
		init();
	}

	public static Settings getInstance() {
		if (instance == null) {
			instance = new Settings();
		}
		return instance;
	}

	private void init() {
		try (InputStream is = Settings.class.getResourceAsStream(PROP_PATH)) {
			props = new Properties();
			props.load(is);
		} catch (IOException e) {
			log.error("", e);
			throw new BootException("Failed to init Settings class");
		}

	}

	public String get(String key) {
		if (!StringUtils.isEmpty(key)) {
			return (String) props.getOrDefault(key, null);
		}
		return null;
	}

}
