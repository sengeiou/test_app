package cn.bevol.app.service;


import cn.bevol.app.cache.CacheKey;
import cn.bevol.app.cache.CacheableTemplate;
import cn.bevol.app.cache.MakeCache;
import cn.bevol.app.cache.redis.RedisCacheProvider;
import cn.bevol.app.dao.db.Paged;
import cn.bevol.app.dao.mapper.CompositionOldMapper;
import cn.bevol.app.entity.model.Composition;
import cn.bevol.app.entity.model.Used;
import cn.bevol.util.ConfUtils;
import cn.bevol.util.StringUtil;
import cn.bevol.util.cache.CACHE_NAME;
import cn.bevol.util.http.HttpUtils;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import flexjson.JSONDeserializer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CompositionService extends BaseService {
	private static Logger logger = LoggerFactory.getLogger(CompositionService.class);


	@Autowired
	RedisCacheProvider cacheProvider;

	@Autowired
	CompositionOldMapper compositionMapper;

	/**
	 * 获取该产品的所有成分列表
	 * @return
	 */
	public List<Composition> getCompositionByIds(String ids) {
		try {
			//根据成分id 获取成分列表
			List<Composition> list=new ArrayList<Composition>();
			if(StringUtils.isNotBlank(ids)){
				String[] idss=ids.split(",");
				for(int j=0;j<idss.length;j++){
					//在所有產品中查找
					Long id= Long.parseLong(idss[j]);
					//获取成分和成分的使用目的
					Composition composition=getCompositionById(id);
					if(composition!=null&&composition.getId()>0) {
						if(composition.getPid()>0){
							Composition pComposition=getCompositionById(composition.getPid());
							if(pComposition!=null) {
								long pid = pComposition.getId();
								long curid = composition.getId();
								pComposition.setPid(pid);
								pComposition.setId(curid);
								pComposition.setTitle(composition.getTitle());
								composition = pComposition;
							}
						}
						list.add(composition);
					}
				}
			}
			return list;
		} catch (Exception e) {
			logger.error("method:getCompositionByIds arg:{"  +ids+ "   desc:" +  ExceptionUtils.getStackTrace(e));
		}
		return null;
	}


	/**
	 * 获取成分使用目的(hq_composition,hq_used)
	 *
	 * @return
	 */
	public Composition getCompositionById(final long id) {
		return new CacheableTemplate<Composition>(cacheProvider) {
			@Override
			protected Composition getFromRepository() {
				try {
					Composition cp = compositionMapper.getById(id);
					if (!StringUtils.isBlank(cp.getUsed())) {
						List<Used> useds = compositionMapper.getUsedsByUid(cp.getUsed());
						cp.setUseds(useds);
					}
					//获取成分使用目的(..保湿剂中含有的成分的使用目的)
					return cp;
				} catch (Exception e) {
					logger.error("method:getCompositionById arg:{" + "   desc:" + ExceptionUtils.getStackTrace(e));
				}
				return null;
			}

			@Override
			protected boolean canPutToCache(Composition returnValue) {
				return (returnValue != null && returnValue.getId() > 0);
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FOREVER_CACHE_QUEUE,
				CACHE_NAME.createInstanceForeverKey(CACHE_NAME.INSTANCE_COMPOSITION_ID_OR_MID_PREFIX , id+"")), true);
	}

	static Map<Long,Composition> cps=new HashMap<Long,Composition>();


	/**
	 * 获取成分使用目的(hq_composition,hq_used)
	 *
	 * @return
	 */
	public Composition getCompositionByMid(final String mid) {
		return new CacheableTemplate<Composition>(cacheProvider) {
			@Override
			protected Composition getFromRepository() {
				try {
					Composition cp = compositionMapper.getByMid(mid);
					if (!StringUtils.isBlank(cp.getUsed())) {
						List<Used> useds = compositionMapper.getUsedsByUid(cp.getUsed());
						cp.setUseds(useds);
						/*StringBuffer sb=new StringBuffer();
                    	for(int i=0;i<useds.size();i++) {
                    		sb.append(useds.get(i).getTitle());
                    		if(i!=useds.size()-1) {
                    			sb.append(",");
                    		}
                    	}
                    	cp.setUsedName(sb.toString());*/
					}
					//获取成分使用目的(..保湿剂中含有的成分的使用目的)
					return cp;
				} catch (Exception e) {
					logger.error("method:getCompositionById arg:{" + "   desc:" + ExceptionUtils.getStackTrace(e));
				}
				return null;
			}

			@Override
			protected boolean canPutToCache(Composition returnValue) {
				return (returnValue != null && returnValue.getId() > 0);
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FOREVER_CACHE_QUEUE,
				CACHE_NAME.createInstanceForeverKey(CACHE_NAME.INSTANCE_COMPOSITION_ID_OR_MID_PREFIX , mid)), true);
	}



	public ReturnListData<Composition> compares(List<String> namelists) {
		//没有过滤的字段
		//过滤了的字段
		List<String> filterName=new ArrayList<String>();
		/**
		 * 过滤和没有过滤字段的关系
		 */
		Map<String,String> kyFilter=new HashMap<String,String>();

		for(int i=0;i<namelists.size();i++) {
			if(!StringUtils.isBlank(namelists.get(i))) {
				namelists.set(i,namelists.get(i));
				//替换 -
				String fv=compositionNameFilter(namelists.get(i));
				filterName.add(fv);
				kyFilter.put(fv, namelists.get(i));
			}
		}
		if(filterName.size()==0) return ReturnListData.ERROR;
		List<Composition> cps=compositionMapper.findByNames(filterName);
		//返回给用户的数据
		List<Composition> displayData=new ArrayList<Composition>();
		//记录没有匹配上的；
		List<String> notDatas=new ArrayList<String>();

		//在成分表中找出匹配上的和匹配不上的
		for(int i=0;i<filterName.size();i++) {
			String cpname=filterName.get(i);
			boolean flag=false;
			Composition cpt=new Composition();
			if(cps!=null) {
				for(int j=0;!flag&&j<cps.size();j++) {
					if(StringUtils.isBlank(cps.get(j).getFliteredTitle()))
						cps.get(j).setFliteredTitle(compositionNameFilter(cps.get(j).getTitle()));
					if(StringUtils.equalsIgnoreCase(cps.get(j).getFliteredTitle(), cpname)||StringUtils.equalsIgnoreCase(cps.get(j).getEnglish(), cpname)) {
						flag=true;
						if(cps.get(j).getPid()>0) {
							cpt=this.compositionMapper.getById(Long.parseLong(cps.get(j).getPid()+""));
							//原成分id
							cpt.setOid(cps.get(j).getId());
						} else {
							cpt=cps.get(j);
						}
					}
				}
			}

			if(!flag) {
				boolean issarch=false;
				//二次查询
				String sm=replaceByS(cpname);
				if(!sm.equals(cpname)) {
					Composition  cxs=seSearchComposition(sm);
					if(cxs!=null) {
						cpt=cxs;
						issarch=true;
					}
				}else  {
					String s2=replaceByS2(cpname);
					if(!s2.equals(cpname)) {
						Composition  cxs=seSearchComposition(s2);
						if(cxs!=null) {
							cpt=cxs;
							issarch=true;
						}
					}
				}

				if(!issarch) {
					notDatas.add(cpname);
				}
			}
			cpt.setUserTitle(kyFilter.get(cpname));
			cpt.setFliteredTitle(cpname);
			displayData.add(cpt);


		}

		//在脏数据表中找出匹配上的和插入匹配不上的，且匹配上的根据pid找到 父级成分
		List<Composition> zsj=new ArrayList<Composition>();
		Map<String,Long> noD=new HashMap<String,Long>();
		if(notDatas.size()>0) {
			//插入脏数据库
			List<Composition> tmps=compositionMapper.findTmpByNames(notDatas);
			for(int i=0;i<notDatas.size();i++) {
				boolean flag=false;
				String ndname=notDatas.get(i);
				int clg=tmps.size();
				for(int j=0;!flag&&j<clg;j++) {
					if(StringUtils.isBlank(tmps.get(j).getFliteredTitle()))
						tmps.get(j).setFliteredTitle(compositionNameFilter(tmps.get(j).getTitle()));
					if(StringUtils.equalsIgnoreCase(tmps.get(j).getFliteredTitle(), ndname)) {
						Long did=tmps.get(j).getId();
						Composition cp=null;
						if(tmps.get(j).getPid()>0) {
							cp=this.compositionMapper.getById(Long.parseLong(tmps.get(j).getPid()+""));
						} else {
							cp=tmps.get(j);
						}
						cp.setUserTitle(kyFilter.get(ndname));
						cp.setFliteredTitle(ndname);
						cp.setDirtyId(did);
						zsj.add(cp);
						flag=true;
					}
					//新插入的
				}
				if(!flag) {
					//插入
					// DateUtils.timeStampParseHtml5Date((int)new Date().getTime()/1000)
					Composition m=new Composition();
					m.setTitle(ndname);
					m.setCrdate(new Date());
					int code=compositionMapper.insertdirtyComposition(m);
					noD.put(ndname, m.getId());
				}
			}
		}
		for(int i=0;i<displayData.size();i++) {
			String utitle=displayData.get(i).getFliteredTitle();
			for(int j=0;j<zsj.size();j++) {
				if(utitle.equals(zsj.get(j).getFliteredTitle())) {
					displayData.set(i, zsj.get(j));
				}
			}
			if(noD.get(utitle)!=null) {
				displayData.get(i).setDirtyId(Long.parseLong(noD.get(utitle)+""));
			}
		}

		return new ReturnListData<Composition>(displayData,displayData.size());


	}
	public ReturnListData<Composition> compares(String names) {
		Map<String,Composition> cps=findByLikeNames();
		for(String l:cps.keySet()) {
			Composition sn=cps.get(l);
			String title=sn.getTitle();
			String english=sn.getEnglish();
			if((StringUtils.indexOfIgnoreCase(names,title))!=-1) {
				int indexOf=StringUtils.indexOfIgnoreCase(names,title);
				int lastOf=indexOf+title.length();
				names=names.substring(0, indexOf)+sn.getId()+","+names.substring(lastOf);
			}else if(StringUtils.indexOfIgnoreCase(names,converEn(title))!=-1) {
				int indexOf=StringUtils.indexOfIgnoreCase(names, converEn(title));
				int lastOf=indexOf+title.length();
				names=names.substring(0, indexOf)+sn.getId()+","+names.substring(lastOf);
			}else if(StringUtils.indexOfIgnoreCase(names,converZh(title))!=-1) {
				int indexOf=StringUtils.indexOfIgnoreCase(names, converZh(title));
				int lastOf=indexOf+title.length();
				names=names.substring(0, indexOf)+sn.getId()+","+names.substring(lastOf);
			}else if(!StringUtils.isBlank(english)&&StringUtils.indexOfIgnoreCase(names, english)!=-1) {
				int indexOf=StringUtils.indexOfIgnoreCase(names, english);
				int lastOf=indexOf+english.length();
				names=names.substring(0, indexOf)+sn.getId()+"-enbevol"+","+names.substring(lastOf);

			}
		}
		//中文逗号
		names=names.replace("，", ",");
		//顿号
		names=names.replace("、", ",");
		//句号
		names=names.replace(".", ",");

		names=names.replace("\n", ",");

		names=names.replace("\r", ",");

		String namess[]=names.split(",");
		for(int i=0;i<namess.length;i++) {
			namess[i]=namess[i].trim();
			if(cps.get(namess[i])!=null) {
				//中文部分
				namess[i]=cps.get(namess[i]).getTitle();
			}   else {
				if(namess[i].indexOf("-enbevol")!=-1) {
					//因为部分
					namess[i]=cps.get(namess[i].replace("-enbevol", "")).getEnglish();
				}
			}
		}
		//1\
		if(namess.length>100) return Composition.COMPARES_LENGHT_MAX;
		return this.compares(Arrays.asList(namess));
	}

	public ReturnData getCompositionDetail(String mid, int pager, int paseSize) {
		Composition c=this.getCompositionByMid(mid);
		Map map =new HashMap();
		map.put("composition", c);
		//请求产品
		String goods= HttpUtils.get("http://search.bevol.cn/composition/goodslists/compositionid/" + c.getId() + "/p/"+pager+"/rows/"+paseSize);
		Map<String,Object> data=null;
		if(goods!=null) {
			Map<String,Object> jsonNode=  new JSONDeserializer<Map<String,Object>>().deserialize(goods, HashMap.class);
			data=(Map<String, Object>) jsonNode.get("data");
			if(data!=null) {
				map.put("goods", data.get("items"));
				map.put("goodsTotal", data.get("total"));
			}
		}
		return new ReturnData<>(map);
	}

	/**
	 * 二次查询成分
	 * @param str
	 * @return
	 */
	private  Composition seSearchComposition(String str) {
		Composition  cxs=compositionMapper.findByName(str);
		Composition cpt=null;
		if(cxs!=null) {
			if(cxs.getPid()>0) {
				cpt=this.compositionMapper.getById(Long.parseLong(cxs.getPid()+""));
				//原成分id
				cpt.setOid(cxs.getId());
			} else {
				cpt=cxs;
			}
		}
		return cpt;
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
		for(String a:replaceAfter) {
			if(StringUtils.isNotBlank(sp)) {
				//非*开头
				//hualong 2017 03 24 * ddd 星号开头的不去掉
				if(!(sp.trim().charAt(0)+"").equals(a)) {
					sp=StringUtils.substringBefore(sp, a);
				}
			}
		}

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


	public static String converEn(String str){
		return str.replaceAll("，",",").replaceAll("。", ".");
	}

	public static String converZh(String str) {
		return str.replaceAll(",","，").replaceAll(".","。");
	}

	/**
	 * 成分过滤
	 * @return
	 */
	private Map<String,Composition> findByLikeNames() {
		return new CacheableTemplate<Map<String,Composition>>(cacheProvider) {
			@Override
			protected Map<String,Composition> getFromRepository() {
				try {
					List<String> ls=new ArrayList<String>();
					ls.add(",");
					ls.add("，");
					ls.add("、");
					ls.add(".");
					ls.add("。");
					Map<String,Composition> mcps=new HashMap<String,Composition>();
					List<Composition> cps=compositionMapper.findByLikeNames(ls);
					for(int i=0;i<cps.size();i++) {
						mcps.put(cps.get(i).getId()+"", cps.get(i));
					}
					return mcps;
				} catch (Exception e) {
					logger.error("method:getCompositionById arg:{"  + "   desc:" +  ExceptionUtils.getStackTrace(e));
				}
				return null;
			}
			@Override
			protected boolean canPutToCache(Map<String,Composition> returnValue) {
				return (returnValue != null && returnValue.size()>0);
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
				"CompositionService.findByLikeNames" ), true);

	}


	/*
	todo   放到工具类
	 * 验证是否包含中文字符
	 */
	public static boolean isChineseChar(String str){
		boolean temp = false;
		Pattern p= Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m=p.matcher(str);
		if(m.find()){
			temp =  true;
		}
		return temp;
	}
	public static String replaceByS(String str) {
		return str.replaceAll("（", "(").replaceAll("）", ")").replaceAll("\\(.*\\)", " ").replaceAll(" +"," ");
	}

	public static String replaceByS2(String str) {
		String fps[]=new String[]{"PEG","PPG"};
		//找到 确定哪一个
		boolean flag=false;
		String pf="";
		str=str.replaceAll(" +"," ");
		for(int i=0;i<fps.length&&!flag;i++) {
			int istr=str.indexOf(fps[i]);
			if(istr!=-1) {
				pf=fps[i];
				int isf=(istr+fps[i].length());
				String sz=str.substring(isf,isf+1);
				//空格
				if(sz.equals(" ")) {
					//去空格
					sz=str.substring(isf+1,isf+2);
					isf=isf+1;
				}
				//检查是否为数组
				if(org.apache.commons.lang3.math.NumberUtils.isNumber(sz)&&!sz.equals("-")){
					flag=true;
					String l1=str.substring(0, isf).trim();
					String l2=str.substring( isf).trim();
					return (l1+"-"+l2);
				}
			}
		}
		return str;
	}


	public static String replaceSpaceBySlit(String str) {
		String regex = "\\w+\\s+\\d+";
		//   String str = "d 4asdf afsaf";
		boolean flag=false;
		Pattern pat = Pattern.compile(regex);
		Matcher matcher = pat.matcher(str);
		while (matcher.find()) {
			String ms = str.substring(matcher.start(),matcher.end());
			ms=ms.replaceAll(" +"," ").replaceAll(" ", "-");
			String md = str.substring(0,matcher.start())+ms+str.substring(matcher.end());
			flag=true;
			return md;
		}
		if(!flag) {
			regex = "\\d+\\s+\\w+";
			pat = Pattern.compile(regex);
			matcher = pat.matcher(str);
			while (matcher.find()) {
				String ms = str.substring(matcher.start(),matcher.end());
				ms=ms.replaceAll(" +"," ").replaceAll(" ", "-");
				String md = str.substring(0,matcher.start())+ms+str.substring(matcher.end());
				flag=true;
				return md;
			}
		}
		return str;
	}
	public static void main(String[] args) {
		String sb="ss(52savsda 223vs";
		System.out.println(replaceSpaceBySlit(sb));

		sb="223  ss(52savsda";
		System.out.println(replaceSpaceBySlit(sb));

		System.out.println(replaceByS("变性(dsaf)乙醇"));

	}

	/**
	 * 缓存所有的成分
	 * @return
	 */
	/**
	 * 缓存所有的成分
	 * @return
	 */
	public List<Composition> getAllComposition() {
		return new CacheableTemplate<List<Composition>>(cacheProvider) {
			@Override
			protected List<Composition> getFromRepository() {
				try {
					List<Composition> allTags=compositionMapper.getAll();
					return allTags;
				} catch (Exception e) {
					logger.error("method:getAllComposition arg:{" + "   desc:" +  ExceptionUtils.getStackTrace(e));
				}
				return null;
			}
			@Override
			protected boolean canPutToCache(List<Composition> returnValue) {
				return (returnValue != null);
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_2DAY_CACHE_QUEUE,
				"CompositionService.getAllComposition_"+CACHE_NAME.VERSION), true);
	}

	/**
	 * 获取所有成分使用目的(hq_used)
	 *
	 * @return
	 */
	public List<Used> getAllUsed() {
		return new CacheableTemplate<List<Used>>(cacheProvider) {
			@Override
			protected List<Used> getFromRepository() {
				try {
					List<Used> useds = compositionMapper.getAllUsed();
					//获取成分使用目的(..保湿剂中含有的成分的使用目的)
					return useds;
				} catch (Exception e) {
					logger.error("method:getAllCompositionUsed arg:{" + "   desc:" + ExceptionUtils.getStackTrace(e));
				}
				return null;
			}

			@Override
			protected boolean canPutToCache(List<Used> returnValue) {
				return (returnValue != null && returnValue.size() > 0);
			}
		}.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.THIRTY_2DAY_CACHE_QUEUE,
				CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_COMPOSITION_USED_PREFIX)), true);
	}

	/**
	 * 缓存预热用  查询主key<id   mid>分页
	 *
	 * @param paged
	 * @return
	 */
	public List findCompositionKeyIdByPage(Paged paged) {
		return compositionMapper.findCompositionKeyIdByPage(paged);
	}
	
	/**
     * 清除逗号等标记
     * @param names
     * @return
     */
	public ReturnListData cleanMarkNames(String names) {
		// TODO Auto-generated method stub
		List<String> array=new ArrayList<String>();
		if(!StringUtils.isBlank(names)) {
			String ns[]=names.split(",");
			for(int i=0;i<ns.length;i++) {
				array.add(StringUtil.strCleanMark(ns[i]));
			}
		}
		return new ReturnListData(array,array.size());
	}

	/**
	 * 缓存预热用  查询可用成分的总数
	 *
	 * @return
	 */
	public int selectTotal() {
		return compositionMapper.selectTotal();
	}

	public Composition getById(Long id) {
		return compositionMapper.getById(id);
	}

	public List<Used> getUsedsByUid(String used) {
		return compositionMapper.getUsedsByUid(used);
	}

	public Composition getByMid(String mid) {
		return compositionMapper.getByMid(mid);
	}


	/**
	 * v3.3
	 * 皂基成分
	 * @return
	 */
	@MakeCache
	public List<Integer> soapCps(){
		return compositionMapper.getSoapCps();
	}
}