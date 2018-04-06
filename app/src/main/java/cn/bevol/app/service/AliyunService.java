package cn.bevol.app.service;

import cn.bevol.app.config.CloudSearchClientConfig;
import cn.bevol.app.config.TaobaoClientConfig;
import cn.bevol.util.CommonUtils;
import cn.bevol.util.ConfUtils;
import cn.bevol.util.Log.LogException;
import cn.bevol.util.response.ReturnData;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.opensearch.CloudsearchDoc;
import com.aliyun.opensearch.CloudsearchSearch;
import com.aliyun.opensearch.CloudsearchSuggest;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.CopyObjectResult;
import com.aliyun.oss.model.PutObjectResult;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.green.model.v20170112.TextScanRequest;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.request.AlibabaAliqinFcTtsNumSinglecallRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
import com.taobao.api.response.AlibabaAliqinFcTtsNumSinglecallResponse;
import flexjson.JSONDeserializer;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * aliyun提供服务
 * @author hualong
 *
 */
@Service
public class AliyunService {

	private static Logger logger = LoggerFactory.getLogger(AliyunService.class);

	@Autowired
	TaobaoClientConfig taobaoClient;// = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23325511", "ee7fcc150be92eac0a10395590f96ae9");

	@Autowired
	private CloudSearchClientConfig client;


	private Map<String,OSSClient> ossMap =new HashMap<String,OSSClient>();

	//阿里云内容检测key
	private static IAcsClient iAcsClient;

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
	 * 获取oss实例
	 * @param bucketName
	 * @return
	 */
	private OSSClient getOSSClient(String bucketName) {
		if(!StringUtils.isBlank(bucketName)) {
			OSSClient oss=ossMap.get(bucketName);
			if(oss==null) {
				//初始化oss实例
				List<Map<String,String>> list= ConfUtils.getList("oss_info");
				for(Map<String,String> m:list){
					if(m.get("name").equals(bucketName)) {
						//获取map
						String endpoint=m.get("endpoint");
						String accessKeyId=m.get("accessKeyId");
						String secretAccessKey=m.get("secretAccessKey");
						oss=new OSSClient(endpoint,accessKeyId,secretAccessKey);
						ossMap.put(bucketName, oss);
					}
				}
			}
			return oss;
		}
		return null;
	}


    /**
     * 证码发送短信
     * @param phone
     * @param vcode
     * @return
     */
    public  Map<String,Object> sendVcode(String phone, String vcode, int type,long id) {
		String rejson="";
		Map<String,Object> infoMap=new HashMap<String,Object>();
		infoMap.put("send",-1);
		try {
			AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
			req.setExtend(String.valueOf(id));
			req.setSmsType("normal");
			req.setSmsFreeSignName("美丽修行");
			req.setSmsParamString("{\"code\":\""+vcode+"\",\"product\":\"美丽修行\"}");
			req.setRecNum(phone);
			if(type==0){
				req.setSmsTemplateCode("SMS_5885382");
			} else if(type==1) {
				req.setSmsTemplateCode("SMS_5885380");
			} else if(type==3) {
				req.setSmsTemplateCode("SMS_33575234");
			} else if(type==4) {
				req.setSmsTemplateCode("SMS_33570392");
			}
			AlibabaAliqinFcSmsNumSendResponse rsp = taobaoClient.getTaobaoClient().execute(req);

			infoMap.put("id",rsp.getParams().get("extend"));
			if(null!=rsp.getSubCode() && rsp.getSubCode().equals("isv.MOBILE_NUMBER_ILLEGAL") &&
                    null!=rsp.getErrorCode() && rsp.getErrorCode().equals("15")){
				infoMap.put("send",15);
			}

            //判断是否发送成功
			if(null!=rsp.getResult() && rsp.getResult().getSuccess()){
				//rsp.getResult().getSuccess():true表示成功，false表示失败
				infoMap.put("send",0);
			}

			//不能判断是否发送成功,做阿里异常输出
			if(!StringUtils.isBlank(rsp.getBody())) {
				Map<String,Object> jsonNode=  new JSONDeserializer<Map<String,Object>>().deserialize(rsp.getBody(), HashMap.class);
				if(jsonNode.get("alibaba_aliqin_fc_sms_num_send_response")!=null) {
					Map<String,Object> result=(Map<String, Object>) jsonNode.get("alibaba_aliqin_fc_sms_num_send_response");
					Map<String,Object> err_code=(Map<String, Object>) result.get("result");
					if(err_code.get("err_code")!=null&&err_code.get("err_code").equals("0")) {
						infoMap.put("send",0);
						return infoMap;
					}
				}
			}
			logger.error("method:msgOpen.error arg:{phone:"+phone+",vcode:\""+vcode+"\"}   desc:"+rejson);
		} catch(Exception e) {
			Map map=new HashMap();
			map.put("method", "AliyunService.sendVcode");
			map.put("phone", phone);
			map.put("vcode", vcode);
			new LogException(e,map);
		}

        return infoMap;
    }

    /**
     * 发送语音短信
     * @param phone
     * @param vcode
     * @return
     */
    public  boolean sendVoiceVcode(String phone, String vcode) {
        String rejson="";
        try {
            AlibabaAliqinFcTtsNumSinglecallRequest req = new AlibabaAliqinFcTtsNumSinglecallRequest();
            req.setCalledNum(phone);
            req.setCalledShowNum("02759771905");
            req.setTtsParamString("{\"code\":\""+vcode+"\"}");
            req.setTtsCode("TTS_125835089");
            AlibabaAliqinFcTtsNumSinglecallResponse rsp = taobaoClient.getTaobaoClient().execute(req);
            if(!StringUtils.isBlank(rsp.getBody())) {
                Map<String,Object> jsonNode=  new JSONDeserializer<Map<String,Object>>().deserialize(rsp.getBody(), HashMap.class);
                if(jsonNode.get("alibaba_aliqin_fc_tts_num_send_response")!=null) {
                    Map<String,Object> result=(Map<String, Object>) jsonNode.get("alibaba_aliqin_fc_tts_num_send_response");
                    Map<String,Object> err_code=(Map<String, Object>) result.get("result");
                    if(err_code.get("err_code")!=null&&err_code.get("err_code").equals("0")) {
                        return true;
                    }
                }
            }
            logger.error("method:sendVoiceVcode.error arg:{phone:"+phone+",vcode:\""+vcode+"\"}   desc:"+rejson);
        } catch(Exception e) {
            Map map=new HashMap();
            map.put("method", "AliyunService.sendVoiceVcode");
            map.put("phone", phone);
            map.put("vcode", vcode);
            new LogException(e,map);
        }
        return false;
    }

	/**
	 * 发送邮件验证 连续发送两次
	 * @param email
	 * @param vcode
	 * @param type
	 * @return
	 */
	public  boolean sendEmailVcode(String email, String vcode, int type) {
		String rejson="";
		try {
			//第一次发送
			sendEmailTpl( email, vcode, type);
			return true;
		} catch(Exception e) {
			try {
				//第二次发送
				sendEmailTpl( email, vcode, type);
				return true;
			} catch(Exception ex) {
				Map map=new HashMap();
				map.put("method", "AliyunService.sendEmailVcode");
				map.put("email", email);
				map.put("vcode", vcode);
				new LogException(ex,map);
			}
		}
		return false;
	}

	public void sendEmailTpl(String email, String vcode, int type)  throws Exception {
		Map<String,String> info=ConfUtils.getMap("email_vcode_type", "vcode_type");
		String vcodeStr=info.get(type+"");
		vcodeStr=StringUtils.replaceEach(vcodeStr, new String[]{"vcode"}, new String[]{vcode});
		vcodeStr=StringUtils.replaceEach(vcodeStr, new String[]{"email"}, new String[]{email});
		CommonUtils.sendEmail(email, "美丽修行验证码", vcodeStr);

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
	public cn.bevol.util.response.ReturnListData openSearch(String appName, String quertyString, long p, int rows, String... fields) {
		long start=0;
		if(p>1) {
			start=(p-1)*rows;
		}
		CloudsearchSearch search = new CloudsearchSearch(client.getClient());
		if(fields.length>0)
			search.getFetchFields().addAll(Arrays.asList(fields));
		String openSearchIndexPre=client.getOpenSearchIndexPre();
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
			return new cn.bevol.util.response.ReturnListData(items,total);
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
		return cn.bevol.util.response.ReturnListData.ERROR;
	}

	/**
	 * 上传文件
	 *
	 * @param dir         oss目录
	 * @param name        名称
	 * @param inputStream
	 * @return
	 */
	public ReturnData upFile(String dir, String name, InputStream inputStream, Long userId) {
		try {


			String bucket = ConfUtils.getUploadBk(dir);

			if (StringUtils.isBlank(bucket)) return ReturnData.ERROR;
			if (StringUtils.isBlank(name) || name.indexOf(".") == -1) return ReturnData.ERROR;


			String exName = name.substring(name.lastIndexOf(".")+1);
			String uuid = userId+"_"+ UUID.randomUUID().toString() + "." + exName;
			String keySuffixWithSlash = dir + "/" + uuid;

			upOss(bucket, keySuffixWithSlash, inputStream);
			Map m = new HashMap();
			m.put("image", uuid);
			m.put("path", keySuffixWithSlash);
			m.put("src", CommonUtils.getImagDomain()+"/"+keySuffixWithSlash);
			return new ReturnData<>(m);
		} catch (Exception e) {
			Map map=new HashMap();
			map.put("method", "AliyunService.upFile");
			map.put("dir", dir);
			map.put("name", name);
			new LogException(e,map);
		}
		return ReturnData.ERROR;
	}

	/**
	 * 上传文件
	 *
	 * @param dir         oss目录
	 * @param name        名称
	 * @param inputStream
	 * @return
	 */
	public ReturnData upFile2(String dir, String name, InputStream inputStream, Long userId) {
		try {
			String tempDir="tmpl";
			String newDir=dir.replace("/", "_");
			//拼接临时目录tmpl/comment_image
			newDir=tempDir+"/"+newDir;

			String bucket = ConfUtils.getUploadBk(dir);

			if (StringUtils.isBlank(bucket)) return ReturnData.ERROR;
			if (StringUtils.isBlank(name) || name.indexOf(".") == -1) return ReturnData.ERROR;

			//.png
			String exName = name.substring(name.lastIndexOf(".")+1);
			//加密拼接图片
			String uuid = UUID.randomUUID().toString() + "." + exName;
			if(null!=userId){
				uuid = userId+"/"+ UUID.randomUUID().toString() + "." + exName;
			}
			
			//目录拼接
			String keySuffixWithSlash = newDir + "_" + uuid;
			//上传到临时目录
			upOss(bucket, keySuffixWithSlash, inputStream);
			Map m = new HashMap();
			m.put("image", uuid);
			m.put("path", keySuffixWithSlash);
			m.put("src", CommonUtils.getImagDomain()+"/"+keySuffixWithSlash);
			return new ReturnData(m);
		} catch (Exception e) {
			Map map=new HashMap();
			map.put("method", "AliyunService.upFile2");
			map.put("dir", dir);
			map.put("name", name);
			new LogException(e,map);
		}
		return ReturnData.ERROR;
	}


	/**
	 * opensearch自动补全
	 * @param indexName
	 * @param suggestName
	 * @param rows
	 * @return
	 */
	public  ReturnData autoComplete(String indexName, String suggestName, int rows) {
		if(rows<10) rows=10;
		CloudsearchSuggest suggest = new CloudsearchSuggest(indexName, suggestName, client.getClient());
		String json="";
		try {
			suggest.setHit(10);
			suggest.setQuery(suggestName);
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
			Map map=new HashMap();
			map.put("method", "AliyunService.autoComplete");
			map.put("indexName", indexName);
			map.put("suggestName", suggestName);
			map.put("rows", rows);
			new LogException(e,map);
		}
		return ReturnData.ERROR;
	}


	/**
	 * 上传base64文件到 oss
	 * @param ossDir oss 目录
	 * @param filename 文件名
	 * @param base64 码
	 */
	public Map<String,String> upOssBase64FileByDir(String ossDir, String filename, String base64) {
		try {
			byte[] bt = null;
			sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
			bt = decoder.decodeBuffer( base64 );
			String bucket = ConfUtils.getUploadBk(ossDir);
			OSSClient oss =getOSSClient(bucket);
			String fileDir=ossDir+"/"+filename;
			if(oss==null) throw new Exception("oss连接失效");
			PutObjectResult  obj=getOSSClient(bucket).putObject(bucket, fileDir,new ByteArrayInputStream(bt) );
			Map<String,String> m = new HashMap<String,String>();
			m.put("image", filename);
			m.put("path", fileDir);
			m.put("src", CommonUtils.getImagDomain()+"/"+fileDir);
			return m;
		} catch(Exception e) {
			Map map=new HashMap();
			map.put("method", "AliyunService.upOssBase64FileByDir");
			map.put("ossDir", ossDir);
			map.put("filename", filename);
			map.put("base64", base64);
			new LogException(e,map);
		}
		return null;
	}

	/**
	 * oss服务	上传
	 * @param bucketName
	 * @param keySuffixWithSlash
	 * @param inputStream
	 */
	public  void upOss(String bucketName, String keySuffixWithSlash, InputStream inputStream) {
		try {
			OSSClient oss =getOSSClient(bucketName);
			if(oss==null) throw new Exception("oss连接失效");
			PutObjectResult  obj=getOSSClient(bucketName).putObject(bucketName, keySuffixWithSlash, inputStream);
		} catch(Exception e) {
			Map map=new HashMap();
			map.put("method", "AliyunService.upOss");
			map.put("bucketName", bucketName);
			map.put("keySuffixWithSlash", keySuffixWithSlash);
			map.put("inputStream", inputStream);
			new LogException(e,map);
		}
	}

	/**
	 * oss服务	-copy
	 * @param sourceDir 临时目录
	 * @param sourceKey	临时文件名(要copy的文件名)
	 */
	public  ReturnData upOss2(String sourceDir, String sourceKey) {
		try {
			//同一个bucket
			String bucketName = ConfUtils.getUploadBk(sourceDir);
			OSSClient oss =getOSSClient(bucketName);
			if(oss==null) throw new Exception("oss连接失效");
			//拼接临时目录
			String newSourceKey=sourceDir.replace("/", "_");
			String tempDir="tmpl";
			newSourceKey=tempDir+"/"+newSourceKey+"_"+sourceKey;
			//copy到那个目录
			String newSaveKey=sourceDir+"/"+sourceKey;

			//开始copy
			CopyObjectResult copyResult=getOSSClient(bucketName).copyObject(bucketName, newSourceKey, bucketName, newSaveKey);
			return new ReturnData();
		} catch(Exception e) {
			Map map=new HashMap();
			map.put("method", "AliyunService.upOss2");
			map.put("sourceDir", sourceDir);
			map.put("sourceKey", sourceKey);
			new LogException(e,map);
		}
		return ReturnData.ERROR;
	}

	/**
	 * 临时目录的图片移到正式目录
	 * 用于微信注册后的第一次登录,头像路径转换
	 * @param proImage: 非第一次的头像
	 * @param tempImage: 用于微信注册后的第一次登录调用
	 * @return
	 */
	public String imgForm(String proImage, String tempImage) {
		try{
			if(StringUtils.isNotBlank(proImage)){
				return  proImage;
			}
			if(StringUtils.isNotBlank(tempImage)){
				//正式目录直接返回
				String proDir="/UploadFile/head/";
				if(tempImage.indexOf(proDir)!=-1&&tempImage.indexOf("http")!=-1){
					return tempImage;
				}

				//拆分临时目录 获取图片名
				//临时目录的文件移动到正式目录
				//tmpl/updload_head_a.jpg
				int index=tempImage.lastIndexOf("/");
				String subTemp=tempImage.substring(index+1);
				index=subTemp.lastIndexOf("_");
				String dir=subTemp.substring(0, index).replace("_", "/");

				String rpl=StringUtils.replace(tempImage, "/tmpl/", "/");
				String imageUrl=StringUtils.replace(rpl, "_", "/");
				if(StringUtils.isNotBlank(imageUrl)){
					String image=imageUrl.substring(imageUrl.lastIndexOf("/")+1);
					//开始copy
					upOss2(dir,image);
					return imageUrl;
				}
			}
		}catch(Exception e){
			Map map=new HashMap();
			map.put("method", "AliyunService.imgForm");
			map.put("proImage", proImage);
			map.put("tempImage", tempImage);
			new LogException(e,map);
		}
		return null;
	}

	public  void cpOss(String bucketName, String keySuffixWithSlash, InputStream inputStream) {
		try {
			OSSClient oss =getOSSClient(bucketName);
			if(oss==null) throw new Exception("oss连接失效");
			///	PutObjectResult  obj=getOSSClient(bucketName).copyObject(bucketName, keySuffixWithSlash, destinationBucketName, destinationKey)
		} catch(Exception e) {
			Map map=new HashMap();
			map.put("method", "AliyunService.cpOss");
			map.put("bucketName", bucketName);
			map.put("keySuffixWithSlash", keySuffixWithSlash);
			map.put("inputStream", inputStream);
			new LogException(e,map);
		}
	}


	/**
	 * oss服务
	 * @param bucketName
	 * @param keySuffixWithSlash
	 * @param json
	 */
	public  void upOss(String bucketName, String keySuffixWithSlash, String json) {
		try {
			OSSClient oss =getOSSClient(bucketName);
			if(oss==null) throw new Exception("oss连接失效");
			PutObjectResult  obj=getOSSClient(bucketName).putObject(bucketName, keySuffixWithSlash,new ByteArrayInputStream(json.getBytes()) );
			System.out.println(obj);
		} catch(Exception e) {
			Map map=new HashMap();
			map.put("method", "AliyunService.upOss");
			map.put("bucketName", bucketName);
			map.put("keySuffixWithSlash", keySuffixWithSlash);
			map.put("json", json);
			new LogException(e,map);
		}
	}

	/**
	 * 推送opeansearch
	 * @param appName
	 * @param feilds
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public  void updateOpearch(String indexName, String appName, List<Map<String,Object>> feilds) throws JSONException, IOException {
		String openSearchIndexPre=client.getOpenSearchIndexPre();
		CloudsearchDoc doc=new CloudsearchDoc(openSearchIndexPre+indexName,client.getClient());
		for(int i=0;i<feilds.size();i++) {
			doc.update(feilds.get(i));
		}
		String s=doc.push(appName);
		System.out.println(s);
	}

	/**
	 * base64图片上传
	 * @param dir 目录
	 * @param base64Image base64
	 * @return
	 */
	public ReturnData upOssBase64FileByDir(Long userId, String dir, String base64Image) {

		return new ReturnData(upOssBase64FileByDir(dir,userId+"_"+ UUID.randomUUID().toString()+".jpg",base64Image));
	}

	/**
	 * 获取环境的配置
	 * @return
	 */
	/*public String getPro(String key) {
		try {
			String val=propertiesFactoryBean.getObject().getProperty(key);
			if(StringUtils.isBlank(val)) return "";
			return val;
		} catch (IOException e) {
			Map map=new HashMap();
			map.put("method", "BaseService.getPro");
			map.put("key", key);
			new LogException(e,map);
		}
		return "";
	}*/
	
	public List<Map> textKeywordScan(String content) throws Exception {
		List<String> contentsList = new ArrayList<>();
		contentsList.add(content);
		return textKeywordScan(contentsList);
	}
	
	/**
	 *  文本验证
	 * 
	 * @param contents
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
