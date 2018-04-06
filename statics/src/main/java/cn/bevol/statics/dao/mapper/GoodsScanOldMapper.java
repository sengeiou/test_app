package cn.bevol.statics.dao.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import cn.bevol.statics.entity.model.Goods;

public interface GoodsScanOldMapper {

	List<Goods> findGoodsByBarcode(@Param("ean") String ean, @Param("size") int size);

	//更新字段
	void updateFields(@Param("isPass") Integer isPass, @Param("goodsId") Long goodsId);


	Goods findGoodsById(@Param("goodsId") Long goodsId);
	//新增条形码(upc)
	void insertGoodsInfo(@Param("ean") String ean);
	//条形码去零(upc表)
	void updateEan(@Param("ean") String ean, @Param("str") String str);
	//新增条形码(goods_prepare)
	void insertGoodsPrepare(@Param("ean") String ean);
	//条形码去零(goods_prepare)
	void updatePrepareEan(@Param("ean") String ean, @Param("str") String str);

	
}
