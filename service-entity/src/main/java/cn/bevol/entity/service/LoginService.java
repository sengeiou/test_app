package cn.bevol.entity.service;

import cn.bevol.log.LogException;
import cn.bevol.model.GlobalConfig;
import cn.bevol.model.user.UserInfo;
import cn.bevol.model.user.VerificationCode;
import cn.bevol.mybatis.dao.UserInfoMapper;
import cn.bevol.mybatis.dao.VerificationCodeMapper;
import cn.bevol.util.ReturnData;
import cn.bevol.entity.service.utils.CommonUtils;
import cn.bevol.conf.client.ConfUtils;
import com.io97.cache.redis.RedisCacheProvider;
import com.io97.utils.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

@Service
public class LoginService extends BaseService {

    private static Logger logger = LoggerFactory.getLogger(LoginService.class);
    @Autowired
    private UserService userService;

    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    VerificationCodeMapper verificationCodeMapper;
    @Autowired
    AliyunService aliyunService;
    @Autowired
    MessageService messageService;
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    RedisCacheProvider cacheProvider;


    private int width = 100;//定义图片的width  
    private int height = 44;//定义图片的height  
    private int codeCount = 4;//定义图片上显示验证码的个数  
    private int xx = 15;
    private int fontHeight = 18;
    private int codeY = 16;
    char[] codeSequence = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
            'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};


    public int register(UserInfo userInfo) {
        userInfo.setRole(3);
        int i = userInfoMapper.register(userInfo);
        return i;
    }


    /**
     * v2.6
     * 验证邮箱或手机
     *
     * @param unionid
     * @return
     */
    public ReturnData wxCheckUser2_6(String unionid) {
        if (!StringUtils.isBlank(unionid)) {
        	//根据unionid查找是否存在,是否绑定
            ReturnData rd = userService.findWXUnionid(unionid);
            //返回用户
            if (rd.getRet() == 1) {
                UserInfo userInfo = (UserInfo) rd.getResult();
                //用户隐藏 
                if (userInfo.getHidden() == 1) {
                    return UserInfo.ERRER_USER_OUT;
                }

                //手机和邮箱都没绑定
                if (StringUtils.isBlank(userInfo.getPhone()) && StringUtils.isBlank(userInfo.getEmail())) {
                    return UserInfo.WX_NOT_BIND;
                }

                ReturnData dr = new ReturnData(userInfo, 1);
                //更新用户信息
                userService.userInit(userInfo);
                return dr;
            } else {
                return UserInfo.WX_NOT_REGIST;
            }
        }
        return ReturnData.ERROR;
    }

    /**
     * v2.6
     *  unionid:	微信统一认证标识
		openid:	微信登录认证id
		nickname:	用户名
		account:	账号
		password:	密码
		vcode:	验证码
		headimgurl:	微信头像
     */
    public ReturnData wxLogin2_6(String unionid, String wxmbopenid,
                                 String country,
                                 String language,
                                 String nickname, String headimgurl, int age, int sex,
                                 String province, String account, String password, String city, int yunfu, String vcode) {

        try {
            ReturnData rdp = null;
            String email = null;
            String phone = null;
            ReturnData rd = null;
            //验证码校验
            if (!StringUtils.isBlank(account)) {
                //1、验证码校验  vcode 用户得到的验证码
                rd = userService.isVcode2_6(account, vcode, 3);
                //验证不通过
                if (rd.getRet() != 0) {
                    return rd;
                }
                //验证账号是否存在  以及账号的格式
                if (account.indexOf("@") == -1) {
                    phone = account;
                    rdp = userService.checkPhone(phone, 1);
                } else {
                    email = account;
                    rdp = userService.checkEmail(email, 1);
                }


            } else {
                //手机号码邮箱都没有输入。。。
                return ReturnData.ERROR;
            }

            //3、验证微信是否注册过
            ReturnData wxrd = userService.findWXUnionid(unionid);

            //密码长度
            if (StringUtils.isBlank(password) || password.trim().length() < 6) {
                return new ReturnData(-3, "密码长度不能少于6位");
            }
            //密码md5加密
            password = CommonUtils.getMd5(password);


            //验证用户名是否唯一
            int ret = 1;
            UserInfo puserInfo = null;


            UserInfo userInfo = null;
            if (wxrd.getRet() == 1) {
                userInfo = (UserInfo) wxrd.getResult();
                //微信绑定过手机或者邮箱
                if (!StringUtils.isBlank(userInfo.getPhone()) || !StringUtils.isBlank(userInfo.getEmail())) {
                    return UserInfo.WX_BIND_ED;
                }

                //用户隐藏了
                if (userInfo.getHidden() == 1) {
                    return UserInfo.ERRER_USER_OUT;
                }
            }

            //手机号或者邮箱注册过
            if (rdp.getRet() == 1) {
                puserInfo = (UserInfo) rdp.getResult();
            }
            //1、微信和手机号或者邮箱都注册过    微信绑定手机号或者邮箱  被绑定的账号被隐藏
            if (wxrd.getRet() == 1 && rdp.getRet() == 1) {
                //舍弃手机号码或者邮箱
                int j = userInfoMapper.updateField("hidden", 3, "id", puserInfo.getId());
                userInfo = (UserInfo) wxrd.getResult();
                if (StringUtils.isBlank(userInfo.getWxmbopenid())) {
                    userInfo.setWxmbopenid(wxmbopenid);
                    //更新微信id
                }
                userInfo.setPhone(phone);
                userInfo.setEmail(email);
                userInfo.setPassword(password);
                ret = 1;
                //绑定手机号码
            } else if (wxrd.getRet() == 2 && rdp.getRet() == 1) {
                //2、手机或者邮箱注册过  微信没有注册--绑定
                //保存微信信息
                userInfo = (UserInfo) rdp.getResult();
                userInfo.setUnionid(unionid);
                userInfo.setWxmbopenid(wxmbopenid);
                //
                userInfo.setPhone(phone);
                userInfo.setEmail(email);
                ret = 2;
            } else if (wxrd.getRet() == 1 && rdp.getRet() == 2) {
                userInfo = (UserInfo) wxrd.getResult();
                //3、微信注册过  手机或者邮箱没有注册--绑定
                userInfo.setPhone(phone);
                userInfo.setEmail(email);
                userInfo.setPassword(password);
                ret = 3;
            } else if (wxrd.getRet() == 2 && rdp.getRet() == 2) {
                //4、新用户
                //用户名过滤
                nickname = userService.getAutoNickName("");
                userInfo = new UserInfo();
                userInfo.setNickname(nickname);
                userInfo.setHeadimgurl(headimgurl);
                userInfo.setAge(age);
                userInfo.setSex(sex);
                userInfo.setProvince(province);
                userInfo.setCity(city);
                userInfo.setYunfu(yunfu);
                userInfo.setUnionid(unionid);
                userInfo.setWxmbopenid(wxmbopenid);
                userInfo.setLanguage(language);
                userInfo.setCountry(country);
                userInfo.setPhone(phone);
                userInfo.setEmail(email);
                userInfo.setPassword(password);
                ret = 4;
            }

            userInfo.setLoginTime(new Date().getTime() / 1000);
            userInfo.setUsercode(CommonUtils.getMd5(userInfo.getUnionid()));


            //记录登录时间
            int i = 0;
            if (userInfo != null && userInfo.getId() != null && userInfo.getId() > 0) {
                i = userInfoMapper.updateOne(userInfo);//登录
            } else {
                i = register(userInfo);//注册
            }
            if (i > 0) {
                //第一次输出的用户发送消息
            	userService.userInit(userInfo);
                //mongo中生成一条记录
                	mysqlSynMongo(userInfo);
            }
            return UserInfo.wxRegistStauts(userInfo, ret);
        } catch (Exception e) {
        	Map map = new HashMap();
			map.put("method", "LoginService.wxLogin2_6");
			map.put("unionid", unionid);
			map.put("wxmbopenid", wxmbopenid);
			map.put("nickname", nickname);
			map.put("sex", sex);
			map.put("age", age);
			new LogException(e, map);
        }
        return ReturnData.ERROR;

    }
    
    /**
     * 用户信息mysql同步mongo
     * @param userInfo
     * 
     */
    public void mysqlSynMongo(UserInfo userInfo) {
    	Update update=new Update();
    	boolean flag=false;
    	if(null!=userInfo){
    		if(StringUtils.isNotBlank(userInfo.getNickname())) {
            	update.set("nickname", userInfo.getNickname());
            	 flag=true;
        	}
        	if(StringUtils.isNotBlank(userInfo.getHeadimgurl())) {
            	update.set("headimgurl", userInfo.getHeadimgurl());
            	 flag=true;
        	}
        	
        	if(userInfo.getAge()!=null&&userInfo.getAge()>0) {
            	update.set("age", userInfo.getAge());
            	 flag=true;
        	}
        	if(userInfo.getSex()!=null&&userInfo.getSex()>0) {
            	update.set("sex", userInfo.getSex());
            	 flag=true;
        	}
        	if(StringUtils.isNotBlank(userInfo.getEmail())) {
            	update.set("email", userInfo.getEmail());
            	 flag=true;
        	}
        	if(StringUtils.isNotBlank(userInfo.getProvince())) {
            	update.set("province", userInfo.getProvince());
            	 flag=true;
        	}
        	
        	if(StringUtils.isNotBlank(userInfo.getCity())) {
            	update.set("city", userInfo.getCity());
            	 flag=true;
        	}
        	if(userInfo.getYunfu()!=null&&userInfo.getYunfu()>0) {
            	update.set("yunfu", userInfo.getYunfu());
            	 flag=true;
        	}
        	if(StringUtils.isNotBlank(userInfo.getWxmbopenid())) {
            	update.set("wxmbopenid", userInfo.getWxmbopenid());
            	 flag=true;
        	}
        	if(StringUtils.isNotBlank(userInfo.getLanguage())) {
            	update.set("language", userInfo.getLanguage());
            	 flag=true;
        	}
        	if(StringUtils.isNotBlank(userInfo.getCountry())) {
            	update.set("country", userInfo.getCountry());
            	 flag=true;
        	}
        	if(StringUtils.isNotBlank(userInfo.getPhone())) {
            	update.set("phone", userInfo.getPhone());
            	 flag=true;
        	}
    	}
    	if(flag&&userInfo.getId()>0)
    		mongoTemplate.findAndModify(new Query(Criteria.where("id").is(userInfo.getId())), update, new FindAndModifyOptions().returnNew(true).upsert(true), UserInfo.class, "user_info");
    
    }

    /**
     * v2.6
     * 手机或者邮箱登录
     * @param account: 账号
     * @param password: 密码
     * @return
     */
    public ReturnData accountLogin2_6(String account, String password) {
        try {
            if (!StringUtils.isBlank(account) && !StringUtils.isBlank(password)) {
                //判断account是手机还是邮箱
                UserInfo userInfo = null;
                if (account.indexOf("@") == -1) {
                    //手机登录
                    userInfo = userInfoMapper.phoneLogin(account, MD5Utils.encode(password));
                } else {
                    //邮箱登录
                    userInfo = userInfoMapper.emailLogin(account, MD5Utils.encode(password));
                }

                if (userInfo != null) {
                    userService.userInit(userInfo);
                    //更新登录时间
                    userInfoMapper.updateField("logintime", new Date().getTime() / 1000, "id", userInfo.getId());
                    return new ReturnData(userInfo);
                }
            }
        } catch (Exception e) {
        	Map map = new HashMap();
			map.put("method", "LoginService.accountLogin2_6");
			map.put("account", account);
			map.put("password", password);
			new LogException(e, map);
        }
        return UserInfo.ERRER_ACCOUNT_PASSWORD;

    }

    /**
     * v2.6
     * 获取验证码 发送消息
     *
     * @param account
     * @param type  0 注册  1 找回密码  3 微信绑定 4 修改账号
     * @return
     */
    public ReturnData getVcode2_6(String account, int type, UserInfo userInfos) {
        try {
        	//VerificationCodeEntity vc = null;
        	VerificationCode vc = null;
            UserInfo userInfo = null;
            String email = "";
            String phone = "";
            ReturnData rdp = null;

            if (account.indexOf("@") == -1) {
                //手机
                rdp = userService.checkPhone(account, 1);
                phone = account;
                userInfo = (UserInfo) rdp.getResult();
            } else {
                rdp = userService.checkEmail(account, 1);
                email = account;
                userInfo = (UserInfo) rdp.getResult();
            }
            if (rdp.getRet() != 1 && rdp.getRet() != 2) {
                return rdp;
            }
            /**
             * 1账号存在
             * 2账号不存在
             */
            if (rdp.getRet() == 1 && type == 0) {    //账号存在  不能注册  0
                return UserInfo.ERRER_ACCOUNT_REGISTER_ED;
            } else if (rdp.getRet() == 2 && type == 1) {    //账号不存在  不能找回密码   1
                return UserInfo.ERRER_ACCOUNT_NOT_FOUND;
            } else if (rdp.getRet() == 1 && type == 4) {  //账号存在  不能绑定(修改)账号   4
                return UserInfo.ERRER_ACCOUNT_BIND_ED;
            } else if (null == userInfos && type == 4) {  //用户没登录 不能绑定(修改)账号   4
                return UserInfo.ERRER_NOT_LOGIN_MSG;
            } else if (rdp.getRet() == 1 && type == 3) {        //账号存在   --微信绑定
                //手机邮箱都绑定了
                if (!StringUtils.isBlank(userInfo.getUnionid())) {
                    return UserInfo.WX_BIND_ED;
                }
            }

            //账号黑名单拦截
        	ReturnData rd=getAccountVcodeState(account);
        	if(rd.getRet()!=0){
        		return rd;
        	}
            

            if (account.indexOf("@") == -1) {
                //手机
                //vc=mongoTemplate.findOne(new Query(Criteria.where("phone").is(account).and("type").is(type)).with(new Sort(Direction.DESC,"id")).limit(1), VerificationCodeEntity.class,"verification_code");
                vc = verificationCodeMapper.getVcode(account, type);
            } else {
                //vc=mongoTemplate.findOne(new Query(Criteria.where("email").is(account).and("type").is(type)).with(new Sort(Direction.DESC,"id")).limit(1), VerificationCodeEntity.class,"verification_code");
                vc = verificationCodeMapper.getEmailVcode(account, type);
            }

            long curTime = new Date().getTime() / 1000;
            if (vc == null || curTime > (vc.getCreateStamp() + 60)) {
                //重新获取验证码
                //vc = new VerificationCodeEntity(account, type);
            	vc = new VerificationCode(account, type);
            	/*long id=this.getId("verification_code");
                vc.setId(id);*/
                if (account.indexOf("@") == -1) {
                	//mongoTemplate.save(vc,"verification_code");
                    verificationCodeMapper.save(vc);
                } else {
                	//mongoTemplate.save(vc,"verification_code");
                    verificationCodeMapper.saveEmail(vc);
                }

                //发送验证码
                int sendsms = ConfUtils.getResourceNum("sendsms");
                //int sendsms=1;
                if (sendsms == 1) {

                    if (account.indexOf("@") == -1) {
                        //发送短信
                        aliyunService.sendVcode(account, vc.getVcode(), type);
                    } else {
                        //发送邮件
                        aliyunService.sendEmailVcode(account, vc.getVcode(), type);
                    }
                    vc.setVcode(null);
                }
                
                //发送的验证码记录
            	long cuTime=new Date().getTime()/1000;
            	mongoTemplate.findAndModify(new Query(Criteria.where("account").is(account).and("updateStamp").lte(CommonUtils.curDayMaxAndMinTime(0)).gte(CommonUtils.curDayMaxAndMinTime(1))), new Update().set("updateStamp", cuTime).inc("total", 1),new FindAndModifyOptions().returnNew(true).upsert(true), HashMap.class,"vcode_state_account");
            
                return new ReturnData(vc, 0, "发送成功");
            } else {
                //没有超过1分钟
                return VerificationCode.NOT_PASS_TIME;
            }
        } catch (Exception e) {
        	Map map = new HashMap();
			map.put("method", "LoginService.getVcode2_6");
			map.put("account", account);
			map.put("type", type);
			new LogException(e, map);
        }
        return ReturnData.ERROR;
    }
    
    private ReturnData getVcode(String account, int type, UserInfo userInfos) {
        try {
        	//VerificationCodeEntity vc = null;
        	VerificationCode vc = null;
            UserInfo userInfo = null;
            String email = "";
            String phone = "";
            ReturnData rdp = null;

            if (account.indexOf("@") == -1) {
                //手机
                rdp = userService.checkPhone(account, 1);
                phone = account;
                userInfo = (UserInfo) rdp.getResult();
            } else {
                rdp = userService.checkEmail(account, 1);
                email = account;
                userInfo = (UserInfo) rdp.getResult();
            }
            if (rdp.getRet() != 1 && rdp.getRet() != 2) {
                return rdp;
            }
            /**
             * 1账号存在
             * 2账号不存在
             */
            if (rdp.getRet() == 1 && type == 0) {    //账号存在  不能注册  0
                return UserInfo.ERRER_ACCOUNT_REGISTER_ED;
            } else if (rdp.getRet() == 2 && type == 1) {    //账号不存在  不能找回密码   1
                return UserInfo.ERRER_ACCOUNT_NOT_FOUND;
            } else if (rdp.getRet() == 1 && type == 4) {  //账号存在  不能绑定(修改)账号   4
                return UserInfo.ERRER_ACCOUNT_BIND_ED;
            } else if (null == userInfos && type == 4) {  //用户没登录 不能绑定(修改)账号   4
                return UserInfo.ERRER_NOT_LOGIN_MSG;
            } else if (rdp.getRet() == 1 && type == 3) {        //账号存在   --微信绑定
                //手机邮箱都绑定了
                if (!StringUtils.isBlank(userInfo.getUnionid())) {
                    return UserInfo.WX_BIND_ED;
                }
            }

            //账号黑名单拦截
        	/*ReturnData rd=getAccountVcodeState(account);
        	if(rd.getRet()!=0){
        		return rd;
        	}*/
            

            if (account.indexOf("@") == -1) {
                //手机
                //vc=mongoTemplate.findOne(new Query(Criteria.where("phone").is(account).and("type").is(type)).with(new Sort(Direction.DESC,"id")).limit(1), VerificationCodeEntity.class,"verification_code");
                vc = verificationCodeMapper.getVcode(account, type);
            } else {
                //vc=mongoTemplate.findOne(new Query(Criteria.where("email").is(account).and("type").is(type)).with(new Sort(Direction.DESC,"id")).limit(1), VerificationCodeEntity.class,"verification_code");
                vc = verificationCodeMapper.getEmailVcode(account, type);
            }

            long curTime = new Date().getTime() / 1000;
            if (vc == null || curTime > (vc.getCreateStamp() + 60)) {
                //重新获取验证码
                //vc = new VerificationCodeEntity(account, type);
            	vc = new VerificationCode(account, type);
            	/*long id=this.getId("verification_code");
                vc.setId(id);*/
                if (account.indexOf("@") == -1) {
                	//mongoTemplate.save(vc,"verification_code");
                    verificationCodeMapper.save(vc);
                } else {
                	//mongoTemplate.save(vc,"verification_code");
                    verificationCodeMapper.saveEmail(vc);
                }

                //发送验证码
                int sendsms = ConfUtils.getResourceNum("sendsms");
                //int sendsms=1;
                if (sendsms == 1) {

                    if (account.indexOf("@") == -1) {
                        //发送短信
                        aliyunService.sendVcode(account, vc.getVcode(), type);
                    } else {
                        //发送邮件
                        aliyunService.sendEmailVcode(account, vc.getVcode(), type);
                    }
                    vc.setVcode(null);
                }
                
                //发送的验证码记录
            	/*long cuTime=new Date().getTime()/1000;
            	mongoTemplate.findAndModify(new Query(Criteria.where("account").is(account).and("updateStamp").lte(CommonUtils.curDayMaxAndMinTime(0)).gte(CommonUtils.curDayMaxAndMinTime(1))), new Update().set("updateStamp", cuTime).inc("total", 1),new FindAndModifyOptions().returnNew(true).upsert(true), HashMap.class);
            */
                return new ReturnData(vc, 0, "发送成功");
            } else {
                //没有超过1分钟
                return VerificationCode.NOT_PASS_TIME;
            }
        } catch (Exception e) {
        	Map map = new HashMap();
			map.put("method", "LoginService.getVcode");
			map.put("account", account);
			map.put("type", type);
			new LogException(e, map);
        }
        return ReturnData.ERROR;
    }


    /**
     * v2.9
     * 获取验证码 发送消息,判断是否需要图片验证
     * @param account
     * @param type  0 注册  1 找回密码  3 微信绑定 4 修改账号
     * @return,ret:10 需要图片验证码
     */
    public ReturnData getVcode2_9(String account, int type, UserInfo userInfo) {
        try {
            //手机注册/改绑/微信的时候 才判断是否需要图片验证
            if (type == 0 && CommonUtils.isMobile(account)) {
                boolean flag = false;

                //用户每日注册人数(手机注册)
                Query query = Query.query(Criteria.where("name").is("global"));
                query.fields().include("allowSMS");
                GlobalConfig gcfig = mongoTemplate.findOne(query, GlobalConfig.class);

                if (null != gcfig && null != gcfig.getAllowSMS() && gcfig.getAllowSMS()) {
                    //需要图片验证
                    return new ReturnData(1, "需要图片验证");
                }
            }

            //手机/邮箱验证
            return this.getVcode2_6(account, type, userInfo);
        } catch (Exception e) {
        	Map map = new HashMap();
			map.put("method", "LoginService.getVcode2_9");
			map.put("account", account);
			map.put("type", type);
			new LogException(e, map);
        }
        return ReturnData.ERROR;
    }

    /**
     * v3.0 手机注册/改绑/微信绑定需要图片验证
     * 获取验证码 发送消息
     *
     * @param account
     * @param type  0 注册  1 找回密码  3 微信绑定 4 修改账号
     * @return
     */
    public ReturnData getVcode2_9_2(String account, int type, UserInfo userInfos) {
        try {
            //VerificationCodeEntity vc = null;
            VerificationCode vc = null;
            String email = "";
            String phone = "";
            UserInfo userInfo = null;
            ReturnData rdp = null;
            if (account.indexOf("@") == -1) {
                //手机
                rdp = userService.checkPhone(account, 1);
                phone = account;
                userInfo = (UserInfo) rdp.getResult();
            } else {
                rdp = userService.checkEmail(account, 1);
                email = account;
                userInfo = (UserInfo) rdp.getResult();
            }
            if (rdp.getRet() != 1 && rdp.getRet() != 2) {
                return rdp;
            }
            /**
             * 1账号存在
             * 2账号不存在
             */
            if (rdp.getRet() == 1 && type == 0) {    //账号存在  不能注册  0
                return UserInfo.ERRER_ACCOUNT_REGISTER_ED;
            } else if (rdp.getRet() == 2 && type == 1) {    //账号不存在  不能找回密码   1
                return UserInfo.ERRER_ACCOUNT_NOT_FOUND;
            } else if (rdp.getRet() == 1 && type == 4) {  //账号存在  不能绑定(修改)账号   4
                return UserInfo.ERRER_ACCOUNT_BIND_ED;
            } else if (null == userInfos && type == 4) {  //用户没登录 不能绑定(修改)账号   4
                return UserInfo.ERRER_NOT_LOGIN_MSG;
            } else if (rdp.getRet() == 1 && type == 3) {        //账号存在   --微信绑定
                //手机邮箱都绑定了
                if (!StringUtils.isBlank(userInfo.getUnionid())) {
                    return UserInfo.WX_BIND_ED;
                }
            }

            //手机号黑名单拦截
            ReturnData rd=getAccountVcodeState(account);
            if(rd.getRet()!=0){
            	return rd;
            }

            if (account.indexOf("@") == -1) {
                //手机注册/改绑/微信注册的时候 才判断是否需要图片验证,即发送短信的行为需要验证
                //用户每日注册人数(手机注册)
                Query query = Query.query(Criteria.where("name").is("global"));
                query.fields().include("allowSMS");
                GlobalConfig gcfig = mongoTemplate.findOne(query, GlobalConfig.class);

                if (null != gcfig && null != gcfig.getAllowSMS() && gcfig.getAllowSMS()) {
                    //需要图片验证
                    return UserInfo.IMAGE_VALID_CODE;
                }
                //手机
                vc = verificationCodeMapper.getVcode(account, type);
                //vc=mongoTemplate.findOne(new Query(Criteria.where("phone").is(account).and("type").is(type)).with(new Sort(Direction.DESC,"id")).limit(1), VerificationCodeEntity.class,"verification_code");
            } else {
                //vc=mongoTemplate.findOne(new Query(Criteria.where("email").is(account).and("type").is(type)).with(new Sort(Direction.DESC,"id")).limit(1), VerificationCodeEntity.class,"verification_code");
                vc = verificationCodeMapper.getEmailVcode(account, type);
            }

            long curTime = new Date().getTime() / 1000;
            if (vc == null || curTime > (vc.getCreateStamp() + 60)) {
                //重新获取验证码
                //vc = new VerificationCodeEntity(account, type);
                vc = new VerificationCode(account, type);
                /*long id=this.getId("verification_code");
                vc.setId(id);*/
                if (account.indexOf("@") == -1) {
                	
                	//mongoTemplate.save(vc,"verification_code");
                    verificationCodeMapper.save(vc);
                } else {
                	//mongoTemplate.save(vc,"verification_code");
                    verificationCodeMapper.saveEmail(vc);
                }

                //发送验证码
                int sendsms = ConfUtils.getResourceNum("sendsms");
                //int sendsms=1;
                if (sendsms == 1) {

                    if (account.indexOf("@") == -1) {
                        //发送短信
                        aliyunService.sendVcode(account, vc.getVcode(), type);
                    } else {
                        //发送邮件
                        aliyunService.sendEmailVcode(account, vc.getVcode(), type);
                    }
                    vc.setVcode(null);
                }
                
                //发送的验证码记录
                long cuTime=new Date().getTime()/1000;
                mongoTemplate.findAndModify(new Query(Criteria.where("account").is(account).and("updateStamp").lte(CommonUtils.curDayMaxAndMinTime(0)).gte(CommonUtils.curDayMaxAndMinTime(1))), new Update().set("updateStamp", cuTime).inc("total", 1),new FindAndModifyOptions().returnNew(true).upsert(true), HashMap.class,"vcode_state_account");
                
                return new ReturnData(vc, 0, "发送成功");
            } else {
                //没有超过1分钟
                return VerificationCode.NOT_PASS_TIME;
            }

        } catch (Exception e) {
        	Map map = new HashMap();
			map.put("method", "LoginService.getVcode2_9_2");
			map.put("account", account);
			map.put("type", type);
			new LogException(e, map);
        }
        return ReturnData.ERROR;
    }

    //图片验证码对比验证
    public ReturnData compareImgVcode(HttpServletResponse response, HttpServletRequest request, String imgVcode, String account, Integer type, UserInfo userInfo) {
        try {
            if (StringUtils.isNotBlank(account) && StringUtils.isNotBlank(imgVcode)) {
                //验证是否是手机注册
                /*if (!CommonUtils.isMobile(account)) {
                    return new ReturnData(2,"非手机号注册,不需要图片验证码");
                }*/
                HttpSession session = request.getSession();
                //session获取验证码
                String sessionImgVcode = (String) session.getAttribute("imgVcode");
                if (StringUtils.isBlank(sessionImgVcode)) {
                    return new ReturnData("session没有返回验证码");
                }
                //session中验证码的格式vcode+createTime+phone
                String[] sessionImgVcodes = sessionImgVcode.split("_");
                if (!imgVcode.equalsIgnoreCase(sessionImgVcodes[0])) {
                    return VerificationCode.VCODE_ERRER;
                }
                if (!account.equals(sessionImgVcodes[1])) {
                    return VerificationCode.VCODE_ERRER;
                }
                Long nowTime = new Date().getTime() / 1000;
                //验证时间超过两分钟
                if (Integer.parseInt(sessionImgVcodes[2]) > (nowTime + 60 * 2)) {
                    return VerificationCode.VCODE_ERRER;
                }

                //图片验证码正确  进行手机/邮箱验证
                //return getVcode2_6(account, type, userInfo);
                return getVcode(account, type, userInfo);
            }
        } catch (Exception e) {
        	Map map = new HashMap();
			map.put("method", "LoginService.compareImgVcode");
			map.put("imgVcode", imgVcode);
			map.put("type", type);
			map.put("account", account);
			new LogException(e, map);
        }

        return ReturnData.ERROR;
    }

    /**
     * 手机或者邮箱注册
     *
     * @param account
     * @param password
     * @param vcode    验证码
     * @return
     */
    public ReturnData accountRegister2_6(String account, String password, String vcode) {
        try {
            //0 注册  1 找回密码  3 微信绑定 4 修改账号
            ReturnData rd = userService.isVcode2_6(account, vcode, 0);
            if (rd.getRet() != 0) {
            	//验证码出错
                return rd;
            }
            ;
            
            //注册
            return accountRegister(account, password);

        } catch (Exception e) {
        	Map map = new HashMap();
			map.put("method", "LoginService.accountRegister2_6");
			map.put("account", account);
			map.put("password", password);
			map.put("vcode", vcode);
			new LogException(e, map);
        }
        return ReturnData.ERROR;
    }


    /**
     * 生成随机用户名
     *
     * @return
     */
    public String getAutoNickName(String prev) {
        //自动生成用户名
        boolean flag = true;
        String nickname = "";
        int lastlength = 4;
        if (StringUtils.isBlank(prev)) {
            lastlength = 6;
            prev = "";
        }
        ;
        while (flag) {
            nickname = UUID.randomUUID().toString().substring(0, lastlength) + prev;
            //验证手机号
            ReturnData nn = findNickname(nickname);
            if (nn.getRet() == 2) {
                flag = false;
            }
        }
        return nickname;
    }

    /**
     * 验证用户名规则
     * @param val: 要验证的值
     * @return
     */
    public ReturnData findNickname(String val) {
        try {
            if (StringUtils.isBlank(val)) {
                return UserInfo.ERRER_LENGTH_NICKNAME;
            }
            //验证长度
            int lengval = CommonUtils.getStrLength(val);
            if (lengval < 4 || lengval > 16) {
                return UserInfo.ERRER_LENGTH_NICKNAME;
            }

            //验证特殊字符
            if (!CommonUtils.checkUserName(val)) {
                return UserInfo.ERRER_CONTENT_NICKNAME;
            }

            //验证用户名
            UserInfo userInfo = userInfoMapper.findFeild("nickname", val);
            if (userInfo != null && !StringUtils.isBlank(userInfo.getNickname())) {
                return new ReturnData(userInfo, 1, "用户名存在");
            }
            return new ReturnData(userInfo, 2, "用户名不存在");
        } catch (Exception e) {
        	Map map = new HashMap();
			map.put("method", "LoginService.findNickname");
			map.put("nickname", val);
			new LogException(e, map);
        }
        return ReturnData.ERROR;
    }


    /**
     * 查询mongo的用户信息
     *
     * @param userId 用户id
     * @return
     */
    public UserInfo getMongoUserInfo(long userId) {
        UserInfo user = mongoTemplate.findOne(new Query(Criteria.where("id").is(userId)), UserInfo.class, "user_info");
        return user;
    }

    /**
     * 手机或者邮箱用户找回密码
     *
     * @param account: 账号
     * @param password: 密码
     * @param vcode: 验证码
     * @return
     */
    public ReturnData restPassword2_6(String account, String password, String vcode) {
        try {
            //验证码是否正确
            VerificationCode vc = null;
            ReturnData rd = userService.isVcode2_6(account, vcode, 1);
            if (rd.getRet() == 0) {
                vc = (VerificationCode) rd.getResult();
            } else {
                return rd;
            }
            //开始重置密码
            return restPassword(account, password);
        } catch (Exception e) {
        	Map map = new HashMap();
			map.put("method", "LoginService.restPassword2_6");
			map.put("account", account);
			map.put("password", password);
			map.put("vcode", vcode);
			new LogException(e, map);
        }
        return ReturnData.ERROR;
    }


    /**
     * 手机或者邮箱注册
     *
     * @param account
     * @param password
     * @return
     */
    public ReturnData accountRegister(String account, String password) {
        try {
            ReturnData sphone = null;
            String phone = "";
            String email = "";
            //检验账号是否存在和格式
            if (account.indexOf("@") != -1) {
                sphone = userService.checkEmail(account);
                email = account;
            } else {
                sphone = userService.checkPhone(account);
                phone = account;
            }

            if (sphone.getRet() != 2) {
                //邮箱或者手机号存在
                return sphone;
            }
            //密码长度
            if (StringUtils.isBlank(password) || password.length() < 6) {
                return UserInfo.ERRER_LENGTH_PASSWORD;
            }

            UserInfo userInfo = new UserInfo();
            //自动生成用户名
            boolean flag = true;
            String nickname = "";
            nickname = getAutoNickName("");
            userInfo.setNickname(nickname);
            userInfo.setUsercode(MD5Utils.encode(account + "bevol" + password));//无用的历史字段，用来验证用户  保留用作验证其他功能
            userInfo.setPhone(phone);
            userInfo.setEmail(email);
            userInfo.setPassword(MD5Utils.encode(password));
            int i = this.register(userInfo);
            userInfo.setPassword("");  //todo  数据同步异常处理回滚
            if (i > 0) {
                //设置用户基础信息  mongo+mysql
                userService.userInit(userInfo);
                mysqlSynMongo(userInfo);
                return new ReturnData<UserInfo>(userInfo);
            }
        } catch (Exception e) {
        	Map map = new HashMap();
			map.put("method", "LoginService.accountRegister");
			map.put("account", account);
			map.put("password", password);
			new LogException(e, map);
        }
        return ReturnData.ERROR;
    }


    /**
     * 手机或者邮箱用户重置密码
     *
     * @param account: 账号
     * @param password: 密码
     * @return
     */
    public ReturnData restPassword(String account, String password) {
        try {
            ReturnData sphone = null;
            String phone = "";
            String email = "";
            if (!StringUtils.isBlank(account)) {
                if (account.indexOf("@") == -1) {
                    //验证手机号
                    sphone = userService.checkPhone(account);
                    phone = account;
                } else {
                    sphone = userService.checkEmail(account);
                    email = account;
                }
            }

            if (sphone.getRet() != 1) {
                //账号不存在
                return sphone;
            }
            int i = 0;
            if (!StringUtils.isBlank(phone)) {
            	//重置密码
                i = userInfoMapper.restPassword2(phone, MD5Utils.encode(password));
            } else {
                i = userInfoMapper.restPassword3(email, MD5Utils.encode(password));
            }

            if (i > 0) {
                UserInfo userInfo = null;
                if (!StringUtils.isBlank(phone)) {
                    //查询用户
                    userInfo = userInfoMapper.findFeild("phone", account);
                } else {
                    userInfo = userInfoMapper.findFeild("email", account);
                }
                //清空密码
                userInfo.setPassword("");
                //更新登录时间
                userInfoMapper.updateField("logintime", new Date().getTime() / 1000, "id", userInfo.getId());
                userService.userInit(userInfo);
                return new ReturnData(userInfo);
            } else {
                return UserInfo.ERRER_REST_PASSWORD;
            }
        } catch (Exception e) {
        	Map map = new HashMap();
			map.put("method", "LoginService.restPassword");
			map.put("account", account);
			map.put("password", password);
			new LogException(e, map);
        }
        return ReturnData.ERROR;
    }


    public ReturnData createImgVcode(String phone, int type, UserInfo userInfo, HttpServletResponse response,
                                     HttpServletRequest request) {
        try {
            // 定义图像buffer
            BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            // Graphics2D gd = buffImg.createGraphics();
            // Graphics2D gd = (Graphics2D) buffImg.getGraphics();
            Graphics gd = buffImg.getGraphics();
            // 创建一个随机数生成器类
            Random random = new Random();
            // 将图像填充为白色
            gd.setColor(Color.WHITE);
            gd.fillRect(0, 0, width, height);

            // 创建字体，字体的大小应该根据图片的高度来定。
            Font font = new Font("Fixedsys", Font.BOLD, fontHeight);
            // 设置字体。
            gd.setFont(font);

            // 画边框。
            gd.setColor(Color.BLACK);
            gd.drawRect(0, 0, width - 1, height - 1);

            // 随机产生40条干扰线，使图象中的认证码不易被其它程序探测到。
            gd.setColor(Color.BLACK);
            for (int i = 0; i < 40; i++) {
                int x = random.nextInt(width);
                int y = random.nextInt(height);
                int xl = random.nextInt(12);
                int yl = random.nextInt(12);
                gd.drawLine(x, y, x + xl, y + yl);
            }

            // randomCode用于保存随机产生的验证码，以便用户登录后进行验证。
            StringBuffer randomCode = new StringBuffer();
            int red = 0, green = 0, blue = 0;

            // 随机产生codeCount数字的验证码。
            for (int i = 0; i < codeCount; i++) {
                // 得到随机产生的验证码数字。
                String code = String.valueOf(codeSequence[random.nextInt(36)]);
                // 产生随机的颜色分量来构造颜色值，这样输出的每位数字的颜色值都将不同。
                red = random.nextInt(255);
                green = random.nextInt(255);
                blue = random.nextInt(255);

                // 用随机产生的颜色将验证码绘制到图像中。
                gd.setColor(new Color(red, green, blue));
                gd.drawString(code, (i + 1) * xx, codeY);

                // 将产生的四个随机数组合在一起。
                randomCode.append(code);
            }

            // 将四位数字的验证码保存到Session中。
            HttpSession session = request.getSession();
            System.out.print(randomCode);
            long date = new Date().getTime() / 1000;
            session.setAttribute("imgVcode", randomCode.toString() + "_" + phone + "_" + date);
            // 禁止图像缓存。
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("image/jpeg");

            // 将图像输出到Servlet输出流中。
            ServletOutputStream sos = response.getOutputStream();
            ImageIO.write(buffImg, "jpeg", sos);
            sos.flush();
            sos.close();
            return ReturnData.SUCCESS;
        } catch (Exception e) {
        	Map map = new HashMap();
			map.put("method", "LoginService.createImgVcode");
			map.put("phone", phone);
			map.put("type", type);
			new LogException(e, map);
        }
        return ReturnData.ERROR;
    }


}
