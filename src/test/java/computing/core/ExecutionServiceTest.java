package computing.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.codejargon.feather.Feather;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import computing.core.AsyncResult;
import computing.core.Binding;
import computing.core.Event;
import computing.core.ExecutionEngine;
import computing.core.Result;

public class ExecutionServiceTest {
	
	private static final Class<?> testClass =  ExecutionServiceTest.class;
	
	private ExecutionEngine executionService;
	
	private Binding binding;
	private Feather feather;
	
	@Before
	public void init() {
		executionService = ExecutionEngine.getInstance();
		binding = new Binding();
		binding.add("bpId", "123");
		
		feather = Feather.with(binding);
	}
	
	@After
	public void close() throws Exception {
		executionService.close();
	}
	
	@Test
	public void testExecutionSuccess() {
		 
		AsyncResult<String> output = executionService.executeAsync(TestTask.class, feather, 2);
		assertNotNull(output);
		List<Result<String>> res = output.getResults();
		assertEquals(2,res.size());
		
		// must not be any errors tied with execution itself
		Event<String> event1 = res.get(0).pollEvent();
		assertFalse(event1.getError().isPresent());
		
		Event<String> event2 = res.get(1).pollEvent();
		assertFalse(event2.getError().isPresent());
	}

	@Test
	public void testExecutionFail() {
		 
		AsyncResult<String> output = executionService.executeAsync(TestFailedTask.class, feather, 2);
		assertNotNull(output);
		List<Result<String>> res = output.getResults();
		assertEquals(2,res.size());

		output.waitFor();

		// check the first result
		// must not be any errors tied with execution itself
		Event<String> event1 = res.get(0).pollEvent();
		assertTrue(event1.getError().isPresent());
		assertEquals("java.lang.RuntimeException: test exception has been thrown", event1.getError().get().getMessage());
		
		
		
		assertTrue(event1.getFuture().isCompletedExceptionally());
		// must be error tied future element
		Exception ex1 = null;
		try {
			event1.getFuture().get();
		}catch(Exception e) {
			ex1 = e;
		}
		assertNotNull(ex1);
		assertEquals("java.lang.RuntimeException: test exception has been thrown", ex1.getMessage());
		
		// check the second result
		Event<String> event2 = res.get(1).pollEvent();
		assertTrue(event2.getError().isPresent());
		assertEquals("java.lang.RuntimeException: test exception has been thrown", event2.getError().get().getMessage());
		
		

		assertTrue(event2.getFuture().isCompletedExceptionally());

		// must be error tied future element
		Exception ex2 = null;
		try {
			event2.getFuture().get();
		}catch(Exception e) {
			ex2 = e;
		}
		assertNotNull(ex2);
		assertEquals("java.lang.RuntimeException: test exception has been thrown", ex2.getMessage());
		
	}


	@Test
	public void testExecutionNullSafety() {
		Exception ex1 = null;
		try {
			executionService.executeAsync(null, feather, 2);
		}catch(Exception e) {
			ex1 = e;
		}
		assertNull(ex1);
		
	}


}
