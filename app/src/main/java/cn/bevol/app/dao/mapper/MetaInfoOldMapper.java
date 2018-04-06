package cn.bevol.app.dao.mapper;

import cn.bevol.app.entity.model.MetaInfo;
import org.apache.ibatis.annotations.Param;

/**
 * Created by mysens on 17-7-3.
 */
public interface MetaInfoOldMapper {
    MetaInfo getSeoMataInfo(@Param("id") Integer id, @Param("type") Integer type);
}
