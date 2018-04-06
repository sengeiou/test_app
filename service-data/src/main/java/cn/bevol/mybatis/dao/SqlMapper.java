package cn.bevol.mybatis.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by mysens on 17-6-19.
 */
public interface SqlMapper {

    List<Map<String,Object>> select(@Param("strsql") String strsql);

    int update(@Param("strsql") String strsql);

    int insert(@Param("strsql") String strsql);

    int delete(@Param("strsql") String strsql);
}
