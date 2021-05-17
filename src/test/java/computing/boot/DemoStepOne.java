package computing.boot;


import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import computing.boot.mappers.FruitDAO;
import computing.core.Binding;
import computing.core.Task;
import computing.model.Fruit;
import computing.model.Listing;

/**
 * 	Demonstrate how to use persistence and Feather annotations
 */
public class DemoStepOne extends Task<Listing> {
	private static final Logger logger = LoggerFactory.getLogger(DemoStepOne.class);
	
	/**
	 * 	Use annotation @Datastore for mappers to be injected in runtime
	 * 
	 *  Injection takes place each time on each Step object instantiation
	 *  See definition of abstract class Step<T> for details 
	 */
//	@InjectStore
	private FruitDAO fruits;
	
	/**
	 * 	All parameters will be created during Step object instantiation
	 */
	@Inject
	public DemoStepOne(Binding binding) {
		this.binding = binding;
	}


	@Override
	public Listing execute() {
		boolean isBindingInjected = binding != null;
		logger.debug(" is binding injected? {}", isBindingInjected);
		logger.debug("load data");

		Listing list = new Listing();
		List<Fruit> records = new ArrayList<>();

		logger.debug("Found = {}", records);

		for (Fruit fruit : records) {
			list.add(fruit.getName());
		}

		logger.debug("done");
		return list;
	}
}
