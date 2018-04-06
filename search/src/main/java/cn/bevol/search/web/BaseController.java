package cn.bevol.search.web;

import cn.bevol.search.config.DefaultCloudsearchClient;
import cn.bevol.util.CommonUtils;
import cn.bevol.util.response.ReturnData;
import com.aliyun.opensearch.CloudsearchSearch;
import com.aliyun.opensearch.CloudsearchSuggest;
import flexjson.JSONDeserializer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.*;


@Controller
public class BaseController {
	@Autowired
	DefaultCloudsearchClient client;


	public  Map returnAjax(Object data,int state,String ... info) {
		return CommonUtils.outTepl(data,state,info);
	}
	
    public Map returnlistAjax(List rows,long total,int state,String ...info) {
    	return CommonUtils.outTepl(rows,total,state,info);
    	}
	
	public Map checkword(String content,String keyword) {
		
		return null;
	}
	
	public  Map returnAjax(String appName,String quertyString,int p,int rows,String... fields) {
		long start=0;
		if(p>1) {
			start=(p-1)*rows;
		}
		CloudsearchSearch search = new CloudsearchSearch(client.getClient());
		if(fields.length>0)
		search.getFetchFields().addAll(Arrays.asList(fields));
		search.addIndex(appName);

		search.setQueryString(quertyString+"&&config=start:"+start+",hit:"+rows);
		search.setFormat("json");
		String json;
		try {
			json = search.search();
			Map<String,Object> jsonNode= new JSONDeserializer<Map<String,Object>>().deserialize(json, HashMap.class);
			Map result=(Map) jsonNode.get("result");	
			Map<String,Object> m=new HashMap();
			m.put("total", result.get("total"));
			m.put("rows", result.get("total"));
			m.put("items", result.get("items"));
			return returnAjax(m,1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnAjax(null,-1);
	}
	/**
	 * 有图片的处理一下
	 * @param appName
	 * @param quertyString
	 * @param p
	 * @param rows
	 * @param type
	 * @param fields
	 * @return
	 */
	public  Map returnAjax(String appName,String quertyString,int p,int rows,int type,String... fields) {
		Map m=returnAjax(appName, quertyString, p, rows);
		Map ms=(Map) m.get("data");
		
		List<Map> itmes=(List<Map>) ms.get("items");
		if(itmes!=null) {
			for(Map map : itmes) {
				if(map.get("image")!=null&&!(map.get("image")+"").equals("")) {
					map.put("path", map.get("path")+"@60p");
				} 
			}
		}
		return m;
	}


	/**
	 * opensearch自动补全
	 * @param indexName
	 * @param suggestName
	 * @param keywords
	 * @param rows
	 * @return
	 */
	public ReturnData autoComplete(String indexName, String suggestName, String keywords, int rows) {
		if(rows>20) rows=20;
        CloudsearchSuggest suggest = new CloudsearchSuggest(indexName, suggestName, client.getClient());
		String json="";
		try {
            suggest.setHit(10);
            suggest.setQuery(keywords);
            json = suggest.search();
			Map<String,Object> jsonNode= new JSONDeserializer<Map<String,Object>>().deserialize(json, HashMap.class);
			   if (jsonNode.containsKey("errors")) {
					return ReturnData.ERROR;
			   }
             List itemsJsonArray =  (List) jsonNode.get("suggestions");
             List<String> suggestions=new ArrayList();
             for (int i = 0; i < itemsJsonArray.size(); i++){
            	 Map item = (Map) itemsJsonArray.get(i);
                 suggestions.add(item.get("suggestion")+"");
             }
			return new ReturnData(suggestions);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ReturnData.ERROR;
	}

	
 
	/*
	 *添加图片前缀
	 */
	public  void  createImageSrc(String appName,Map datas,String ...imgFields) {
		if(imgFields!=null&&imgFields.length>0) {
			if(datas!=null) {
				List<Map> itmes=(List<Map>) datas.get("items");
				for(Map map : itmes) {
					for(String f:imgFields) {
						if(map.get(f)!=null&&!StringUtils.isBlank(map.get(f)+"")) {
							map.put(f+"Src", CommonUtils.getImageSrc(appName,map.get(f)+""));
						}
					}
				}
			}
		}
	}	
	

	/**
	 * 重新登录
	 * @return
	 */
	public  Map resetLogin() {
		return returnAjax(null,-5,"请重新登录");
	}
	
	/**
	 * 用户禁用
	 * @return
	 */
	public  Map hiddenUser() {
		return returnAjax(null,4,"用户违规暂时锁定");
	}

	
	/**
	 * 正常返回
	 * @return
	 */
	public  Map defaultAjax() {
		return returnAjax(null,1);
	}


}
