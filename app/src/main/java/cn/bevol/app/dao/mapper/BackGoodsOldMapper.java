package cn.bevol.app.dao.mapper;

import cn.bevol.app.dao.GoodsTag;
import org.apache.ibatis.annotations.Param;

import cn.bevol.app.entity.model.GoodsTagComposition;
import cn.bevol.app.entity.model.GoodsTagResult;
import cn.bevol.app.entity.model.RedisCache;

import java.util.List;
import java.util.Map;

/**
 *
 * @author chenhaijian 
 *
 */
public interface BackGoodsOldMapper {
	int addTagRule(@Param("tagId") long tagId, @Param("rule1") String rule1, @Param("createStamp") long createStamp);
	int addTag(@Param("tagName") String tagName, @Param("createStamp") long createStamp);
	GoodsTag getTagByTagName(@Param("tagName") String tagName);
	int updateGoodsTag(@Param("tagId") long tagId, @Param("tagName") String tagName);
	int updateGoodsTagName(@Param("newName") String newName, @Param("oldName") String oldName);
	int updateGoodsTagRule(@Param("tagId") long tagId, @Param("rule1") String rule1);
	List<GoodsTag> getTag(@Param("start") long start, @Param("pageSize") int pageSize);
	List<GoodsTagComposition> getAllTagComposition();
	List<GoodsTagComposition> getTagCompositionByIsMain(@Param("tagId") long tagId, @Param("isMain") int isMain, @Param("pager") long pager, @Param("pageSize") int pageSize);

	void insertBatch(List<Map> listMap);
	void delBatch(@Param("tagId") String tagId, @Param("compositionIds") String compositionIds);
	int updateGoodsResult(GoodsTagResult gtr);
	List<GoodsTagResult> getGoodsByTagId(@Param("tagId") long tagId, @Param("start") long start, @Param("pageSize") int pageSize);
	void delGoodsByTagId(@Param("tagId") long tagId);
	int insertResult(GoodsTagResult gtr);

	List<Map<String,Object>> getPolyCategoryBygoodsIds(@Param("goodsIds") String goodsIds);
	//分类id
	List<Map<String,Object>> getPolyCategoryById(@Param("id") Integer id);
	int madeUpdateCategory(@Param("list") List<Map<String, Object>> list);
	int updateGoodsTagByName(@Param("name") String name);

	List<Map<String, Object>> getCommonGoodsCategoryById(@Param("id") long id);
	int updateCommonGoodsCategoryById(@Param("id") long id, @Param("rule_1") String rule_1, @Param("rule_2") String rule_2);
	int addCategoryRule(@Param("newRule1") String newRule1, @Param("newRule2") String newRule2, @Param("categoryId") long categoryId);
	int addRedisCache(RedisCache rc);
	List<RedisCache> getRedisList(@Param("pager") int pager, @Param("pageSize") int pageSize);
}
