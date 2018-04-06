package cn.bevol.search.web;

import cn.bevol.search.config.EnvironmentConfig;
import cn.bevol.util.client.AdClient;
import cn.bevol.util.response.ReturnData;
import com.aliyun.opensearch.CloudsearchClient;
import flexjson.JSONSerializer;
import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

@Controller
@RequestMapping({"/search", "/"})
public class IndexController extends BaseController{
	//环境配置
	//public final static String model="test_";
	//public final static String model="";

	@Autowired
	EnvironmentConfig environmentConfig;

	 private static Logger logger = LoggerFactory.getLogger(IndexController.class);
	@Autowired
	CloudsearchClient client;
	
	@RequestMapping(value="/index")
	@ResponseBody
	public Map<String, Object> goods(){
		return this.defaultAjax();
	}
	
	/**
	 * 无数据的情况
	 * @return
	 */
	private Map<String,Object> notDta() {
		Map data=new HashMap();
		Map d2=new HashMap();
		d2.put("items", new ArrayList());
		d2.put("rows", 0);
		d2.put("total", 0);
		data.put("data", d2);
		return data;
	}
	
 	/**
	 * 自动 补全
	 * @param index 索引名称
	 * @param keywords 关键字
	 * @param rows 记录数
	 * @return
	 */
	@RequestMapping(value={"/autocomplete/{index}/{suggestName}"})
	@ResponseBody
	public ReturnData autoComplete(@PathVariable String index, @PathVariable String suggestName, @RequestParam String keywords, @RequestParam(defaultValue="10",required=false) int rows){
		try {
			keywords=URLDecoder.decode(keywords, "utf-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ReturnData error = ReturnData.ERROR;
			return error;
		};
		return super.autoComplete(index,suggestName, keywords, rows);
	}
	
	@RequestMapping(value={"/index/list"})
	@ResponseBody
	public ReturnData index(HttpServletRequest request,@RequestParam String keywords){
		int  rows=3;
		Map m=new HashMap();
		//话题
		m.put("lists", this.lists(request, keywords,1,rows).get("data"));
		//心得
		m.put("userpart", this.userpart(request, keywords,1,rows).get("data"));
		//发现
		m.put("find", this.findlist(request, keywords, null, -1, "", "", "", 1,rows).get("data"));
		//产品
		m.put("goods", this.goods3(request, keywords, "", "", "","", "", "", 0, 0, 1, rows,null,false).get("data"));
		//成分
		m.put("compostion", this.compositions(request, keywords, 1, rows).get("data"));
		return new ReturnData<>(m);
	}
	
	
	
	/**
	 * 话题搜索
	 * @param request
	 * @param keywords
	 * @param p
	 * @param rows
	 * @return
	 */
	@RequestMapping(value={"/lists/index"})
	@ResponseBody
	public Map<String, Object> lists(HttpServletRequest request,@RequestParam String keywords,
			@RequestParam(defaultValue="0") int p,
			@RequestParam(defaultValue="20") int rows){
		Long reqStartTime=System.currentTimeMillis();
		String appName =  environmentConfig.getModel()+"hq_lists";
		//String appName =  "hq_lists";
		if(StringUtils.isBlank(keywords)) {
			return notDta();
		}
		
		try {
			keywords=URLDecoder.decode(keywords, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		String outkeywords="hide_status:'0' AND title:'"+keywords+"'"; 
		long time=new Date().getTime()/1000;
		String sort="sort=+sort;-publish_time";
		String quertyString=outkeywords+"&&filter=publish_time<"+time+"&&"+sort;
			Map map= this.returnAjax(appName, quertyString, p, rows);
			Map data=(Map) map.get("data");
			this.createImageSrc(appName, data, "image","mini_image");
			outLog(request,reqStartTime, data,"lists", outkeywords, p, rows);
		return map;
	}

	
	/**
	 * 心得
	 * @param request
	 * @param keywords
	 * @param p
	 * @param rows
	 * @return
	 */
	@RequestMapping(value={"/userpart/index"})
	@ResponseBody
	public Map<String, Object> userpart(HttpServletRequest request, @RequestParam String keywords, @RequestParam(defaultValue="0") int p, @RequestParam(defaultValue="20") int rows){
		String appName =  environmentConfig.getModel()+"hq_user_part_lists";
		if(StringUtils.isBlank(keywords)) {
			return notDta();
		}
		Long reqStartTime=System.currentTimeMillis();

		try {
			keywords=URLDecoder.decode(keywords, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		String outkeywords="hidden:'0' AND title:'"+keywords+"'"; 
		String sort="sort=+sort;-hit_num;-id";
		String quertyString=outkeywords+"&&"+sort;
		Map map= this.returnAjax(appName, quertyString, p, rows);
		Map data=(Map) map.get("data");
		this.createImageSrc(appName, data, "image");
		 outLog(request,reqStartTime, data,"userpart", outkeywords, p, rows);
		return map;
	}



	
	@RequestMapping(value={"/goods/index"})
	@ResponseBody
	public Map<String, Object> goods(HttpServletRequest request, @RequestParam String keywords, @RequestParam(defaultValue="",required=false) String cps, @RequestParam(defaultValue="",required=false) String notcps, @RequestParam(defaultValue="0",required=false) int category, @RequestParam(defaultValue="1") int p, @RequestParam(defaultValue="20",required=false) int rows){
		String appName =  "new_bevol_hq_goods";
		try {
			keywords=URLDecoder.decode(keywords, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		Long reqStartTime=System.currentTimeMillis();

		Map<String,String> query=new HashMap<String,String>();
		String outkeywords="";//"hidden:'0'";
		if(!StringUtils.isBlank(keywords)) {
			if(keywords.indexOf("洗面")!=-1){
				String keywd=keywords;
				keywd=keywd.replaceAll("洗面","洁面");
				keywords="title:'"+keywords+"' OR title:'"+keywd+"' OR alias:'"+keywords+"' OR alias:'"+keywd+"' OR remark:'"+keywords+"' OR remark:'"+keywd+"' OR alias_2:'"+keywords+"' OR alias_2:'"+keywd+"'";
			} else {
				keywords="title:'"+keywords+"' OR alias:'"+keywords+"' OR alias_2:'"+keywords+"' OR remark:'"+keywords+"'";
			}
			outkeywords="("+keywords+")";
		}  
		if(category>0) {
			if(!StringUtils.isBlank(outkeywords)) {
				outkeywords+=" AND ( category:'"+category+"' )";
			} else {
				outkeywords+="  ( category:'"+category+"' )";
			}
		} 		
		if(!StringUtils.isBlank(outkeywords))
			outkeywords+=" hidden:'0' AND flag:'1' AND deleted:'0'  AND "+outkeywords;
		else
			outkeywords+=" hidden:'0' AND flag:'1' AND deleted:'0'  ";
		query.put("query", outkeywords);
		List<String> filter=new ArrayList<String>();
		if(!StringUtils.isBlank(notcps)) {
			StringBuffer sb=new StringBuffer();
			String cpss[]=notcps.split(",");
			for(int i=0;i<cpss.length;i++) {
				sb.append(" cps!= "+cpss[i]+" ");
				if(i!=cpss.length-1) {
					sb.append(" AND ");
				}
			}
			filter.add("("+sb.toString()+")");
		}
		
		if(!StringUtils.isBlank(cps)) {
			StringBuffer sb=new StringBuffer();
			String cpss[]=cps.split(",");
			for(int i=0;i<cpss.length;i++) {
				sb.append(" cps= "+cpss[i]+" ");
				if(i!=cpss.length-1) {
					sb.append(" AND ");
				}
			}
			filter.add("("+sb.toString()+")");
		}
		query.put("filter", StringUtils.join(filter, "AND"));
		String quertyString="";
		for(String q:query.keySet()) {
			if(!StringUtils.isBlank(query.get(q))){
				if(q.equals("query"))  {
					quertyString+="&&"+query.get(q)+"";
				} else {
					quertyString+="&&"+q+"="+query.get(q)+"";
				}
			}
		}
		if(!StringUtils.isBlank(quertyString)){
			quertyString=quertyString.substring(2);
		}
		 quertyString+="&&sort=-hot;-hit";
		Map m=this.returnAjax(appName, quertyString, p, rows,0);
		Map data=(Map) m.get("data");
		outLog(request,reqStartTime, data,"goods", quertyString, p, rows);
		return m; 
	}
    public static final String FIELD_USER_ID = "uid";
    public static final String FIELD_UUID = "uuid";
    public static final String FIELD_PLATFORM = "platform";
    public static final String FIELD_MODEL = "model";
    public static final String FIELD_VERSION = "version";

    public static final String FIELD_TNAME = "tname";
    public static final String FIELD_KEYWORDS = "keywords";

    public static final String KEY_USER_ID = "uid";
    public static final String KEY_UUID = "uuid";
    public static final String KEY_PLATFORM = "o";
    public static final String KEY_MODEL = "model";
    public static final String KEY_VERSION = "v";
    public static final String KEY_SYS_V = "sys_v";
    public static final String KEY_CHANEL = "channel";
    public static final String KEY_IP = "ip";

	private void outLog(HttpServletRequest request,Long reqStartTime,Map data,String table,String outkeywords,int p,int rows) {
		Map out=new HashMap();
		int its=((List)data.get("items")).size();
		out.put("keywords", outkeywords);
		out.put("inp", p);
		out.put("inrow", rows);
		out.put("table", table);
		out.put("total", data.get("total"));
		out.put("uri", request.getRequestURI().toString());
		out.put("url", request.getRequestURL().toString());
        String uuid = request.getParameter(KEY_UUID);
        String machineModel = request.getParameter(KEY_MODEL) == null ? "" : request.getParameter(KEY_MODEL);
        String platform = request.getParameter(KEY_PLATFORM);
        String version = request.getParameter(KEY_VERSION) ;
        String sys_v = request.getParameter(KEY_SYS_V);
        String channel = request.getParameter(KEY_CHANEL);
    	if(!org.apache.commons.lang3.StringUtils.isBlank(uuid))
        out.put(FIELD_UUID, uuid);
    	if(!org.apache.commons.lang3.StringUtils.isBlank(version))
    		out.put(FIELD_VERSION, version.toLowerCase());
    	if(!org.apache.commons.lang3.StringUtils.isBlank(platform))
    	out.put(FIELD_PLATFORM, platform.toLowerCase());
    	if(!org.apache.commons.lang3.StringUtils.isBlank(machineModel))
    	out.put(FIELD_MODEL, machineModel.toLowerCase());
    	if(!org.apache.commons.lang3.StringUtils.isBlank(sys_v))
    	out.put(KEY_SYS_V, sys_v.toLowerCase());
    	if(!org.apache.commons.lang3.StringUtils.isBlank(channel))
    		out.put(KEY_CHANEL, channel.toLowerCase());
		out.put(KEY_IP, getIpAddr(request));

		//请求结束时间
		long reqEndTime=new Date().getTime();
		// 请求占用时间
		long reqTime=reqEndTime-reqStartTime;
		out.put("req_start_time", reqStartTime);
		out.put("req_end_time", reqEndTime);
		out.put("req_exc_time", reqTime);
		Map<String, String[]> map =request.getParameterMap();

		for (Map.Entry<String, String[]> entry : map.entrySet()) {
	        String[] value=new String[1];
	        if(entry.getValue() instanceof String[]){
	            value=(String[])entry.getValue();
	        }else{
	            value[0]=entry.getValue().toString();
	        }
	        String str=null;
			try {
				str=URLDecoder.decode(value[0], "utf-8");
				str=str.trim();
				out.put("in_"+entry.getKey(),str);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
		}
    	//cookies
  	  Cookie[] cookies = request.getCookies();
  	  if(null!=cookies){
  	    for(Cookie cookie : cookies){
  	    	if(cookie!=null&& StringUtils.isNotBlank(cookie.getName())) {
  		 		String encryptData=cookie.getValue();
  			 		//String encryptData="QTxrR1Nl4Ng5vNtVALQKGvGP9%2FGgZJtj";
  					if(encryptData.indexOf("%")!=-1) {
  				 		try {
  				 			encryptData = URLDecoder.decode(encryptData,   "utf-8");
  				 		} catch (UnsupportedEncodingException e) {
  				 			// TODO Auto-generated catch block
  				 			e.printStackTrace();
  				 		}
  					}
  					out.put("cookie_"+cookie.getName(), encryptData);
  			    }
  	    	}
  	  }

		String strjson=new JSONSerializer().deepSerialize(out);
		logger.info(strjson);
	}
	public static String getIpAddr(HttpServletRequest request) {

		String ip = request.getHeader("x-forwarded-for");

		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {

		ip = request.getHeader("Proxy-Client-IP");

		}

		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {

		ip = request.getHeader("WL-Proxy-Client-IP");

		}

		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {

		ip = request.getRemoteAddr();

		}

		return ip;

		}


	
	@RequestMapping(value={"/composition/index"})
	@ResponseBody
	public Map<String, Object> compositions(HttpServletRequest request, @RequestParam String keywords, @RequestParam(defaultValue="0") int p, @RequestParam(defaultValue="20") int rows){
		String appName =  environmentConfig.getModel()+"hq_bevol_composition_1";
		if(StringUtils.isBlank(keywords)) {
			return notDta();
		}
		Long reqStartTime=System.currentTimeMillis();

		try {
			keywords=URLDecoder.decode(keywords, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		String outkeywords=keywords; 
		 keywords=" (name:'"+keywords+"' OR english:'"+keywords+"' OR other_title:'"+keywords+"') AND pid:'0'";
	     String quertyString =keywords+"&&sort=+namelength;-top;-hot;-sorting";
			Map map= this.returnAjax(appName, quertyString, p, rows);
			Map data=(Map) map.get("data");
			outLog(request,reqStartTime, data,"composition", outkeywords, p, rows);
			return map;
	}

	@RequestMapping(value={"/composition/goodslists"})
	@ResponseBody
	public Map<String, Object> goodslists(HttpServletRequest request, @RequestParam(defaultValue="") String compositionid, @RequestParam(defaultValue="0") int p, @RequestParam(defaultValue="20") int rows){
		
		Long reqStartTime=System.currentTimeMillis();
		String appName =  "new_bevol_hq_goods";
		String outkeywords=compositionid;
		String keywords="cps:'"+compositionid+"'";
		String quertyString=keywords+" AND hidden:'0' AND flag:'1' AND deleted:'0'&&sort=-hot;-hit";
		Map map= this.returnAjax(appName, quertyString, p, rows,0);
		Map data=(Map) map.get("data");
		if(data!=null) {
			List<Map<String,Object>> list=(List<Map<String,Object>>) data.get("items");
			for(int i=0;i<list.size();i++) {
				Map m=list.get(i);
				m.put("mgoods_id", m.get("mid"));
				m.put("goods_id", m.get("id"));
				
			}
		}
		outLog(request,reqStartTime, data,"goods_composition", outkeywords, p, rows);
		return map;
	}
	
	@RequestMapping(value={"/composition/goodslists/compositionid/{compositionid}/p/{p}/rows/{rows}"})
	@ResponseBody
	public Map<String, Object> goodslists2(HttpServletRequest request, @PathVariable String compositionid, @PathVariable int p, @PathVariable int rows){
		Map resutl=this.goods3(request,  "", "",  "","", compositionid+"", "", "", 0, 0, p, rows,null, true);
		Map data=(Map)resutl.get("data");
		if(data!=null&&data.get("items")!=null) {
			List<Map<String,Object>> list=(List<Map<String, Object>>) data.get("items");
			for(int i=0;i<list.size();i++) {
				list.get(i).put("mgoods_id", list.get(i).get("mid"));
				list.get(i).put("goods_id", list.get(i).get("id"));
			}
		}
		return resutl;
	}

	/**
	 * 发现搜索
	 * @param request
	 * @param type
	 * @param tag
	 * @param skin
	 * @param p
	 * @param rows
	 * @return
	 */
	@RequestMapping(value={"/find/index"})
	@ResponseBody
	public Map<String, Object> gs(HttpServletRequest request, @RequestParam(defaultValue="-1") int type, @RequestParam(defaultValue="") String tag, @RequestParam(defaultValue="") String skin, @RequestParam(defaultValue="1") int p, @RequestParam(defaultValue="20") int rows){
		Long reqStartTime=System.currentTimeMillis();
		try {
			tag=URLDecoder.decode(tag, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};

		String appName =  "hq_find";
 		String keywords="hidden:'0'";
		if(type>0) {
			keywords=keywords+" AND type:"+type;
		}
		if(!StringUtils.isBlank(tag)) {
			String tagss[]=tag.split(",");
			String ts="";
			for(int i=0;i<tagss.length;i++) {
				ts+=" OR tag:'"+tagss[i]+"'";
			}
			ts=ts.substring(4);
			keywords+="AND ("+ts+")";
		}
		
		if(!StringUtils.isBlank(skin)) {
 			String tagss[]=skin.split(",");
			String ts="";
			for(int i=0;i<tagss.length;i++) {
				ts+=" OR skin:'"+tagss[i]+"'";
			}
			ts=ts.substring(4);
			keywords+=" AND ("+ts+")";
		}
		long time=new Date().getTime()/1000;
		String quertyString=keywords+"&&filter=publish_time<"+time+"&&sort=-crdate";
		Map map=this.returnAjax(appName, quertyString, p, rows);
		Map data=(Map) map.get("data");
		if(data!=null&&data.get("items")!=null) {
			List<Map<String,Object>> list=(List<Map<String, Object>>) data.get("items");
			for(int i=0;i<list.size();i++) {
				String image=(String) list.get(i).get("image");
				list.get(i).put("path", image+"@60p");
			}
		}
		outLog(request,reqStartTime, data,"find", quertyString,0, rows);
		return map; 
	}
	
	/**
	 * 发现搜索
	 * @param request
	 * @param title 标题
	 * @param type 类型
	 * @param tag 标签
	 * @param skin 肤质 
	 * @param sort 默认 是时间排序 如果有标题就是按照查看数量排序
	 * @param p
	 * @param rows
	 * @return
	 */ 
	@RequestMapping(value={"/find/list"})
	@ResponseBody
	public Map<String, Object> findlist(HttpServletRequest request,
                                        @RequestParam(defaultValue="",required=false) String title,
                                        @RequestParam(defaultValue="",required=false) String keywords,
                                        @RequestParam(defaultValue="-1",required=false) int type,
                                        @RequestParam(defaultValue="",required=false) String tag,
                                        @RequestParam(defaultValue="",required=false) String skin
			, @RequestParam(defaultValue="",required=false) String sort,
                                        @RequestParam(defaultValue="1") int p, @RequestParam(defaultValue="20") int rows){
 		String appName =  environmentConfig.getModel()+"hq_find";
		Long reqStartTime=System.currentTimeMillis();

 		String querys="hidden:'0'";
		if(type>0) {
			querys=querys+" AND type:'"+type+"'";
		}
		if(!StringUtils.isBlank(keywords)) {
			querys=querys+" AND (kewords:'"+keywords+"' OR tag:'"+keywords+"')";
			if(StringUtils.isBlank(sort)) sort="hit_num";
		}
		
		if(title!=null&&!title.trim().equals("")) {
			querys=querys+" AND title:'"+title.trim()+"'";
			if(StringUtils.isBlank(sort)) sort="hit_num";
		}
		if(!StringUtils.isBlank(tag)) {
			String tagss[]=tag.split(",");
			String ts="";
			for(int i=0;i<tagss.length;i++) {
				ts+=" OR tag:'"+tagss[i]+"'";
			}
			ts=ts.substring(4);
			querys+="AND ("+ts+")";
		}
		
		if(!StringUtils.isBlank(skin)) {
 			String tagss[]=skin.split(",");
			String ts="";
			for(int i=0;i<tagss.length;i++) {
				ts+=" OR skin:'"+tagss[i]+"'";
			}
			ts=ts.substring(4);
			querys+=" AND ("+ts+")";
		}
		String quertyString="";
		long time=new Date().getTime()/1000;
		if(!StringUtils.isBlank(sort)&&sortFields.get(sort)!=null) {
			quertyString=querys+"&&filter=publish_time<"+time+"&&sort=-"+sort;
		}else {
			quertyString=querys+"&&filter=publish_time<"+time+"&&sort=+sort;-crdate";
		}
		Map m=this.returnAjax(appName, quertyString, p, rows,0);
		Map data=(Map) m.get("data");
		this.createImageSrc(appName, data, "image","header_image","pc_image");
		outLog(request,reqStartTime, data,"find", quertyString, p, rows);
		return m; 
	}
	
 	
	/**
	 * 
	 * @param request
	 * @param keywords
	 * @param sort -hit_num 查看数（默认） -like_num 喜欢数  -safety_1_num星级排序
	 * @param sort_1	用户喜欢度排序
	 * @param tag_ids 功效id
	 * @param cps
	 * @param notcps
	 * @param category
	 * @param p
	 * @param rows
	 * @return
	 */
	@RequestMapping(value={"/goods/index2"})
	@ResponseBody
	public Map<String, Object> goods2(HttpServletRequest request,
			@RequestParam(defaultValue="",required=false) String keywords,
			@RequestParam(defaultValue="-hit_num") String sort,
			@RequestParam(defaultValue="",required=false) String sort_1,
			@RequestParam(defaultValue="",required=false) String tag_ids,
			@RequestParam(defaultValue="",required=false) String cps,
			@RequestParam(defaultValue="",required=false) String notcps,
			@RequestParam(defaultValue="",required=false) String rule_cps_ids,
			@RequestParam(defaultValue="0",required=false) int category,
			@RequestParam(defaultValue="1") int p,
			@RequestParam(defaultValue="20",required=false) int rows){
		String appName =  "goods_search";
		Long reqStartTime=System.currentTimeMillis();
		try {
			keywords=URLDecoder.decode(keywords, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		//分类或者产品名称存在
		if(StringUtils.isBlank(keywords)&&category==0) {
			return notDta();
		}
		
		StringBuffer query=new StringBuffer();
		query.append("hidden:'0'");
		if(!StringUtils.isBlank(keywords)) {
			if(keywords.indexOf("洗面")!=-1){
				String keywd=keywords;
				keywd=keywd.replaceAll("洗面","洁面");
				keywords="title:'"+keywords+"' OR title:'"+keywd+"' OR alias:'"+keywords+"' OR alias:'"+keywd+"' OR remark:'"+keywords+"' OR remark:'"+keywd+"' OR alias_2:'"+keywords+"' OR alias_2:'"+keywd+"'";
			} else {
				keywords="title:'"+keywords+"' OR alias:'"+keywords+"' OR alias_2:'"+keywords+"' OR remark:'"+keywords+"'";
			}
			query.append("AND ").append("("+keywords+") ");
		}  
		if(category>0) {
			query.append("AND ").append("( category:'"+category+"' )  ");
		}

		if(!StringUtils.isBlank(cps)) {
			String nt=cps.substring(cps.length()-1);
			if(nt.equals(",")) {
				cps=cps.substring(0,cps.length()-1);
			}
			query.append("AND ").append("(cps_search:'"+ StringUtils.replace(cps, ",", "' AND  cps_search:'")+"') ");
		}
		
		

		
		if(!StringUtils.isBlank(tag_ids)) {
			String nt=tag_ids.substring(tag_ids.length()-1);
			if(nt.equals(",")) {
				tag_ids=tag_ids.substring(0,tag_ids.length()-1);
			}
				query.append("AND ").append(" (tag_ids:'"+ StringUtils.replace(tag_ids, ",", "' OR  tag_ids:'")+"') ");
		}
		//逗号替换为 AND 
		if(!StringUtils.isBlank(notcps)) {
			String nt=notcps.substring(notcps.length()-1);
			if(nt.equals(",")) {
				notcps=notcps.substring(0,notcps.length()-1);
			}
			if(notcps.indexOf(",")!=-1) {
				query.append("ANDNOT ").append(" cps_search:'"+ StringUtils.replace(notcps, ",", "' ANDNOT cps_search:'")+"'  ");
			} else {
					query.append("ANDNOT  cps_search:'"+notcps+"'  ");
			}
		}

		//不含成分组的
		if(!StringUtils.isBlank(rule_cps_ids)) {
			String nt=rule_cps_ids.substring(rule_cps_ids.length()-1);
			if(nt.equals(",")) {
				rule_cps_ids=rule_cps_ids.substring(0,rule_cps_ids.length()-1);
			}
			if(rule_cps_ids.indexOf(",")!=-1) {
				query.append("ANDNOT ").append(" rule_cps_ids:'"+ StringUtils.replace(rule_cps_ids, ",", "' ANDNOT rule_cps_ids:'")+"'  ");
			} else {
					query.append("ANDNOT  rule_cps_ids:'"+rule_cps_ids+"'  ");
			}
		}
		

		String st="";
		if(sort!=null)
			st=sort.substring(1);
		if(sortFields.get(st)==null) {
			sort="";
		}  
		//第一种排序
		if(!sort.equals("-hit_num"))
			sort+=";-hit_num";

		//第二种排序
		if(!StringUtils.isBlank(sort_1)) {
			if(sort_1!=null)
				 st=sort_1.substring(1);
			if(sortFields.get(st)!=null) {
				sort+=";"+sort_1;
			}
		}

		
		String quertyString=query+"&&filter=flag=1 AND deleted=0&&sort=+hidden_skin;"+sort;
		Map m=this.returnAjax(appName, quertyString, p, rows,0);
		Map data=(Map) m.get("data");
		outLog(request,reqStartTime, data,"goods", quertyString, p, rows);
		return m; 
	}
	
	private static Map<String,String> sortFields=new HashMap<String,String>();
	static{
		sortFields.put("like_num", "like_num");
		sortFields.put("hit_num", "hit_num");
		sortFields.put("safety_1_num", "safety_1_num");
		sortFields.put("comment_num", "comment_num");
		sortFields.put("grade", "grade");
	}

	/**
	 * 品牌限制
	 */
	private static Map<String,String> brands=new HashMap<String,String>();
	static{
		brands.put("荟诗", "1576");
	}

	public static void main(String[] args){

    }


	/**
	 * 
	 * @param request
	 * @param keywords
	 * @param sort -hit_num 查看数（默认） -like_num 喜欢数  -safety_1_num星级排序 -comment_num评论数
	 * @param sort_1	第二搜索排序条件
	 * @param tag_ids 功效id
	 * @param cps
	 * @param notcps
	 * @param category
	 * @param position 请求的位置,index keywords
	 * @param p
	 * @param rows
	 * @return
	 */
	@RequestMapping(value={"/goods/index3"})
	@ResponseBody
	public Map<String, Object> goods3(HttpServletRequest request,
			@RequestParam(defaultValue="",required=false) String keywords,
			@RequestParam(defaultValue="-hit_num") String sort,
			@RequestParam(defaultValue="",required=false) String sort_1,
			@RequestParam(defaultValue="",required=false) String tag_ids,
			@RequestParam(defaultValue="",required=false) String cps,
			@RequestParam(defaultValue="",required=false) String notcps,
			@RequestParam(defaultValue="",required=false) String rule_cps_ids,
			@RequestParam(defaultValue="0",required=false) int category,
			@RequestParam(defaultValue="0",required=false) int p_category,
			@RequestParam(defaultValue="1") int p,
			@RequestParam(defaultValue="20",required=false) int rows,
			@RequestParam(required=false) String position,
			@RequestParam(defaultValue="true") boolean ad
			){
		
		try {
			keywords=URLDecoder.decode(keywords, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		Long reqStartTime=System.currentTimeMillis();

		String appName =  environmentConfig.getModel()+"goods_search";
		//String appName =  "goods_search";
 		//分类或者产品名称存在
		StringBuffer query=new StringBuffer();
		query.append("hidden:'0'");
		if(!StringUtils.isBlank(keywords)) {
			if(keywords.indexOf("洗面")!=-1){
				String keywd=keywords;
				keywd=keywd.replaceAll("洗面","洁面");
				keywords="title:'"+keywords+"' OR title:'"+keywd+"' OR alias:'"+keywords+"' OR alias:'"+keywd+"' OR remark:'"+keywords+"' OR remark:'"+keywd+"' OR alias_2:'"+keywords+"' OR alias_2:'"+keywd+"'";
			} else {
				keywords="title:'"+keywords+"' OR alias:'"+keywords+"' OR alias_2:'"+keywords+"' OR remark:'"+keywords+"'";
			}
			query.append("AND ").append("("+keywords+") ");
		}  
		if(category>0) {
			query.append("AND ").append("( category:'"+category+"' )  ");
		}
		
		if(p_category>0) {
			query.append("AND ").append("( p_category:'"+p_category+"' )  ");
		}


		if(!StringUtils.isBlank(cps)) {
			String nt=cps.substring(cps.length()-1);
			if(nt.equals(",")) {
				cps=cps.substring(0,cps.length()-1);
			}
			query.append("AND ").append("(cps_search:'"+ StringUtils.replace(cps, ",", "' AND  cps_search:'")+"') ");
		}
		
		

		
		if(!StringUtils.isBlank(tag_ids)) {
			String nt=tag_ids.substring(tag_ids.length()-1);
			if(nt.equals(",")) {
				tag_ids=tag_ids.substring(0,tag_ids.length()-1);
			}
				query.append("AND ").append(" (tag_ids:'"+ StringUtils.replace(tag_ids, ",", "' OR  tag_ids:'")+"') ");
		}
		//逗号替换为 AND 
		if(!StringUtils.isBlank(notcps)) {
			String nt=notcps.substring(notcps.length()-1);
			if(nt.equals(",")) {
				notcps=notcps.substring(0,notcps.length()-1);
			}
			if(notcps.indexOf(",")!=-1) {
				query.append("ANDNOT ").append(" cps_search:'"+ StringUtils.replace(notcps, ",", "' ANDNOT cps_search:'")+"'  ");
			} else {
					query.append("ANDNOT  cps_search:'"+notcps+"'  ");
			}
		}

		//不含成分组的
		if(!StringUtils.isBlank(rule_cps_ids)) {
			String nt=rule_cps_ids.substring(rule_cps_ids.length()-1);
			if(nt.equals(",")) {
				rule_cps_ids=rule_cps_ids.substring(0,rule_cps_ids.length()-1);
			}
			if(rule_cps_ids.indexOf(",")!=-1) {
				query.append("ANDNOT ").append(" rule_cps_ids:'"+ StringUtils.replace(rule_cps_ids, ",", "' ANDNOT rule_cps_ids:'")+"'  ");
			} else {
					query.append("ANDNOT  rule_cps_ids:'"+rule_cps_ids+"'  ");
			}
		}
		

		String st="";
		if(!StringUtils.isBlank(sort))
			st=sort.substring(1);
		if(sortFields.get(st)==null) {
			sort="";
		} 
		
		if(!sort.equals("-hit_num"))
			sort=sort+";-hit_num";
		if(query.toString().equals("hidden:'0'")) {
			return notDta();
		}
		if(!StringUtils.isBlank(sort_1)) {
			if(sortFields.get(sort_1.substring(1))==null) {
				sort="";
			} 
			sort=sort+";"+sort_1;
		}

		if(position!=null&&"index".equals(position)) {
			sort="-c_sort";
		}
		
		//品牌xianz
		StringBuilder brandIds=new StringBuilder();
	        for (Map.Entry<String, String> entry : brands.entrySet()) {
	        	if(entry.getKey()!=null) {
	        		if(keywords.contains(entry.getKey())) {
	        			brandIds.append("brand_id=").append(entry.getValue()).append(" AND ");
	        		}
	        	}
 	        }

		String quertyString=query+"&&filter="+brandIds.toString()+"flag=1 AND deleted=0&&sort=+hidden_skin;"+sort;
		Map m=this.returnAjax(appName, quertyString, p, rows,0);
		
		if(position!=null&&"keywords".equals(position)){
			if((p*rows)>=0 &&(p*rows)<=20){
				//搜索词搜索 前20条数据排序
				m=sortGoods(m);
			}
		}
		
		
		Map data=(Map) m.get("data");
		outLog(request,reqStartTime, data,"goods", quertyString, p, rows);
		
		try {
		// 有广告
		if (ad && p <= 1 && StringUtils.isBlank(keywords) && StringUtils.isBlank(tag_ids)
				&& StringUtils.isBlank(notcps) && StringUtils.isBlank(rule_cps_ids)) {
			//System.out.println("进入广告------------");
			AdClient adc = new AdClient("ad");
			Map adMap = null;
			// 产品
			if (category > 0 && StringUtils.isBlank(cps)) {
				adMap = adc.findAd(4, category + "", null,"1");
				// 成分
			} else if (category == 0 && StringUtils.isNotBlank(cps)) {
				adMap = adc.findAd(5, null, Integer.parseInt(cps),"1");
			}
			if (null != adMap && adMap.size() > 0) {
				// 广告信息
				List<Map<String, String>> entityInfo = this.getAdInfo(adMap);

				// 解析返回结果
				List<Map> result = (List<Map>) data.get("items");
				for (int j = 0; null != entityInfo && j < entityInfo.size(); j++) {
					Map<String, String> map = entityInfo.get(j);
					// 分类
					int adCategory = Integer.parseInt(map.get("entityType"));
					// 广告位置
					int adOrientation = Integer.parseInt(map.get("adOrientation"));
					// 广告id
					int adId = Integer.parseInt(map.get("adId"));
					// 要替换的实体id
					long entityId = Integer.parseInt(map.get("entityId"));
					
					//跳转终端 1app 2model 3pc
					String positionType = (String) map.get("positionType");
					//跳转类型(站内/站外)
					int redirectType = Integer.parseInt(map.get("redirectType"));
					//跳转地址
					String redirectUrl = (String) map.get("redirectUrl");
					
					int replace = Integer.parseInt(map.get("replace"));
					// 成分没有分类 默认返回-1
					if (adCategory == -1) {
						adCategory = 0;
					}
					if (adCategory == category) {
						if (result.size() > 0) {
							for (int i = 0; i < result.size(); i++) {
								// 找到广告位
								if ((adOrientation - 1) == i) {
									// 查数据库 得到实体
									quertyString = "id:'" + entityId + "'";
									;
									Map dmaps = this.returnAjax(appName, quertyString, p, 1, 0);
									Map dataMap = (Map) dmaps.get("data");
									List<Map> goodsArray = (List<Map>) dataMap.get("items");
									if (null != goodsArray && goodsArray.size() > 0) {
										Map goodsMap = (Map) goodsArray.get(0);
										if (null != goodsMap && goodsMap.size() > 0) {
											// 替换
											if (StringUtils.isNotBlank(map.get("image"))) {
												goodsMap.put("image", map.get("image"));
											}
											goodsMap.put("adOrientation", adOrientation);
											goodsMap.put("adId", adId);
											goodsMap.put("replace", replace);
											
											goodsMap.put("positionType", positionType);
											goodsMap.put("redirectType", redirectType);
											goodsMap.put("redirectUrl", redirectUrl);
											//插入
											if(replace==0){
												result.add(i, goodsMap);
											}else if(replace==1){//替换
												result.set(i, goodsMap);
											}
											
										}
									}
								}
							}
						}

					}
				}

				// 带广告的结果
				if (null != result && result.size() > 0) {
					//去重
					//分类下去除重复  
					result=distink(result,entityInfo);
					data.put("items", result);
					m.put("data", data);
					this.createImageSrc(appName, data, "image");
					return m;
				}
			}
		}
		this.createImageSrc(appName, data, "image");
		}catch(Exception e) {
			e.printStackTrace();
		}
		return m; 
	}
	
	
	public Map sortGoods(Map map){
		Map dataMap = (Map) map.get("data");
		List<Map> goodsList = (List<Map>) dataMap.get("items");
		List<Map> listMap=new ArrayList();
		if(null!=goodsList && goodsList.size()>0){
			//根据条件分成3组
			List<Map> listMap1=new ArrayList();
			List<Map> listMap2=new ArrayList();
			List<Map> listMap3=new ArrayList();
			for(Map m: goodsList){
				int hitNum=Integer.parseInt(m.get("hit_num")+"");
				//Double grade=Double.parseDouble(m.get("grade")+"");
				int commentNum=Integer.parseInt(m.get("comment_num")+"");
				//org.apache.commons.lang3.ArrayUtils.
				if(hitNum>100000){
					//1、如果点击数大于10w，按照点击量倒叙排序；
					listMap1.add(m);
				}else if(hitNum<100000 && commentNum>50){
					//2、如果点击数小于10w，评论数大于50的排序权重靠前，且按照用户评分倒叙排序，同时如果相同评分按照点击数倒叙排序；
					listMap2.add(m);
				}else if(hitNum<100000 && commentNum<50){
	    			//3、如果点击数小于10w，评论数小于50的排序权重低于条件1和2，且按照用户评分倒叙排序，同时如果相同评分按照点击数倒叙排序；
					listMap3.add(m);
				}
			}
			
			//三组分别排序
			//listMap1.addAll(listMap2);
			listMap1=resetSort(listMap1,"hit_num");
			listMap2=resetSort(listMap2,"grade","hit_num");
			listMap3=resetSort(listMap3,"grade","hit_num");
			
			//三组融合
			listMap.addAll(listMap1);
			listMap.addAll(listMap2);
			listMap.addAll(listMap3);
			
		}
		dataMap.put("items",listMap);
		return map;
	}
	
	/**
	 * 重写Collections.sort方法
	 * strings 排序优先级
	 * 纯粹的排序 写死
	 * @param listMap
	 * @return
	 */
	public List resetSort(List<Map> listMap,final String...strings){
		Collections.sort(listMap, new Comparator() {
		      public int compare(Object o1, Object o2) {
		    	  Map map1=(Map)o1;
		    	  Map map2=(Map)o2;
		    	  int hitNum_1=-1;
		    	  Double grade_1=-1D;
		    	  int commentNum_1=-1;
		    	  
		    	  int hitNum_2=-1;
		    	  Double grade_2=-1D;
		    	  int commentNum_2=-1;
		    	  boolean flag1=false;
		    	  boolean flag2=false;
		    	  boolean flag3=false;
		    	  if(null!=strings && strings.length>0){
		    		  for(int i=0;i<strings.length;i++){
			    		  if("hit_num".equals(strings[i])){
			    			  hitNum_1=Integer.parseInt(map1.get("hit_num")+"");
			    			  hitNum_2=Integer.parseInt(map2.get("hit_num")+"");
			    			  flag1=true;
			    		  }else if("grade".equals(strings[i])){
			    			  grade_1=Double.parseDouble(map1.get("grade")+"");
			    			  grade_2=Double.parseDouble(map2.get("grade")+"");
			    			  flag2=true;
			    		  }else if("comment_num".equals(strings[i])){
			    			  commentNum_1=Integer.parseInt(map1.get("comment_num")+"");
			    			  commentNum_2=Integer.parseInt(map2.get("comment_num")+"");
			    			  flag3=true;
			    		  }
			    	  }
		    	  }
		    	  int cr = 0;
		    	  if(flag1&&!flag2&&!flag3){
		    		  //点击数
		    		  //优先级最高
		    		  int a=hitNum_1-hitNum_2;
		    		  if(a!=0){
		    			  cr=(a>0)?-1:1;
		    		  }
		    	  }
		    	  if(flag2&&flag1){
		    		  //评分和点击数
		    		  Double a=grade_1-grade_2;
		    		  if(a!=0){
		    			  cr=(a>0)?-1:1;
		    		  }else{
		    			  //评分相同,点击数排序
		    			  int a2=hitNum_1-hitNum_2;
		    			  cr=(a2>0)?-2:2;
		    		  }
		    	  }
				  return cr;
		      }

		    });
		return listMap;
	}
	
	public List distink(List goodsList ,List<Map<String, String>> adList){
		for(int i=0;i<adList.size();i++){
			Map<String, String> adMap = (Map<String, String>) adList.get(i);
			int adEntityId = Integer.parseInt(adMap.get("entityId"));
			for(int j=0;j<goodsList.size();j++){
				Map map=(Map)goodsList.get(j);
				int entityId=Integer.parseInt(map.get("id")+"");
				Integer adId=(Integer)map.get("adId");
				if(adEntityId==entityId && (null==adId || adId==0)){
					goodsList.remove(j);
				}
			}
		}
		return goodsList;
	}
	
	//获取广告信息
    public List<Map<String,String>> getAdInfo(Map adMap){
    	//System.out.println("map:"+adMap);
    	//得到内容
    	JSONArray result = JSONArray.fromObject(adMap.get("result"));
    	List<Map> list2 = new ArrayList<Map>();
    	//实体id集合
    	List<Map<String,String>> entityInfo=new ArrayList<Map<String,String>>();
        if (result.size() > 0) {
            for (int i = 0; i < result.size(); i++) {
                Map<String, String> map = new HashMap<String, String>();
                net.sf.json.JSONObject job = result.getJSONObject(i);
                //得到实体id
                long entityId=(Integer)job.get("entityId");
                String enImage=(String)job.get("imgUrl");
                int adOrientation=(Integer)job.get("orientation");
                int replace = (Integer) job.get("isReplace");
                
                //跳转终端 1app 2model 3pc
                String positionType = (String) job.get("positionType");
				//跳转类型(站内/站外)
				int redirectType = (Integer)job.get("redirectType");
				//跳转地址
				String redirectUrl = "";
				if(!(job.get("redirectUrl") instanceof JSONNull)){
					redirectUrl=(String) job.get("redirectUrl");
                }
				
                int entityType=-1;
                if(!(job.get("type") instanceof JSONNull)){
                	entityType=Integer.parseInt((String)job.get("type"));
                }
                
                long adId=(Integer)job.get("id");
                map.put("adOrientation", adOrientation+"");
                map.put("adId",adId+"");
                
                map.put("entityId",entityId+"");
                map.put("entityType",entityType+"");
                map.put("replace", replace+"");
                
                map.put("positionType",positionType);
                map.put("redirectType",redirectType+"");
                map.put("redirectUrl", redirectUrl);
                
                //广告图片
                if(StringUtils.isNotBlank(enImage)){
                	map.put("image",enImage);
                }
                entityInfo.add(map);
            }
        }
        return entityInfo;
    }

}
