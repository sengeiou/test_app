package cn.bevol.internal.dao.mapper;


import cn.bevol.internal.entity.dto.Classification;
import cn.bevol.internal.entity.dto.Doyen;
import cn.bevol.internal.entity.dto.EssenceComment;
import cn.bevol.internal.entity.dto.IndexImage;
import cn.bevol.internal.entity.dto.Share;
import cn.bevol.internal.entity.dto.ShoppingAddress;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * @author ruanchen
 *
 */
public interface IndexOldMapper {


	IndexImage findImage();

	List<Classification> fingClassification();

	List<Share> shareDescByType();

	int deleteShare(Share test);

	int insertShare(Share test);

	List<ShoppingAddress> fingShopAddress();
	
	//首页精选点评
	List<EssenceComment> essenceCommentList();
	
	//v2.9 首页精选点评
	List<EssenceComment> essenceCommentList2();
	
	//往期文章列表
	List<EssenceComment> oldAarticleList(@Param("start") long start, @Param("size") int size);


	//2.后台添加精选点评
	int addEssenceComment(EssenceComment esc);

	//1.后台添加修行说
	int addDoyen(Doyen doyen);

	long count();

	//更新精选点评的产品图片
	void updateEssenceImage();

	List<Map<String,Object>> getNum(@Param("type") int type);

	EssenceComment getEssenceCommentByID(@Param("id") long id);

	Map checkVersion(@Param("ver") int ver);

	EssenceComment findByGoodsId(@Param("goodsId") long goodsId);

}
