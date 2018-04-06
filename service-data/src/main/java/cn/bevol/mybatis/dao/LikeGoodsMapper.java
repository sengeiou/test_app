package cn.bevol.mybatis.dao;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import cn.bevol.mybatis.dto.LikeGoods;

/**
 * SELECT id,left(right(REPLACE(CONCAT(",",tags,","),",11,",""),LENGTH(tags)+1),LENGTH(tags)) lm  FROM `hq_find` where tags is not null and CONCAT(",",tags,",") like '%,11,%'

 * @author Administrator
 *
 */
public interface LikeGoodsMapper {
		void deleteAll();
		void insert(@Param("id") long id, @Param("entityid") long entityid, @Param("skin") String skin, @Param("entityid_skin") String entityid_skin);

	 List<LikeGoods> findGood(@Param("skin") String skin, @Param("id") Integer id);
	void insertBatch(List<Map> insertbatch);

	
//	 long addLikeGoodsBatch(List<LikeGoods> trainRecordList);

}
