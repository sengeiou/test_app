package cn.bevol.app.dao.mapper;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import cn.bevol.model.entity.EntityFind;

/**
 * SELECT id,left(right(REPLACE(CONCAT(",",tags,","),",11,",""),LENGTH(tags)+1),LENGTH(tags)) lm  FROM `hq_find` where tags is not null and CONCAT(",",tags,",") like '%,11,%'

 * @author Administrator
 *
 */
public interface EntityFindOldMapper {
	    List<EntityFind> list(@Param("type") int type, @Param("start") long start, @Param("size") int size);
		long count(@Param("type") int type);

		List<EntityFind> findByUserId(@Param("id") String id, @Param("start") long start, @Param("size") int size);
		long countByUserId(@Param("id") String id);

		List<EntityFind> industryList(@Param("start") long start, @Param("size") int size);
		long count2();

		List<EntityFind> articleList(@Param("ids") String ids);



		String getConfigValue(@Param("key") String key);
		int updateConfigValue(@Param("key") String key, @Param("value") String value);

		List<Map<String,Object>> getComemntTagsByCategory(@Param("tabId") Integer tabId);
}
