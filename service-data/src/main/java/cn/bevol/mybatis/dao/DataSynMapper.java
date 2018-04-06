package cn.bevol.mybatis.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * SELECT id,left(right(REPLACE(CONCAT(",",tags,","),",11,",""),LENGTH(tags)+1),LENGTH(tags)) lm  FROM `hq_find` where tags is not null and CONCAT(",",tags,",") like '%,11,%'

 * @author Administrator
 *
 */
public interface DataSynMapper {
	
	
	void deleteBySql(@Param("sql") String sql);
	void insertBySql(@Param("sql") String sql);
	void updateBySql(@Param("sql") String sql);
	List<Map<String,Object>> selectBySql(@Param("sql") String sql);

	
//	 long addLikeGoodsBatch(List<LikeGoods> trainRecordList);

}
