package computing.core;

import javax.annotation.Nonnull;

import org.apache.ibatis.session.SqlSession;
import org.codejargon.feather.Feather;

import computing.client.vault.VaultService;
import computing.core.database.BatisAbstractFactory;
import lombok.Getter;
import lombok.Setter;

public class ExecutionFlow {

	public static final String FAIL_INFO = "failInfo";

	protected static final int IN_PROGRESS = 1;
	protected static final int FAILED = 3;
	
	@Getter
	@Setter
	protected Binding binding;
	
	@Getter
	@Setter
	protected Feather feather;
	
	protected VaultService vaultService;
	private SqlSession session;

	// will be set to true if framework finds fields annotated with @Datastore
	// annotation
	protected boolean isPersistencePresent = false;

	protected <T> AsyncResult<T> executeAsync(Class<? extends Task<T>> clazz, int workers) {
		AsyncResult<T> result = ExecutionEngine.getInstance().executeAsync(clazz, feather, workers);
		updateProcessLaunchesTable(IN_PROGRESS);
		return result;
	}

	protected <T> AsyncResult<T> executeAsync(Class<? extends Task<T>> clazz, AsyncResult<T> input) {
		AsyncResult<T> result = ExecutionEngine.getInstance().executeAsync(clazz, input, feather);
		updateProcessLaunchesTable(IN_PROGRESS);
		return result;
	}

	protected <T> AsyncResult<T> execute(Class<? extends Task<T>> clazz, AsyncResult<T> input) {
		AsyncResult<T> result = ExecutionEngine.getInstance().execute(clazz, input, feather);
		updateProcessLaunchesTable(IN_PROGRESS);
		return result;
	}

	/**
	 * Put into this section logic tied with persistence
	 * @param launchId
	 * @param processId
	 */
	public void initProcess(String launchId, int processId) {
		session = BatisAbstractFactory.getInstance().getFactory().openSession(true);
	}

	public SqlSession getSession() {
		return session;
	}

	protected void handleStepError() {
		if (binding != null) {
			String existingFailInfo = binding.get(FAIL_INFO);
			if (existingFailInfo != null) {
				binding.add(FAIL_INFO, existingFailInfo + ", " + this.getClass().getSimpleName());
			} else {
				binding.add(FAIL_INFO, "failed on Task: " + this.getClass().getSimpleName());
			}
		}
	}

	@SafeVarargs
	protected static <T> void waitFor(long timeout, long poolInterval, @Nonnull AsyncResult<T>... results) {
		for (AsyncResult<T> result : results) {
			result.waitFor(timeout, poolInterval);
		}
	}

	@SafeVarargs
	protected static <T> void waitFor(long timeout, @Nonnull AsyncResult<T>... results) {
		for (AsyncResult<T> result : results) {
			result.waitFor(timeout);
		}
	}

	@SafeVarargs
	protected static <T> void waitFor(@Nonnull AsyncResult<T>... results) {
		for (AsyncResult<T> result : results) {
			result.waitFor();
		}
	}

	public void updateProcessLaunchesTable(int status) {
		if (binding != null) {
			String failInfo = binding.get(FAIL_INFO);
			if (failInfo != null) {
				status = FAILED;
			}
		}
	}

	public void setPersistencePresent(boolean isPersistencePresent) {
		this.isPersistencePresent = isPersistencePresent;
	}

}
