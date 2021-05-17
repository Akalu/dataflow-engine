package computing.boot;

import javax.inject.Inject;

import org.slf4j.LoggerFactory;

import computing.core.Stage;
import computing.core.AsyncResult;
import computing.core.Binding;
import computing.model.Listing;

public class TestBusinessProcess extends Stage {

	
	@Inject
	public TestBusinessProcess(Binding binding) {
		this.binding = binding;
	}

	@Override
	public void exec() {
		logger = LoggerFactory.getLogger(TestBusinessProcess.class);
		logger.debug(binding.get("bpId"));
		logger.debug("DemoBusinessProcess has started!");

		logger.debug("======== Executing Step One ========");
		// the first step is used as data supplier hence there is not need in input
		AsyncResult<Listing> results = executeAsync(DemoStepOne.class, 2);
		waitFor(results);
		String resultsExtracted = results.getResults().toString();
		logger.debug("Step One results list (to be passed to the next stage): {}", resultsExtracted);
	}
}
