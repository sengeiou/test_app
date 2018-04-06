package cn.bevol.mybatis.dao;


import cn.bevol.mybatis.model.DownloadRecord;
import com.io97.utils.db.Paged;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DownloadRecordMapper {

	public Integer insertOrUpdate(DownloadRecord record);

	public List<DownloadRecord> findByPage(Paged<DownloadRecord> paged);

	int findByPageCount(Paged<DownloadRecord> paged);

	public DownloadRecord findByQrcodeId(@Param("qrcodeId")Integer qrcodeId,@Param("statisticsDate")Integer statisticsDate);

	public  int insert(DownloadRecord record);

	public  int update(DownloadRecord record);
}
