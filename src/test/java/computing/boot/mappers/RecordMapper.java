package computing.boot.mappers;

import java.util.List;
import org.apache.ibatis.annotations.Select;

import computing.model.DataTO;


public interface RecordMapper {
	/**
     * Get a single Record from the database based on the record
     * identified.
     *
     * @param id record identifier.
     * @return a record object.
     */
    @Select("SELECT * FROM records WHERE id = #{id}")
    List<DataTO> getOne(String id);
}
