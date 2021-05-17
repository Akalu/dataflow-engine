package computing.core;

import org.slf4j.Logger;

import lombok.Setter;

public class Stage extends ExecutionFlow {
	
	@Setter
	protected Logger logger;

	public <T> void log(Iterable<T> input) {
		for (T object : input) {
			logger.info("{}", object);
		}
	}

	public void exec() throws Exception {
		// Activity is used interface-like in ProcessStarter, so we have to make an
		// empty method here
	}

}
