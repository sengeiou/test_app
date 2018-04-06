package cn.bevol.app.dao.mapper;


import cn.bevol.app.entity.model.Tags;
import cn.bevol.app.dao.db.Paged;
import org.apache.ibatis.annotations.Param;

import cn.bevol.app.entity.model.Lists;

import java.util.List;

/**
 * @author ruanchen
 *
 */
public interface ListsOldMapper {
	Lists getById(@Param("id") Long id);
	List<Tags> findTagByPage(Paged<Lists> paged);
	List<Lists> findByPage(Paged<Lists> paged);
	int selectTotal();
	List<Lists> getListsByTagId(@Param("tagId") Integer tagId, @Param("pagedBegin") Integer page, @Param("pageSize") Integer pageSize);
	int selectTotalByTag(@Param("tagId") Integer tagId);
}
