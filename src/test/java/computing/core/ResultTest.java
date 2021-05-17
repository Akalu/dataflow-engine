package computing.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CompletableFuture;
import org.junit.Test;

import computing.core.Event;
import computing.core.Result;

public class ResultTest {
	
	private static final Class<?> testClass =  ResultTest.class;


    @Test
    public void constuctorTest() {
        Result<String> result = new Result<>();
        assertNotNull(result.getId());
        assertEquals(0, result.getHistory().size());
		CompletableFuture<String> future = new CompletableFuture<>();
        result.updateHistory(testClass, future);

        assertEquals(1, result.getHistory().size());
        Event<String> lastEvent = result.pollEvent();
        assertNotNull(lastEvent);
        assertFalse(lastEvent.getError().isPresent());
    }

    @Test
    public void compareTest() {
        Result<String> result1 = new Result<>();
        Result<String> result2 = new Result<>();
        assertNotEquals(result1, result2);
    }

    @Test
    public void updateHistoryTest() {
        Result<String> result = new Result<>();
		CompletableFuture<String> future = new CompletableFuture<>();
        result.updateHistory(testClass, future);
        
        Event<String> lastEvent = result.pollEvent();
        assertNotNull(lastEvent);
        assertFalse(lastEvent.getError().isPresent());

        result.updateHistory(testClass, future);
        
        lastEvent = result.pollEvent();
        assertNotNull(lastEvent);
        assertFalse(lastEvent.getError().isPresent());

    }

    @Test
    public void updateHistoryWithErrorTest() {
        Result<String> result = new Result<>();
        Exception e = new RuntimeException("just a test");
        result.updateHistory(testClass, e);
        Event<String> lastEvent = result.pollEvent();
        assertNotNull(lastEvent);
        assertTrue(lastEvent.getError().isPresent());
        assertEquals("just a test",lastEvent.getError().get().getMessage());

    }
    
    @Test
    public void poolEventWithDataTest() {
        Result<String> result = new Result<>();
        assertNotNull(result.getId());
        assertEquals(0, result.getHistory().size());
		CompletableFuture<String> future = new CompletableFuture<>();
        result.updateHistory(testClass, future);

        assertEquals(1, result.getHistory().size());
        Event<String> lastEvent = result.pollEventWithData();
        assertNotNull(lastEvent);
        assertTrue(lastEvent.getHasData());
 
    }
    
    @Test
    public void poolEmptyQueueTest() {
        Result<String> result = new Result<>();
        assertNotNull(result.getId());
        assertEquals(0, result.getHistory().size());

        Event<String> lastEvent = result.pollEventWithData();
        assertNull(lastEvent);
 
    }

    @Test
    public void poolOverflowQueueTest() {
        Result<String> result = new Result<>();
        assertNotNull(result.getId());
        assertEquals(0, result.getHistory().size());
        
        CompletableFuture<String> future = new CompletableFuture<>();
        for (int i = 0; i < Result.CAPACITY; i++) {
           result.updateHistory(testClass, future);
        }
        assertEquals(Result.CAPACITY, result.getHistory().size());

        result.updateHistory(testClass, future);
        assertEquals(Result.CAPACITY+1, result.getHistory().size());
        
        result.updateHistory(testClass, future);
        assertEquals(Result.CAPACITY+1, result.getHistory().size());

    }
    
    @Test
    public void nullSafetyQueueTest() {
        Result<String> result = new Result<>();
        assertNotNull(result.getId());
        assertEquals(0, result.getHistory().size());
        
        CompletableFuture<String> future = new CompletableFuture<>();
        result.updateHistory(null, future);
        
        future = null;
        result.updateHistory(testClass, future);

        result.updateHistory(null, future);

    }

 
}
