package cn.bevol.app.dao.mapper;


import cn.bevol.model.user.UserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**

 * @author Administrator
 *
 */
public interface UserInfoOldMapper {
	    int register(UserInfo userInfo);

	    int updateOne(UserInfo userInfo);
	    /**
	     * 根据字段查询userInfo
	     * @param key 需要更新的字段名
	     * @param val  需要更新的字段值
	     * @param feild 条件
	     * @param fval  条件值
	     * @return
	     */
	    int updateField(@Param("key") String key, @Param("val") Object val, @Param("feild") String feild, @Param("fval") Object fval);

	     UserInfo findFeild(@Param("feild") String feild, @Param("val") Object val);
	    /**
	     * 带密码查询
	     * @param feild
	     * @param val
	     * @return
	     */
	     UserInfo findFeild2_6(@Param("feild") String feild, @Param("val") Object val);

	    UserInfo phoneLogin(@Param("phone") String phone, @Param("password") String password);

	    /**
	     * 登录
	     * @param email 邮箱
	     * @param password 密码
	     * @return
	     */
	    UserInfo emailLogin(@Param("email") String email, @Param("password") String password);


	    /**
	     * 重置秘密
	     * @param phone 手机
	     * @param password 密码
	     * @return
	     */
	    int restPassword(@Param("phone") String phone, @Param("password") String password, @Param("usercode") String usercode);
	    int restPassword2(@Param("phone") String phone, @Param("password") String password);
	    int restPassword3(@Param("email") String email, @Param("password") String password);

	    /**
		 * 删除用户
		 * @return
		 */
	    int deleteField(@Param("feild") String feild, @Param("val") Object val);


	    /**
	     * 更新用户
	     * @param userInfo
	     * @return
	     */
		int updateUserInfo(UserInfo userInfo);

		int updateUserInfo2_6(UserInfo userInfo);

		/**
		 * 上面的方法sex无法保存
		 * @param userInfo
		 * @return
		 */
		int updateUserById(@Param("id") long id, @Param("nickname") String nickname, @Param("headimgurl") String headimgurl, @Param("age") int age, @Param("sex") int sex, @Param("province") String province, @Param("city") String city,
                           @Param("yunfu") int yunfu);
		/**
		 * 更新肤质信息
		 * @param userInfo
		 * @return
		 */
	    int updateSKin(UserInfo userInfo);
		List<UserInfo> findUserinfoByIds(@Param("ids") String ids);

	/**
	 * 根据ID查询用户查询修行说文章总数
	 * @param id
	 * @return
     */
	int findDoyenTotalById(@Param("id") String id);
	/**
	 * 根据ID查询用户查询发现文章总数
	 * @param id
	 * @return
	 */
	int findTotalById(@Param("id") String id);


    /**
     * 绑定用户手机号码
     * @param id
     * @param phone
     * @param password
     * @return
     */
	int userbindPhoneById(@Param("id") long id, @Param("phone") String phone, @Param("password") String password);

	List getSkinTitle(@Param("ids") String ids);
	List getSkinQuestion(@Param("titleIds") String titleIds);
	List getSkinAnswer();

	int updatePasswordById(@Param("id") long id, @Param("password") String password);

	Long getRegisterDayNum();
	
	String getPhoneById(@Param("id") long id);
	
	
}
