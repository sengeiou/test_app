package cn.bevol.statics.dao.mapper;


import cn.bevol.statics.entity.entityAction.Recovery;
import org.apache.ibatis.annotations.Param;


/**

 * @author hualong
 *
 */
public interface RecoveryOldMapper {
	    /**
	     * 添加纠错
	     * @param userInfo
	     * @return
	     */
		int save(Recovery vc);
		/**
		 * 查询纠错
		 * @param feild
		 * @param val
		 * @return
		 */
		Recovery findFeild(@Param("feild") String feild, @Param("val") Object val);

		/**
		 * 删除纠错
		 * @param string
		 * @param phone
		 * @return
		 */
	    int deleteField(@Param("feild") String feild, @Param("val") Object val);

}
