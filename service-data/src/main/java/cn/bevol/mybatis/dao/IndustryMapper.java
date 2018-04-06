package cn.bevol.mybatis.dao;

import java.util.List;

import cn.bevol.mybatis.model.Industry;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import com.io97.utils.db.Paged;
public interface IndustryMapper {

	 @SuppressWarnings("Industry查询")
	 @Select("select * from hq_industry   WHERE id = #{id}")
	    @ResultMap("BaseResultMap")
	 Industry findById(long id);

	  int selectTotal();


	List<Industry> industryByPage(Paged<Industry> paged);
	  
	  
}
