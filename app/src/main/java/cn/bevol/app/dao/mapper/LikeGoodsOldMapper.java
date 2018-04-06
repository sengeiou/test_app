package cn.bevol.app.dao.mapper;


import cn.bevol.app.entity.dto.LikeGoods;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * SELECT id,left(right(REPLACE(CONCAT(",",tags,","),",11,",""),LENGTH(tags)+1),LENGTH(tags)) lm  FROM `hq_find` where tags is not null and CONCAT(",",tags,",") like '%,11,%'

 * @author Administrator
 *
 */
public interface LikeGoodsOldMapper {
		void deleteAll();
		void insert(@Param("id") long id, @Param("entityid") long entityid, @Param("skin") String skin, @Param("entityid_skin") String entityid_skin);

	 List<LikeGoods> findGood(@Param("skin") String skin, @Param("id") Integer id);
	void insertBatch(List<Map> insertbatch);

	
//	 long addLikeGoodsBatch(List<LikeGoods> trainRecordList);

}
