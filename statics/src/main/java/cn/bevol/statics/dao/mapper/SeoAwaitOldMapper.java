package cn.bevol.statics.dao.mapper;

import java.util.List;

import cn.bevol.statics.entity.model.SeoAwait;
import cn.bevol.statics.dao.db.Paged;
import org.apache.ibatis.annotations.Param;

public interface SeoAwaitOldMapper {

	int insert(SeoAwait record);
	int insertOrUpdate(SeoAwait record);
	int selectTotal(Paged<SeoAwait> paged);
	List<SeoAwait> findByPage(Paged<SeoAwait> paged);
	List<SeoAwait> findAll();
	List<SeoAwait> findByDate(@Param("beginTime") Integer beginTime, @Param("endTime") Integer endTime);
	int selectTotalByWhere(String dataType, String dataSource, String operateType);

	int update(SeoAwait seoAwait);
}
