package cn.bevol.app.service;

import cn.bevol.app.cache.redis.RedisCacheProvider;
import cn.bevol.app.dao.mapper.UserInfoOldMapper;
import cn.bevol.model.user.UserInfo;
import cn.bevol.util.CommonUtils;
import cn.bevol.util.Log.LogException;
import cn.bevol.util.MD5Utils;
import cn.bevol.util.response.ReturnData;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MyService extends BaseService {
    private static Logger logger = LoggerFactory.getLogger(MyService.class);

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    GoodsService goodsService;

    @Autowired
	RedisCacheProvider cacheProvider;

    @Autowired
    private UserService userService;
    
    
     
    @Autowired
    private UserInfoOldMapper userInfoMapper;

    @Resource
    private LoginService loginService;


	/**
	 *  用户改绑账号
	 * 如果该账号不存在就可以绑定
	 * @param userInfo
	 * @param new_account: 账号
	 * @param vcode: 验证码
	 * @return
	 */
	public ReturnData modifyAccount(UserInfo userInfo, String new_account, String vcode) {
		try{
			int result=-1;
			UserInfo UserInfoByAccount=null;
			String phone="";
			String email="";
			ReturnData rd=null;
			if(null!=userInfo && !StringUtils.isBlank(new_account) && !StringUtils.isBlank(vcode)){
				//检验账号格式和账号是否被注册
				if(new_account.indexOf("@")==-1){
					rd=userService.checkPhone(new_account);
					phone=new_account;
					userInfo.setPhone(phone);
				}else{
					rd=userService.checkEmail(new_account);
					email=new_account;
					userInfo.setEmail(email);
				}
			}
			
			//要绑定的账号不存在  验证码正确  就可以修改   
			if(null!=rd && rd.getRet()==2){
				ReturnData rd2=userService.isVcode2_6(new_account, vcode, 4);
				if(rd2.getRet()!=0) {return rd2;};
				//修改
				result=userInfoMapper.updateUserInfo2_6(userInfo);
			}else{
				return rd;
			}
			if(result==1){
				UserInfo resultUserInfo=null;
				//查找刚刚绑定的账号,检验是否绑定成功
				if(!StringUtils.isBlank(phone)){
					resultUserInfo=userInfoMapper.findFeild("phone", phone);
				}else{
					resultUserInfo=userInfoMapper.findFeild("email", email);
				}
				if(null!=resultUserInfo){
					//同步mongo的用户信息
					loginService.mysqlSynMongo(resultUserInfo);
					return new ReturnData(resultUserInfo);
				}else{
					return UserInfo.ERRER_REST_ACCOUNT;
				}
			}else{
				return ReturnData.FAILURE;
			}
		}catch(Exception e){
			Map map = new HashMap();
			map.put("method", "MessageService.modifyAccount");
			map.put("new_account", new_account);
			map.put("vcode", vcode);
			map.put("userId", userInfo.getId());
			new LogException(e, map);
		}
		return ReturnData.ERROR;
	}
	
	/**
	 * 修改密码
	 * 先把 old_password 和 new_password加密 再对比密码是否相同
	 * @param userInfo
	 * @param old_password: 旧的密码
	 * @param newPassword: 新的密码
	 * @return
	 */
	public ReturnData modifyPassword(UserInfo userInfo, String old_password, String newPassword) {
		if(null!=userInfo && userInfo.getId()!=0 && !StringUtils.isBlank(old_password) && !StringUtils.isBlank(newPassword)){
			//查找用户
			UserInfo userInfoById = userInfoMapper.findFeild2_6("id", userInfo.getId());
			//该用户存在   旧密码正确  新密码符合规则  新旧密码不能相同  ---可以update密码
			if(null!=userInfoById){
				//加密对比
				if(!userInfoById.getPassword().equals(MD5Utils.encode(old_password))){
					return UserInfo.ERRER_PASSWORD;
				}
				//密码长度
	            if (StringUtils.isBlank(newPassword) || newPassword.length() < 6) {
	                return UserInfo.ERRER_LENGTH_PASSWORD;
	            }
	            if(!MD5Utils.encode(newPassword).equals(MD5Utils.encode(old_password))){
	            	//更新密码
	            	int result=userInfoMapper.updatePasswordById(userInfo.getId(),MD5Utils.encode(newPassword));
	            	//清空密码
	                userInfo.setPassword("");
	                //更新登录时间
	                userInfoMapper.updateField("logintime", new Date().getTime() / 1000, "id", userInfo.getId());
	                userService.userInit(userInfo);
	                return new ReturnData(userInfo);
	            }else{
	            	return UserInfo.ERRER_MODIFY_PASSWORD;
	            }
			}
		}
		return ReturnData.ERROR;
	}
/**
 * 身份实名验证
 * @return
 */
	public ReturnData proofOfIdentiy(String realName, String idCard,UserInfo userInfo,String uuid) {
		idCard = idCard.trim();
		realName = realName.trim();

 	try {
		//判断是否已经认证通过
		UserInfo NuserInfo=new UserInfo();
		NuserInfo=mongoTemplate.findOne(new Query(Criteria.where("id").is(userInfo.getId())), UserInfo.class,"user_info");
		Boolean verifyState;
		 verifyState=NuserInfo.getVerifyState();
		if(verifyState!=null&&verifyState==true){
			return new ReturnData(-7,"该用户已进行过验证");
		}
		//如果用户已经实名制验证就不让了
		
		// 身份证为15位则拼接成18位
				if (idCard.length() == 15) {
					String temp = idCard.substring(0, 6) + "19" + idCard.substring(6, 15) + "X";
				}
				if (realName.equals("0") || idCard.equals("0")) {
					return new ReturnData(-2,"输入信息格式有误");
				}
				// 非18位为假
				if (idCard.length() != 18) {
					return new ReturnData(-2,"输入信息格式有误");
				}
				// 前17位全部为数字
				String idcard17 = idCard.substring(0, 17);
				Pattern pattern = Pattern.compile("[0-9]*");
				Matcher isNum = pattern.matcher(idcard17);
				if (!isNum.matches()) {
					return new ReturnData(-2,"输入信息格式有误");
				}  
				// 检验省份
				Boolean isFalse = true;
				String cityCode[] = { "11", "12", "13", "14", "15", "21", "22", "23", "31", "32", "33", "34", "35", "36", "37",
						"41", "42", "43", "44", "45", "46", "50", "51", "52", "53", "54", "61", "62", "63", "64", "65", "71",
						"81", "82", "91" };
				String provinceid = idCard.substring(0, 2);
				for (String citycode : cityCode) {
					if (provinceid.equals(citycode)) {
						isFalse = false;
					}
				}
				if (isFalse) {
					return new ReturnData(-2,"输入信息格式有误");
				}
				// 校验出生日期
				String birthday = idCard.substring(6, 14);
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
					Date birthDate = sdf.parse(birthday);
					String tmpDate = sdf.format(birthDate);
					if (!tmpDate.equals(birthday)) {// 出生年月日不正确
						return new ReturnData(-2,"输入信息格式有误");
					}
				} catch (java.text.ParseException e) {
					e.printStackTrace();
				}

				/**
				 * 若为15位身份证 则不验证最后一位验证码
				 */
				if (idCard.length() != 15) {
					// 每位加权因子
					int power[] = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
					int sum = 0;
					// 获取第18位
					String idcard18Code = idCard.substring(17, 18);
					char c[] = idcard17.toCharArray();
					for (int i = 0; i < c.length; i++) {
						int b = Integer.parseInt(c[i] + "");
						sum = sum + power[i] * b;
					}
					int num = sum % 11;
					String reg[] = { "1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2" };
					String str = reg[num];
					if (!idcard18Code.equals(str)) {
						return new ReturnData(-2,"输入信息格式有误");
					}
				}
				
				UserInfo u=mongoTemplate.findOne(new Query(Criteria.where("idCard").is(idCard).and("realName").is(realName)), UserInfo.class,"user_info");
				if(u!=null){
					return new ReturnData(-8,"该身份证号已被绑定过");
				}
				
				//接口验证如果通过 将认证信息设置到实体类中
				Long id=userInfo.getId();
				Map idCardInfor=new HashMap();
				//idCardInfor.put("real_name", realName);
				//idCardInfor.put("id_card", idCard);
				//userInfo.setRealName(realName);
				//userInfo.setIdCard(idCard);
				
				Map detail= CommonUtils.verifyIdCard(idCard, realName);
				//不管验证是否通过 只要调用了验证的接口就记录
				if(StringUtils.isNotBlank(uuid)){
					long cuTime=new Date().getTime()/1000;
	            	mongoTemplate.findAndModify(new Query(Criteria.where("uuid").is(uuid).and("updateStamp").lte(CommonUtils.curDayMaxAndMinTime(0)).gte(CommonUtils.curDayMaxAndMinTime(1))), new Update().set("updateStamp", cuTime).inc("total", 1),new FindAndModifyOptions().returnNew(true).upsert(true), HashMap.class,"user_authentication");
				}
				
				if(detail!=null) {
					Boolean verifyState1=true;
					Long verifyStamp= new Date().getTime() / 1000; 
					Query query=new Query(Criteria.where("id").is(id));
					Update update= Update.update("realName", realName).set("idCard", idCard).set("verifyState", verifyState1).set("verifyStamp", verifyStamp).set("IdCardInfor", detail);
					mongoTemplate.updateFirst(query, update, "user_info");
					return new ReturnData(0,"认证通过");
				} else {
					return new ReturnData(-3,"验证接口无法通过");
				}
		} catch(Exception e) {
        	Map map = new HashMap();
			map.put("method", "MyService.proofOfIdentiy");
			map.put("realName",realName);
			map.put("idCard", idCard);
			map.put("uuid", uuid);
			new LogException(e, map);
            return ReturnData.ERROR;
		}
				
				//如果信息有误 
				//return ReturnData(-3,"输入信息有误");
	}

 }
