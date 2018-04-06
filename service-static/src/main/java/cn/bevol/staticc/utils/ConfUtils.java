package cn.bevol.staticc.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bevol.conf.client.ConfClient;
import flexjson.JSONDeserializer;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

/***
 * 配置服务工具类
 * @author admin
 *
 */
public class ConfUtils {
	private final static ConfClient confClient=new ConfClient("api");
	private final static Map<String,String> stringMap=new HashMap<String,String>();
	public final static String[] imgs = {"https://img0.bevol.cn","https://img1.bevol.cn","https://img2.bevol.cn"};
	public  final static  Integer staticEveryDayNum = confClient.getResourceNum("static_everyday_num")!=null?confClient.getResourceNum("static_everyday_num"):80000;
	public static Map<String,String> mps = initStaticFtl();

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
	
	/**
	 * 获取资源值
	 * @param key
	 * @return
	 */
	public static String getResourceString(String key){
		//所有带数字的版本转化成不带数字的
	    key = key.replaceAll("\\d+", "");
		String val= confClient.getResourceString(key);
		if(val==null) val=stringMap.get(key);
		return val;
	}



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
	public static Map<String,String>  getMap(String key,String subKey){
		Map<String,Map<String,String>>   json=getMap(key);
		Map<String,String> map = json.get(key);
		JSONObject jsonO =  JSONObject.fromObject(map);
		if(json!=null) {
			return json.get(subKey);
		}
		return null;
	}
	
	/**
	 * 获取一个json数组 
	 * @param key
	 * @return
	 */
	public static List<Map<String,String>>  getList(String key){
		String  json= stringMap.get(key);
		List<Map<String,String>> jsonNode=  new JSONDeserializer<List<Map<String,String>>>().deserialize(json, ArrayList.class);
		return jsonNode;
	}

	public static Map<String, String> getEntityInfo(String key){
        String  json=getResourceString(key);
        Map<String, String> jsonNode=  new JSONDeserializer<Map<String, String>>().deserialize(json, HashMap.class);
        return jsonNode;
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

	/**
	 * 图片路径结构
	 * @author Administrator
	 *
	 */
	static class ImageEntity{
		String path;

		String defImage;
		public ImageEntity() {

		}
		public ImageEntity(String path, String defImage) {
			super();
			this.path = path;
			this.defImage = defImage;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}
		public String getDefImage() {
			return defImage;
		}
		public void setDefImage(String defImage) {
			this.defImage = defImage;
		}

	}

	/**
	 * 获取图片域名
	 * @return
	 */
	public static String getImageDomain() {
		/*java.util.Random random=new java.util.Random();// 定义随机类
        List<Map<String, String>> los=ConfUtils.getList("img_domain");
		int result=random.nextInt(los.size());// 返回[0,10)集合中的整数，注意不包括10
		return los.get(result).get("domain");*/
		java.util.Random random=new java.util.Random();// 定义随机类
		int result=random.nextInt(2);
		return "https://img"+result+".bevol.cn";
	}

	/**
	 * 拼装图片路径
	 * @param tname key
	 * @param image 图片名称
	 * @return
	 */
	public static  String getImageSrc(String tname,String image){
		String domain=getImageDomain();
		ImageEntity ie= new ImageEntity(tname, "");
		String path=ie.getPath();
		String defimg=ie.getDefImage();
		if(StringUtils.isBlank(image)) {
			//默认图片
			if(StringUtils.isBlank(defimg)) {
				//最终默认图片
				return null;
			}else {
				return domain+"/"+defimg;
			}
		}
		return domain+"/"+path+"/"+image;
	}
}
