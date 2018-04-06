package cn.bevol.statics.dao.mapper;

import java.util.List;

import cn.bevol.statics.entity.model.Industry;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import cn.bevol.statics.dao.db.Paged;

public interface IndustryOldMapper {

	 @SuppressWarnings("Industry查询")
	 @Select("select * from hq_industry   WHERE id = #{id}")
	    @ResultMap("BaseResultMap")
	 Industry findById(long id);

	  int selectTotal();


	List<Industry> industryByPage(Paged<Industry> paged);
	  
	  
}
