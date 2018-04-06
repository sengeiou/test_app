package cn.bevol.mybatis.dao;


import org.apache.ibatis.annotations.Param;

import cn.bevol.model.entityAction.Recovery;

/**

 * @author hualong
 *
 */
public interface RecoveryMapper {
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
