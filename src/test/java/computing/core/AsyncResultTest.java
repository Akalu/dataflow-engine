package computing.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.Test;

import computing.core.AsyncResult;
import computing.core.Event;
import computing.core.Result;

public class AsyncResultTest {

	private static final Class<?> testClass =  ExecutionServiceTest.class;
	
	@Test
	public void finctionalTest() {
		AsyncResult<String> asyncResult = new AsyncResult<>();  
		asyncResult.addResult(new Result<>());
		
		List<Result<String>> added = asyncResult.getResults();
		assertNotNull(added);
		assertEquals(1,added.size());
	}
	
	@Test
	public void nullSafeTest() {
		AsyncResult<String> asyncResult = new AsyncResult<>();  
		asyncResult.addResult(null);
		
		List<Result<String>> added = asyncResult.getResults();
		assertNotNull(added);
		assertEquals(0,added.size());
	}

	@Test
	public void waiterTest() {
		AsyncResult<String> input = new AsyncResult<>();
		Result<String> result = new Result<>();
		CompletableFuture<String> future = new CompletableFuture<>();
		future.complete("completed");
		result.updateHistory(null, future);
		input.addResult(result);

		assertTrue(future.isDone());
		Boolean isError = false;
		try {
			assertEquals("completed",future.get());
		} catch (InterruptedException | ExecutionException e) {
			isError = true;
		}
		assertFalse(isError);

		List<Result<String>> results = input.getResults();
		assertEquals(1,results.size());
		List<Event<String>> history = results.get(0).getHistory();
		assertEquals(1,history.size());

		assertTrue(AsyncResult.isCompleted(results.get(0).pollEvent().getFuture()));

		input.waitFor(1000, 500);


		results = input.getResults();
		assertEquals(1,results.size());
		history = results.get(0).getHistory();
		assertEquals(1,history.size());

	}

	@Test
	public void waiterTimeoutTest() {
		AsyncResult<String> input = new AsyncResult<>();
		Result<String> result = new Result<>();
		CompletableFuture<String> future = new CompletableFuture<>();
		result.updateHistory(testClass, future);
		input.addResult(result);


		List<Result<String>> results = input.getResults();
		assertEquals(1,results.size());
		List<Event<String>> history = results.get(0).getHistory();
		assertEquals(1,history.size());

		assertFalse(AsyncResult.isCompleted(results.get(0).pollEvent().getFuture()));

		input.waitFor(1000, 500);

		results = input.getResults();
		assertEquals(1,results.size());
		history = results.get(0).getHistory();
		assertEquals(2,history.size());


		Event<String> lastEvent = results.get(0).pollEvent();
		assertTrue(AsyncResult.isCompleted(results.get(0).pollEvent().getFuture()));

		assertFalse(lastEvent.getHasData());
		assertNull(lastEvent.getFuture());

	}

	@Test
	public void multiWaiterTimeoutTest() {
		AsyncResult<String> input = new AsyncResult<>();
		Result<String> result = new Result<>();
		CompletableFuture<String> futureOne = new CompletableFuture<>();
		result.updateHistory(testClass, futureOne);
		CompletableFuture<String> futureTwo = new CompletableFuture<>();
		result.updateHistory(testClass, futureTwo);

		input.addResult(result);


		List<Result<String>> results = input.getResults();
		assertEquals(1,results.size());
		List<Event<String>> history = results.get(0).getHistory();
		assertEquals(2,history.size());

		assertFalse(AsyncResult.isCompleted(results.get(0).pollEvent().getFuture()));

		input.waitFor(1000, 500);

		results = input.getResults();
		assertEquals(1,results.size());
		history = results.get(0).getHistory();
		assertEquals(3,history.size());

		// all 3 events must not have data due to canceling in wait method
		// plus FAILED event must not have a future object
		List<Event<String>> allHistory = results.get(0).getHistory();

		Event<String> lastEvent = allHistory.get(2);
		assertTrue(AsyncResult.isCompleted(results.get(0).pollEvent().getFuture()));
		assertFalse(lastEvent.getHasData());
		assertNull(lastEvent.getFuture());

		Event<String> previousEvent = allHistory.get(1);
		assertTrue(AsyncResult.isCompleted(results.get(0).pollEvent().getFuture()));
		assertFalse(previousEvent.getHasData());
		assertNotNull(previousEvent.getFuture());

		Event<String> firstEvent = allHistory.get(0);
		assertTrue(AsyncResult.isCompleted(results.get(0).pollEvent().getFuture()));
		assertFalse(firstEvent.getHasData());
		assertNotNull(firstEvent.getFuture());
	}


}
