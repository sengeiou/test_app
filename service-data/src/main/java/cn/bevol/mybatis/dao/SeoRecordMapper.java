package cn.bevol.mybatis.dao;

import java.util.List;

import cn.bevol.mybatis.model.SeoRecord;
import com.io97.utils.db.Paged;

public interface SeoRecordMapper {

	int insert(SeoRecord record);
	int insertOrUpdate(SeoRecord record);
	List<SeoRecord> findByPage(Paged<SeoRecord> paged);
	
}
