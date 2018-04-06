package cn.bevol.app.dao.mapper;

import cn.bevol.app.dao.db.Paged;
import cn.bevol.app.entity.model.Industry;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IndustryOldMapper {

	 /*@SuppressWarnings("Industry查询")
	 @Select("select * from hq_industry   WHERE id = #{id}")
	 @ResultMap("BaseResultMap")*/
	 Industry findById(@Param("id") long id);

	  int selectTotal();


	List<Industry> industryByPage(Paged<Industry> paged);
	  
	  
}
