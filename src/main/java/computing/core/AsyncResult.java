package computing.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Aggregator class for Result<T> objects
 * 
 * @param <T>
 */

@Slf4j
public class AsyncResult<T> {

	@Setter
	private Class<?> providerClass;

	@Getter
	private final List<Result<T>> results = new ArrayList<>();

	public void addResult(Result<T> result) {
		if (result != null) {
			results.add(result);
		}
	}

	private static <T> boolean join(Event<T> event) {
		try {
			CompletableFuture<T> future = event.getFuture();
			if (future != null) {
				future.join();
			}
			return false;
		} catch (CompletionException | CancellationException e) {
			log.error("exception in join: ", e);
			event.setHasData(false);
			event.setError(e);
		}
		return true;
	}

	public static <T> Boolean isCompleted(@Nullable CompletableFuture<T> future) {
		if (future == null) {
			return true;
		}
		return future.isCancelled() || future.isCompletedExceptionally() || future.isDone();
	}

	public AsyncResult<T> waitFor() {
		return waitFor(30000, 1000);
	}

	public AsyncResult<T> waitFor(long timeout) {
		return waitFor(timeout, 1000);
	}

	/**
	 * Wait all threads for completion
	 *
	 * @param timeout      - defines how long to wait for completion before
	 *                     terminating thread, ms
	 * @param poolInterval - defines pooling interval, ms
	 * @return updated AsyncResult
	 */
	public AsyncResult<T> waitFor(long timeout, long poolInterval) {
		try {
			int counter = (int) (timeout / poolInterval);
			while (counter-- > 0) {
				Thread.sleep(poolInterval);
				// look up history against any incomplete future
				Optional<Result<T>> op = this.getResults().stream()
						.filter(result -> !isCompleted(result.pollEvent().getFuture())).findAny();
				if (!op.isPresent()) {// complete
					// this block is executed only if all threads have completed
					// their work with/without errors
					this.getResults().stream()
							.forEach(result -> result.getHistory().stream().forEach(AsyncResult::join));
					return this;
				}
			}
			// complete with force
			this.getResults().stream().forEach(result -> complete(result, providerClass, timeout));

		} catch (Exception e) {
			log.error("", e);
		}
		return this;
	}

	/**
	 *
	 * @param <T>     - type of transfer object
	 * @param result  - all data associated with thread
	 * @param clazz   - type of class which invoked this method
	 * @param timeout - defines how long to wait for completion before terminating
	 *                thread, ms
	 * @return
	 */
	private static <T> void complete(Result<T> result, Class<?> clazz, long timeout) {
		boolean hasErrors = false;
		for (Event<T> event : result.getHistory()) {// close all which are not done
			CompletableFuture<T> future = event.getFuture();
			if (Boolean.TRUE.equals(!isCompleted(future))) {
				// Note: this line is executed if future is not already completed
				// i.e. some step still has the chance to complete its work and update transfer
				// object
				future.complete(null);
				event.setHasData(false);
				hasErrors = true;
			}
			hasErrors = hasErrors || join(event);
		}

		if (hasErrors) {
			Exception e = new Exception(String.format("timeout :%s", timeout));
			result.updateHistory(clazz, e);
		}
	}

	public T getCompleted() throws ExecutionException, InterruptedException {
		return getCompleted(0);
	}

	public T getCompleted(int id) throws ExecutionException, InterruptedException {
		return getResults().get(id).pollEventWithData().getFuture().get();
	}

}
