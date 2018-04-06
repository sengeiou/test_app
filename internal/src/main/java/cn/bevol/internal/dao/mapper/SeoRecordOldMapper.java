package cn.bevol.internal.dao.mapper;

import java.util.List;

import cn.bevol.internal.entity.model.SeoRecord;
import cn.bevol.internal.dao.db.Paged;

public interface SeoRecordOldMapper {

	int insert(SeoRecord record);
	int insertOrUpdate(SeoRecord record);
	List<SeoRecord> findByPage(Paged<SeoRecord> paged);
	
}
