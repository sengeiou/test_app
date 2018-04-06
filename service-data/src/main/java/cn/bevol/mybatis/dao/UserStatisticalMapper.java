package cn.bevol.mybatis.dao;

import java.util.List;

import cn.bevol.mybatis.model.UserStatistical;
import org.apache.ibatis.annotations.Param;

import com.io97.utils.db.Paged;

/**

 * @author Administrator
 *
 */
public interface UserStatisticalMapper {
	int insertOrUpdate(UserStatistical userStatistical);
	
	public List<UserStatistical> findByPage(Paged<UserStatistical> paged);
	
	public UserStatistical findByStatisticsDate(@Param("statisticsDate")Integer statisticsDate);
}
