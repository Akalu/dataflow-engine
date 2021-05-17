package computing.boot;

import java.util.Properties;

import org.apache.ibatis.session.SqlSession;
import org.codejargon.feather.Feather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import computing.core.Stage;
import lombok.extern.slf4j.Slf4j;
import computing.core.Configuration;
import computing.core.ExecutionEngine;

/**
 * Launcher for business processes
 * 
 * There are two ways of using it
 * 
 * 1) Indirectly -
 * 
 * set in the POM the main class as computing.boot.ProcessStarter
 * 
 * annotate Activity classes to be executed with @Entry annotation
 * 
 * 
 * 2) Directly -
 * 
 * invoke static method ProcessStarter.launchWith(...)
 *
 */
@Slf4j
public class ProcessStarter {
	private static final String APP_PATH = "/boot.properties";

	private static final int COMPLETED = 2;

	private static final String CRITICAL_FAILURE = "Critical Failure";

	private static final String SYSTEM_BPID = "2000";

	public static void main(String[] args) {

		try (ExecutionEngine service = ExecutionEngine.getInstance()){
			Properties prop = Configurator.loadConfig(APP_PATH);

			ExecutionContext executionContext = new ExecutionContext();
			executionContext.createWith(prop);

			String bpid = null;
			if (args.length == 0) {
				bpid = SYSTEM_BPID;
			} else {
				bpid = args[0];
			}

			log.debug("running all classes");
			for (Class<? extends Stage> clazz : executionContext.getCandidates()) {
				log.debug("launching class : {}", clazz);
				launch(executionContext.getConfigurations(), clazz, Integer.valueOf(bpid));
			}
		} catch (Exception e) {
			log.error("", e);
		} 
	}

	/**
	 * Used to launch processes on the basis of result of package scan
	 * 
	 * @param withConfigClass - list of Configuration classes which provide
	 *                        configurations
	 * @param clazz           - class to be run
	 */
	private static void launch(Class<?>[] withConfigClass, Class<? extends Stage> clazz, int processId) {
		Logger log = LoggerFactory.getLogger(clazz);
		String bpid = null;
		SqlSession processSqlSession = null;
		try {

			Feather configFeather = Feather.with();
			Object[] configs = new Configuration[withConfigClass.length];
			for (int i = 0; i < withConfigClass.length; i++) {
				configs[i] = configFeather.instance(withConfigClass[i]);
			}

			Feather feather = Feather.with(configs);
			Stage process = feather.instance(clazz);

			process.setFeather(feather);
			process.setLogger(log);
			bpid = process.getBinding().get("bpId");
			process.initProcess(bpid, processId);
			processSqlSession = process.getSession();
			process.exec();
			process.updateProcessLaunchesTable(COMPLETED);

		} catch (Exception e) {
			handleProcessException(e, bpid, processId);
		} finally {
			try {
				if (processSqlSession != null) {
					processSqlSession.close();
				}
			} catch (Exception sqlException) {
				log.error("Couldn't close process sql session: {}", sqlException);
			}
		}
	}

	/**
	 * Used for direct launch of process with config file specified as parameter
	 * 
	 * @param clazz      - class to be run
	 * @param configName - name of properties file to run with
	 */
	public static void launchWith(Class<? extends Stage> clazz, Properties prop, int processId) {
		try (ExecutionEngine service = ExecutionEngine.getInstance()){

			ExecutionContext executionContext = new ExecutionContext();
			executionContext.createWith(prop);

			log.debug("launching class: {}", clazz);
			launch(executionContext.getConfigurations(), clazz, processId);
		} catch (Exception e) {
			log.error("", e);
		} 
	}

	private static void handleProcessException(Exception e, String bpid, int processId) {
		log.error("{}", e);
	}

}
