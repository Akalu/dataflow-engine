package computing.boot;

import java.io.InputStream;
import java.util.Properties;

import computing.boot.ProcessStarter;
import computing.core.Stage;
import computing.exception.BootException;

public class ProcessStarterFunctionalTest {
	public final static String configFile = "/application_test.properties";

	public static void main(String[] args) {
		int processId = 2000;
		Class<? extends Stage> clazz = TestBusinessProcess.class;

		ProcessStarter.launchWith(clazz, getProperties(configFile), processId);
	}

	private static Properties getProperties(String path) {
		Properties props = new Properties();
		try (InputStream is = ProcessStarterFunctionalTest.class.getResourceAsStream(path)) {
			props.load(is);
		} catch (Exception e) {
			throw new BootException("Failed to init load starter configuration", e);
		}
		return props;
	}

}
