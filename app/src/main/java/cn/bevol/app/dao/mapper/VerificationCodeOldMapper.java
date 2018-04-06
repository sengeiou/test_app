package cn.bevol.app.dao.mapper;


import cn.bevol.model.user.VerificationCode;
import org.apache.ibatis.annotations.Param;


/**

 * @author hualong
 *
 */
public interface VerificationCodeOldMapper {
	/**
	 * 添加验证码
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

    int updateSendState(@Param("id")long id, @Param("send")int send);

	VerificationCode findByPhone(@Param("phone")String phone);

    void updateSendStateByPhone(@Param("phone")String phone, @Param("send")int send, @Param("type")int type);
}
