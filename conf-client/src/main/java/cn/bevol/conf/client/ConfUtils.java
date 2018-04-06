package cn.bevol.conf.client;

import flexjson.JSONDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 配置文件管理 
 * @author hualong
 *
 */
public class ConfUtils {

	
	private final static ConfClient confClient=new ConfClient("api");
	
	private final static Map<String,Integer> numMap=new HashMap<String,Integer>();

	private final static Map<String,String> stringMap=new HashMap<String,String>();
	
	public final static String UPDATE_DIR="upload_dir_";
	
	

 	static {
 		//初始化资源
 		stringMap.put("domain", "localhost");
 		//发送短信
 		numMap.put("sendsms", 0); 
 		//默认分页条目
 		numMap.put("defual_pagesize", 10); 
 		numMap.put("skinlikegoods_mongo_find_limit", 10000); //同肤质喜欢的产品mongo每次查询的记录数
 		numMap.put("skinlikegoods_mysql_batch_num", 1000); //同肤质喜欢的产品每次插入到mysql中的记录数

 		
 		
 		numMap.put("mangeUserId", 248660); //管理员id
 		
 		numMap.put("cookie_max_time", 1000*60*24*30); //管理员id
 		

 		numMap.put("write_log", 0);
 		
 		//子评论显示数量
 		numMap.put("subcomment_count", 1); 
 		//发送内容最小时间间隔
 		numMap.put("send_min_time", 30); 

 		stringMap.put("upload_dir_feedback", "bevol-static");  //反馈图片目录
 		stringMap.put("upload_dir_Goods/userupload", "bevol-static"); //同肤质喜欢的产品mongo每次查询的记录数
 		stringMap.put("upload_dir_comment/images", "bevol-static");  //反馈图片目录
 		stringMap.put("upload_dir_UploadFile/head", "bevol-static");  //反馈图片目录
 		
 		stringMap.put("upload_dir_user_part/lists", "bevol-static");  //心得
 		
 		stringMap.put("upload_dir_user_skin_protection/images", "bevol-static");  //用户肤质方案 
 		
 		//oss秘钥
 		stringMap.put("oss_info", "[{'name':'bevol-static','endpoint':'oss-cn-hangzhou.aliyuncs.com','accessKeyId':'lMZwMNFhiR5o1TfO','secretAccessKey':'Qt6ZzNDaoGtwwcHWWGAn1bhdP3qvFC'},{'name':'bevol-source','endpoint':'oss-cn-hangzhou.aliyuncs.com','accessKeyId':'lMZwMNFhiR5o1TfO','secretAccessKey':'Qt6ZzNDaoGtwwcHWWGAn1bhdP3qvFC'}]"); //同肤质喜欢的产品mongo每次查询的记录数

 		
 		
 		stringMap.put("aliyun_iacs", "{'regionId':'cn-shanghai','accessKeyId':'lMZwMNFhiR5o1TfO','accessKeySecret':'Qt6ZzNDaoGtwwcHWWGAn1bhdP3qvFC'}"); 

 		//confClient.setResourceString("domain", ".bevol.cn","域名设置");
 		//发送短信
 		//confClient.setResourceNum("sendsms", 1, "是否发送短信");
 		//默认分页条目
 	//	confClient.setResourceNum("defual_pagesize", 10,"默认获取分页数目"); 
 		//confClient.setResourceNum("defual_max_pagesize", 40,"最大获取数目"); 
 		
 		//oss
 		stringMap.put("oss_upload_dir", "{'goods_info':{'oss_name':'bevol-source','domain':'http://source.bevol.cn/','dir':'goodsCalculate/info/mid/'}}"); //同肤质喜欢的产品mongo每次查询的记录数

 		//微信登录秘钥
 		stringMap.put("wx_login_key", "{'pc':{'APPID':'wx55456de48120a75f','APPSECRET':'5e51f7203e2cb686571e823afa8117a9','REDIRECT_URI':'http://passport.bevol.cn/wx/pc/login/callback'}}"); 
 		stringMap.put("pc_domain", "http://t.bevol.cn"); //同肤质喜欢的产品mongo每次查询的记录数
 		
 		/**
 		 * 产品的cps信息
 		 * 成分顺序说明cps_sort_desc
 		 * 
 		 */
 		stringMap.put("goods_info", "{'cps_sort_desc':{'def_cps_1':'产品配方顺序','cps_def_2':'产品配方顺序','def_cps_3':'产品备案顺序','gc_cps':'标签顺序','mfj_cps':'标签顺序','def':'标签顺序'},'skin_goods_category_desc':{'3':'防晒解读','2':'清洁解读','1':'功效说','def':'功效说'}}"); 
 		
 		/**
 		 * 成分名称过滤
 		 */
 		stringMap.put("composition_name_match", "{'replace_after':['*','.','。'],'replace_space':['organic','*','　','。','.'],'replace_common':{},'replace_chs_char':{'!':'！','\\'':'’','`':'`','(':'（',')':'）','.':'。',',':'，',';':'；',']':'】',':':'：','\':'、','}':'}','|':'|','{':'{','[':'【'}}"); 
 		
 		stringMap.put("email_info", "{'host_info':[{'host_name':'smtp.exmail.qq.com','auth_name':'service_1@bevol.cn','auth_pass':'1209xxYX!^','from_eamil':'service_1@bevol.cn'},{'host_name':'smtp.exmail.qq.com','auth_name':'service_2@bevol.cn','auth_pass':'1209xxYX!^','from_eamil':'service_2@bevol.cn'}]}"); 
 		
 		/**
 		 * 邮件发送文案
 		 */
 		stringMap.put("email_vcode_type", "{'vcode_type':{'0':'尊敬的用户，您好：<br>您已选择email为您的注册邮箱， 为验证此电子邮件地址属于您，请在您的电子邮件验证页面输入下方验证码：<br>vcode<br><br>感谢您对于美丽修行的支持，我们会一直做到更好。<br>此致<br>美丽修行，最懂安全和功效的护肤神器','4':'尊敬的用户，您好：<br>您已选择email为您的注册邮箱， 为验证此电子邮件地址属于您，请在您的电子邮件验证页面输入下方验证码：<br>vcode<br><br>感谢您对于美丽修行的支持，我们会一直做到更好。<br>此致<br>美丽修行，最懂安全和功效的护肤神器','1':'尊敬的用户，您好：<br>您试图通过以绑定邮箱email来重新设置您的账号密码， 为验证此电子邮件地址属于您，请在您的电子邮件验证页面输入下方验证码：<br>vcode<br><br>感谢您对于美丽修行的支持，我们会一直做到更好。<br>此致<br>美丽修行，最懂安全和功效的护肤神器','3':'尊敬的用户，您好：<br>您已选择email为您的绑定邮箱， 为验证此电子邮件地址属于您，请在您的电子邮件验证页面输入下方验证码：<br>vcode<br><br>感谢您对于美丽修行的支持，我们会一直做到更好。<br>此致<br>美丽修行，最懂安全和功效的护肤神器'}}"); 
 		
 		/**
 		 * 消息发送控制
 		 * msg_common_user_time= 默认用户接受消息的时间段 5天内 (60 * 60 * 24) * 5
 		 * msg_new_user_time=新用户拉消息的时间 1天 (60 * 60 * 24) 
 		 */
 		stringMap.put("msg_send", "{'get_msg':{'msg_common_user_time':'432000','msg_new_user_time':'86400'}}"); //同肤质喜欢的产品mongo每次查询的记录数
 	
 		/**
 		 * 最新一条系统消息发送时间 默认0
 		 */
 		//numMap.put(MessageService.SYS_LAST_MSG_TIME, 0);

 		
 		//实体最后同步opensearch时间

 		stringMap.put("entity_statistics_to_opeansarch_find", "{'app_table':'hq_statistics_find','index_Name':'hq_find','monogo_tab':'entity_find','last_time':'','start_time':'86400','end_time':'123443','syn_num':'11','mongo_limit':'1000','opensearch_batch_num':'300'}"); //同肤质喜欢的产品mongo每次查询的记录数
 		
 		
 		/**
 		 * 分享文案
 		 */
 		stringMap.put("share_type", "shareUnique"); 

 		
 		/**
 		 * 图片路径
 		 */
 		stringMap.put("img_domain", "[{'domain':'https://img0.bevol.cn'},{'domain':'https://img1.bevol.cn'}]"); 

 		/**
 		 * mysql同步mongo 全量
 		 */
 		stringMap.put("mysql_to_mongo_goods", "{'mysql_tab':'hq_goods','mongo_tab':'entity_goods','keys':'id:id','replace_fields':'mid:mid,title:title,image:image,alias:alias','insert':'1','mysql_limit':'300'}"); 
 
 		stringMap.put("mysql_to_mongo_find", "{'mysql_tab':'hq_new_find','mongo_tab':'entity_find','keys':'id:id','replace_fields':'image:image,header_image:headerImage,pc_image:pcImage','insert':'1','mysql_limit':'300'}"); 

 		/**
 		 * mysql同步mongo 全量
 		 */
 		stringMap.put("mysql_to_mongo_composition", "{'mysql_tab':'hq_composition','mongo_tab':'entity_composition','keys':'id:id','replace_fields':'mid:mid,mpid:mpid','insert':'1','mysql_limit':'300'}"); 
 		
 		/**
 		 * mongo同步mysql 全量
 		 */
 		stringMap.put("mongo_to_mysql_find", "{'mysql_tab':'hq_statistics_find','mongo_tab':'entity_find','keys':'entity_id:id','replace_fields':'like_num:likeNum,notlike_num:notLikeNum,hit_num:hitNum,comment_num:commentNum','mongo_limit':'300'}"); 

 		/**
 		 *统计插入
 		 */
 		stringMap.put("entity_statistics_to_mysql_goods", "{'is_all':'1','mysql_tab':'hq_goods_search','mysql_tmp_tab':'hq_statistics_goods','mongo_tab':'entity_goods','last_time':'','start_time':'86400','end_time':'123443','syn_num':'11','mongo_limit':'1000','mysql_batch_num':'300'}"); //同肤质喜欢的产品mongo每次查询的记录数

 		
 		/**
 		 * mongo同步mysql 全量
 		 */
 		stringMap.put("mongo_to_mysql_lists", "{'mysql_tab':'hq_statistics_lists','mongo_tab':'entity_lists','keys':'entity_id:id','replace_fields':'like_num:likeNum,notlike_num:notLikeNum,hit_num:hitNum,comment_num:commentNum,part_num:partNum','mongo_limit':'300'}"); 
 		/**
 		 * mongo同步mysql 全量
 		 */
 		stringMap.put("mongodb_to_opeansarch_user_part_lists", "{'app_table':'hq_user_part_lists','index_Name':'hq_user_part_lists','monogo_tab':'entity_user_part_lists','last_time':'','start_time':'86400','end_time':'123443','syn_num':'11','mongo_limit':'1000','keys':'id:id','replace_fields':'id:id,hidden:hidden,title:title,image:image,likeNum:like_num,notLikeNum:notlike_num,hitNum:hit_num,commentNum:comment_num','opensearch_batch_num':'300'}"); //同肤质喜欢的产品mongo每次查询的记录数

 		/**
 		 * mongo同步mysql 全量 composition
 		 */
 		stringMap.put("mongo_to_mysql_composition", "{'mysql_tab':'hq_statistics_composition','mongo_tab':'entity_composition','keys':'entity_id:id','replace_fields':'like_num:likeNum,notlike_num:notLikeNum,hit_num:hitNum,comment_num:commentNum','mongo_limit':'300'}"); 

 		/**
 		 * 实名认证开关(open:0开 1关),是否强制实名(mandatory:0强制,1非强制),每天每人实名认证的最大次数(maxNum)
 		 */
 		stringMap.put("user_authentication", "{'open':'1','mandatory':'1','uuidMaxNum':'3'}"); 
 		
 		/**
 		 * 手机认证开关(open:0开 1关),是否强制实名(mandatory:0强制,1非强制),每天每人实名认证的最大次数(maxNum)
 		 */
 		stringMap.put("user_switchOfPhone", "{'open':'0','mandatory':'0','uuidMaxNum':'3'}");
 		
 		/**
 		 * 实名认证ip拦截
 		 */
 		stringMap.put("interceptor_user_authentication_ip", "{'ip':'0:0:0:0:0:0:0:4,0:0:0:0:0:0:0:2,0:0:0:0:0:0:0:3'}"); 

 		/**
 		 * 实名认证的测试环境(1为测试,其它数值为正式)
 		 */
 		numMap.put("user_authentication_test",1);
 		/**
 		 * 手机认证的测试环境(1为测试,其他数值为正式)
 		 * 
 		 */
 		numMap.put("user_switchOfPhone_test",0);
 		/**
 		 * 短信key
 		 */
 		stringMap.put("verifyidcard_info", "{ 'url':'http://aliyunverifyidcard.haoservice.com/idcard/VerifyIdcardv2?cardNo=${cardNo}&realName=${realName}','appcode':'17093df3c7fa4140b44d8ccf6a3a0670','success_field':'result.isok=true','idcard_field':'IdCardInfor'}"); 

 		/**  
 		 * 实名认证与旧的发送评论/心得等接口,为1则旧的发送接口实名关闭,0为开启
 		 */
 		numMap.put("user_authentication_old_send",1);
 		
 		/**  
 		 * 手机认证与旧的发送评论/心得等接口,为1则旧的发送接口实名关闭,0为开启
 		 */
 		numMap.put("user_switchOfPhone_old_send",0);
 		
 		/**
 		 * 每天用户可以获取的验证码的次数,超过则弹出图片验证码
 		 */
 		numMap.put("day_vcode_maxNum",3);
 		
 		/**
 		 *  		 *每日注册人数
 		 */
 		numMap.put("register_day_num",0);
 		
 		//发送内容最小时间间隔
 		numMap.put("compareGoods_like_send_min_time", 1); 
 		
 	}
 	
 	
	/**
	 * 获取资源值
	 * @param key
	 * @return
	 */
	public static String getResourceString(String key){
		String val= confClient.getResourceString(key);
		if(StringUtils.isBlank(val)) val=stringMap.get(key);
		return val;
	}
	
	/**
	 * 获取资源
	 * @param key
	 * @return
	 */
	public static Integer getResourceNum(String key){
		Integer num=confClient.getResourceNum(key);
		if(num==null) num=numMap.get(key);
		return num;
	}
	
	/**
	 * 设置配置信息int
	 * @param key val
	 * @return
	 */
	public static boolean setResourceNum(String key,int val){
		if(confClient!=null) {
			 if(confClient.setResourceNum(key,val)) {
				return true; 
			 }
		}
			numMap.put(key,val);
		return true;
	}
	
	/**
	 * 设置配置信息string
	 * @param key val
	 * @return
	 */
	public static boolean setResourceString(String key,String val){
		if(confClient!=null&&confClient.setResourceString(key,val)) {
			return true;
		} else {
			stringMap.put(key,val);
		}
		return true;
	}


	
	private static  Map<String,String> rp=new HashMap<String,String>();
	private static Map<String,String> bidi = new HashMap<String,String>();
	static{
		//String domain=	getResourceString("domain");
		//1、通用匹配
		rp.put("organic", "");
		rp.put("*", "");
		rp.put("　", " ");
		rp.put("。", "");
		rp.put(".", "");
 		//2、k、v 都匹配 中英文
		 bidi.put("{", "{");
		 bidi.put("}", "}");
		 bidi.put("[", "【");
		 bidi.put("]", "】");
		 
		 bidi.put("(", "（");
		 bidi.put(")", "）");
		 bidi.put("`", "`");
		 bidi.put("!", "！");
		 bidi.put("\\", "、");
		 bidi.put("|", "|");
		 bidi.put(",", "，");
		 bidi.put(".", "。");
		 bidi.put(";", "；");
		 
		 bidi.put("'", "‘");
		 bidi.put(":", "：");
		 bidi.put("'", "’");
	}
	public static void main(String[] args) {


		 
		String ad="organicorganicorganic cdd  0d　*　--【[";
		
	    System.out.println(compositionNameFilter(ad));
	}
	/**
	 * 成分过滤
	 * @param str
	 * @return
	 */
	public static String compositionNameFilter(String str) {
		String sp=str;
		
		//
		boolean ischs=isChineseChar(sp);
		String key1="composition_name_match";
		Map<String,Object> jsonNode=  new JSONDeserializer<Map<String,Object>>().deserialize(ConfUtils.getResourceString(key1), HashMap.class);
		//空格匹配规则
		List<String> replaceSpaceList=(List<String>) jsonNode.get("replace_space");
		//中英文匹配规则
		Map<String,String> replaceChsChar=(Map<String,String>) jsonNode.get("replace_chs_char");
		//其他匹配规则
		Map<String,String> replaceCommon=(Map<String,String>) jsonNode.get("replace_common");
		
		List<String> replaceAfter=(List<String>) jsonNode.get("replace_after");
		for(String a:replaceAfter)
			sp=StringUtils.substringBefore(sp, a);

		if(replaceChsChar!=null)
		for(String key : replaceChsChar.keySet()){
			if(ischs) {
				//英文换中文
				sp=StringUtils.replace(sp, key, replaceChsChar.get(key));
			} else {
				//中文换英文
				sp=StringUtils.replace(sp, replaceChsChar.get(key), key);
			}
		}
		
		//通用替换
		if(replaceCommon!=null&&!replaceCommon.isEmpty())
		for(String key : replaceCommon.keySet()){
			sp=StringUtils.replace(sp, key, replaceCommon.get(key));
		}

		//空格替换
		if(replaceSpaceList!=null)
		for(String key : replaceSpaceList){
			sp=StringUtils.replace(sp, key, "");
		}
		//2、公共处理
		sp=sp.replaceAll(" +"," ");
		sp=sp.trim();
		
		
		return sp;
	}
	/*
	 * 验证是否包含中文字符
	 */
	public static boolean isChineseChar(String str){
	       boolean temp = false;
	       Pattern p=Pattern.compile("[\u4e00-\u9fa5]"); 
	       Matcher m=p.matcher(str); 
	       if(m.find()){ 
	           temp =  true;
	       }
	       return temp;
	 }

	/**
	 * 获取上传目录
	 * @param key
	 * @return
	 */
	public static String getUploadBk(String key){
		return getResourceString(UPDATE_DIR+key);
	}
	
	
	/**
	 * 获取一个json数组 
	 * @param key
	 * @return
	 */
	public static List<Map<String,String>>  getList(String key){
		String  json=getResourceString(key);
		List<Map<String,String>> jsonNode=  new JSONDeserializer<List<Map<String,String>>>().deserialize(json, ArrayList.class);
		return jsonNode;
	}
	
	
	
	
	/**
	 * 获取一个json map
	 * @param key
	 * @return
	 */
	public static Map<String,String>  getJSONMap(String key){
		String  json=getResourceString(key);
		Map<String,String> jsonNode=  new JSONDeserializer<Map<String,String>>().deserialize(json, HashMap.class);
		return jsonNode;
	}

	/**
	 * 获取一个json map
	 * @param key
	 * @return
	 */
	public static Map<String,Map<String,String>>  getMap(String key){
		String  json=getResourceString(key);
		Map<String,Map<String,String>> jsonNode=  new JSONDeserializer<Map<String,Map<String,String>>>().deserialize(json, HashMap.class);
		return jsonNode;
	}
	
	/**
	 * 获取一个json map
	 * @param key
	 * @return
	 */
	public static List<Map<String,String>>  getList(String key,String subkey){
		String  json=getResourceString(key);
		Map<String,Object> jsonNode=  new JSONDeserializer<Map<String,Object>>().deserialize(json, HashMap.class);
		if(jsonNode.get(subkey)!=null) {
			List<Map<String,String>> lmaps=(List<Map<String, String>>) jsonNode.get(subkey);
			return lmaps;
		}
		return null;
	}

 
	/**
	 * 获取一个json map
	 * @param key
	 * @return
	 */
	public static Map<String,String>  getMap(String key,String subKey){
		Map<String,Map<String,String>>   json=getMap(key);
		if(json!=null) {
			return json.get(subKey);
		}
		return null;
	}
 
 

}
