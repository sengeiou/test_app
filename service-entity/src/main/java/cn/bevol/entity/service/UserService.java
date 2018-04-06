package cn.bevol.entity.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.io97.cache.CacheKey;
import com.io97.cache.CacheableTemplate;
import com.io97.cache.redis.RedisCacheProvider;
import com.io97.utils.MD5Utils;

import cn.bevol.cache.CACHE_NAME;
import cn.bevol.conf.client.ConfUtils;
import cn.bevol.model.entity.EntityBase;
import cn.bevol.model.entity.EntityFind;
import cn.bevol.model.entity.EntityGoods;
import cn.bevol.model.metadata.UserBaseInfo;
import cn.bevol.model.metadata.UserGoodsCategory;
import cn.bevol.model.user.UserAddressInfo;
import cn.bevol.model.user.UserInfo;
import cn.bevol.model.user.UserSkinProtection;
import cn.bevol.model.user.VerificationCode;
import cn.bevol.model.vo.SmartUserInfo;
import cn.bevol.mybatis.dao.DoyenMapper;
import cn.bevol.mybatis.dao.EntityFindMapper;
import cn.bevol.mybatis.dao.UserInfoMapper;
import cn.bevol.mybatis.dao.VerificationCodeMapper;
import cn.bevol.mybatis.dto.Doyen;
import cn.bevol.mybatis.model.UserBlackList;
import cn.bevol.entity.service.utils.CommonUtils;
import cn.bevol.util.ReturnData;
import cn.bevol.util.ReturnListData;

@Service
public class UserService extends BaseService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    VerificationCodeMapper verificationCodeMapper;

    @Autowired
    RedisCacheProvider cacheProvider;

    @Autowired
    private EntityFindMapper findMapper;

    @Autowired
    private DoyenMapper doyenMapper;

    @Autowired
    EntityService entityService;

    @Autowired
    MessageService messageService;


    @Autowired
    AliyunService aliyunService;

    @Autowired
    LoginService loginService;

    @Autowired
    BackUserService backUserService;

    @Autowired
    SkinProtectionService skinProtectionService;
    private static Logger logger = LoggerFactory.getLogger(UserService.class);


    /**
     * 根据id列表查询
     *
     * @param ids
     * @return userinfo集合
     */
    public List<SmartUserInfo> findSmartUserInfoByIds(List<Long> ids) {
        List<UserInfo> lm = mongoTemplate.find(new Query(Criteria.where("id").in(ids)), UserInfo.class, "user_info");
        List<SmartUserInfo> sis = new ArrayList<SmartUserInfo>();
        for (UserInfo ui : lm) {
            SmartUserInfo su = new SmartUserInfo();
            su.setHeadimgurl(ui.getHeadimgurl());
            su.setId(ui.getId());
            su.setNickname(ui.getNickname());
            sis.add(su);
        }
        return sis;
    }

    /**
     * 获取用户基本信息
     * @param ids
     * @return
     */
    public List<SmartUserInfo> findSmartUserInfoByIds2(List<Long> ids) {
        // TODO Auto-generated method stub
        try {
            String idss=StringUtils.join(ids,",");
            List<UserInfo> userInfo = userInfoMapper.findUserinfoByIds(idss);
            if (userInfo == null) {
                userInfo = new ArrayList();
            }
            List<SmartUserInfo> sis = new ArrayList<SmartUserInfo>();
            for (UserInfo ui : userInfo) {
                SmartUserInfo su = new SmartUserInfo();
                su.setHeadimgurl(ui.getHeadimgurl());
                su.setId(ui.getId());
                su.setNickname(ui.getNickname());
                sis.add(su);
            }
            return sis;
        } catch (Exception e) {
            logger.error("method:findSmartUserInfoByIds2 arg:{\"ids\":\"" + ids + "\"}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    /**
     * 新消息数量
     *
     * @param userId
     * @return
     */
    public Integer newMsgCount(UserInfo userInfo) {
        try {
            userInit(userInfo);
            String tname = "user_info";
            UserInfo m = mongoTemplate.findOne(new Query(Criteria.where("id").is(userInfo.getId())), UserInfo.class, tname);
            return m.getNewMsgNum();
        } catch (Exception e) {
            logger.error("method:newMsgCount arg:{userId:" + userInfo.getId() + "}   desc:" +  ExceptionUtils.getStackTrace(e));
            e.printStackTrace();
        }
        return null;

    }


    /**
     * 用户初始化数据
     *
     * @param userId 用户id
     */
    public UserInfo userInitMsg(long userId) {
        messageService.firstMessage(userId);
        //初始化消息
        messageService.builderUserMessage(userId);
        //获取用户信息
        UserInfo user = getMongoUserInfo(userId);

        //防止消息没有及时清除的情况
        messageService.msgAllOpen(user);
        return user;
    }

    /**
     * 查询mongo的用户信息
     * @param userId 用户id
     * @return
     */
    public UserInfo getMongoUserInfo(long userId) {
        UserInfo user = mongoTemplate.findOne(new Query(Criteria.where("id").is(userId)), UserInfo.class, "user_info");
        return user;
    }

    public enum ScoreOpt {
        /**
         * finalskin
         * 完成肤质测试
         */
        FINISHEDSKIN {
            public int getVal() {
                return 50;
            }
        },
        /**
         * 发送产品评论评论
         */
        SENDCOMMNEGOODS {
            public int getVal() {
                return 5;
            }
        },
        /**
         * 发送成分评论
         */
        SENDCOMMNECOMPOSITION {
            public int getVal() {
                return 5;
            }
        },
        /**
         * 发送发现评论
         */
        SENDCOMMENTFIND {
            public int getVal() {
                return 2;
            }
        },
        /**
         * 点评加精
         */
        COMMENTESSENCE {
            public int getVal() {
                return 20;
            }
        };

        public abstract int getVal();
    }

    /**
     * 增加用户积分
     */
    public void addScore(long userId, ScoreOpt opt) {
        mongoTemplate.findAndModify(new Query(Criteria.where("id").is(userId)), new Update().inc("score", opt.getVal()), new FindAndModifyOptions().returnNew(true).upsert(true), UserInfo.class, "user_info");
    }

    /**
     * 获取验证码
     *
     * @param phone
     * @param type
     * @return
     */
    public ReturnData getVcode(String phone, int type) {
        try {
            //手机号黑名单拦截
            ReturnData rd=getAccountVcodeState(phone);
            if(rd.getRet()!=0){
                return rd;
            }

            VerificationCode vc = verificationCodeMapper.getVcode(phone, type);
            long curTime = new Date().getTime() / 1000;
            if (vc == null || curTime > (vc.getCreateStamp() + 60)) {
                //重新获取验证码
                vc = new VerificationCode(phone, type);
                verificationCodeMapper.save(vc);
                //发送验证码
                int sendsms=ConfUtils.getResourceNum("sendsms");
                if(sendsms==1) {
                    aliyunService.sendVcode(phone, vc.getVcode(), type);
                    vc.setVcode(null);
                }

                //发送的验证码记录
                long cuTime=new Date().getTime()/1000;
                mongoTemplate.findAndModify(new Query(Criteria.where("account").is(phone).and("updateStamp").lte(CommonUtils.curDayMaxAndMinTime(0)).gte(CommonUtils.curDayMaxAndMinTime(1))), new Update().set("updateStamp", cuTime).inc("total", 1),new FindAndModifyOptions().returnNew(true).upsert(true), HashMap.class);


                return new ReturnData(vc);
            } else {
                //没有超过1分钟
                return VerificationCode.NOT_PASS_TIME;
            }
        } catch (Exception e) {
            logger.error("method:getVcode arg:{phone:\"" + phone + "\",type:\"" + type + "\"}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnData.ERROR;
    }

    /**
     * 手机注册
     *
     * @param nickname
     * @param phone
     * @param password
     * @param qcode    验证码
     * @return
     */
    @Deprecated
    public ReturnData phoneRegister(String phone, String password, String vcode) {
        try {
            //验证手机号
            ReturnData sphone = checkPhone(phone);
            if (sphone.getRet() != 2) {
                return sphone;
            }
            //密码长度
            if (StringUtils.isBlank(password) || password.length() < 6) {
                return UserInfo.ERRER_LENGTH_PASSWORD;
            }
            //验证码是否正确
            VerificationCode vc = verificationCodeMapper.getVcode(phone, 0);
            if (vc == null) {
                //找不到验证码
                return VerificationCode.VCODE_ERRER;
            }
            //大于10分钟
            long curTime = new Date().getTime() / 1000;
            if (curTime > (vc.getCreateStamp().longValue() + (60 * 10))) {
                //验证码超时
                return VerificationCode.VCODE_ERRER;
            }
            if (!vc.getVcode().equals(vcode)) {
                //验证码错误
                return VerificationCode.VCODE_ERRER;
            }

            UserInfo userInfo = new UserInfo();
            //自动生成用户名
            boolean flag = true;
            String nickname =getAutoNickName(phone.substring(8));
            userInfo.setNickname(nickname);
            userInfo.setUsercode(MD5Utils.encode(phone + "bevol" + password));
            userInfo.setPhone(phone);
            userInfo.setPassword(MD5Utils.encode(password));
            int i = loginService.register(userInfo);
            userInfo.setPassword("");
            if (i > 0) {
                userInit(userInfo);
                return new ReturnData<UserInfo>(userInfo);
            }
        } catch (Exception e) {
            logger.error("method:phoneRegister arg:{phone:\"" + phone + "\",password:\"" + password + "\",vcode:\"" + vcode + "\"}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnData.ERROR;
    }

    /**
     * 生成随机用户名
     * @return
     */
    public String getAutoNickName(String prev) {
        //自动生成用户名
        boolean flag = true;
        String nickname = "";
        int lastlength=4;
        if(StringUtils.isBlank(prev)) {lastlength=6;prev=""; };
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
     * 手机登录
     *
     * @param nickname
     * @param phone
     * @param password
     * @return
     */
    @Deprecated
    public ReturnData phoneLogin(String phone, String password) {
        try {
            if(!StringUtils.isBlank(phone)&&!StringUtils.isBlank(password)) {
                UserInfo userInfo = userInfoMapper.phoneLogin(phone, MD5Utils.encode(password));
                if(userInfo!=null) {
                    userInit(userInfo);
                    //更新登录时间
                    userInfoMapper.updateField("logintime", new Date().getTime() / 1000, "id", userInfo.getId());
                    return new ReturnData(userInfo);
                }
            }
        } catch (Exception e) {
            logger.error("method:phoneLogin arg:{phone:\"" + phone + "\",password:\"" + password + "\"}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return UserInfo.ERRER_PHONE_PASSWORD;

    }

    /**
     * 用于登录时的查询mongodb
     *各种用户状态相关查询，登录与注册共用
     * @param userInfo
     */
    public void userInit(UserInfo userInfo) {
        /**
         * 消息与用户行为相关数
         */
        UserInfo msguser = this.userInitMsg(userInfo.getId());


        userInfo.setNewMsgNum(msguser.getNewMsgNum());
        userInfo.setCommentNum(msguser.getCommentNum());
        userInfo.setCommentLikeNum(msguser.getCommentLikeNum());
        userInfo.setMsgNum(msguser.getMsgNum());
        userInfo.setScore(msguser.getScore());

        userInfo.setNewCommentMsgNum(msguser.getNewCommentMsgNum());
        userInfo.setCommentMsgNum(msguser.getCommentMsgNum());

        userInfo.setNewSysMsgNum(msguser.getNewSysMsgNum());
        userInfo.setSysMsgNum(msguser.getSysMsgNum());

        userInfo.setXxjMsgNum(msguser.getXxjMsgNum());
        userInfo.setNewXxjMsgNum(msguser.getNewXxjMsgNum());

        userInfo.setCommentLikeMsgNum(msguser.getCommentLikeMsgNum());
        userInfo.setNewCommentLikeMsgNum(msguser.getNewCommentLikeMsgNum());
        userInfo.bindState();//绑定手机，微信，邮箱等业务判断
        //用户感兴趣的标签
        userInfo.setSkinTags(msguser.getSkinTags());

        //用户认证状态
        userInfo.setVerifyState(msguser.getVerifyState());

        //用户地址
        userInfo.setUserAddressInfos(msguser.getUserAddressInfos());

        //判断是否被禁言
        UserBlackList userBlack = backUserService.getUserBlackById(userInfo.getId());
        if(null!=userBlack && userBlack.getId()>0){
            userInfo.setBlack(true);
        }

        //初始化用户默认的护肤方案分类
        //List idsList=initUserGoodsCategory(userInfo);
        userInfo.setExpireGoodsCatagory(initUserGoodsCategory(userInfo));

    }

    /**
     * 查看护肤方案是否有过期的产品
     * @param userInfo
     * @return
     */
    public Boolean getExpireGoods(Long userId){
        String actionType="user_skin_protection";
        List<Long> ids=new ArrayList<Long>();
        List<Long> uspIds=new ArrayList<Long>();
        long curTime=new Date().getTime()/1000;
        Criteria crt= Criteria.where("userId").is(userId).and("expireTime").lte(curTime).and("expire").is(true);
        Query query=new Query(crt);
        String[] fields={"id"};
        this.setQueryFeilds(query, fields);
        //方案下的所有实体信息
        UserSkinProtection uais = mongoTemplate.findOne(query.limit(1), UserSkinProtection.class, actionType);
        boolean flag=false;
        if(null!=uais && null!=uais.getId() &&uais.getId()>0){
        	flag=true;
        }
        /*for(int i=0;i<uais.size();i++){
            UserSkinProtection uai=uais.get(i);
            if(null!=uai.getOpen()&& 1==uai.getOpen() && null!=uai.getRemainingDays() && uai.getRemainingDays()==0&&null!=uai.getExpire()&&uai.getExpire()){
                //过期产品
                uspIds.add(uai.getId());
                if(!ids.contains(uai.getCategoryPid())){
                    //含有过期产品的方案
                    ids.add(uai.getCategoryPid());
                }
            }
        }*/
		
		/*if(uspIds.size()>0){
			mongoTemplate.updateMulti(new Query(Criteria.where("id").in(uspIds)), new Update().set("expire", true), UserSkinProtection.class,actionType);
		}*/
        //因为3.2之前的护肤方案中,删除方案时没有删除相应的产品,因此查询方案是否存在
        /*if(ids.size()>0){
        	Query que=new Query(Criteria.where("id").in(ids));
        	que.fields().include("id");
        	List<UserGoodsCategory> ugcList=mongoTemplate.find(que, UserGoodsCategory.class,"user_goods_category");
        	ids.clear();
        	if(null!=ugcList){
        		for(UserGoodsCategory ugc:ugcList){
        			ids.add(ugc.getId());
        		}
        	}
        }*/
        
        return flag;
    }


    /**
     * 初始化护肤方案
     * @param userInfo
     */
    private Boolean initUserGoodsCategory(UserInfo userInfo) {
        Long userId=userInfo.getId();
        //初始化肤质方案 基础分类
        List<UserGoodsCategory>  ugcs=skinProtectionService.getUserGoodsCategorys(userId);
        boolean exists=true;
        if(null==ugcs){
            exists=false;
        }

        if(ugcs.size()==1 && ugcs.get(0).isBase()) {//如果没有不生成
            List<Map<String,Object>> ls=new ArrayList<Map<String,Object>>();
            for(UserGoodsCategory u:ugcs){
                List<UserGoodsCategory>  sug=skinProtectionService.getSubCategory(userId, u.getId());
                for(UserGoodsCategory s:sug) {
                    Map<String,Object> ms=new HashMap<String,Object>();
                    ms.put("id", s.getId());
                    ms.put("type", s.getType());
                    ms.put("srcCategoryId", s.getSrcCategoryId());
                    ls.add(ms);
                }
            }
        }

        //查看护肤方案是否有过期的产品
        if(exists){
            return this.getExpireGoods(userInfo.getId());
        }
        return null;
    }

    /**
     * 根据手机号码重置密码
     *
     * @param phone
     * @param password
     * @return
     */
    @Deprecated
    public ReturnData restPassword(String phone, String password, String vcode) {
        try {
            //验证手机号
            ReturnData sphone = checkPhone(phone);
            if (sphone.getRet() != 1) {
                return sphone;
            }

            //验证码是否正确
            VerificationCode vc = verificationCodeMapper.getVcode(phone, 1);
            //大于10分钟
            long curTime = new Date().getTime() / 1000;
            if (vc == null || curTime > (vc.getCreateStamp() + (60 * 10))) {
                //验证码出问题
                return VerificationCode.VCODE_ERRER;
            }
            if (!vc.getVcode().equals(vcode)) {
                //验证码错误
                return VerificationCode.VCODE_ERRER;
            }
            int i = userInfoMapper.restPassword2(phone, MD5Utils.encode(password));

            if (i > 0) {
                //查询用户
                UserInfo userInfo = userInfoMapper.findFeild("phone", phone);
                userInfo.setPassword("");
                //更新登录时间
                userInfoMapper.updateField("logintime", new Date().getTime() / 1000, "id", userInfo.getId());
                userInit(userInfo);
                return new ReturnData(userInfo);
            } else {
                return UserInfo.ERRER_REST_PASSWORD;
            }
        } catch (Exception e) {
            logger.error("method:restPassword arg:{phone:\"" + phone + "\",password:\"" + password + "\",vcode:\"" + vcode + "\"}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnData.ERROR;

    }


    /**
     * 验证用户名
     *
     * @param nickname
     * @param phone
     * @param password
     * @return ReturnData.ret=1 存在 2不存在
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
            logger.error("method:findNickname arg:{\"nickname\":\"" + val + "\"}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnData.ERROR;

    }

    /**
     * 验证手机号
     *
     * @return
     */
    @Deprecated
    public ReturnData checkPhone(String val,int... ret) {
        try {
            if (!CommonUtils.isMobile(val)) {
                return UserInfo.ERRER_PHONE;
            }
            UserInfo userInfo = userInfoMapper.findFeild("phone", val);
            if (userInfo != null && !StringUtils.isBlank(userInfo.getPhone())) {
                if(ret!=null&&ret.length>0&&ret[0]==1) return new ReturnData(userInfo,1, "手机号码存在");
                return new ReturnData(1, "手机号码存在");
            }
            return new ReturnData(2, "手机号码不存在");
        } catch (Exception e) {
            logger.error("method:checkPhone arg:{\"phone\":\"" + val + "\"}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnData.ERROR;

    }
    /**
     * 验证邮箱
     *
     * @param nickname
     * @param phone
     * @param password
     * @return
     */
    public ReturnData checkEmail(String val,int... ret) {
        try {
            if (!CommonUtils.isEmail(val)) {
                return UserInfo.ERRER_EMAIL;
            }
            UserInfo userInfo = userInfoMapper.findFeild("email", val);
            if (userInfo != null && !StringUtils.isBlank(userInfo.getEmail())) {
                if(ret!=null&&ret.length>0&&ret[0]==1) return new ReturnData(userInfo,1, "邮箱存在");
                return new ReturnData(1, "邮箱存在");
            }
            return new ReturnData(2, "邮箱不存在");
        } catch (Exception e) {
            logger.error("method:checkEmail arg:{\"email\":\"" + val + "\"}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnData.ERROR;

    }


    /**
     * 查询用户信息
     *
     * @param id
     * @return
     */
    public ReturnData<UserInfo> getUserById(long id) {
        try {
            UserInfo userInfo = userInfoMapper.findFeild("id", id);
            //获取消息数量

            if (userInfo != null)
                return new ReturnData<UserInfo>(userInfo);
        } catch (Exception e) {
            logger.error("method:getUserById arg:{\"id\":\"" + id + "\"}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnData.ERROR;
    }


    /**
     * 查询用户信息
     *
     * @param id
     * @return
     */
    public ReturnData getUserByUsercode(String usercode) {
        try {
            UserInfo userInfo = userInfoMapper.findFeild("usercode", usercode);
            if (userInfo != null)
                return new ReturnData(userInfo);
        } catch (Exception e) {
            logger.error("method:getUserById arg:{\"usercode\":\"" + usercode + "\"}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return UserInfo.ERRER_NOT_LOGIN;
    }

    /**
     * 查询用户信息
     *
     * @param id
     * @return
     */
    public ReturnData openApp(String usercode) {
        ReturnData d = getUserByUsercode(usercode);
        //修改登录时间
        if (d.getRet() == 0)
            this.userInfoMapper.updateField("logintime", new Date().getTime() / 1000, "usercode", usercode);
        return d;
    }

    public boolean deleteUser(String feild, Object val) {
        int i = userInfoMapper.deleteField(feild, val);
        return i > 0 ? true : false;
    }

    public boolean deleteVcode(String feild, Object val) {
        int i = verificationCodeMapper.deleteField(feild, val);
        return i > 0 ? true : false;
    }

    /**
     * 修改资料新接口 手机为必填项
     * @param userId
     * @param nickname
     * @param headimgurl
     * @param age
     * @param sex
     * @param province
     * @param phone
     * @param city
     * @param yunfu
     * @param vcode
     * @return
     */
    public ReturnData updateUser2(long userId, String nickname, String headimgurl, int age, int sex, String province,String phone, String city, int yunfu,String vcode) {
        ReturnData rd= updateUser(userId,nickname,headimgurl,age,sex,province,city,yunfu);

        return rd;

    }
    /**
     * 修改用户
     *
     * @param userId     id
     * @param nickname   昵称
     * @param headimgurl 头像
     * @param age        年龄
     * @param province   省份
     * @param city       城市
     * @param yunfu      是否怀孕
     * @return
     */
    public ReturnData updateUser(long userId, String nickname, String headimgurl, int age, int sex, String province, String city, int yunfu) {
        //验证用户名是否合法
        try {

            //用户名过滤
            String key="nickname";
            String value=entityService.getConfig(key);
            if(!StringUtils.isBlank(value)){
                int result=entityService.keywordInfiltration(nickname, value);
                if(result==-1){
                    return new ReturnData(-2,"昵称中不能出现敏感词汇");
                }
            }

            //验证是否有这个用户
            ReturnData rd = this.getUserById(userId);
            if (rd.getRet() != 0) return UserInfo.ERRER_USER_OUT;
            //验证用户名
            ReturnData fn = this.findNickname(nickname);
            if (fn.getRet() == 1) {
                UserInfo userInfo = (UserInfo) fn.getResult();
                if (userInfo.getId() != userId) {
                    //用户名重复
                    return UserInfo.ERRER_REPEAT_NICKNAME;
                }
            }  else if(fn.getRet()!=2) return fn;


            UserInfo userInfo = (UserInfo) rd.getResult();

            userInfo.setNickname(nickname);
            if(StringUtils.isNotBlank(headimgurl)) {
                userInfo.setHeadimgurl(headimgurl);
            }
            if(age>0) {
                userInfo.setAge(age);
            }
            if(sex>0) {
                userInfo.setSex(sex);
            }
            if(StringUtils.isNotBlank(headimgurl)) {
                userInfo.setProvince(province);
            }
            if(StringUtils.isNotBlank(province)) {
                userInfo.setProvince(province);
            }
            if(StringUtils.isNotBlank(city)) {
                userInfo.setCity(city);
            }
            userInfo.setYunfu(yunfu);
            if(userId>0)
                userInfo.setId(userId);
            //更新用户
            userInfoMapper.updateUserInfo2_6(userInfo);
            //int i = userInfoMapper.updateUserInfo(userInfo);

            loginService.mysqlSynMongo(userInfo);
            return new ReturnData(userInfo);
        } catch (Exception e) {
            logger.error("method:updateUser arg:{\"userId\":\"" + userId + "\",\"nickname\":\"" + nickname + "\",\"headimgurl\":\"" + headimgurl + "\",\"age\":" + age + ",\"sex\":" + sex + ",\"province\":\"" + province + "\",\"city\":\"" + city + "\",\"yunfu\":" + yunfu + "}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnData.ERROR;
    }


    /**
     * 根据微信编码查询用户
     *
     * @param val
     * @return
     */
    public ReturnData findWXUnionid(String val) {
        try {
            UserInfo userInfo = userInfoMapper.findFeild("unionid", val);
            if (userInfo != null && !StringUtils.isBlank(userInfo.getUnionid())) {
                return new ReturnData(userInfo, 1, "微信注册过");
            }
            return new ReturnData(2, "微信没有注册过");
        } catch (Exception e) {
            logger.error("method:findWXUnionid arg:{\"unionid\":\"" + val + "\"}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnData.ERROR;

    }

    /**
     * 微信登录2手机为必填项
     *
     * @param unionid    微信id
     * @param wxmbopenid 微信对应的手机端id
     * @param nickname
     * @param headimgurl
     * @param age
     * @param sex
     * @param province
     * @param city
     * @param vcode 验证码
     * @return
     */
    @Deprecated
    public ReturnData wxLogin2(String unionid, String wxmbopenid,
                               String country,
                               String language,
                               String nickname, String headimgurl, int age, int sex,
                               String province,String phone,String password, String city, int yunfu,String vcode) {
        try {
            //验证码校验
            //1、验证码校验
            ReturnData rd=this.isVcode(phone, vcode, 3);
            if(rd.getRet()!=0) {return new ReturnData(-2,"验证码错误或超时,请重新获取");};
            //密码长度
            if(StringUtils.isBlank(password)||password.trim().length()<6){
                return new ReturnData(-3,"密码长度不能少于6位");
            }
            //密码md5加密
            password=CommonUtils.getMd5(password);
            //2、手机号码是否注册过
            //微信是新的手机号码是老
            //rd= wxLogin(unionid,wxmbopenid,country,language,nickname,headimgurl,age,sex,province,city,yunfu);
            //查询用户情况
            //验证是否有这个用户 
            ReturnData  wxrd = this.findWXUnionid(unionid);
            ReturnData rdp=this.checkPhone(phone,1);
            //验证用户名是否唯一
            int ret=1;
            UserInfo puserInfo=null;


            UserInfo userInfo=null;
            //手机和微信都绑定过
            if(wxrd.getRet()==1) {
                userInfo = (UserInfo) wxrd.getResult();
                if(!StringUtils.isBlank(userInfo.getPhone())) {
                    return UserInfo.WX_BINDPHONE_ED;
                }
                //用户隐藏了
                if (userInfo.getHidden() == 1) {
                    return UserInfo.ERRER_USER_OUT;
                }
            }

            if(rdp.getRet()==1) {
                //已经注册过
                //1、微信是否注册过
                puserInfo=(UserInfo) rdp.getResult();
            }
            //1、微信和手机号都注册过
            if(wxrd.getRet()==1&&rdp.getRet()==1) {
                //舍弃手机号码
                int j=userInfoMapper.updateField("hidden", 3, "id", puserInfo.getId());
                userInfo = (UserInfo) wxrd.getResult();
                if (StringUtils.isBlank(userInfo.getWxmbopenid())) {
                    userInfo.setWxmbopenid(wxmbopenid);
                    //更新微信id
                }
                userInfo.setPhone(phone);
                userInfo.setPassword(password);
                ret=1;
                //绑定手机号码
            } else if(wxrd.getRet()==2&&rdp.getRet()==1){
                //2、手机注册过微信没有注册
                //保存微信信息
                userInfo = (UserInfo) rdp.getResult();
                userInfo.setUnionid(unionid);
                userInfo.setWxmbopenid(wxmbopenid);
                ret=2;
            }else if(wxrd.getRet()==1&&rdp.getRet()==2) {
                userInfo = (UserInfo) wxrd.getResult();
                //3、微信注册过 手机没有绑定
                userInfo.setPhone(phone);
                userInfo.setPassword(password);
                ret=3;
            }else {
                //4、新用户
                //用户名过滤

                userInfo=new UserInfo();
                nickname =getAutoNickName(null);
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
                userInfo.setPassword(password);
                ret=4;
            }

            userInfo.setLoginTime(new Date().getTime()/1000);
            userInfo.setUsercode(CommonUtils.getMd5(userInfo.getUnionid()));


            //记录登录时间
            int i=0;
            boolean newUser=false;
            if(userInfo!=null&&userInfo.getId()!=null&&userInfo.getId()>0) {
                i=userInfoMapper.updateOne(userInfo);
            } else {
                i = loginService.register(userInfo);
                newUser=true;
            }
            if(i>0) {
                userInit(userInfo);
                if(newUser) {
                    loginService.mysqlSynMongo(userInfo);
                }
            }
            return UserInfo.wxRegistStauts(userInfo, ret);
        } catch (Exception e) {
            logger.error("method:wxLogin2 arg:{\"unionid\":\"" + unionid + "\",\"wxmbopenid\":\"" + wxmbopenid + "\",\"nickname\":\"" + nickname + "\",\"headimgurl\":\"" + headimgurl + "\",\"age\":" + age + ",\"sex\":" + sex + ",\"province\":\"" + province + "\",\"city\":\"" + city + "\",\"yunfu\":" + yunfu + "}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnData.ERROR;

    }

    /**
     * 微信登录
     *
     * @param unionid    微信id
     * @param wxmbopenid 微信对应的手机端id
     * @param nickname
     * @param headimgurl
     * @param age
     * @param sex
     * @param province
     * @param city
     * @return
     */
    @Deprecated
    public ReturnData wxLogin(String unionid, String wxmbopenid,
                              String country,
                              String language,
                              String nickname, String headimgurl, int age, int sex,
                              String province, String city, int yunfu) {
        try {
            //查询用户情况
            //验证是否有这个用户
            ReturnData rd = this.findWXUnionid(unionid);
            if (rd.getRet() == 1) {
                //更新用户
                UserInfo userInfo = (UserInfo) rd.getResult();
                if (StringUtils.isBlank(userInfo.getWxmbopenid())) {
                    //更新微信id
                    userInfoMapper.updateField("wxmbopenid", wxmbopenid, "unionid", unionid);
                }
                userInit(userInfo);

                ReturnData uu=  this.updateUser(userInfo.getId(), nickname, headimgurl, age, sex, province, city, yunfu);
                if(uu.getRet()==0)
                    userInfoMapper.updateField("logintime", new Date().getTime()/1000, "id", userInfo.getId());
                return uu;
            } else if (rd.getRet() == 2) {
                //验证用户名是否唯一
                ReturnData fn = this.findNickname(nickname);
                if (fn.getRet()!= 2) {
                    return fn;
                }
                //创建用户
                //用户名过滤
                String key="nickname";
                String value=entityService.getConfig(key);
                if(!StringUtils.isBlank(value)){
                    int result=entityService.keywordInfiltration(nickname, value);
                    if(result==-1){
                        return new ReturnData(-2,"昵称中不能出现敏感词汇");
                    }
                }

                UserInfo userInfo = new UserInfo();
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
                userInfo.setUsercode(CommonUtils.getMd5(unionid));
                int i = loginService.register(userInfo);
                if (i > 0) {
                    userInit(userInfo);
                    return new ReturnData(userInfo);
                }
            }
        } catch (Exception e) {
            logger.error("method:wxLogin arg:{\"unionid\":\"" + unionid + "\",\"wxmbopenid\":\"" + wxmbopenid + "\",\"nickname\":\"" + nickname + "\",\"headimgurl\":\"" + headimgurl + "\",\"age\":" + age + ",\"sex\":" + sex + ",\"province\":\"" + province + "\",\"city\":\"" + city + "\",\"yunfu\":" + yunfu + "}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnData.ERROR;

    }


    /**
     * 获取用户信息
     *
     * @param id
     * @return
     */
    public ReturnData findUserinfoByIds(String id) {
        // TODO Auto-generated method stub
        try {
            if(StringUtils.isBlank(id)) return ReturnData.ERROR;
            StringBuffer sb = new StringBuffer();
            //验证数组
            String[] ids = id.split(",");
            for (int i = 0; i < ids.length; i++) {
                if(ids[i]!=null) {
                    Long n = Long.parseLong(ids[i]);
                    if(n!=null)
                        sb.append("," + n);
                }
            }
            id = sb.substring(1);
            if(StringUtils.isBlank(id)) return ReturnData.ERROR;
            List<UserInfo> userInfo = userInfoMapper.findUserinfoByIds(id);
            if (userInfo == null) {
                userInfo = new ArrayList();
            }
            return new ReturnData(userInfo);
        } catch (Exception e) {
            logger.error("method:findUserinfoByIds arg:{\"id\":\"" + id + "\"}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnData.ERROR;
    }


    /**
     * 打开app
     *
     * @param id
     * @return
     */
    public ReturnData openApp(long id) {
        try {
            ReturnData rd = this.getUserById(id);
            if (rd.getRet() == 0) {
                UserInfo userInfo = (UserInfo) rd.getResult();
                if (userInfo.getHidden() == 1) {
                    return UserInfo.ERRER_USER_OUT;
                }

                userInit(userInfo);
                this.userInfoMapper.updateField("logintime", new Date().getTime() / 1000, "id", id);
                return new ReturnData(rd.getResult(),rd.getRet());
            }
            return UserInfo.ERRER_NOT_LOGIN;
        } catch (Exception e) {
            logger.error("method:openApp arg:{\"id\":\"" + id + "\"}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnData.ERROR;

    }

    /**
     * 判断微信是否注册过
     *
     * @param unionid
     * @return
     */
    @Deprecated
    public ReturnData wxCheckUser(String unionid) {
        if (!StringUtils.isBlank(unionid)) {
            ReturnData rd =this.findWXUnionid(unionid);
            //返回用户
            if (rd.getRet() == 1) {
                UserInfo userInfo = (UserInfo) rd.getResult();
                if (userInfo.getHidden() == 1) {
                    return UserInfo.ERRER_USER_OUT;
                }
                ReturnData dr = new ReturnData(userInfo,1);
                userInit(userInfo);
                return dr;
            } else {
                return UserInfo.WX_NOT_REGIST;
            }
        }
        return ReturnData.ERROR;
    }

    /**
     * 判断微信是否注册过
     *
     * @param unionid
     * @return
     */
    @Deprecated
    public ReturnData wxCheckUser2(String unionid) {
        if (!StringUtils.isBlank(unionid)) {
            ReturnData rd =this.findWXUnionid(unionid);
            //返回用户
            if (rd.getRet() == 1) {
                UserInfo userInfo = (UserInfo) rd.getResult();
                //用户隐藏 
                if (userInfo.getHidden() == 1) {
                    return UserInfo.ERRER_USER_OUT;
                }

                //没有本地手机
                if(StringUtils.isBlank(userInfo.getPhone())) {
                    return UserInfo.WX_NOT_BINDPHONE;
                }

                ReturnData dr = new ReturnData(userInfo,1);
                userInit(userInfo);
                return dr;
            } else {
                return UserInfo.WX_NOT_REGIST;
            }
        }
        return ReturnData.ERROR;
    }

    /**
     * 获取用户信息  2.9与之前几个版本使用，3.0版本没用到，后续版本看需求
     *
     * @param id
     * @return
     */
    public ReturnData expert(final String id) {
        return new CacheableTemplate<ReturnData>(cacheProvider) {
            @Override
            protected ReturnData getFromRepository() {
                try {
                    List<UserInfo> userInfo = userInfoMapper.findUserinfoByIds(id);
                    int doyenTotal = userInfoMapper.findDoyenTotalById(id);
                    int findTotal = userInfoMapper.findTotalById(id);
                    ReturnListData EntityList = findByUserId(id, 1, 10);
                    ReturnListData DoyenList = doyenList(id, 1, 10);
                    Map map = new HashMap();
                    map.put("id", userInfo.get(0).getId());
                    map.put("nickname", userInfo.get(0).getNickname());
                    map.put("headimgurl", userInfo.get(0).getHeadimgurl());
                    map.put("desz", userInfo.get(0).getDescz());
                    map.put("doyenTotal", doyenTotal);
                    map.put("findTotal", findTotal);
                    map.put("find", EntityList.getResult());
                    map.put("doyens", DoyenList.getResult());
                    return new ReturnData(map);
                } catch (Exception e) {
                    logger.error("method:expert arg:{\"id\":\"" + id + "\"}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
                    return ReturnData.ERROR;
                }
            }

            @Override
            protected boolean canPutToCache(ReturnData returnValue) {
                return (returnValue != null &&
                        returnValue.getRet() == 0);
            }
        }.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
                "UserService.expert_" + id), true);
    }

    /**
     * 初始化达人页面查询达人文章不带缓存方法
     * @param userId
     * @param pager
     * @param pageSize
     * @return
     */
    public ReturnListData findByUserId(String userId, int pager, int pageSize) {
        try {
            long start = 0;
            if (pager > 1) {
                start = Long.valueOf(((pager - 1) * pageSize) + "");
            } else if (pager < 0) {
                return null;
            }

            List<EntityFind> efs = findMapper.findByUserId(userId, start, pageSize);
            List list = new ArrayList();
            long total = 0;
            if (efs.size() > 0) {
                List<Long> flis = new ArrayList<Long>();
                for (EntityFind f : efs) {
                    if (!org.apache.commons.lang.StringUtils.isBlank(f.getImage())) {
                        f.setPath(f.getImage() + "@30p");
                    }
                    flis.add(f.getId());
                }
                List<EntityBase> ebs = getStates("find", flis);
                for (EntityFind f : efs) {
                    boolean flag = true;
                    for (EntityBase fe : ebs) {
                        if (flag && f.getId().equals(fe.getId())) {
                            flag = false;
                            f.setCommentNum(fe.getCommentNum());
                            f.setHitNum(fe.getHitNum());
                        }
                    }
                }
                total = findMapper.countByUserId(userId);

                for (EntityFind f1 : efs) {
                    Map map=new HashMap();
                    map.put("id",f1.getId());
                    map.put("title",f1.getTitle());
                    map.put("image",f1.getImage());
                    map.put("updateTime",f1.getUpdateStamp());
                    map.put("hitNum",f1.getHitNum());
                    map.put("commentNum",f1.getCommentNum());
                    map.put("imageSrc",f1.getImageSrc());
                    map.put("headerImageSrc",f1.getHeaderImageSrc());
                    list.add(map);
                }
            }
            return new ReturnListData(list, total);
        } catch (Exception e) {
            logger.error("method:entityList arg:{userId:" + userId + ",pager:" + pager + ",pageSize:" + pageSize + "}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
            return ReturnListData.ERROR;
        }
    }

    /**
     *  达人mongo数据查询方法
     * @param tname
     * @param ids
     * @return
     */
    public List<EntityBase> getStates(String tname, List<Long> ids) {
        try {
            String tnameuser = "entity_" + tname;
            List<EntityBase> map = mongoTemplate.find(new Query(Criteria.where("id").in(ids)), EntityBase.class, tnameuser);
            return map;
        } catch (Exception e) {
            logger.error("method:getStates arg:{tname:" + tname + ",id:" + ids + "}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    /**
     * 初始化达人页面达人文章加载带缓存分页方法  2.9与之前几个版本使用，3.0版本没用到，后续版本看需求
     * @param userId
     * @param pager
     * @param pageSize
     * @return
     */
    public ReturnListData findByUserIdCache(final String userId, final int pager, final int pageSize) {
        return new CacheableTemplate<ReturnListData>(cacheProvider) {
            @Override
            protected ReturnListData getFromRepository() {
                return findByUserId(userId, pager, pageSize);
            }

            @Override
            protected boolean canPutToCache(ReturnListData returnValue) {
                return (returnValue != null &&
                        returnValue.getRet() == 0 &&
                        returnValue.getTotal() > 0);
            }
        }.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
                "UserService.entityList_" + userId + "_" + pager + "_" + pageSize), true);
    }

    /**
     * 修行说初始化不带缓存加载
     * @param userId
     * @param pager
     * @param pageSize
     * @return
     */
    public ReturnListData doyenList(String userId, int pager, int pageSize) {
        try {
            long start = 0;
            if (pager > 1) {
                start = Long.valueOf(((pager - 1) * pageSize) + "");
            } else if (pager < 0) {
                return null;
            }

            List<Doyen> efs = doyenMapper.getDoyenById(userId, start, pageSize);
            long total = 0;
            StringBuilder sb = new StringBuilder();
            if (efs.size() > 0) {
                for (int i = 0; i < efs.size(); i++) {
                    if (i == efs.size() - 1) {
                        sb.append(efs.get(i).getGoodsId());
                    } else {
                        sb.append(efs.get(i).getGoodsId());
                        sb.append(",");
                    }
                }
                List<EntityGoods> goods = doyenMapper.findByGoodsIds(sb.toString());
                for (Doyen e : efs) {
                    for (EntityGoods f : goods) {
                        boolean flag = true;
                        if (flag && e.getGoodsId().equals(f.getId())) {
                            flag = false;
                            e.setImage(f.getImage());
                            e.setTitle(f.getTitle());
                            e.setMid(f.getMid());
                        }
                    }
                }
                total = doyenMapper.countByGoodsId(userId);
            }
            return new ReturnListData(efs, total);
        } catch (Exception e) {
            logger.error("method:doyenList arg:{userId:" + userId + ",pager:" + pager + ",pageSize:" + pageSize + "}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
            return ReturnListData.ERROR;
        }
    }

    /**
     * 初始化达人页面修行说缓存分页方法  2.9与之前几个版本使用，3.0版本没用到，后续版本看需求
     * @param userId
     * @param pager
     * @param pageSize
     * @return
     */
    public ReturnListData doyenListCache(final String userId, final int pager, final int pageSize) {
        return new CacheableTemplate<ReturnListData>(cacheProvider) {
            @Override
            protected ReturnListData getFromRepository() {
                return doyenList(userId, pager, pageSize);
            }

            @Override
            protected boolean canPutToCache(ReturnListData returnValue) {
                return (returnValue != null &&
                        returnValue.getRet() == 0 &&
                        returnValue.getTotal() > 0);
            }
        }.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
                "UserService.doyenList_" + userId + "_" + pager + "_" + pageSize), true);
    }


    /**
     * 判断验证码是否有效
     * @param phone
     * @param vcode
     * @param type
     * @return
     */
    public ReturnData isVcode(String phone,String vcode,int type){
        //验证码是否正确
        VerificationCode vc = verificationCodeMapper.getVcode(phone, type);
        if (vc == null) {
            //找不到验证码
            return VerificationCode.VCODE_ERRER;
        }
        //大于10分钟
        long curTime = new Date().getTime() / 1000;
        if (curTime > (vc.getCreateStamp().longValue() + (60 * 10))) {
            //验证码超时
            return VerificationCode.VCODE_ERRER;
        }
        if (!vc.getVcode().equals(vcode)) {
            //验证码错误
            return VerificationCode.VCODE_ERRER;
        }
        return ReturnData.SUCCESS;
    }

    /**
     * 判断验证码是否有效
     * @param phone
     * @param vcode
     * @param type
     * @return
     */
    public ReturnData isVcode2_6(String account,String vcode,int type){
        //验证码是否正确
        //VerificationCodeEntity vc =null;
        VerificationCode vc =null;
        if(account.indexOf("@")==-1){
            //vc=mongoTemplate.findOne(new Query(Criteria.where("phone").is(account).and("type").is(type)).with(new Sort(Direction.DESC,"id")).limit(1), VerificationCodeEntity.class,"verification_code");
            vc = verificationCodeMapper.getVcode(account, type);
        }else{
            //vc=mongoTemplate.findOne(new Query(Criteria.where("email").is(account).and("type").is(type)).with(new Sort(Direction.DESC,"id")).limit(1), VerificationCodeEntity.class,"verification_code");
            vc = verificationCodeMapper.getEmailVcode(account, type);
        }

        if (vc == null) {
            //找不到验证码
            return VerificationCode.VCODE_ERRER;
        }
        //大于10分钟
        long curTime = new Date().getTime() / 1000;
        if (curTime > (vc.getCreateStamp().longValue() + (60 * 10))) {
            //验证码超时
            return VerificationCode.VCODE_ERRER;
        }
        if (!vc.getVcode().equals(vcode)) {
            //验证码错误
            return VerificationCode.VCODE_ERRER;
        }
        return ReturnData.SUCCESS;
    }

    /**
     * 获取用户的最小信息
     * @param userIds
     * @return
     */
    public List<UserInfo> getMongoUserInfos(Collection<Long> userIds) {
        if(userIds!=null&&userIds.size()>0) {
            List<UserInfo> userInfos=mongoTemplate.find(new Query(Criteria.where("id").in(userIds)), UserInfo.class, "user_info");
            return userInfos;
        }
        return null;
    }

    /**
     * 用户信息的赋值
     * @param userMaps
     */
    public void synUserInfo(Map<Long,UserBaseInfo> userMaps) {
        //用户信息赋值
        if(userMaps!=null&&userMaps.size()>0) {
            Collection<Long> userIdss=userMaps.keySet();
            List<UserInfo> userInfos = getMongoUserInfos(userIdss);
            for(int i=0;i<userInfos.size();i++) {
                userMaps.get(userInfos.get(i).getId()).copyUserInfo(userInfos.get(i));
            }

        }
    }

    /**
     * 添加用户地址
     * @param userId
     * @param detail
     * @param province
     * @param city
     * @param district
     * @param phone
     * @return
     */
    public ReturnData addOrUpdateAddress(long userId, String detail, String province,
                                         String city, String district,String phone,String zip,String receiver) {
        try{
            UserAddressInfo uai= new UserAddressInfo();
            boolean flag=false;
            if(StringUtils.isNotBlank(detail)){
                uai.setDetail(detail);
                flag=true;
            }
            if(StringUtils.isNotBlank(province)){
                uai.setProvince(province);
                flag=true;
            }
            if(StringUtils.isNotBlank(city)){
                uai.setCity(city);
                flag=true;
            }
            if(StringUtils.isNotBlank(district)){
                uai.setDistrict(district);
                flag=true;
            }
            if(StringUtils.isNotBlank(phone)){
                uai.setPhone(phone);
                flag=true;
            }
			/*if(StringUtils.isNotBlank(zip)){
				uai.setZip(zip);
				flag=true;
			}else{
				uai.setZip("");
			}*/
            uai.setZip(zip);

            if(StringUtils.isNotBlank(receiver)){
                uai.setReceiver(receiver);
                flag=true;
            }
            Criteria cri=Criteria.where("id").is(userId);
            Query query=new Query(cri);
            UserInfo userInfo=mongoTemplate.findOne(query, UserInfo.class);

            if(null!=userInfo && flag){
                UserAddressInfo[] uais= new UserAddressInfo[]{uai};
				/*if(null!=userInfo.getUserAddressInfos() && userInfo.getUserAddressInfos().length>0){
					//多个地址
					int index=userInfo.getUserAddressInfos().length;
					UserAddressInfo[] uais=userInfo.getUserAddressInfos();
					uais[index]=uai;
					userInfo.setUserAddressInfos(uais);
				}else{
					//当前没有地址
					UserAddressInfo[] uais=new UserAddressInfo[]{};
					uais[0]=uai;
					userInfo.setUserAddressInfos(uais);
				}*/
                mongoTemplate.updateFirst(query, new Update().set("userAddressInfos",uais), UserInfo.class,"user_info");
                return ReturnData.SUCCESS;
            }


        }catch(Exception e){
            logger.error( "desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnData.ERROR;
    }

}


