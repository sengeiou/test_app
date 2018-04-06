package cn.bevol.statics.dao.mapper;

import java.util.List;

import cn.bevol.statics.entity.model.SeoRecord;
import cn.bevol.statics.dao.db.Paged;

public interface SeoRecordOldMapper {

	int insert(SeoRecord record);
	int insertOrUpdate(SeoRecord record);
	List<SeoRecord> findByPage(Paged<SeoRecord> paged);
	
}
