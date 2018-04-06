package cn.bevol.statics.dao.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author mysens
 * @date 17-12-27 下午4:01
 */
public interface GoodsPloyCategoryComplexMapper {

    void updatePloyCategory(@Param("list") List<Map<String, Object>> list);

}
