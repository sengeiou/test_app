package cn.bevol.entity.service.utils;

import cn.bevol.conf.client.ConfClient;
import cn.bevol.entity.service.MessageService;
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
	private final static Map<String,String> stringMap=new HashMap<String,String>();
	public final static String[] imgs = {"https://img0.bevol.cn","https://img1.bevol.cn","https://img1.bevol.cn"};
	public  final static  Integer staticEveryDayNum = confClient.getResourceNum("static_everyday_num")!=null?confClient.getResourceNum("static_everyday_num"):80000;
	public static Map<String,String> mps = initStaticFtl();
	
	private final static Map<String,Integer> numMap=new HashMap<String,Integer>();

	public final static String UPDATE_DIR="upload_dir_";


	/**
	 * 静态化初始化
	 */
	static {

		//实体详情
		stringMap.put(
				"composition",
				"[{'platform':'pc','ftl':'composition.html'},{'platform':'mobile','ftl':'composition.html'},{'platform':'mobile',ftl:'composition_more.html'},{'platform':'pc','ftl':'composition_more.html'}]"
		);
		stringMap.put(
				"goods",
				"[{'platform':'pc','ftl':'goods.html'},{'platform':'mobile','ftl':'goods.html'}]"
		);

		stringMap.put(
				"find",
				"[{'platform':'pc','ftl':'find.html'},,{'platform':'mobile','ftl':'find.html'}]"
		);

		stringMap.put(
				"industry",
				"[{'platform':'pc',ftl:'industry.html'}]"
		);

		//oss秘钥
		stringMap.put("oss_info", "[{'name':'bevol-static','endpoint':'oss-cn-hangzhou.aliyuncs.com','accessKeyId':'lMZwMNFhiR5o1TfO','secretAccessKey':'Qt6ZzNDaoGtwwcHWWGAn1bhdP3qvFC'},"
				+ "{'name':'bevol-source','endpoint':'oss-cn-hangzhou.aliyuncs.com','accessKeyId':'lMZwMNFhiR5o1TfO','secretAccessKey':'Qt6ZzNDaoGtwwcHWWGAn1bhdP3qvFC'},"
				+ "{'name':'m-bevol','endpoint':'oss-cn-hangzhou.aliyuncs.com','accessKeyId':'Kjswdndtlw7PJ4Tj','secretAccessKey':'78f7rXOd0JrycxSEgfU2h5H79uWP2P'},"
				+ "{'name':'bevol-test','endpoint':'oss-cn-shanghai.aliyuncs.com','accessKeyId':'RQgkm7sblqL7arK3','secretAccessKey':'5TG5PmdDo2VYFJuJAYAfHrc8IomL0B'}]"); //同肤质喜欢的产品mongo每次查询的记录数

		stringMap.put("img", "['http://img0.bevol.cn','http://img1.bevol.cn','http://img2.bevol.cn']");

		//只作为默认参数，实际参数可在配置服务里进行配置
		//stringMap.put("composition_index", "{'title':'国内与进口护肤品成分中英文查询-化妆品成分安全分析-成分配方使用频率查询-美丽修行网', 'keywords':'化妆品成分批量查询，护肤品配方批量查询，化妆品原料批量查询，化妆品成分查询，护肤品配方查询，化妆品原料查询，化妆品成分分析，成分查询，化妆品成分安全，成分配方使用频率，配方频率，配方分析，化妆品安全风险查询，护肤品成分检索，化妆品成分大全，护肤品成分查询', 'description':'提供化妆品配方单成分与批量成分查询,支持化妆品成分中文与国际标准英文名称查询，化妆品化学配方使用频率查询，成份安全风险分析'}");
		stringMap.put("wx_topic_info", "{'title':'', 'keywords':'', 'description':''}");
		stringMap.put("wx_topic", "{'title':'', 'keywords':'', 'description':''}");
		stringMap.put("wx_all_composition", "{'title':'', 'keywords':'', 'description':''}");
		stringMap.put("wx_composition", "{'title':'', 'keywords':'', 'description':''}");
		stringMap.put("wx_product", "{'title':'', 'keywords':'', 'description':''}");
		stringMap.put("wx_find", "{'title':'', 'keywords':'', 'description':''}");
		stringMap.put("app_find", "{'title':'', 'keywords':'', 'description':''}");
		stringMap.put("wx_lists", "{'title':'', 'keywords':'', 'description':''}");
		stringMap.put("index", "{'title':'', 'keywords':'', 'description':''}");
		stringMap.put("search", "{'title':'', 'keywords':'', 'description':''}");
		stringMap.put("third_index", "{'title':'', 'keywords':'', 'description':''}");
		stringMap.put("m_find", "{'title':'护肤品成分查询搜索、化妆品分类推荐-美丽修行网', 'keywords':'洁面推荐，化妆水推荐，精华推荐，乳霜推荐，眼霜推荐，面膜推荐，防晒推荐，洗护推荐，美体推荐，美白推荐，彩妆推荐，卸妆推荐，去角质推荐，抗痘推荐，化妆品分类推荐，护肤品成分查询，护肤品查询，化妆品成分查询，成分查询，化妆品安全，cosdna，化妆品成分，成份查询', 'description':'美丽修行妆品分类栏目提供化妆品品牌大全排行榜的分类和成分查询,提供各个分类的化妆品品牌的详细介绍和成分说明'}");
		stringMap.put("m_product", "{'title':'护肤品成分查询搜索、化妆品分类推荐-美丽修行网', 'keywords':'洁面推荐，化妆水推荐，精华推荐，乳霜推荐，眼霜推荐，面膜推荐，防晒推荐，洗护推荐，美体推荐，美白推荐，彩妆推荐，卸妆推荐，去角质推荐，抗痘推荐，化妆品分类推荐，护肤品成分查询，护肤品查询，化妆品成分查询，成分查询，化妆品安全，cosdna，化妆品成分，成份查询', 'description':'美丽修行妆品分类栏目提供化妆品品牌大全排行榜的分类和成分查询,提供各个分类的化妆品品牌的详细介绍和成分说明'}");
		stringMap.put("m_composition", "{'title':'查询护肤品成分-搜索化妆品成分库-美丽修行网', 'keywords':'化妆品成分库，护肤品成分检索，化妆品成分大全，化妆品分类推荐，护肤品成分查询，护肤品查询，化妆品成分查询，成分查询，化妆品安全，cosdna', 'description':'美丽修行妆品分类栏目提供化妆品品牌大全排行榜的分类和成分查询,提供各个分类的化妆品品牌的详细介绍和成分说明'}");
		stringMap.put("m_topic", "{'title':'话题列表', 'keywords':'化妆品成分库，护肤品成分检索，化妆品成分大全，化妆品分类推荐，护肤品成分查询，护肤品查询，化妆品成分查询，成分查询，化妆品安全，cosdna', 'description':'美丽修行妆品分类栏目提供化妆品品牌大全排行榜的分类和成分查询,提供各个分类的化妆品品牌的详细介绍和成分说明'}");
		stringMap.put("pc_index", "{'title':'美丽修行网-为用户提供更好的化妆品、护肤品全成分查询和安全分析', 'keywords':'化妆品查询，护肤品成分查询，护肤品查询，化妆品成分查询，化妆品产品批号，护肤品产品批号，成分查询，全成分分析，化妆品安全，cosdna，化妆品成分分析，化妆品安全，护肤防晒攻略，美丽修行', 'description':'美丽修行网为您提供48万种以上化妆品、护肤品成分权威数据查询，提供中国最全最权威的化妆品与成分数据查询服务，包括洁面、化妆水、精华、乳霜、眼霜、面膜、防晒、洗护等产品批号查询，彩妆教程，美妆心得，护肤指南，最资深的专家用户为您提供权威的产品点评，中国最全的INCI查询、cosdna查询类网站，微信关注美丽修行和下载APP科学安全的保护您的皮肤。'}");
		stringMap.put("pc2_index", "{'title':'美丽修行网-为用户提供更好的化妆品、护肤品全成分查询和安全分析', 'keywords':'化妆品查询，护肤品成分查询，护肤品查询，化妆品成分查询，化妆品产品批号，护肤品产品批号，成分查询，全成分分析，化妆品安全，cosdna，化妆品成分分析，化妆品安全，护肤防晒攻略，美丽修行', 'description':'美丽修行网为您提供48万种以上化妆品、护肤品成分权威数据查询，提供中国最全最权威的化妆品与成分数据查询服务，包括洁面、化妆水、精华、乳霜、眼霜、面膜、防晒、洗护等产品批号查询，彩妆教程，美妆心得，护肤指南，最资深的专家用户为您提供权威的产品点评，中国最全的INCI查询、cosdna查询类网站，微信关注美丽修行和下载APP科学安全的保护您的皮肤。'}");
		stringMap.put("pc_download", "{'title':'化妆品成分查询工具| INCI查询|cosdna查询|美丽修行APP下载', 'keywords':'化妆品成分,化妆品成分查询,INCI查询,cosdna查询,美丽修行APP下载', 'description':'美丽修行APP下载页面,美丽修行是国内最权威的INCI查询,cosdna查询类工具,提供化妆品全成分表的查询,目前有安卓和IOS版本的APP下载地址'}");
		stringMap.put("pc_product", "{'title':'护肤品成分查询搜索、化妆品分类推荐-美丽修行网', 'keywords':'洁面推荐，化妆水推荐，精华推荐，乳霜推荐，眼霜推荐，面膜推荐，防晒推荐，洗护推荐，美体推荐，美白推荐，彩妆推荐，卸妆推荐，去角质推荐，抗痘推荐，化妆品分类推荐，护肤品成分查询，护肤品查询，化妆品成分查询，成分查询，化妆品安全，cosdna，化妆品成分，成份查询', 'description':'美丽修行妆品分类栏目提供化妆品品牌大全排行榜的分类和成分查询,提供各个分类的化妆品品牌的详细介绍和成分说明'}");
		stringMap.put("pc_composition", "{'title':'国内与进口护肤品成分中英文查询-化妆品成分安全分析-成分配方使用频率查询-美丽修行网', 'keywords':'化妆品成分批量查询，护肤品配方批量查询，化妆品原料批量查询，化妆品成分查询，护肤品配方查询，化妆品原料查询，化妆品成分分析，成分查询，化妆品成分安全，成分配方使用频率，配方频率，配方分析，化妆品安全风险查询，护肤品成分检索，化妆品成分大全，护肤品成分查询', 'description':'提供化妆品配方单成分与批量成分查询,支持化妆品成分中文与国际标准英文名称查询，化妆品化学配方使用频率查询，成份安全风险分析'}");
		stringMap.put("pc_find", "{'title':'化妆品教程-美白教程-抗痘心得-护肤品攻略-健康达人分享-面膜专家点评-皮肤保养-美丽修行网', 'keywords':'彩妆教程,美妆心得,化妆品成分分析,化妆品成分功效,化妆品成分安全,彩妆心得,美妆教程', 'description':'美丽修行特约行业彩妆美妆达人分享关于化妆品成分分析,成分功效和成分安全的精品文章推荐和彩妆,美妆的教程和心得的分享'}");
		stringMap.put("pc_industry", "{'title':'化妆品教程-美白教程-抗痘心得-护肤品攻略-健康达人分享-面膜专家点评-皮肤保养-美丽修行网', 'keywords':'彩妆教程,美妆心得,化妆品成分分析,化妆品成分功效,化妆品成分安全,彩妆心得,美妆教程', 'description':'美丽修行特约行业彩妆美妆达人分享关于化妆品成分分析,成分功效和成分安全的精品文章推荐和彩妆,美妆的教程和心得的分享'}");
		stringMap.put("pc_subject_list", "{'title':'化妆品教程-美白教程-抗痘心得-护肤品攻略-健康达人分享-面膜专家点评-皮肤保养-美丽修行网', 'keywords':'彩妆教程,美妆心得,化妆品成分分析,化妆品成分功效,化妆品成分安全,彩妆心得,美妆教程', 'description':'美丽修行特约行业彩妆美妆达人分享关于化妆品成分分析,成分功效和成分安全的精品文章推荐和彩妆,美妆的教程和心得的分享'}");
		stringMap.put("pc_contact", "{'title':'美丽修行APP|美丽修行官网|美丽修行招聘|武汉智美无限信息科技有限公司', 'keywords':'美丽修行官网,美丽修行APP,美丽修行联系方式,美丽修行招聘', 'description':'美丽修行官方网站联系方式和招聘哦,希望加入我们团队的小伙伴请看清楚我们的招聘条件和联系方式赶紧联系我们，没有看到合适岗位的小伙伴也不要灰心哟，随着我们团队的发展后面还有更多的机会。'}");
		stringMap.put("pc_question", "{'title':'化妆品成分知识|化妆品安全常识|美丽修行使用常见问题', 'keywords':'化妆品成分,化妆品安全,化妆品常识,美丽修行怎么用,化妆品常见问题', 'description':'化妆品常见问题大全,美丽修行整理的化妆品成分安全的小知识和一些很多人都希望了解的常识性问题的回答'}");
		stringMap.put("pc_my", "{'title':'化妆品成分|cosdna查询|cosme|品牌大全排行榜|美丽修行app官网', 'keywords':'化妆品成分,化妆品品牌大全,美丽修行,化妆品数据,化妆品排行榜,INCI查询,cosdna查询', 'description':''}");
		stringMap.put("pc_about", "{'title':'化妆品成分|cosdna查询|cosme|品牌大全排行榜|美丽修行app官网', 'keywords':'化妆品成分,化妆品品牌大全,美丽修行,化妆品数据,化妆品排行榜,INCI查询,cosdna查询', 'description':''}");
	}

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

 		//confClient.setResourceString("domain", ".bevol.cn","域名设置");
 		//发送短信
 		//confClient.setResourceNum("sendsms", 1, "是否发送短信");
 		//默认分页条目
 	//	confClient.setResourceNum("defual_pagesize", 10,"默认获取分页数目"); 
 		//confClient.setResourceNum("defual_max_pagesize", 40,"最大获取数目"); 
 		
 		//oss
 		stringMap.put("oss_upload_dir", "{'goods_info':{'oss_name':'bevol-source','domain':'http://source.bevol.cn/','dir':'goods/info/mid/'}}"); //同肤质喜欢的产品mongo每次查询的记录数

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
 		numMap.put(MessageService.SYS_LAST_MSG_TIME, 0); 

 		
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
 		stringMap.put("mysql_to_mongo_goods", "{'mysql_tab':'hq_goods','mongo_tab':'entity_goods','keys':'id:id','replace_fields':'mid:mid,title:title,image:image,alias:alias,hidden:hidden,deleted:deleted','insert':'1','mysql_limit':'300'}"); 
 
 		stringMap.put("mysql_to_mongo_find", "{'mysql_tab':'hq_new_find','mongo_tab':'entity_find','keys':'id:id','replace_fields':'image:image,header_image:headerImage,pc_image:pcImage','insert':'1','mysql_limit':'300'}"); 

 		stringMap.put("mysql_to_mongo_user", "{'mysql_tab':'hq_user','mongo_tab':'user_info','keys':'id:id','replace_fields':'nickname:nickname','insert':'1','mysql_limit':'300'}"); 

 		/**
 		 * mysql同步mongo 全量
 		 */
 		stringMap.put("mysql_to_mongo_composition", "{'mysql_tab':'hq_composition','mongo_tab':'entity_composition','keys':'id:id','replace_fields':'mid:mid,mpid:mpid','insert':'1','mysql_limit':'300'}"); 
 		
 		/**
 		 * mongo同步mysql 全量
 		 */
 		stringMap.put("mongo_to_mysql_find", "{'mysql_tab':'hq_statistics_find','mongo_tab':'entity_find','keys':'entity_id:id','replace_fields':'like_num:likeNum,notlike_num:notLikeNum,hit_num:hitNum,comment_num:commentNum,collection_num:collectionNum','mongo_limit':'300'}"); 

 		
 		stringMap.put("mongo_to_mysql_like", "{'mysql_tab':'hq_like_goods_mong','mongo_tab':'entity_like2_goods_skin','keys':'entityid:entityId','replace_fields':'like_num:likeNum,notlike_num:notlikeNum,comment_sum_score:commentSumScore,comment_num:commentNum,skin:skin,num:num','insert':'1','mysql_limit':'300','mongo_limit':'1000'}"); 

 		/**
 		 *统计插入
 		 */
 		stringMap.put("entity_statistics_to_mysql_goods", "{'is_all':'1','mysql_tab':'hq_goods_search','mysql_tmp_tab':'hq_statistics_goods','mongo_tab':'entity_goods','last_time':'','start_time':'86400','end_time':'123443','syn_num':'11','mongo_limit':'1000','mysql_batch_num':'300'}"); //同肤质喜欢的产品mongo每次查询的记录数

 		
 		stringMap.put("mongo_to_mysql_goodscomment", "{'mysql_tab':'hq_goodscomment','mongo_tab':'entity_goods','keys':'entity_id:id','replace_fields':'like_num:likeNum,notlike_num:notLikeNum,hit_num:hitNum,comment_num:commentNum,comment_sum_score:commentSumScore,comment_content_num:commentContentNum','mongo_limit':'3000'}"); 

 		
 		/**
 		 * mongo同步mysql 全量
 		 */
 		stringMap.put("mongo_to_mysql_lists", "{'mysql_tab':'hq_statistics_lists','mongo_tab':'entity_lists','keys':'entity_id:id','replace_fields':'like_num:likeNum,notlike_num:notLikeNum,hit_num:hitNum,comment_num:commentNum,part_num:partNum','mongo_limit':'300'}"); 
 		/**
 		 * mongo同步mysql 全量
 		 */
 		stringMap.put("mongodb_to_opeansarch_user_part_lists", "{'app_table':'hq_user_part_lists','index_Name':'hq_user_part_lists','monogo_tab':'entity_user_part_lists','last_time':'','start_time':'86400','end_time':'123443','syn_num':'11','mongo_limit':'1000','keys':'id:id','replace_fields':'id:id,hidden:hidden,title:title,image:image,likeNum:like_num,notLikeNum:notlike_num,hitNum:hit_num,commentNum:comment_num,userId:user_id,userBaseInfo.nickname:nickname,userBaseInfo.headimgurl:headimgurl,tags:tags','opensearch_batch_num':'300'}"); //同肤质喜欢的产品mongo每次查询的记录数

 		stringMap.put("mongodb_to_opeansarch_user_part_lists_artile", "{'app_table':'hq_entity','index_Name':'article_search','isall':'0','monogo_tab':'entity_user_part_lists','last_time':'','start_time':'86400','end_time':'123443','syn_num':'11','mongo_limit':'1000','keys':'id:id','replace_fields':'uniqueId:unique_id,id:id,hidden:hidden,title:title,image:image,userId:author_id,userBaseInfo.nickname:author,userBaseInfo.headimgurl:author_image,tags:tag_ids,tname:tname,userPartDetails_str:descp','opensearch_batch_num':'300'}"); //同肤质喜欢的产品mongo每次查询的记录数

 		/**
 		 * mongo同步mysql 全量 composition
 		 */
 		stringMap.put("mongo_to_mysql_composition", "{'mysql_tab':'hq_statistics_composition','mongo_tab':'entity_composition','keys':'entity_id:id','replace_fields':'like_num:likeNum,notlike_num:notLikeNum,hit_num:hitNum,comment_num:commentNum','mongo_limit':'300'}"); 

 		
 		/**
 		 *  		 *每日注册人数
 		 */
 		numMap.put("register_day_num",0);

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
	private static String compositionNameFilter(String str) {
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
	
	/**
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

	/***
	 * 初始化FTL静态资源
	 * @return
	 */
	private static Map<String,String> initStaticFtl(){
		Map<String,String> map = new HashMap<String, String>();
		String pc_js="";
		String pc_css="" ;
		String wx_js="";
		String wx_css="" ;
		String url= confClient.getResourceString("url");
		String ApiUrlHttps= confClient.getResourceString("api_url_https");
		String searchUrl= confClient.getResourceString("search_url");
		String version = confClient.getResourceString("static_version");
		String skinType = confClient.getResourceString("skin_type");
		pc_js= confClient.getResourceString("pc_js");
		pc_css= confClient.getResourceString("pc_css");
		wx_js= confClient.getResourceString("wx_js");
		wx_css= confClient.getResourceString("wx_css");

		map.put("pc_domain", "https://www.bevol.cn");
		map.put("m_domain", "https://m.bevol.cn");
		map.put("pc_js", pc_js);
		map.put("pc_css", pc_css);
		map.put("wx_js", wx_js);
		map.put("wx_css", wx_css);
		map.put("url", url);
		map.put("api_url_https", ApiUrlHttps);
		map.put("searchUrl", searchUrl);
		map.put("version", version);
		map.put("skinType", skinType);

		return map;
	}

}
