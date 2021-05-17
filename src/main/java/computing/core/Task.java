package computing.core;

import java.util.UUID;

import org.apache.ibatis.session.SqlSession;

import computing.core.database.BatisAbstractFactory;

/**
 * Encapsulates business logic
 * 
 * @param <T> defines the type of input/output for each step
 */
public abstract class Task<T> extends ExecutionFlow {

	// autonumber
	protected final String id = UUID.randomUUID().toString();

	/**
	 * this method is a processor - defines code to execute on each chunk of data of
	 * type T
	 * 
	 * @return processed data plus input if needed
	 */
	public T execute(T input) {
		return input;
	}

	/**
	 * this method is a supplier - defines code to execute on each invoke
	 * 
	 * @return data to be processed on next stage
	 */
	public T execute() {
		return null;
	}

	/**
	 * These two methods will be invoked by framework
	 * 
	 * If persistence annotations are detected then: <br>
	 * 
	 * 1) each execution will be wrapped in MyBatis session <br>
	 * 
	 * 2) all fields annotated with @InjectStore will be populated <br>
	 * 
	 * else: <br>
	 * 
	 * normal invoking of (overridden) methods execute()/execute(T t) will take
	 * place
	 * 
	 */
	public final T shadowExecute(T input) {
		if (this.isPersistencePresent) {
			try (SqlSession session = BatisAbstractFactory.getInstance().getFactory().openSession(true)) {
				TaskFactory.updateStep(this, session);
				return execute(input);
			}
		} else {
			return execute(input);
		}
	}

	public final T shadowExecute() {
		if (this.isPersistencePresent) {
			try (SqlSession session = BatisAbstractFactory.getInstance().getFactory().openSession(true)) {
				TaskFactory.updateStep(this, session);
				return execute();
			}
		} else {
			return execute();
		}
	}

	public String getId() {
		return id;
	}

}
