package cn.bevol.mybatis.dao;


import cn.bevol.mybatis.model.Tags;
import com.io97.utils.db.Paged;
import org.apache.ibatis.annotations.Param;

import cn.bevol.mybatis.model.Lists;

import java.util.List;

/**
 * @author ruanchen
 *
 */
public interface ListsMapper {	
	Lists getById(@Param("id") Long id);
	List<Tags> findTagByPage(Paged<Lists> paged);
	List<Lists> findByPage(Paged<Lists> paged);
	int selectTotal();
	List<Lists> getListsByTagId(@Param("tagId")Integer tagId, @Param("pagedBegin")Integer page, @Param("pageSize")Integer pageSize);
	int selectTotalByTag(@Param("tagId")Integer tagId);
}
