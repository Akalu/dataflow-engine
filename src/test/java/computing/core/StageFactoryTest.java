package computing.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.codejargon.feather.Feather;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import computing.core.Binding;
import computing.core.Task;
import computing.core.TaskFactory;
import computing.model.DefaultConfiguration;

public class StageFactoryTest {
	
	private Feather feather;

	@Before
	public void init() {
		DefaultConfiguration testConfiguration = new DefaultConfiguration();
		feather = Feather.with(testConfiguration);
	}
	
	@After
	public void close() {
	}
	


	@Test
	public void getTest() {
		Boolean isErrors = false;
		
		Task<String> stage = null;
		
		try {
			stage = TaskFactory.get(TestTask.class, feather);
		} catch (IllegalArgumentException e) {
			isErrors = true;
		}
		assertFalse(isErrors);
		assertNotNull(stage);
		
		String result = stage.execute();
		assertEquals("1 ok",result);
		
		Binding binding = stage.getBinding();
		assertNotNull(binding);
		assertNotNull(binding.get("bpId"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getExceptionTest() {
		Boolean isErrors = false;
		
		@SuppressWarnings("rawtypes")
		Class clazz = StageFactoryTest.class;
		
		try {
			TaskFactory.get(clazz, feather);
		} catch (Exception e) {
			isErrors = true;
		}
		assertTrue(isErrors);
	}
	
	@Test
	public void exceptionHandlerTest() {
		Boolean isErrors = false;
		
		@SuppressWarnings("rawtypes")
		Class clazz = TestFailedTask.class;
		Task<String> stage = null;
		
		try {
			stage = TaskFactory.get(clazz, feather);

			Binding binding = stage.getBinding();
			assertNotNull(binding);
			assertNotNull(binding.get("bpId"));
			stage.execute("input");
		} catch (Exception e) {
			isErrors = true;
		}
		// must not be any errors
		assertFalse(isErrors);
		Binding binding = stage.getBinding();
		assertNotNull(binding);
		assertNotNull(binding.get("failInfo"));
	}


	
}
