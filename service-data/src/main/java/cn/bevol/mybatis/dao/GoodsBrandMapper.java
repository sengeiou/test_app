package cn.bevol.mybatis.dao;

import cn.bevol.model.entity.GoodsBrand;

public interface GoodsBrandMapper {
	//根据品牌id查询品牌详情
	GoodsBrand findBrandById(Integer brand);
	
}
