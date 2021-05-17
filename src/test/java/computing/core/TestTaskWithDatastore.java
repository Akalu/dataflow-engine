package computing.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import computing.annotation.InjectStore;
import computing.core.Task;

public class TestTaskWithDatastore extends Task<String> {
	
	private static final Logger logger = LoggerFactory.getLogger(TestTaskWithDatastore.class);
	
	@InjectStore
	DAOMock dao;

	@Override
	public String execute() {
		logger.debug("started TestTask...");
		return "ok";
	}
}
