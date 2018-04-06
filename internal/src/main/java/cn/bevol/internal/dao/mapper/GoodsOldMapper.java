package cn.bevol.internal.dao.mapper;


import cn.bevol.internal.dao.GoodsTag;
import cn.bevol.internal.entity.dto.GoodsDTO;
import cn.bevol.internal.entity.model.*;
import cn.bevol.internal.dao.db.Paged;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author ruanchen
 *
 */
public interface GoodsOldMapper {

	Goods getById(@Param("id") Long id);

	List<Goods> findByPage(Paged<Goods> paged);

	List<Goods> findMidByPage(Paged<Goods> paged);

	List<Goods> findGoodsMidByPage(Paged<Goods> goodsCondition);

	int selectTotal();

	/**
	 * 获取所有的功效关系
	 * @return
	 */
	List<GoodsEffectUsed> getAllGoodsEffectUsed();
	List<GoodsUsedEffect> getAllGoodsUsedEffect();

	List<Goods> getGoodsByIds(@Param("ids") String ids);

	Goods getByGoodsByMid(@Param("mid") String mid);

	List<Goods> getGoodsByMids(String[] mids);

	List<Goods> getGoodsByCategory(@Param("categoryId") String categoryId);
	//List<Goods> getGoodsByCategory2(@Param("category")int category,@Param("te")String te);

	void goodsType3();

	void goodsType12();

	/**
	 * 插入
	 * @param fileds
	 * @param vals
	 */
	void insertTable(@Param("tableName") String tableName, @Param("fileds") String fileds, @Param("vals") String vals);

	void updateSafter(@Param("updateStr") String updateStr, @Param("goodsId") long goodsId);

	void insertGoodsSkin(@Param("fileds") String fileds, @Param("vals") String vals);
	void updateGoodsSkin(@Param("updateStr") String updateStr, @Param("goodsId") long goodsId);

	List<Map<String,Object>> select(@Param("strsql") String strsql);

	int update(@Param("strsql") String strsql);

	int insert(@Param("strsql") String strsql);

	int delete(@Param("strsql") String strsql);

	/**
	 * 国外来源产品分类
	 */
	List<Map<String, Object>> getAllEnglishCategory();

	/**
	 * 特殊分类
	 */
	List<Map<String,Object>> getAllSpecialCategory();
	List<Map<String,Object>> getAllCommonGoodsCategory();
	List<GoodsTag> getAllTag();
	List<GoodsRule> getAllRule();
	List<Map<String,Object>> getAllGoodsSkin();
	List<Map<String,Object>> getAllSafter();

	List<Map<String,Object>> getSafter(@Param("goodsIds") String goodsIds);
	List<Map<String,Object>> getGoodsSkin(@Param("goodsIds") String goodsIds);

	GoodsTagResult getTagResultByGoodsId(@Param("goodsId") long goodsId);
	List<GoodsTagComposition> getTagCompositionByCps(@Param("cps") String cps);
	/**
	 * 产品多分类
	 * @return
	 */
	List<Map<String,Object>> getPolyCategryByGoodsId(@Param("goodsId") long goodsId);
	/**
	 * 产品搜索结果表
	 * @return
	 */
	List<Map<String,Object>> getGoodsSearchByGoodsId(@Param("goodsId") long goodsId);

	List<GoodsTagComposition> getAllTagComposition();

	List<Map<String,Object>> getAllOutComposition();


	int updatePcategory();

	List<Map<String,Object>> getAllGoodsCategory();

	void addGoodsBaseInfo(GoodsDTO goodsDTO);

	void saveGoodsBaseInfo(GoodsDTO goodsDTO);

	void saveGoodsBaseList(GoodsDTO goodsDTO);

	void saveGoodsMid(@Param("id") long id, @Param("mid") String mid);

	String getMidById(@Param("id") long id);

	List<String> getMidListById(Integer[] ids);

	List<Map<String,Object>> getPolyCategoryBygoodsIds(@Param("goodsIds") String goodsIds);

	List<GoodsHitItems> findGoodsHit(@Param("id") Integer dataType, @Param("pagedBegin") int pagedBegin, @Param("pageSize") int pageSize);

	List<GoodsByNameItems> findByName(@Param("title") String title);

	int selectCategoryTotalById(@Param("id") Integer id);
	Goods findOneCategoryById(@Param("id") Integer id, @Param("skip") Integer skip);

	List<Goods> getFindByTagId(@Param("tagId") Integer tagId, @Param("pagedBegin") int pagedBegin, @Param("pageSize") int pageSize);

	int selectTotalByTag(@Param("tagId") Integer tagId);

	List<Goods> getTopGoods();


}
