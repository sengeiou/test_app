package cn.bevol.app.dao.mapper;

import java.util.List;

import cn.bevol.app.entity.model.UserStatistical;
import org.apache.ibatis.annotations.Param;

import cn.bevol.app.dao.db.Paged;

/**

 * @author Administrator
 *
 */
public interface UserStatisticalOldMapper {
	int insertOrUpdate(UserStatistical userStatistical);
	
	public List<UserStatistical> findByPage(Paged<UserStatistical> paged);
	
	public UserStatistical findByStatisticsDate(@Param("statisticsDate") Integer statisticsDate);
}
