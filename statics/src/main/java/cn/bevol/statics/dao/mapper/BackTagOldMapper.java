package cn.bevol.statics.dao.mapper;




import org.apache.ibatis.annotations.Param;

import cn.bevol.statics.entity.dto.Label;

import java.util.List;
import java.util.Map;

/**
 *  
 * @author chenHaiJian
 *
 */
public interface BackTagOldMapper {
	
	List<Map<String, Object>> getTagListsByLikeTabls(@Param("tabs") String tabs);

	List<Map<String, Object>> getComemntTagsByCategory(@Param("tabId") Integer tabId);

	List<Map<String, Object>> getGoodsParentCategory();

	void updateTagByTagId(@Param("tagIds") String tagIds);

	void updateTagByTabId(@Param("tabId") Integer tabId);

	void insertSql(@Param("sql") String sql);

	List<Map<String, Object>> getNewTagByTabs(@Param("tabs") String tabs);

	List<Map<String, Object>> getPtagById(@Param("id") Integer id);

	void saveNewTag(@Param("pid") Integer pid, @Param("title") String title, @Param("tabId") Integer tabId, @Param("tabs") String tabs);

	void hiddenNewTagsByPid(@Param("pid") Integer pid, @Param("tabId") Integer tabId, @Param("tabs") String tabs);

	void saveByTabId(@Param("tabs") String tabs, @Param("tabId") Integer tabId);
	
	Label getTagListById(long id);
	
	Label getNewTagsByTitle(@Param("title") String title, @Param("tabs") String tabs);

	int insertNewTag2(Label newLabel);
	
	List<Map> getNewTagById(@Param("id") Integer id);
	
	int updateNewTagsById(Label newLabel);

	List<Label> getNewTagsByTabs2(@Param("tabs") String tabs, @Param("pager") Integer pager, @Param("pageSize") Integer pageSize);

	List<Label> getAllTags(@Param("start") long start, @Param("pageSize") int pageSize);

	Label getTagListByTitle(@Param("title") String title);

	void insertNewTag(@Param("pid") Long pid, @Param("title") String title, @Param("tabs") String tabs, @Param("tabId") Integer tabId);

	int delTagList(@Param("id") long id);

	void delNewTagsByTitle(@Param("title") String title);

	List<Label> getNewTagsByTabs(@Param("tabs") String tabs);

	void updateListTagById(Label label);
	
	void updateNewTags(@Param("newTitle") String newTitle, @Param("title") String title);

	Label getNewTagsByMaxTop(@Param("tabs") String tabs);

	void deleteTagByTagId(@Param("tagIds") String tagIds);

	List<Map<String, Object>> getNewTagByTabsAndTabId(@Param("tabs") String tabs, @Param("tabId") int tabId);

	
}
