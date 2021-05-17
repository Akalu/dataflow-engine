package computing.boot;

import java.util.function.Consumer;

import computing.core.Stage;
import computing.annotation.Datastore;
import computing.annotation.Entity;
import computing.annotation.Entry;
import computing.core.Configuration;
import computing.utils.CollectionUtils;

/**
 * Collections of processors to work with metadata
 */
public class AnnotationProcessors {

	private AnnotationProcessors() {
		throw new IllegalStateException("Utility class");
	}

	@SuppressWarnings("unchecked")
	public static Consumer<Class<?>> processEntryAnnotation(ExecutionContext context) {
		return clazz -> {
			if (Stage.class.isAssignableFrom(clazz) && clazz.isAnnotationPresent(Entry.class)) {
				// Annotation annotation = clazz.getAnnotation(Entry.class);
				// Entry stepInfo = (Entry) annotation;
				// do smth if needed
				context.getCandidates().add((Class<? extends Stage>) clazz);
			}
		};
	}

	public static Consumer<Class<?>> processDatastoreAnnotation(ExecutionContext context) {
		return clazz -> {
			if (clazz.isAnnotationPresent(Datastore.class)) {// check this class is mapper
				// Annotation mapperAnnotation = clazz.getAnnotation(Datastore.class);
				if (CollectionUtils.inSet(clazz.getCanonicalName(), context.getMapperPaths())) {
					context.getMappers().add(clazz);
				}
			}
		};
	}

	public static Consumer<Class<?>> processConfiguration(ExecutionContext context) {
		return clazz -> {
			if (Configuration.class.isAssignableFrom(clazz) && !clazz.isInterface()
					&& context.getConfigClassNames().contains(clazz.getCanonicalName())) {
				context.getConfigurationsAsList().add(clazz);
			}
		};
	}

	public static Consumer<Class<?>> processEntityAnnotation(ExecutionContext context) {
		return clazz -> {
			if (clazz.isAnnotationPresent(Entity.class)) {// check this class is entity
				Entity entityAnnotation = clazz.getAnnotation(Entity.class);
				context.getEntities().put(entityAnnotation.alias(), clazz);
			}
		};
	}

}
