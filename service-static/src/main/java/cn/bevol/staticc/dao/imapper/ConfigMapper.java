package cn.bevol.staticc.dao.imapper;

import cn.bevol.staticc.model.entity.Config;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Rc. on 2017/2/23.
 */
public interface ConfigMapper {

    Config selectByKey(@Param("key") String key);

    List<Config> getConfigByKeys(String[] keys);

    int saveConfigType(@Param("key") String key, @Param("type") String type );

    int insertOrUpdate(@Param("key") String key, @Param("value") String value );
}
