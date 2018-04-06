package cn.bevol.internal.service;

import cn.bevol.util.response.ReturnListData;
import cn.bevol.util.ConfUtils;
import cn.bevol.util.Log.LogException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.opensearch.CloudsearchClient;
import com.aliyun.opensearch.CloudsearchDoc;
import com.aliyun.opensearch.CloudsearchSearch;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.green.model.v20170112.TextScanRequest;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import flexjson.JSONDeserializer;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * aliyun提供服务
 * @author hualong
 *
 */
@Service
@ConfigurationProperties("cloud.search")
public class AliyunService {

	private static Logger logger = LoggerFactory.getLogger(AliyunService.class);

	@Autowired
	private CloudsearchClient client;

	//阿里云内容检测key
	private static IAcsClient iAcsClient;

	private String openSearchIndexPre;

	static{
		try {
			Map<String,String> iacsMap= ConfUtils.getJSONMap("aliyun_iacs");
			String regionId=iacsMap.get("regionId");
			String accessKeyId=iacsMap.get("accessKeyId");
			String accessKeySecret=iacsMap.get("accessKeySecret");
			IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
			iAcsClient=new DefaultAcsClient(profile);
		} catch(Exception e) {
			Map map=new HashMap();
			map.put("method", "AliyunService.ossInfo");
			new LogException(e,map);
		}
	}


	/**
	 * 搜索opensearch提供服务
	 * @param appName
	 * @param quertyString
	 * @param p
	 * @param rows
	 * @param fields
	 * @return
	 */
	public ReturnListData openSearch(String appName, String quertyString, long p, int rows, String... fields) {
		long start=0;
		if(p>1) {
			start=(p-1)*rows;
		}
		CloudsearchSearch search = new CloudsearchSearch(client);
		if(fields.length>0)
			search.getFetchFields().addAll(Arrays.asList(fields));
		String openSearchIndexPre=this.openSearchIndexPre;
		search.addIndex(openSearchIndexPre+appName);
		search.setQueryString(quertyString+"&&config=start:"+start+",hit:"+rows);
		search.setFormat("json");

		String json;
		try {
			json = search.search();
			Map<String,Object> jsonNode= new JSONDeserializer<Map<String,Object>>().deserialize(json, HashMap.class);
			Map result=(Map) jsonNode.get("result");
			Map<String,Object> m=new HashMap();
			long total= Long.parseLong(result.get("total")+"") ;
			List items=(List) result.get("items");
			return new ReturnListData(items,total);
		} catch (Exception e) {
			Map map=new HashMap();
			map.put("method", "AliyunService.openSearch");
			map.put("appName", appName);
			map.put("quertyString", quertyString);
			map.put("p", p);
			map.put("rows", rows);
			map.put("fields", fields);
			new LogException(e,map);
		}
		return ReturnListData.ERROR;
	}


	/**
	 * 推送opeansearch
	 * @param appName
	 * @param feilds
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public  void updateOpearch(String indexName,String appName,List<Map<String,Object>> feilds) throws ClientProtocolException, IOException, JSONException {
		String openSearchIndexPre=this.openSearchIndexPre;
		CloudsearchDoc doc=new CloudsearchDoc(openSearchIndexPre+indexName,client);
		for(int i=0;i<feilds.size();i++) {
			doc.update(feilds.get(i));
		}
		String s=doc.push(appName);
		System.out.println(s);
	}

	public List<Map> textKeywordScan(String content) throws Exception {
		List<String> contentsList = new ArrayList<>();
		contentsList.add(content);
		return textKeywordScan(contentsList);
	}
	
	/**
	 *  文本验证
	 * 
	 * @param bucketName
	 * @return
	 */
	public List<Map> textKeywordScan(List<String> contents) throws Exception {
		TextScanRequest textScanRequest = new TextScanRequest();
		textScanRequest.setAcceptFormat(FormatType.JSON); // 指定api返回格式
		textScanRequest.setContentType(FormatType.JSON);
		textScanRequest.setMethod(com.aliyuncs.http.MethodType.POST); // 指定请求方法
		textScanRequest.setEncoding("UTF-8");

		List<Map<String, Object>> tasks = new ArrayList<Map<String, Object>>();

		for(int i=0;i<contents.size();i++) {
			Map<String, Object> task1 = new LinkedHashMap<String, Object>();
			task1.put("dataId", UUID.randomUUID().toString());
			task1.put("content",contents.get(i));
			tasks.add(task1);
		}

		JSONObject data = new JSONObject();
		data.put("scenes", Arrays.asList("antispam"));
		data.put("tasks", tasks);
		textScanRequest.setContent(data.toJSONString().getBytes("UTF-8"), "UTF-8", FormatType.JSON);

		List<Map> results=new ArrayList<>();
		/**
		 * 请务必设置超时时间
		 */
		textScanRequest.setConnectTimeout(3000);
		textScanRequest.setReadTimeout(6000);
			HttpResponse httpResponse = iAcsClient.doAction(textScanRequest);
			if (httpResponse.isSuccess()) {
				JSONObject scrResponse = JSON.parseObject(new String(httpResponse.getContent(), "UTF-8"));
				System.out.println(JSON.toJSONString(scrResponse, true));
				if (200 == scrResponse.getInteger("code")) {
					JSONArray taskResults = scrResponse.getJSONArray("data");
//					results=taskResults.toJavaList(Map.class);
					for (Object taskResult : taskResults) {
						
						if (200 == ((JSONObject) taskResult).getInteger("code")) {
							JSONArray sceneResults = ((JSONObject) taskResult).getJSONArray("results");
							for (Object sceneResult : sceneResults) {
								JSONObject sceneResultObject = (JSONObject) sceneResult;
								HashMap map = new HashMap();
								map.put("rate", sceneResultObject.getDoubleValue("rate"));
								map.put("scene", sceneResultObject.getString("scene"));
								map.put("suggestion", sceneResultObject.getString("suggestion"));
								map.put("label", sceneResultObject.getString("label"));
								results.add(map);
								/*String scene = ((JSONObject) sceneResult).getString("scene");
								String suggestion = ((JSONObject) sceneResult).getString("suggestion");
								String label = ((JSONObject) sceneResult).getString("label");
								// 根据scene和suggetion做相关的处理
								// do something
								System.out.println("args = [" + scene + "]");
								System.out.println("args = [" + suggestion + "]");
								System.out.println("args = [" + label + "]");*/
							}
						} else {
							System.out.println("task process fail:" + ((JSONObject) taskResult).getInteger("code"));
						}
					}
				} else {
					System.out.println("detect not success. code:" + scrResponse.getInteger("code"));
				}
			} else {
				System.out.println("response not success. status:" + httpResponse.getStatus());
			}
			return results;
	}

}
