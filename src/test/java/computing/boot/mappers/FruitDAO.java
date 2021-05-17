package computing.boot.mappers;

import java.util.List;
import org.apache.ibatis.annotations.Select;

import computing.annotation.Datastore;
import computing.model.Fruit;


@Datastore
public interface FruitDAO {
	/**
     * Get all records from the database based on the record
     * identified. 
     * 
     * See also a definition of class Fruit
     *
     * @param name record identifier
     * @return all record objects
     */
    @Select("SELECT * FROM fruits WHERE name = #{name}")
    List<Fruit> getAll(String name);
}
