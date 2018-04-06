package cn.bevol.mybatis.dao;

import cn.bevol.mybatis.model.MetaInfo;
import org.apache.ibatis.annotations.Param;

/**
 * Created by mysens on 17-7-3.
 */
public interface MetaInfoMapper {
    MetaInfo getSeoMataInfo(@Param("id") Integer id, @Param("type") Integer type);
}
