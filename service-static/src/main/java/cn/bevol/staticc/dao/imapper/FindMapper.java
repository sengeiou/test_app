package cn.bevol.staticc.dao.imapper;

import java.util.List;

import cn.bevol.staticc.model.entity.Find;
import cn.bevol.staticc.model.entity.Tags;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import com.io97.utils.db.Paged;
public interface FindMapper {

	 @SuppressWarnings("Find查询")
	 @Select("select * from hq_new_find   WHERE id = #{id}")
	    @ResultMap("BaseResultMap")
     Find findById(long id);

	int selectTotal();

	List<Find> findByPage(Paged<Find> paged);

	List<Tags> findTagByPage(Paged<Find> paged);

	List<Find>  getFindByTagId(@Param("tagId")Integer tagId, @Param("pagedBegin")int pagedBegin, @Param("pageSize")int pageSize);

	int selectTotalByTag(@Param("tagId")Integer tagId);
}
