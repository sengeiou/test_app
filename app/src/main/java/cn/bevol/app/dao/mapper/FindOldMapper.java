package cn.bevol.app.dao.mapper;


import java.util.List;
import java.util.Map;

import cn.bevol.app.entity.model.Tags;
import cn.bevol.app.dao.db.Paged;
import org.apache.ibatis.annotations.Param;

import cn.bevol.app.entity.dto.Label;
import cn.bevol.app.entity.model.Find;


public interface FindOldMapper {
	
	
	Find getById(@Param("id") Long id);

	List<Label> findLabelList();

	List<Label> findArticle(@Param("id") Integer id);

	List<Label> findType();

	int selectTotal();

	List<Find> findByPage(Paged<Find> paged);

	List<Tags> findTagByPage(Paged<Find> paged);

	List<Find> getFindByTagId(@Param("tagId") Integer tagId, @Param("pagedBegin") int pagedBegin, @Param("pageSize") int pageSize);

	int selectTotalByTag(@Param("tagId") Integer tagId);

	int insertFindType(Label test);

	int deleteFindType(Label test);

	int insertFindTags(Label test);

	int deleteFindTags(Label test);

	int insertNewFind(Label test);

	int deleteNewFind(Label test);

	List<Label> getAllTagList();

	List<Find> getAllFind();

	int updateFindIds(@Param("id") Long id, @Param("ids") String ids);

	List<Map> getUserTag(@Param("tabs") String tabs);

	int insertListTags(Label label);

	void delNewTagsByTitle2(@Param("title") String title, @Param("tabs") String tabs);

	void updateNewTags2(@Param("newTitle") String newTitle, @Param("title") String title, @Param("tabs") String tabs);

	List<Label> getNewTagsByTitle2(@Param("title") String title);

	int updateFindIds2(@Param("id") Long id, @Param("tagIds") String tagIds, @Param("tags") String tags);

	void updateTabs(@Param("id") Long id, @Param("tabs") String tabs);

	void excut(@Param("strsql") String strsql);

}
