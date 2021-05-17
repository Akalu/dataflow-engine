package computing.core;

import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import computing.core.Binding;
import computing.core.Task;

public class TestTask extends Task<String> {
	
	private static final Logger logger = LoggerFactory.getLogger(TestTask.class);
	
	private Binding binding;
	
	@Inject
	public TestTask(Binding binding) {
		this.binding = binding;
	}

	@Override
	public String execute() {
		logger.debug("started TestTask...");
		return "1 ok";
	}

	public Binding getBinding() {
		return binding;
	}

	public void setBinding(Binding binding) {
		this.binding = binding;
	}
}
