package cn.bevol.statics.service;

import cn.bevol.statics.dao.db.Paged;
import cn.bevol.statics.dao.mapper.FindOldMapper;
import cn.bevol.statics.dao.mapper.IndustryOldMapper;
import cn.bevol.statics.entity.model.Find;
import cn.bevol.statics.entity.model.Industry;
import cn.bevol.statics.entity.model.MetaInfo;
import cn.bevol.statics.entity.model.Tags;
import cn.bevol.statics.service.iservice.StaticInfoService;
import cn.bevol.util.ConfUtils;
import cn.bevol.util.PropertyUtils;
import cn.bevol.util.StringUtil;
import cn.bevol.util.http.HttpUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class StaticFindService implements StaticInfoService {
	private static Logger logger = LoggerFactory.getLogger(StaticFindService.class);

	@Resource
	SinglePageService singlePageService;

	@Resource
	private FindOldMapper findOldMapper;
	@Resource
	private IndustryOldMapper industryOldMapper;
	@Resource
	StaticRecordService staticRecordService;
	@Resource
	private SidebarService sidebarService;
	@Resource
	private FreemarkerService freemarkerService;
	/**
	 * 发现静态化
	 * @param id
	 * 新版 -- staticFindPage
	 */
	@Deprecated
	public Boolean findStatic(String id) throws IOException {
		boolean result = false;
		try {
			List<Map<String,String>> json = ConfUtils.getList("find");
			for (Map<String, String> info : json) {
				String platform = info.get("platform");
				String ftlName = info.get("ftl");
				String js=ConfUtils.mps.get("pc_js");
				String css=ConfUtils.mps.get("pc_css");
				String version = ConfUtils.mps.get("version");
				String[] imgs = ConfUtils.imgs;
				String img = imgs[(new Random().nextInt(2))];
				Find find=	findOldMapper.getById(Long.parseLong(id));
				if(find!=null){
					JSONObject dataObj = JSONObject.fromObject(find);
					String descp = find.getDescp().replaceAll("\\<.*?>", "").replaceAll("\\s*|\t|\r|\n","").trim();
					if(descp.length()>0 && descp.length()>200){
						descp = descp.substring(0,200)+"...";
					}
					Map<String, Object> context = new HashMap<String, Object>();
					// 将json字符串加入数据模型
					context.put("data", dataObj);
					context.put("staticType", "find");
					context.put("js", js);
					context.put("css", css);
					context.put("img", img);
					context.put("version", version);
					context.put("title", find.getTitle()+"-美丽修行");
					String tag = find.getTag() == null?"":find.getTag();
					if(StringUtils.isEmpty(tag.trim())){
						context.put("keywords", find.getTitle());
					}else{
						context.put("keywords", tag);
					}
					context.put("description", descp);

					result = freemarkerService.createHtmlFile2PC(ftlName, id+"", context);
					if(result){
						String filePath = FreemarkerService.freemarkerPath+"/"+platform+"/"+id+".html";
						String uploadPath = "find/"+id+".html";
						OSSService.upload2OSS(filePath,uploadPath,platform);
						staticRecordService.insertOrUpdate(id+"",platform,"find",1,uploadPath);
					}
				}else{
					logger.error("未查询到结果！");
					staticRecordService.insertOrUpdate(id+"",platform,"find",0,null);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e.getStackTrace());
		}

		return result;
	}

	/**
	 * 静态化全部发现页面
	 * @param id
	 * @return
	 * @throws IOException
	 */
	public Boolean staticFindPage(String id) throws IOException {
		Boolean res1 = staticFindPCPage(id);
		Boolean res2 = staticFindMPage(id);
		Boolean res3 = staticFindWXPage(id);
		Boolean res4 = staticFindAppPage(id);
		return res1 && res2 && res3 && res4;
	}

	/**
	 * 按type类型静态化发现页面
	 * @param id
	 * @param type
	 * @return
	 * @throws IOException
	 */
	public Boolean staticFindPage(String id, String type) throws IOException {
		Boolean res = false;
		switch (type){
			case "pc":
				res = staticFindPCPage(id);
				break;
			case "mobile":
				res = staticFindMPage(id);
				break;
			case "wx":
				res = staticFindWXPage(id);
				break;
			case "app":
				res = staticFindAppPage(id);
				break;
		}
		return res;
	}

	/**
	 * 读取meta信息
	 * @param dataMap
	 * @param id
	 * @param type
	 * @param title
	 * @param tag
	 * @param descp
	 * @return
	 */
	private Map<String, Object> getMetaInfo(Map<String, Object> dataMap,
                                            String id,
                                            Integer type,
                                            String title,
                                            String tag,
                                            String descp){
		MetaInfo metaInfo = singlePageService.getSeoMetaInfo(Integer.parseInt(id), type);
		if(metaInfo == null){
			dataMap.put("title", title+"-美丽修行网");
			if(StringUtils.isEmpty(tag.trim())){
				dataMap.put("keywords", title);
			}else{
				dataMap.put("keywords", tag);
			}
			descp = descp.replaceAll("\\<.*?>", "").replaceAll("\\s*|\t|\r|\n","").trim();
			if(descp.length()>0 && descp.length()>200){
				descp = StringUtil.subTextString(descp, 200);
			}
			dataMap.put("description", descp);
		}else{
			dataMap.put("title", metaInfo.getTitle());
			dataMap.put("keywords",metaInfo.getKeywords());
			dataMap.put("description",metaInfo.getDescription());
		}
		return dataMap;
	}

	/**
	 * 准备行业资讯信息
	 * @param id
	 * @return
	 */
	private Map<String, Object> prepareIndustryStaticInfo(String id){
		Industry industry = industryOldMapper.findById(Long.parseLong(id));
		String title = industry.getTitle();
		String descp = industry.getDescp();
		descp = descp.replaceAll("<h4 class=\"card_sub_title\"><span", "<h4 class=\"card_sub_title\"><a");
		descp = descp.replaceAll("</span></span></h4>", "</span></a></h4>");
		Map<String, Object> dataMap = new HashMap<String, Object>();
		//侧边栏
		dataMap = sidebarService.getSidebar(dataMap);
		//普通静态资源
		dataMap = SinglePageService.getStaticInfo(dataMap, "pc", "industry");
		dataMap.put("id", industry.getId());
		dataMap.put("header_image", industry.getHeaderImage());
		dataMap.put("title", title);
		dataMap.put("descp", descp);
		String tag = industry.getTag();
		if(tag != null){
			String[] tags = tag.split(",");
			dataMap.put("tag", tags);
		}else{
			dataMap.put("tag", new String[0]);
		}
		//取n作为图片cdn的随机数 0/1/2
		dataMap.put("n", new Random().nextInt(2));

		dataMap = getMetaInfo(dataMap, id, 11, title, tag, descp);
		return dataMap;

	}

	/**
	 * 静态化行业资讯
	 * @param id
	 * @return
	 * @throws IOException
	 */
	public Boolean staticIndustryPage(String id) throws IOException {
		Map<String, Object> dataMap = prepareIndustryStaticInfo(id);
		String uploadPath = "industry/"+id+".html";
		staticRecordService.insertOrUpdate(id+"", "pc","industry",1, uploadPath);
		return SinglePageService.staticGeneralPage(dataMap, "pc", "industry", uploadPath);
	}

	/**
	 * 准备发现文章信息
	 * @param id
	 * @param platform
	 * @return
	 */
	private Map<String, Object> prepareFindStaticInfo(String id, String platform){
		if (platform.equals("m")){
			platform = "mobile";
		}
		//info详情
		String find_api_url = ConfUtils.mps.get("url") + "find/info/" + id;
		String res = HttpUtils.get(find_api_url);
		JSONObject resJson = JSONObject.fromObject(res);
		//评论
		String find_comment_url = ConfUtils.mps.get("url") + "entity/comment4/lists/find";

		if(resJson.has("ret")) {
			int ret = resJson.getInt("ret");
			if (ret == 0) {
				Map<String, Object> dataMap = new HashMap<String, Object>();
				//侧边栏
				if("pc".equals(platform)){
					dataMap = sidebarService.getSidebar(dataMap, 0, Integer.parseInt(id));
				}
				//普通静态资源
				dataMap = SinglePageService.getStaticInfo(dataMap, platform, "find");
				String descp = resJson.getJSONObject("result").getString("descp");
				String title = resJson.getJSONObject("result").getString("title");
				String tag = resJson.getJSONObject("result").getString("tag");

				//兼容苹果和旧版本app中的发现文章，将span改为a
				descp = descp.replaceAll("<span href=", "<a href=");
				descp = descp.replaceAll("</span></span>", "</span></a>");
				descp = descp.replaceAll("<span  a-href", "<a a-href");


				String regex = "\\.\\./goods/info\\.html\\?id=(\\w{32})";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(descp);
				while(matcher.find()) {
					descp = descp.replace(matcher.group(), "/product/" + matcher.group(1) + ".html");
				}

				Map<String, Object> commentData = SinglePageService.getCommentInfo(id, find_comment_url);
				JSONArray commentList = (JSONArray) commentData.get("commentList");
				int commentTotal = (int) commentData.get("total");
				dataMap.put("commentTotal", commentTotal);
				dataMap.put("comments", commentList);
				dataMap.put("id", id);
				dataMap.put("content", descp);
				dataMap.put("descp", descp.replaceFirst("<div class=\"page_head\">\r\n.*?\r\n</div>\r\n", ""));
				dataMap.put("article_title", title);
				dataMap.put("tags", tag.trim().split(","));

				dataMap = getMetaInfo(dataMap, id, 1, title, tag, descp);
				return dataMap;
			}
		}
		return null;
	}

	/**
	 * 静态化发现pc
	 * @param id
	 * @return
	 * @throws IOException
	 */
	private Boolean staticFindPCPage(String id) throws IOException {
		Map<String, Object> dataMap = prepareFindStaticInfo(id, "pc");
		String uploadPath = "find/"+id+".html";
		staticRecordService.insertOrUpdate(id+"", "pc","find",1, uploadPath);
		return SinglePageService.staticGeneralPage(dataMap, "pc", "find", uploadPath);
	}

	/**
	 * 静态化发现移动站
	 * @param id
	 * @return
	 * @throws IOException
	 */
	private Boolean staticFindMPage(String id) throws IOException {
		Map<String, Object> dataMap = prepareFindStaticInfo(id, "mobile");
		String uploadPath = "find/"+id+".html";
		staticRecordService.insertOrUpdate(id+"", "mobile","find",1, uploadPath);
		return SinglePageService.staticGeneralPage(dataMap, "mobile", "find", uploadPath);
	}

	/**
	 * 静态化发现微信页面
	 * @param id
	 * @return
	 * @throws IOException
	 */
	private Boolean staticFindWXPage(String id) throws IOException {
		Map<String, Object> dataMap = prepareFindStaticInfo(id, "mobile");
		return SinglePageService.staticGeneralPage(dataMap, "mobile", "wx_find", "app_share/article/"+id);
	}

	/**
	 * 静态化发现app
	 * @param id
	 * @return
	 * @throws IOException
	 */
	private Boolean staticFindAppPage(String id) throws IOException {
		Map<String, Object> dataMap = prepareFindStaticInfo(id, "mobile");
		return SinglePageService.staticGeneralPage(dataMap, "mobile", "app_find", "app/article/"+id);
	}

	/**
	 * 行业资讯回源
	 * @param id
	 * @return
	 * @throws IOException
	 */
	public String getBackIndustry(String id) throws IOException {
		Map<String,Object> dataMap = prepareIndustryStaticInfo(id);
		if(dataMap == null){
			logger.error("pc站行业咨询文章模板:数据读取出错！");
		}else{
			String html = FreemarkerService.getHtml("industry", "pc", dataMap, true);
			if(!StringUtils.isEmpty(html)){
				OSSService.uploadHtml2OSS(html, "industry/"+id+".html", "pc");
				return html;
			}else{
				logger.error("pc站行业咨询文章模板:生成出错！");
			}
		}
		return "404";
	}

	/***
	 * 没查到的发现文章处理
	 * @param id
	 * @return
	 */
	public String getBackFind(String id, String type) throws IOException {
		String ftlName=null;
		String path=null;
		String platform=null;
		switch(type){
			case "pc":
				ftlName = "find";
				path = "find/"+id+".html";
				platform = "pc";
				break;
			case "mobile":
				ftlName = "find";
				path = "find/"+id+".html";
				platform = "mobile";
				break;
			case "wx":
				ftlName = "wx_find";
				path = "app_share/article/"+id;
				platform = "mobile";
				break;
			case "app":
				ftlName = "app_find";
				path = "app/article/"+id;
				platform = "mobile";
				break;
			default:
		}
		Map<String,Object> dataMap = prepareFindStaticInfo(id, platform);
		if(dataMap == null){
			logger.error(platform + "站" + type + "文章模板" + ":发现生成出错！");
		}else{
			if(ftlName != null){
				String html = FreemarkerService.getHtml(ftlName, platform, dataMap, true);
				if(!StringUtils.isEmpty(html)){
					OSSService.uploadHtml2OSS(html, path, platform);
					return html;
				}else{
					logger.error(platform + "站" + type + "文章模板" + ":发现生成出错！");
				}
			}else{
				logger.error(type + "文章模板" + ":回源操作type传参错误！");
			}

		}
		return "404";
	}

	/**
	 * 行业资讯静态化
	 * @param id
	 */
	@Deprecated
	public Boolean industryStatic(Integer id) throws ClientProtocolException {
		boolean result = false;
		try {
			List<Map<String,String>> json = ConfUtils.getList("industry");
			for (Map<String, String> info : json) {
				String platform = info.get("platform");
				String ftlName = info.get("ftl");
				String js=ConfUtils.mps.get("pc_js");
				String css=ConfUtils.mps.get("pc_css");
				String version = ConfUtils.mps.get("version");
				String[] imgs =  ConfUtils.imgs;
				String img = imgs[(new Random().nextInt(2))];
				Industry industry=	industryOldMapper.findById(id);
				if(industry!=null){
					JSONObject dataObj = JSONObject.fromObject(industry);
					String descp = industry.getDescp().replaceAll("\\<.*?>", "").replaceAll("\\s*|\t|\r|\n","").trim();
					if(descp.length()>0 && descp.length()>200){
						descp = descp.substring(0,200)+"...";
					}
					Map<String, Object> context = new HashMap<String, Object>();
					// 将json字符串加入数据模型
					context.put("data", dataObj);
					context.put("staticType", "industry");
					context.put("js", js);
					context.put("css", css);
					context.put("img", img);
					context.put("version", version);
					context.put("title", industry.getTitle()+"-美丽修行网");
					context.put("keywords", industry.getTag());
					context.put("description", descp);

					result = freemarkerService.createHtmlFile2PC(ftlName, id+"", context);
					if(result){
						String filePath = FreemarkerService.freemarkerPath+"/"+platform+"/"+id+".html";
						String uploadPath="industry/"+id+".html";
						OSSService.upload2OSS(filePath,uploadPath,platform);
						staticRecordService.insertOrUpdate(id+"",platform,"industry",1,uploadPath);
					}
				}else{
					staticRecordService.insertOrUpdate(id+"",platform,"industry",0,null);
					logger.error("未查询到结果！");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e.getStackTrace());
		}
		return result;
	}

	/***
	 * 没查到的发现文章处理
	 * @param id
	 * @param platform
	 * @return
	 */
	@Deprecated
	public Map<String,Object> getBackIndustry(Integer id, String platform) throws ClientProtocolException {
		Map<String,Object> map = new HashMap<String, Object>();
		Industry industry=	industryOldMapper.findById(id);
		if(industry!=null){
			JSONObject dataObj = JSONObject.fromObject(industry);
			String descp = industry.getDescp().replaceAll("\\<.*?>", "").replaceAll("\\s*|\t|\r|\n","").trim();
			if(descp.length()>0 && descp.length()>200){
				descp = descp.substring(0,200)+"...";
			}
			String[] imgs =  ConfUtils.imgs;
			String img = imgs[(new Random().nextInt(2))];
			// 将json字符串加入数据模型
			map.put("data", dataObj);
			map.put("staticType", "find");

			map.put("img", img);
			map.put("title", industry.getTitle()+"-美丽修行网");
			map.put("keywords", industry.getTag());
			map.put("description", descp);
			if("pc".equals(platform)){
				map.put("path", "pc/industry");
				map.put("js", ConfUtils.mps.get("pc_js"));
				map.put("css", ConfUtils.mps.get("pc_css"));
			}else{
				map.put("path","mobile/industry");
				map.put("js", ConfUtils.mps.get("wx_js"));
				map.put("css", ConfUtils.mps.get("wx_css"));
			}
			industryStatic(id);
		}else{
			logger.error("查询接口数据失败!出错id:"+id);
			map.put("path","404");
		}

		return map;
	}


	/***
	 * 发现批量静态化
	 * @param page
	 */
	public void findStatics(Integer page){
		List<Find> list =findByPage(page);
		for (Find find : list) {
			try {
				//findStatic(find.getId()+"");
				//微信分享页面静态化
				String id = find.getId().toString();
				//singlePageService.staticFindPage("wx_find", "app_share/article/" + id, id, "m");
				//app webview页面
				//singlePageService.staticFindPage("app_find", "app/article/" + id, id, "m");
				staticFindPage(id);
				//app内文章json,兼容旧版
				singlePageService.staticFindJson(find.getId().intValue());
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/***
	 * 行业资讯批量静态化
	 * @param page
	 */
	public void industryStatics(Integer page){
		List<Industry> list =industryList(page);
		for (Industry industry : list) {
			try {
				industryStatic(industry.getId());
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 发现列表
	 * @param page
	 * @return
	 */
	private List<Find> findByPage(Integer page) {
		Paged<Find> paged = new Paged<Find>();
		paged.setCurPage(page);
		paged.addOrderBy("id", "desc");
		return findOldMapper.findByPage(paged);
	}

	/**
	 * 行业资讯列表
	 * @param page
	 * @return
	 */
	private List<Industry> industryList(Integer page) {
		Paged<Industry> paged = new Paged<Industry>();
		paged.setCurPage(page);
		paged.addOrderBy("id", "desc");
		return industryOldMapper.industryByPage(paged);
	}
	/***
	 * 初始化发现线程
	 */
	public Boolean initFindStatic(int arg){
		if(arg ==1 ||arg ==8) {
			int j = 0;
			final BlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>();
			int tatolPage = findOldMapper.selectTotal() / 20 + 1;
			logger.info("共" + tatolPage + "页");
			for (int i = 1; i <= tatolPage; i++) {
				try {
					queue.put(i);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			while (j < 3) {
				Executors.newFixedThreadPool(3).submit(new Runnable() {
					@Override
					public void run() {
						try {
							int page = 1;
							while (!queue.isEmpty()) {
								try {
									page = queue.take();

									if(page != 0) {
										try {
											findStatics(page);
										} catch (Exception e) {
											e.printStackTrace();
											queue.put(page);
										}
									}
								} catch (Exception e) {
									//异常页码直接跳过  todo 后期处理异常情况
									logger.error(e.getMessage(), e.getCause());
								}

								logger.info("第" + page + "页成功！");
								Thread.sleep(500);
							}

						} catch (Exception e) {
							logger.error(e.getMessage(), e.getStackTrace());
						}

					}
				});
				j++;
			}
		}
		return true;
	}
	/***
	 * 初始化行业资讯线程
	 */
	public void initIndustryStatic(int arg){
		if(arg ==2 ||arg ==8){
			Executors.newFixedThreadPool(1).submit(new Runnable() {
				public void run() {
					while (true) {
						try {
							BlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>();
							int tatolPage =  industryOldMapper.selectTotal()/20 +1;
							for (int i = 1; i <= tatolPage; i++) {
								queue.put(i);
							}
							int page = 1;
							while (true) {
								try {
									page = queue.take();
									try {
										industryStatics(page);
									} catch (Exception e) {
										e.printStackTrace();
									}
								} catch (Exception e) {
									//异常页码直接跳过  todo 后期处理异常情况
									logger.error(e.getMessage(), e.getCause());
								}

								logger.info("第"+page+"页成功！");
								Thread.sleep(500);
							}

						} catch (Exception e) {
							logger.error(e.getMessage(), e.getStackTrace());
						}

					}


				}
			});
		}
	}


	/**
	 * 查询所有标签统计
	 * @return
	 */
	public Map<String, Integer> findTags() {
		Paged<Find> paged = new Paged<Find>();
		List<Tags> ls = new ArrayList<Tags>();
		int totalPage = findOldMapper.selectTotal() / 200 + 1;
		for (int i = 1; i <= totalPage; i++) {
			paged.setCurPage(i);
			paged.setPageSize(200);
			List<Tags> tmpList = findOldMapper.findTagByPage(paged);
			ls.addAll(tmpList);
		}
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (Tags find : ls) {
			if (find != null) {
				String tags = find.getId();
				int cNum = 1;
				String[] split = tags.split(",");
				for (String s : split) {
					if (map.containsKey(s)) {
						Integer tNum = map.get(s);
						++tNum;
						cNum = tNum;
					}
					map.put(s, cNum);
				}
			}
		}
		return map;
	}

	/**
	 * @param page
	 * @param tagId
	 * @return
	 */
	public Paged getTagFindList(Integer tagId, Integer page, Integer pageSize) {
		Paged<Find> paged = new Paged<Find>();
		paged.setPageSize(pageSize);
		paged.setCurPage(page);
		paged.setTotal(findOldMapper.selectTotalByTag(tagId));
		paged.setResult(findOldMapper.getFindByTagId(tagId,page,pageSize));
		return paged;
	}

	@Override
	public Map<String, Object> getStaticInfo(Map<String, Object> dataMap) {
		String init = HttpUtils.post(dataMap.get("url") + "/init7", new HashMap<String, String>());
		JSONObject initObj = JSONObject.fromObject(init);
		if(initObj.has("ret") && initObj.getInt("ret") == 0){
			//
			HashMap<String, String> param = new HashMap<String, String>();
			param.put("sort_type", "0");
			param.put("pager", "1");
			param.put("pageSize", "10");
			param.put("type", "");
			//最新数据   sort_type = 0
			String findDataString2 = HttpUtils.post(dataMap.get("url") +"entity/list2/find", param);
			JSONObject findData2 = JSONObject.fromObject(findDataString2);
			int findTotal = findData2.getInt("total");
			JSONArray findDataArr2 = findData2.getJSONArray("result");
			ArrayList<Long> idList2 = new ArrayList<Long>();
			for(Object obj : findDataArr2){
				JSONObject jsonObject = JSONObject.fromObject(obj);
				idList2.add(jsonObject.getLong("id"));
			}
			findDataArr2 = SinglePageService.getEntityState("find", idList2, findDataArr2);
			dataMap.put("findDataArr2", findDataArr2);
			//最热数据 sort_type = 1
			param.put("sort_type", "1");
			String findDataString1 = HttpUtils.post(dataMap.get("url") +"entity/list2/find", param);
			JSONObject findData1 = JSONObject.fromObject(findDataString1);
			JSONArray findDataArr1 = findData1.getJSONArray("result");
			ArrayList<Long> idList1 = new ArrayList<Long>();
			for(Object obj : findDataArr1){
				JSONObject jsonObject = JSONObject.fromObject(obj);
				idList1.add(jsonObject.getLong("id"));
			}
			findDataArr2 = SinglePageService.getEntityState("find", idList1, findDataArr1);
			dataMap.put("findDataArr1", findDataArr1);
			//获取发现文章分类信息
//			JSONArray findType = initObj.getJSONObject("result").getJSONArray("findtype");
//			dataMap.put("findType", findType);
			//获取美修原创数据
			JSONObject xxsButton = initObj.getJSONObject("result").getJSONArray("xxsButton").getJSONObject(0);
			xxsButton.put("total", findTotal);
			dataMap.put("xxsButton", xxsButton);
		}
		return dataMap;
	}

	@Override
	public Map<String, Object> getStaticLoopInfo(Map<String, Object> dataMap, Integer curPage) {
		return null;
	}

	public static void main(String[] args) throws IOException {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/*.xml");
		StaticFindService staticFindService = (StaticFindService) context.getBean("staticFindService");
		//findService.staticIndustryPage("30");
		staticFindService.staticFindPage("426");
		//findService.findStatic("22");
		//findService.initIndustryStatic(2);
		//findService.initFindStatic(1);
		//findService.findTags();
		//findService.staticFindMPage("401");
		//findService.staticFindMPage("402");
		//findService.staticFindPage("384");
	}

}
