package cn.bevol.app.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;


public interface DataCategoryOldMapper {

List selectGoodsById(@Param("category") Long category);

List<Long> selectSfetyGoodsById(@Param("category") Long category);
	
//根据品牌查询goods_id
List<Long> selectByBrandId(@Param("brandId") Long brandId);

String selectTitleByBrandId(@Param("brandId") Long brandId);
}
