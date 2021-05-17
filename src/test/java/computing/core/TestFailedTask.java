package computing.core;

import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import computing.core.Binding;
import computing.core.Task;

public class TestFailedTask extends Task<String> {
	
	private static final Logger logger = LoggerFactory.getLogger(TestFailedTask.class);
	
	@Inject
	public TestFailedTask(Binding binding) {
		this.binding = binding;
	}

	@Override
	public String execute() {
		logger.debug("started TestFailedTask...");
		throw new RuntimeException("test exception has been thrown");
	}
	
	@Override
	public String execute(String s) {
		try {
			logger.debug("started TestFailedTask with parameter...");
			throw new Exception("asd");
		}catch (Exception e) {
			logger.error(e.getMessage());
			this.handleStepError();
		}
		return "ok";
	}
	
	public Binding getBinding() {
		return binding;
	}

	public void setBinding(Binding binding) {
		this.binding = binding;
	}


}
