package cn.bevol.mybatis.dao;

import java.util.List;

import cn.bevol.mybatis.model.StaticRecord;
import com.io97.utils.db.Paged;

public interface StaticRecordMapper {

	int insert(StaticRecord record);
	int insertOrUpdate(StaticRecord record);
	int selectTotal(Paged<StaticRecord> paged);
	/***
	 * 没有修改过的
	 * @param paged
	 * @return
	 */
	int selectNoAgainTotal(Paged<StaticRecord> paged);
	/***
	 * 没有修改过的集合
	 * @return
	 */
	List<StaticRecord> staticRecordByAgainPage(Paged<StaticRecord> paged);
	
	List<StaticRecord> staticRecordByPage(Paged<StaticRecord> paged);
	
	List<StaticRecord> recordTotal();
	int update(StaticRecord record);
	
}
