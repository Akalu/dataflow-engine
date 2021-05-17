package computing.boot;

import static computing.boot.AnnotationProcessors.processConfiguration;
import static computing.boot.AnnotationProcessors.processDatastoreAnnotation;
import static computing.boot.AnnotationProcessors.processEntityAnnotation;
import static computing.boot.AnnotationProcessors.processEntryAnnotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;

import javax.sql.DataSource;

import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import computing.core.Stage;
import computing.core.database.BatisAbstractFactory;
import computing.exception.BootException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * A simple POJO class to hold all business process configuration
 */

@Slf4j
public class ExecutionContext {
	
	@Getter
	private final List<Class<? extends Stage>> candidates = new LinkedList<>();
	private final List<Class<?>> configurations = new LinkedList<>();
	
	@Getter
	private final List<Class<?>> mappers = new LinkedList<>();
	
	@Getter
	private final Map<String, Class<?>> entities = new HashMap<>();
	
	@Getter
	private final Set<String> configClassNames = new HashSet<>();
	
	@Getter
	private final Set<String> mapperPaths = new HashSet<>();

	public void createWith(Properties prop) {

		if (!prop.containsKey("packages")) {
			throw new BootException("Starter configuration does not contain package names list");
		}

		String[] names = prop.getProperty("packages").split(",");
		if (names.length == 0) {
			throw new BootException("Starter configuration must contain at least one package name");
		}

		if (prop.containsKey("configuration")) {
			String[] configs = prop.getProperty("configuration").split(",");
			for (String config : configs) {
				configClassNames.add(config.trim());
			}
		}
		log.debug("configuration: {}", configClassNames);

		if (prop.containsKey("mapperscan")) {
			String[] paths = prop.getProperty("mapperscan").split(",");
			for (String path : paths) {
				mapperPaths.add(path.trim());
			}
		}
		log.debug("mapperscan: {}", mapperPaths);

		List<Class<?>> found = new ArrayList<>();
		for (String packageName : names) {
			log.debug("get classes for package: {}", packageName);
			found.addAll(getClasses(packageName));
		}
		log.debug("found classes: {}", found.size());

		log.debug("scanning all classes");
		List<Consumer<Class<?>>> processors = new ArrayList<>();
		processors.add(processEntryAnnotation(this));
		processors.add(processDatastoreAnnotation(this));
		processors.add(processConfiguration(this));
		processors.add(processEntityAnnotation(this));
		for (Class<?> clazz : found) {
			// analyze class level annotations
			processors.forEach(proc -> proc.accept(clazz));
		}
		log.debug("found candidates: {}", candidates);
		log.debug("found configs: {}", configurations);
		log.debug("found mappers: {}", mappers);
		log.debug("found entities: {}", entities);

		DataSource dataSource = Configurator.getDataSource(prop);

		if (dataSource != null) {
			BatisAbstractFactory batisAbstractFactory = BatisAbstractFactory.getInstance();
			batisAbstractFactory.setDataSource(dataSource);
			batisAbstractFactory.buildFactory(entities, mappers);
		}
	}

	private static Set<Class<?>> getClasses(String packageName) {

		List<ClassLoader> classLoadersList = new LinkedList<>();
		classLoadersList.add(ClasspathHelper.contextClassLoader());
		classLoadersList.add(ClasspathHelper.staticClassLoader());
		log.debug(classLoadersList.get(0).getParent().toString());

		Reflections reflections = new Reflections(new ConfigurationBuilder()
				.setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner(), new TypeAnnotationsScanner())
				.setUrls(ClasspathHelper.forPackage(packageName))
				.filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(packageName))));


		return reflections.getSubTypesOf(Object.class);
	}

	public Class<?>[] getConfigurations() {
		log.debug("config classes : {}", configurations);
		return configurations.toArray(new Class[configurations.size()]);
	}

	public List<Class<?>> getConfigurationsAsList() {
		return configurations;
	}


}
