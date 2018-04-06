package cn.bevol.app.dao.mapper;

import java.util.List;

import cn.bevol.app.entity.model.SeoRecord;
import cn.bevol.app.dao.db.Paged;

public interface SeoRecordOldMapper {

	int insert(SeoRecord record);
	int insertOrUpdate(SeoRecord record);
	List<SeoRecord> findByPage(Paged<SeoRecord> paged);
	
}
