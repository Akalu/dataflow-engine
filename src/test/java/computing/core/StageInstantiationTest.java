package computing.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.ibatis.session.SqlSession;
import org.codejargon.feather.Feather;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import computing.core.Binding;
import computing.core.Task;
import computing.core.TaskFactory;

public class StageInstantiationTest {
	
	private Binding binding;
	private Feather feather;

	@Before
	public void init() {
		binding = new Binding();
		binding.add("bpId", "123");
		
		feather = Feather.with(binding);
	}
	
	@After
	public void close() {
	}
	
	
	@Test
	public void stageUpdateTest() throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		
		Task<String> stage = TaskFactory.get(TestTaskWithDatastore.class, feather);
		assertNotNull(stage);
		
		SqlSession session = null;
		TaskFactory.updateStep(stage, session);
		
	}
	
	@Test
	public void stageShadowExecutionTest() throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		
		Task<String> stage = TaskFactory.get(TestTaskWithDatastore.class, feather);
		assertNotNull(stage);
		
		SqlSession session = null;
		stage.setPersistencePresent(false);
		String result = stage.shadowExecute();
		assertEquals("ok",result);
	}

}
