package cn.bevol.mybatis.dao;

import java.util.List;

import cn.bevol.mybatis.model.SeoAwait;
import com.io97.utils.db.Paged;
import org.apache.ibatis.annotations.Param;

public interface SeoAwaitMapper {

	int insert(SeoAwait record);
	int insertOrUpdate(SeoAwait record);
	int selectTotal(Paged<SeoAwait> paged);
	List<SeoAwait> findByPage(Paged<SeoAwait> paged);
	List<SeoAwait> findAll();
	List<SeoAwait> findByDate(@Param("beginTime")Integer beginTime,@Param("endTime")Integer endTime);
	int selectTotalByWhere(String dataType, String dataSource, String operateType);

	int update(SeoAwait seoAwait);
}
