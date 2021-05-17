package computing.core;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.concurrent.ThreadSafe;

import org.codejargon.feather.Feather;
import lombok.extern.slf4j.Slf4j;

@ThreadSafe
@Slf4j
public class ExecutionEngine implements AutoCloseable {

	private final ExecutorService executorService;

	private static volatile ExecutionEngine instance = null;

	private ExecutionEngine() {
		this.executorService = Executors.newScheduledThreadPool(3);
	}

	public static ExecutionEngine getInstance() {
		if (instance == null) {
			instance = new ExecutionEngine();
		}
		return instance;
	}

	/**
	 * Asynchronously process data using code in clazz and data from input
	 * 
	 * If previous step still has not completed its work, add a new chain
	 *
	 * @param clazz - code to execute
	 * @param input - data to process
	 * @return AsyncResult
	 */
	public <T> AsyncResult<T> executeAsync(Class<? extends Task<T>> clazz, AsyncResult<T> input, Feather feather) {

		if (input == null) {
			throw new IllegalArgumentException("input cannot be null");
		}

		for (Result<T> r : input.getResults()) {
			Event<T> lastEvent = r.pollEvent();
			if (Boolean.TRUE.equals(!isExtensible(lastEvent.getFuture()))) {
				try {
					Task<T> stage = TaskFactory.get(clazz, feather);
					CompletableFuture<T> future = lastEvent.getFuture();
					if (future != null) {
						r.updateHistory(clazz, future.thenApplyAsync(stage::shadowExecute));
					}
				} catch (Exception e) {
					log.error("", e);
					r.updateHistory(clazz, e);
				}
			}
		}

		input.setProviderClass(clazz);
		return input;
	}

	public <T> AsyncResult<T> execute(Class<? extends Task<T>> clazz, AsyncResult<T> input, Feather feather) {
		AsyncResult<T> result = new AsyncResult<>();
		Result<T> r = new Result<>();
		try {
			Task<T> stage = TaskFactory.get(clazz, feather);
			CompletableFuture<T> future = CompletableFuture.completedFuture(input.getCompleted(0));
			r.updateHistory(clazz, future.thenApplyAsync(stage::shadowExecute));
		} catch (Exception e) {
			log.error("", e);
			r.updateHistory(clazz, e);
		}
		result.addResult(r);
		result.setProviderClass(clazz);
		return result;
	}

	/**
	 * Asynchronously supply data using code in clazz and data from other source
	 * (the source must be specified inside Class clazz)
	 *
	 * @param <T>     - type of transfer object
	 * @param clazz   - code to execute
	 * @param workers - number of threads to start process with
	 * @return AsyncResult
	 */
	public <T> AsyncResult<T> executeAsync(Class<? extends Task<T>> clazz, Feather feather, int workers) {
		AsyncResult<T> result = new AsyncResult<>();
		for (int i = 0; i < workers; i++) {
			Result<T> r = new Result<>();
			try {
				Task<T> stage = TaskFactory.get(clazz, feather);
				CompletableFuture<T> future = CompletableFuture.supplyAsync(stage::shadowExecute);
				r.updateHistory(clazz, future);
			} catch (Exception e) {
				log.error("", e);
				r.updateHistory(clazz, e);
			}
			result.addResult(r);
		}
		result.setProviderClass(clazz);
		return result;
	}

	private static <T> Boolean isExtensible(CompletableFuture<T> future) {
		if (future == null) {
			return false;
		}
		return future.isCancelled() || future.isCompletedExceptionally();
	}

	@Override
	public void close() throws Exception {
		executorService.shutdown();
	}

}
