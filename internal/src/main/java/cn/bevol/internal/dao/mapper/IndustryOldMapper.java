package cn.bevol.internal.dao.mapper;

import java.util.List;

import cn.bevol.internal.entity.model.Industry;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import cn.bevol.internal.dao.db.Paged;

public interface IndustryOldMapper {

	 @SuppressWarnings("Industry查询")
	 @Select("select * from hq_industry   WHERE id = #{id}")
	    @ResultMap("BaseResultMap")
	 Industry findById(long id);

	  int selectTotal();


	List<Industry> industryByPage(Paged<Industry> paged);
	  
	  
}
