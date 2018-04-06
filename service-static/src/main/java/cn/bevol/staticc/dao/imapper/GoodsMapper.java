package cn.bevol.staticc.dao.imapper;

import cn.bevol.staticc.model.dto.GoodsDTO;
import cn.bevol.staticc.model.entity.Goods;
import cn.bevol.model.items.GoodsHitItems;
import com.io97.utils.db.Paged;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsMapper {
	
	 List<Goods> getGoodsByMids(String[] mids);

	List<Goods> getGoodsByIds(String[] ids);
	 
	 List<Goods> findByPage(Paged<Goods> paged);

	List<Goods> findMidByPage(Paged<Goods> paged);

	 int selectTotal();

	int selectCategoryTotalById(@Param("id") Integer id);
	Goods findOneCategoryById(@Param("id") Integer id, @Param("skip") Integer skip);

	List<Goods>  getFindByTagId(@Param("tagId")Integer tagId, @Param("pagedBegin")int pagedBegin, @Param("pageSize")int pageSize);

	int selectTotalByTag(@Param("tagId")Integer tagId);

	List<Goods> getTopGoods();

	void addGoodsBaseInfo(GoodsDTO goodsDTO);

	void saveGoodsBaseInfo(GoodsDTO goodsDTO);

	void saveGoodsBaseList(GoodsDTO goodsDTO);

	void saveGoodsMid(@Param("id")long id, @Param("mid") String mid);

	String getMidById(@Param("id") long id);

	List<String> getMidListById(Integer[] ids);

	List<GoodsHitItems> findGoodsHit(@Param("id") Integer dataType,  @Param("pagedBegin")int pagedBegin, @Param("pageSize")int pageSize);

	List<GoodsByNameItems> findByName(@Param("title") String title);
}
