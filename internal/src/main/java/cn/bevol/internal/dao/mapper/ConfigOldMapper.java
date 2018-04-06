package cn.bevol.internal.dao.mapper;

import cn.bevol.internal.entity.model.Config;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by mysens on 17-6-7.
 */
public interface ConfigOldMapper {
    Config getConfigByKey(@Param("key") String key);

    Config selectByKey(@Param("key") String key);

    List<Config> getConfigByKeys(String[] keys);

    int saveConfigType(@Param("key") String key, @Param("type") String type);

    int insertOrUpdate(@Param("key") String key, @Param("value") String value);
}
