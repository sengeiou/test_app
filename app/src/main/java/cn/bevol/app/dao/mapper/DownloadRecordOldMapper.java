package cn.bevol.app.dao.mapper;


import cn.bevol.app.entity.model.DownloadRecord;
import cn.bevol.app.dao.db.Paged;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DownloadRecordOldMapper {

	public Integer insertOrUpdate(DownloadRecord record);

	public List<DownloadRecord> findByPage(Paged<DownloadRecord> paged);

	int findByPageCount(Paged<DownloadRecord> paged);

	public DownloadRecord findByQrcodeId(@Param("qrcodeId") Integer qrcodeId, @Param("statisticsDate") Integer statisticsDate);

	public  int insert(DownloadRecord record);

	public  int update(DownloadRecord record);
}
