package computing.boot;

import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import computing.exception.BootException;

/**
 * Tooling class containing only static methods
 *
 * Used to load configuration, create DataSource objects, etc
 */
public class Configurator {

	private Configurator() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Used to load configuration from path specified
	 * 
	 * @param path - path of properties file
	 * @return Properties object
	 */

	public static Properties loadConfig(String path) {
		Properties props = new Properties();
		try (InputStream is = Configurator.class.getResourceAsStream(path)) {
			props.load(is);
		} catch (Exception e) {
			throw new BootException("Failed to init load starter configuration", e);
		}
		return props;
	}

	/**
	 * Returns a DataSource object
	 * 
	 * @return DataSource object if and only if proper db configuration parameters
	 *         have been found else null
	 */
	public static DataSource getDataSource(Properties props) {
		if (props.containsKey("datasource.url") && props.containsKey("datasource.username")
				&& props.containsKey("datasource.password")) {

			BasicDataSource dataSource = new BasicDataSource();
			dataSource.setUrl(props.getProperty("datasource.url"));
			dataSource.setUsername(props.getProperty("datasource.username"));
			dataSource.setPassword(props.getProperty("datasource.password"));
			return dataSource;
		}
		return null;
	}

}
