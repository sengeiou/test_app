package cn.bevol.statics.dao.mapper;


import org.apache.ibatis.annotations.Param;

import cn.bevol.statics.entity.user.VerificationCode;

/**

 * @author hualong
 *
 */
public interface VerificationCodeOldMapper {
	    /**
	     * 添加验证码
	     * @param userInfo
	     * @return
	     */
		long save(VerificationCode vc);
		
		long saveEmail(VerificationCode vc);
		
	    //获取手机验证码
		VerificationCode getVcode(@Param("phone") String phone, @Param("type") int type);

		//获取邮箱验证码
		VerificationCode getEmailVcode(@Param("email") String email, @Param("type") int type);

		//获取图片验证码
		VerificationCode getImgVcode(@Param("imgVcode") String imgVcode, @Param("type") int type);
		/**
		 * 删除验证码
		 * @param feild
		 * @param val
		 * @return
		 */
	    int deleteField(@Param("feild") String feild, @Param("val") Object val);
	    
	    long getVerificationNum();

}
