package computing.core;

import java.lang.reflect.Field;

import org.apache.ibatis.session.SqlSession;
import org.codejargon.feather.Feather;

import computing.annotation.InjectStore;
import computing.exception.CoreException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskFactory {

	private TaskFactory() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Used to generate instances of steps Injects into steps necessary resources
	 * based on annotations
	 * 
	 * @param <T>   - type of transfer object
	 * @param clazz - type of step to be instantiated
	 * @return instantiated Task object
	 * 
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static <T> Task<T> get(Class<? extends Task<T>> clazz, Feather feather) {

		if (clazz == null) {
			throw new IllegalArgumentException("Class name cannot be null");
		}

		log.debug("creating stage {}", clazz);
		Task<T> stage = feather.instance(clazz);
		// validate stage
		stage.setPersistencePresent(containsPersistenseMechanism(clazz));

		return stage;
	}

	/**
	 * Used to inject objects with complex instantiation logic
	 * 
	 * @param <T>
	 * @param stage   - object to insert into
	 * @param session - myBatis session
	 */
	public static <T> void updateStep(Task<T> stage, SqlSession session) {

		if (stage == null) {
			throw new IllegalArgumentException("Task object cannot be null");
		}

		Class<?> clazz = stage.getClass();
		log.debug("class to analyze: {}", clazz);
		// analyze field level annotations
		Field[] fields = clazz.getDeclaredFields();
		for (Field f : fields) {
			InjectStore dbAnnotation = f.getAnnotation(InjectStore.class);
			if (dbAnnotation != null) {
				String type = f.getAnnotatedType().getType().getTypeName();
				log.debug("found annotation: {}", dbAnnotation);
				try {
					Class<?> classToInject = Class.forName(type);
					log.debug("class to inject: {}", type);
					log.debug("injecting");
					if (session != null) {
						setValue(stage, f, session.getMapper(classToInject));
					}
				} catch (IllegalAccessException | ClassNotFoundException e) {
					throw new CoreException("Update Task error", e);
				}
			}
		}
	}

	@SuppressWarnings("squid:CallToDeprecatedMethod")
	private static void setValue(Object object, Field field, Object value) throws IllegalAccessException {
		if (!field.isAccessible()) {
			field.setAccessible(true);
			field.set(object, value);
			field.setAccessible(false);
		} else {
			field.set(object, value);
		}
	}

	private static <T> boolean containsPersistenseMechanism(Class<? extends Task<T>> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		for (Field f : fields) {
			InjectStore dbAnnotation = f.getAnnotation(InjectStore.class);
			if (dbAnnotation != null) {
				return true;
			}
		}
		return false;
	}

}
