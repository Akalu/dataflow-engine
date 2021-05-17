package computing.core.database;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.concurrent.ThreadSafe;
import javax.sql.DataSource;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.type.TypeAliasRegistry;

import lombok.Getter;

@ThreadSafe
public class BatisAbstractFactory {

	@Getter
	private SqlSessionFactory factory;

	@Getter
	private DataSource dataSource;

	private static volatile BatisAbstractFactory instance = null;

	public static BatisAbstractFactory getInstance() {
		if (instance == null) {
			instance = new BatisAbstractFactory();
		}
		return instance;
	}

	private BatisAbstractFactory() {
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void buildFactory(Map<String, Class<?>> entities, List<Class<?>> recordMappers) {

		// Creates a transaction factory.
		TransactionFactory trxFactory = new JdbcTransactionFactory();

		// Creates an environment object with the specified name, transaction
		// factory and a data source.
		Environment env = new Environment("dev", trxFactory, dataSource);

		// Creates a Configuration object base on the Environment object.
		// We add all needed type aliases and mappers

		Configuration config = new Configuration(env);
		TypeAliasRegistry aliases = config.getTypeAliasRegistry();

		for (Entry<String, Class<?>> entry : entities.entrySet()) {
			aliases.registerAlias(entry.getKey(), entry.getValue());
		}

		for (Class<?> mapper : recordMappers) {
			config.addMapper(mapper);
		}

		//

		// Build the SqlSessionFactory based on the created Configuration object.
		factory = new SqlSessionFactoryBuilder().build(config);
	}

}
