package cn.bevol.mybatis.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;


public interface DataCategoryMapper {

List selectGoodsById(@Param("category") Long category);

List<Long> selectSfetyGoodsById(@Param("category") Long category);
	
//根据品牌查询goods_id
List<Long> selectByBrandId(@Param("brandId") Long brandId);

String selectTitleByBrandId(@Param("brandId") Long brandId);
//查询点击量前10000产品
List<Map> selectbrandGoods();
//查询分类榜单全部产品
List<Long> selectAllGoodsById();
//查询安全榜单全部产品
List<Long> selectAllSfetyGoods();




}
