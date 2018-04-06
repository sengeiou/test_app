package cn.bevol.internal.service;


import java.util.ArrayList;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.io97.cache.CacheKey;
import com.io97.cache.CacheableTemplate;
import com.io97.cache.redis.RedisCacheProvider;

import cn.bevol.cache.CACHE_NAME;
import cn.bevol.mybatis.dao.BackGoodsMapper;
import cn.bevol.mybatis.dao.GoodsMapper;
import cn.bevol.mybatis.dao.GoodsScanMapper;
import cn.bevol.mybatis.model.Goods;
import cn.bevol.mybatis.model.GoodsRule;
import cn.bevol.mybatis.model.GoodsTag;
import cn.bevol.mybatis.model.GoodsTagComposition;
import cn.bevol.mybatis.model.GoodsTagResult;
import cn.bevol.entity.service.BaseService;
import cn.bevol.entity.service.GoodsService;
import cn.bevol.log.LogException;
import cn.bevol.util.ReturnData;
import cn.bevol.util.ReturnListData;

/**
 * 产品标签相关的业务
 * @author chenHaiJian
 *
 */

@Service
public class BackGoodsService extends BaseService {
	private static Logger logger = LoggerFactory.getLogger(BackGoodsService.class);
	
	@Resource
	private GoodsService goodsService;
	
	@Autowired
	private BackGoodsMapper backGoodsMapper;
	
	@Autowired
	private GoodsMapper goodsMapper;
	
	 @Autowired
     RedisCacheProvider cacheProvider;
	 
	 @Autowired
     private GoodsScanMapper goodsScanMapper;
	
	 /**
	  * 新增产品标签和相应的标签规则
	  * @param tagName: 标签名
	  * @param rules: 标签规则  逗号分隔
	  * @return
	  */
 	 public ReturnData addTagRule(String tagName,String rules){
 		 try{
 			 if(!StringUtils.isBlank(tagName) && !StringUtils.isBlank(rules)){
 				//ValJason解析
 	 			int result=0;
 	 			GoodsTag goodsTag=new GoodsTag();
 	 			List<GoodsTag> gtList=goodsService.getAllGoodsTag();
 	 			for(GoodsTag gt:gtList){
 	 				//标签名已经存在
 	 				if(gt.getName().equals(tagName)){
 	 					return ReturnData.FAILURE;
 	 				}
 	 			}
 	 			
 	 			//标签名不存在插入
 	 			result=backGoodsMapper.addTag(tagName,new Date().getTime()/1000);
 	 			if(result!=1){
 	 				return ReturnData.FAILURE;
 	 			}
 	 			//获取插入的数据
 	 			goodsTag=backGoodsMapper.getTagByTagName(tagName);
 	 			if(null!=goodsTag && goodsTag.getId()!=0){
 	 				//添加标签对应的规则
 	 				result=backGoodsMapper.addTagRule(goodsTag.getId(),rules,goodsTag.getCreateStamp());
 	 			}
 	 			if(result!=1){
 	 				return ReturnData.FAILURE;
 	 			}
 			 }else{
 				 return ReturnData.FAILURE;
 			 }
 			return ReturnData.SUCCESS;
 		 }catch(Exception e){
 			Map map=new HashMap();
 			map.put("method", "BackGoodsService.addTagRule");
 			map.put("tagName", tagName);
 			map.put("rules", rules);
 			new cn.bevol.log.LogException(e,map);
 		 }
 		 return ReturnData.ERROR;
 	 }
 
 	 /**
 	  * 修改标签名或者标签规则
 	  * tagId: 产品标签id
 	  * tagName: 标签名
 	  * rules: 标签规则
 	  */
 	 public ReturnData editTagRule(long tagId,String tagName,String rules){
 		try{
 			int result=0;
 			if(StringUtils.isNotBlank(tagName)){
 				//修改标签名
 	 	 		result=backGoodsMapper.updateGoodsTag(tagId,tagName);
 	 	 		if(result==1){
 	 				return ReturnData.SUCCESS;
 	 			}
 			}
 	 		if(StringUtils.isNotBlank(rules)){
 	 			//修改标签规则
 	 			result=backGoodsMapper.updateGoodsTagRule(tagId,rules);
 	 	 		if(result==1){
 	 				return ReturnData.SUCCESS;
 	 			}
 	 		}
 	 		return ReturnData.FAILURE;
 		}catch(Exception e){
 			Map map=new HashMap();
 			map.put("method", "BackGoodsService.editTagRule");
 			map.put("tagId", tagId);
 			map.put("tagName", tagName);
 			map.put("rules", rules);
 			new cn.bevol.log.LogException(e,map);
 			return ReturnData.ERROR;
 		}
 	 }
 	 
 	 
 	 /**
 	  * 查询所有产品标签对应的规则
 	  * @param pager
 	  * @param pageSize
 	  * @return
 	  */
 	public ReturnListData findGoodsTag(final int pager,final int pageSize){
        try {
        	long start=0L;
        	if(pager>1){
        		start=Long.valueOf((pager-1)*pageSize);
        	}
        	List<GoodsTag> gtList=backGoodsMapper.getTag(start,pageSize);
        	List<GoodsRule> grList=goodsService.getAllGoodsRule();
        	List<Map<String,Object>> listMap=new ArrayList<Map<String,Object>>();
        	for(int i=0;i<gtList.size();i++){
        		List list=new ArrayList();
        		String tagName=gtList.get(i).getName();
        		long tagId=gtList.get(i).getId();
        		for(GoodsRule gr:grList){
        			if(gr.getTagId()==tagId){
        				list.add(gr);
        			}
        		}
        		gtList.get(i).setRuleList(list);
        	}
        	
    		long total=gtList.size();
        	return new ReturnListData(gtList,total);
        } catch (Exception e) {
        	Map map=new HashMap();
 			map.put("method", "BackGoodsService.findGoodsTag");
 			map.put("pager", pager);
 			map.put("pageSize", pageSize);
 			new cn.bevol.log.LogException(e,map);
 			return ReturnListData.ERROR;
        }
 	 }
 	
 	/**
 	 * 根据tagId查成分 
 	 * @param tag_id: 产品标签id
 	 * @param is_main: 是否是核心标签成分 1是,0不是
 	 * @param pager
 	 * @param pageSize
 	 * @return
 	 */
 	public ReturnListData findCpsByTagId(Long tagId,int isMain,int pager,int pageSize){
 		try{
 			if(null!=tagId && tagId!=0){
 				long start=0;
 				List<GoodsTagComposition> gtcList=null;
 				if(pager>1){
 					start=Long.valueOf((pager-1)*pageSize);
 				}
 				//根据is_main和tagId进行查询
				gtcList=backGoodsMapper.getTagCompositionByIsMain(tagId, isMain, pager, pageSize);
 				return new ReturnListData(gtcList,gtcList.size());
 			}else{
 				return ReturnListData.ERROR;
 			}
 		}catch(Exception e){
 			Map map=new HashMap();
 			map.put("method", "BackGoodsService.findCpsByTagId");
 			map.put("tagId", tagId);
 			map.put("isMain", isMain);
 			map.put("pager", pager);
 			map.put("pageSize", pageSize);
 			new cn.bevol.log.LogException(e,map);
 			return ReturnListData.ERROR;
 		}
 	}
 	
 	
 	/**
 	 * 添加标签成分关系,一个标签对应多个成分
 	 * @param tag_ids: 标签id,逗号分隔
 	 * @param composition_ids: 成分id,逗号分隔
 	 * @param is_main: 1为核心成分
 	 * @return
 	 */
	public ReturnData addTagComposition(String tagIds,String compositionIds,int isMain){
       try {
	       	if(!StringUtils.isBlank(tagIds) && !StringUtils.isBlank(compositionIds)){
	       		List<GoodsTagComposition> allGtcList= getAllGoodsTagComposition();
	       		String[] tag_idss=tagIds.split(",");
	       		String[] composition_idss=compositionIds.split(",");
	       		List<Map> listMap=new ArrayList<Map>();
	       		for(int i=0;i<tag_idss.length;i++){
	       			List<Long> existCpsList=new ArrayList<Long>();
	       			List<GoodsTagComposition> gtcList=new ArrayList<GoodsTagComposition>();
	       			for(GoodsTagComposition gtc:allGtcList){
	       				if(gtc.getTagId()==Long.valueOf(tag_idss[i])){
	       					//避免误操作,表中存在的标签才能添加成分
	       					gtcList.add(gtc);
	       				}
	       			}
	       			
	       			//查找存在的标签,用于排除
	       			if(gtcList.size()>0){
	       				for(GoodsTagComposition gtc:gtcList){
	       					for(int j=0;j<composition_idss.length;j++){
	       						if(gtc.getCompositionId()==Long.valueOf(composition_idss[j])){
	       							//要添加的成分存在某个标签里
	       							existCpsList.add(Long.valueOf(composition_idss[j]));
	       						}
	       					}
	       				}
	       			}
	       			
	       			//排除已经存在的成分,部分成分进行添加
	       			if(existCpsList.size()>0){
       					for(int j=0;j<composition_idss.length;j++){
       						Map<String,Object> map=new HashMap<String,Object>();
       						//排除存在的成分
	       					if(!existCpsList.contains(Long.valueOf(composition_idss[j]))){
	       						map.put("tagId", tag_idss[i]);
	       						map.put("compositionId", composition_idss[j]);
	       						map.put("status", 1);
	       						map.put("createStamp", new Date().getTime()/1000);
	       						map.put("isMain", isMain);
	       						if(isMain==1){
	       							map.put("mainName", "Y");
	       						}else{
	       							map.put("mainName", "");
	       						}
	       						listMap.add(map);
	       					}
       					}
	       				//要添加的成分都是可以直接添加的
	       			}else if(gtcList.size()>0 && (existCpsList.size()==0 || null==existCpsList)){
       					for(int j=0;j<composition_idss.length;j++){
       						Map<String,Object> map=new HashMap<String,Object>();
       						map.put("tagId", tag_idss[i]);
       						map.put("compositionId", composition_idss[j]);
       						map.put("status", 1);
       						map.put("createStamp", new Date().getTime()/1000);
       						map.put("isMain", isMain);
       						if(isMain==1){
       							map.put("mainName", "Y");
       						}else{
       							map.put("mainName", "");
       						}
       						listMap.add(map);
       					}
	       			}
	       		}
	       		if(listMap.size()>0){
       				backGoodsMapper.insertBatch(listMap);
       			}
	       		return ReturnData.SUCCESS;
	       	}else{
	       		return ReturnData.ERROR;
	       	}
       } catch (Exception e) {
    	    Map map=new HashMap();
    		map.put("method", "BackGoodsService.addTagComposition");
    		map.put("tagIds", tagIds);
    		map.put("isMain", isMain);
    		map.put("compositionIds", compositionIds);
    		new cn.bevol.log.LogException(e,map);
       }
       return ReturnData.ERROR;
	 }


	/**
	 * 获取所有的非隐藏产品成分标签
	 * @return
     */
	public List<GoodsTagComposition> getAllGoodsTagComposition(){
        try {
        	List<GoodsTagComposition> gtcList=backGoodsMapper.getAllTagComposition();
    		return gtcList;
        } catch (Exception e) {
        	Map map=new HashMap();
    		map.put("method", "BackGoodsService.getAllGoodsTagComposition");
    		new cn.bevol.log.LogException(e,map);
        }
        return new ArrayList();
 	 }
	
	/**
	 * 根据tagId 删除成分 status=1
 	 * 删除标签成分关系,一个标签对应多个成分
 	 * @param tag_ids
 	 * @param composition_ids
 	 * @return
 	 */
	public ReturnData delTagComposition(String tagIds,String compositionIds){
		try{
			if(!StringUtils.isBlank(tagIds) && !StringUtils.isBlank(compositionIds)){
				List<Map> listMap=new ArrayList<Map>();
				String[] tagIdss=tagIds.split(",");
				for(int i=0;i<tagIdss.length;i++){
					backGoodsMapper.delBatch(tagIdss[i],compositionIds);
				}
			}else{
				return ReturnData.ERROR;
			}
			return ReturnData.SUCCESS;
		}catch(Exception e){
			Map map=new HashMap();
    		map.put("method", "BackGoodsService.delTagComposition");
    		map.put("tagIds", tagIds);
    		map.put("compositionIds", compositionIds);
    		new cn.bevol.log.LogException(e,map);
		}
		return ReturnData.ERROR;
	}
	
	/**
 	 * 手动修改产品标签结果
 	 * 一个产品对应多个标签
 	 * @param tag_ids
 	 * @param goods_ids
 	 * @return
 	 */
	public ReturnData madeEditTagResult(String tagIds,String goodsIds){
		try{
			if(!StringUtils.isBlank(tagIds) && !StringUtils.isBlank(goodsIds)){
				//修改之前 验证是否存在该goods_id
				String[] tagIdss=tagIds.split(",");
				String[] goodsIdss=goodsIds.split(",");
				List<GoodsTag> gtList=goodsMapper.getAllTag();
				
				for(int i=0;i<goodsIdss.length;i++){
					//根据goodsId找到tagId
					GoodsTagResult gtr=goodsMapper.getTagResultByGoodsId(Long.valueOf(goodsIdss[i]));
					boolean flag=false;
					
					//表中存在就修改
					if(null!=gtr){
						String madeTagIds="";
						String madeTagNames="";
						for(GoodsTag gt:gtList){
							for(int j=0;j<tagIdss.length;j++){
								if(Long.valueOf(tagIdss[j])==gt.getId()){
									//添加手动修改
									madeTagIds+=gt.getId()+",";
									madeTagNames+=gt.getName()+",";
								}
							}
						}
						if(!StringUtils.isBlank(madeTagIds)){
							madeTagIds=madeTagIds.substring(0,madeTagIds.length()-1);
							madeTagNames=madeTagNames.substring(0,madeTagNames.length()-1);
							gtr.setTagIds(madeTagIds);
							gtr.setTagNames(madeTagNames);
							gtr.setMadeTagIds(madeTagIds);
							gtr.setMadeTagNames(madeTagNames);
							gtr.setMadeDelete(0);
							flag=true;
						}
						
					}
					if(flag){
						backGoodsMapper.updateGoodsResult(gtr);
					}
				}
			}else{
				return ReturnData.ERROR;
			}
			return ReturnData.SUCCESS;
		}catch(Exception e){
			Map map=new HashMap();
    		map.put("method", "BackGoodsService.madeEditTagResult");
    		map.put("tagIds", tagIds);
    		map.put("goodsIds", goodsIds);
    		new cn.bevol.log.LogException(e,map);
		}
		return ReturnData.ERROR;
	}
	
	/**
	 * 查询含有某个标签的产品
	 * @param tag_id
	 * @param size
	 * @param pageSize
	 * @return
	 */
	public ReturnListData findGoodsByTagId(Long tagId,int size,int pageSize){
        try {
        	if(size>1){
        		size=(size-1)*pageSize;
        	}else{
        		size=0;
        	}
        	//查找
        	List<GoodsTagResult> gtrList=getGoodsByTagId(tagId,size,pageSize);
        	String ids="";
        	for(GoodsTagResult gtr:gtrList){
        		//获取产品id
        		ids+=gtr.getGoodsId()+",";
        	}
        	List<Goods> glist=new ArrayList<Goods>();
        	if(!StringUtils.isBlank(ids)){
        		ids=ids.substring(0,ids.length()-1);
        		//获取产品列表
        		glist=goodsMapper.getGoodsByIds(ids);
        	}
    		return new ReturnListData(glist,glist.size());
        } catch (Exception e) {
        	Map map=new HashMap();
    		map.put("method", "BackGoodsService.method:findGoodsByTagId");
    		map.put("tagId", tagId);
    		map.put("size", size);
    		map.put("pageSize", pageSize);
    		new cn.bevol.log.LogException(e,map);
        }
		return ReturnListData.ERROR;
	}
	
	
	/**
	 * 删除含有某个标签的产品 --批量隐藏产品(hq_goods_tag_result)
	 * @param tagId: 产品标签id
	 * @param size
	 * @param pageSize
	 */
	public ReturnData delGoodsByTagId(Long tagId){
        try {
        	List<GoodsTag> tlist=goodsMapper.getAllTag();
        	boolean flag=false;
        	for(int i=0;!flag && i<tlist.size();i++){
        		if(tlist.get(i).getId()==tagId){
        			flag=true;
        		}
        	}
        	if(flag){
        		backGoodsMapper.delGoodsByTagId(tagId);
        	}else{
        		return ReturnData.FAILURE;
        	}
        	return ReturnData.SUCCESS;
        } catch (Exception e) {
        	Map map=new HashMap();
    		map.put("method", "BackGoodsService.delGoodsByTagId");
    		map.put("tagId", tagId);
    		new cn.bevol.log.LogException(e,map);
        }
		return ReturnData.ERROR;
	}
	
	/**
	 * 查找含有某个标签的产品(多个)
	 * @param tagId: 产品标签id
	 * @param size
	 * @param pageSize
	 * @return
	 */
	public List<GoodsTagResult> getGoodsByTagId(final Long tagId,final int size,final int pageSize){
        try {
        	Long start=0L;
        	if(size>1){
        		start=Long.valueOf((size-1)*pageSize);
        	}
        	List<GoodsTagResult> gtrList=backGoodsMapper.getGoodsByTagId(tagId,start,pageSize);
    		return gtrList;
        } catch (Exception e) {
        	Map map=new HashMap();
    		map.put("method", "BackGoodsService.getGoodsByTagId");
    		map.put("tagId", tagId);
    		map.put("size", size);
    		map.put("pageSize", pageSize);
    		new cn.bevol.log.LogException(e,map);
        }
		return new ArrayList();
	}
	
	/**
	 * 手动增加/修改产品标签
	 * 一个产品对应多个标签
	 * @param tagIds: 标签id,逗号分隔
	 * @param goodsIds: 产品id,逗号分隔
	 * @return
	 */
	public ReturnData madeAddResult(String tagIds,String goodsIds){
		try{
			if(!StringUtils.isBlank(goodsIds) && !StringUtils.isBlank(tagIds)){
				List<GoodsTag> gtList=goodsMapper.getAllTag();
				String[] goodsIdss=goodsIds.split(",");
				String[] tagIdss=tagIds.split(",");
				Map<Long,String> map=new HashMap<Long,String>();
				for(int i=0;i<goodsIdss.length;i++){
					//查找数据,是否存在
					GoodsTagResult goodsTagResult=goodsMapper.getTagResultByGoodsId(Long.valueOf(goodsIdss[i]));
					boolean flag=false;
					String madeTagIds="";
					String madeTagNames="";

					if(null!=goodsTagResult){
						flag=true;
					}else{
						goodsTagResult=new GoodsTagResult();
					}
					
					for(GoodsTag gt:gtList){
						for(int j=0;j<tagIdss.length;j++){
							if(tagIdss[j].equals(gt.getId()+"")){
								//手动修改的信息
								madeTagIds+=gt.getId()+",";
								madeTagNames+=gt.getName()+",";
								map.put(gt.getId(), gt.getName());
							}
						}
					}
					
					goodsTagResult.setCreateStamp(new Date().getTime()/1000);
					//表中没有数据,创建一条
					if(!flag && !StringUtils.isBlank(madeTagIds)){
						madeTagIds=madeTagIds.substring(0,madeTagIds.length()-1);
						madeTagNames=madeTagNames.substring(0,madeTagNames.length()-1);
						goodsTagResult.setGoodsId(Long.valueOf(goodsIdss[i]));
						goodsTagResult.setMadeTagIds(madeTagIds);
						goodsTagResult.setMadeTagNames(madeTagNames);
						goodsTagResult.setTagIds(madeTagIds);
						goodsTagResult.setTagNames(madeTagNames);
						if(!StringUtils.isBlank(goodsTagResult.getTagIds())){
							backGoodsMapper.insertResult(goodsTagResult);
						} 
					}else if(flag && !StringUtils.isBlank(madeTagIds)){
						//表中存在数据,添加手动修改的记录
						//有goods_id  该记录添加tagIds  不重复
						String idss=goodsTagResult.getTagIds();
						String tagNames=goodsTagResult.getTagNames();
						String idss2=idss;
						for(int j=0;j<tagIdss.length;j++){
							if(!(","+idss+",").contains(","+tagIdss[j]+",")){
								idss+=","+tagIdss[j];
								tagNames+=","+map.get(Long.valueOf(tagIdss[j]));
							}
						}
						//原标签为空
						if(StringUtils.isBlank(idss2) && !StringUtils.isBlank(idss)){
							idss=idss.substring(1);
							tagNames=tagNames.substring(1);
						}
						goodsTagResult.setMadeTagIds(idss);
						goodsTagResult.setMadeTagNames(tagNames);
						goodsTagResult.setTagIds(idss);
						goodsTagResult.setTagNames(tagNames);
						goodsTagResult.setMadeDelete(0);
						backGoodsMapper.updateGoodsResult(goodsTagResult);
					}
				}
			}else{
				return ReturnData.FAILURE;
			}
			return ReturnData.SUCCESS;
		}catch(Exception e){
			Map map=new HashMap();
    		map.put("method", "BackGoodsService.madeAddResult");
    		map.put("tagIds", tagIds);
    		map.put("goodsIds", goodsIds);
    		new cn.bevol.log.LogException(e,map);
		}
		return ReturnData.ERROR;
	}
	
	/**
	 * 手动删除产品中含有的的某个标签
	 * 一个产品对应多个标签
	 * @param tagIds
	 * @param goodsIds
	 * @return
	 */
	public ReturnData madeDelResult(String tagIds, String goodsIds) {
		try{
			if(!StringUtils.isBlank(goodsIds) && !StringUtils.isBlank(tagIds)){
				String[] goodsIdss=goodsIds.split(",");
				for(int i=0;i<goodsIdss.length;i++){
					//查找该goodsId
					GoodsTagResult goodsTagResult=goodsMapper.getTagResultByGoodsId(Long.valueOf(goodsIdss[i]));
					boolean flag=false;
					if(null==goodsTagResult){
						goodsTagResult=new GoodsTagResult();
					}
					
					String newTagIds="";
					String newTagNames="";
					boolean flag2=false;
					//标签不为空才能删除标签
					if(!StringUtils.isBlank(goodsTagResult.getTagIds())){
						String[] goodsTagIds=goodsTagResult.getTagIds().split(",");
						String[] goodsTagNames=goodsTagResult.getTagNames().split(",");
						for(int j=0;j<goodsTagIds.length;j++){
							//判断该产品是否含有要删除的标签  只删除含有的 不含有不做处理
							if(!(","+tagIds+",").contains(","+goodsTagIds[j]+",")){
								//不删除的标签
								newTagIds+=goodsTagIds[j]+",";
								newTagNames+=goodsTagNames[j]+",";
								flag2=true;
							}
						}
						
						if(!StringUtils.isBlank(newTagIds)){
							//删除该产品部分标签
							newTagIds=newTagIds.substring(0, newTagIds.length()-1);
							newTagNames=newTagNames.substring(0, newTagNames.length()-1);
						}else{
							//不删除的标签为空,即删除该产品所有标签
							goodsTagResult.setMadeDelete(1);
							flag2=true;
						}
					}
					
					if(flag2){
						goodsTagResult.setMadeTagIds(newTagIds);
						goodsTagResult.setMadeTagNames(newTagNames);
						goodsTagResult.setTagIds(newTagIds);
						goodsTagResult.setTagNames(newTagNames);
						goodsTagResult.setUpdateTime(new Date().getTime()/1000);
						backGoodsMapper.updateGoodsResult(goodsTagResult);
					}
				}
			}else{
				return ReturnData.FAILURE;
			}
			return ReturnData.SUCCESS;
		}catch(Exception e){
			Map map=new HashMap();
    		map.put("method", "BackGoodsService.madeDelResult");
    		map.put("tagIds", tagIds);
    		map.put("goodsIds", goodsIds);
    		new cn.bevol.log.LogException(e,map);
		}
		return ReturnData.ERROR;
	}
 	 
	/**
	 * 后台修改多分类表中的分类
	 * @param tagIds
	 * @param goodsIds
	 * @return
	 */
	public ReturnData madeEditCategory(int categoryId, String goodsIds) {
		try{
			if(!StringUtils.isBlank(goodsIds) && categoryId!=0){
				String[] goodsIdss=goodsIds.split(",");
				for(int j=0;j<goodsIdss.length;j++){
					List<Map<String,Object>> newCategoryList=new ArrayList<Map<String,Object>>();
					//该产品的分类,可能计算出来的为多个分类
					List<Map<String,Object>> categoryList=this.getPolyCategoryBygoodsIdsLocal(goodsIdss[j]);
					if(null!=categoryList && categoryList.size()>0){
						Map<String,Object> map=new HashMap<String,Object>();
						map.put("goodsId", categoryList.get(0).get("goods_id"));
						//设置手动分类
						map.put("madeCategoryId", categoryId);
						map.put("categoryId", categoryId);
						map.put("updateTime", new Date().getTime()/1000);
						map.put("existCategoryIds", 0);
						newCategoryList.add(map);
						backGoodsMapper.madeUpdateCategory(newCategoryList);
					}
				}
			}else{
				return ReturnData.FAILURE;
			}
			return ReturnData.SUCCESS;
		}catch(Exception e){
			Map map=new HashMap();
    		map.put("method", "BackGoodsService.madeEditCategory");
    		map.put("categoryId", categoryId);
    		map.put("goodsIds", goodsIds);
    		new cn.bevol.log.LogException(e,map);
		}
		return ReturnData.ERROR;
	}
	
	/**
	 * 带缓存的 获取单个产品的多分类数据
	 * 用于产品计算
	 * @param goodsIds
	 * @return
	 */
	public List<Map<String,Object>>  getPolyCategoryBygoodsIds(final String goodsIds) {
		return new CacheableTemplate<List<Map<String,Object>>>(cacheProvider) {
            @Override
            protected List<Map<String,Object>> getFromRepository() {
            	try {
                	List<Map<String,Object>> geu=backGoodsMapper.getPolyCategoryBygoodsIds(goodsIds);
                	return geu;
                } catch (Exception e) {
                	Map map=new HashMap();
            		map.put("method", "BackGoodsService.getPolyCategoryBygoodsIds");
            		map.put("goodsIds", goodsIds);
            		new cn.bevol.log.LogException(e,map);
                }
                return null;
            }
            @Override
            protected boolean canPutToCache(List<Map<String,Object>> returnValue) {
                return (returnValue != null && returnValue.size()>0);
            }
        }.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,
        		CACHE_NAME.createInstanceKey(CACHE_NAME.INSTANCE_POLY_CATEGORY_PREFIX)), true);
		
	}
	
	/**
	 * 获取单个产品的多分类数据
	 * @param goodsIds
	 * @return
	 */
	public List<Map<String,Object>>  getPolyCategoryBygoodsIdsLocal(String goodsIds) {
        try {
        	List<Map<String,Object>> geu=backGoodsMapper.getPolyCategoryBygoodsIds(goodsIds);
        	return geu;
        } catch (Exception e) {
        	Map map=new HashMap();
    		map.put("method", "BackGoodsService.getPolyCategoryBygoodsIdsLocal");
    		map.put("goodsIds", goodsIds);
    		new cn.bevol.log.LogException(e,map);
        }
        return null;
	}

	/**
	 * 产品分类规则列表
	 * @param type 1普通类 2特殊类
	 * @return
	 */
	public ReturnData categoryRoleList(int type) {
		try{
			List<Map<String,Object>> listMap=new ArrayList<Map<String,Object>>();
			if(type==1){
            	listMap=goodsMapper.getAllCommonGoodsCategory();
			}else if(type==2){
         		listMap=goodsMapper.getAllSpecialCategory();
			}
			
			//获取产品分类
			List<Map<String,Object>> categoryList=goodsMapper.getAllGoodsCategory();
			for(Map map:listMap){
				int categoryId=(Integer)map.get("category_id");
				for(Map cmap:categoryList){
					int id=(Integer)cmap.get("id");
					String name=(String)cmap.get("name");
					if(categoryId==id){
						map.put("name", name);
					}
				}
			}
			return new ReturnData(listMap);
		}catch(Exception e){
			Map map=new HashMap();
    		map.put("method", "BackGoodsService.categoryRoleList");
    		map.put("type", type);
    		new cn.bevol.log.LogException(e,map);
		}
		return ReturnData.ERROR;
	}

	/**
	 * 新增/原有基础上添加产品分类规则
	 */
	public ReturnData addeCategoryRules(long id, String newRule1, String newRule2) {
		try{
			if(StringUtils.isBlank(newRule1) && StringUtils.isBlank(newRule2)){
				return new ReturnData("修改规则不能都为空");
			}
			//查找id是否存在
			List<Map<String, Object>> listMap =backGoodsMapper.getCommonGoodsCategoryById(id);
			if(null!=listMap && listMap.size()>0){
				Map map=listMap.get(0);
				String nowRule1=(String)map.get("rule_1");
				String nowRule2=(String)map.get("rule_2");
				//开始修改
				if(StringUtils.isNotBlank(nowRule1)){
					//已经有规则
					if(StringUtils.isNotBlank(newRule1)){
						//新加的规则不为空
						nowRule1+=","+newRule1;
					}
				}else{
					//原来的规则为空
					if(StringUtils.isNotBlank(newRule1)){
						//新加的规则不为空
						nowRule1=newRule1;
					}
				}
				
				if(StringUtils.isNotBlank(nowRule2)){
					//已经有规则
					if(StringUtils.isNotBlank(newRule2)){
						//新加的规则不为空
						nowRule2+=","+newRule2;
					}
				}else{
					//原来的规则为空
					if(StringUtils.isNotBlank(newRule2)){
						//新加的规则不为空
						nowRule2=newRule2;
					}
				}
				
				//更新数据
				backGoodsMapper.updateCommonGoodsCategoryById(id,nowRule1,nowRule2);
				
				return ReturnData.SUCCESS;
			}
			return ReturnData.FAILURE;
		}catch(Exception e){
			Map map=new HashMap();
    		map.put("method", "BackGoodsService.addeCategoryRules");
    		map.put("newRule1", newRule1);
    		map.put("id", id);
    		map.put("newRule2", newRule2);
    		new cn.bevol.log.LogException(e,map);
		}
		return ReturnData.FAILURE;
	}
	
	
	/**
	 * 修改产品分类规则
	 * @param id
	 * @param newRole1
	 * @param newRole2
	 * @return
	 */ 
	public ReturnData updateCategoryRules(long id, String newRule1, String newRule2) {
		try{
			if(StringUtils.isBlank(newRule1) && StringUtils.isBlank(newRule2)){
				return new ReturnData("修改规则不能都为空");
			}
			//查找id是否存在
			List<Map<String, Object>> listMap =backGoodsMapper.getCommonGoodsCategoryById(id);
			if(null==listMap || listMap.size()==0){
				return new ReturnData("id不存在");
			}
			Map map=listMap.get(0);
			String nowRule1=(String)map.get("rule_1");
			String nowRule2=(String)map.get("rule_2");
			if(StringUtils.isNotBlank(newRule1)){
				nowRule1=newRule1;
			}
			if(StringUtils.isNotBlank(newRule2)){
				nowRule2=newRule2;
			}
			//更新数据
			backGoodsMapper.updateCommonGoodsCategoryById(id,nowRule1,nowRule2);
			
			return ReturnData.SUCCESS;
		}catch(Exception e){
			Map map=new HashMap();
    		map.put("method", "BackGoodsService.updateCategoryRules");
    		map.put("newRule1", newRule1);
    		map.put("id", id);
    		map.put("newRule2", newRule2);
    		new cn.bevol.log.LogException(e,map);
		}
		return ReturnData.FAILURE;
	}

	/**
	 * 新增一个产品分类的规则,只支持普通分类
	 * @param categoryId: 分类id
	 * @param newRule1: 包含的规则
	 * @param newRule2: 不包含的规则
	 * @return
	 */
	public ReturnData addCategoryRules(int categoryId, String newRule1, String newRule2) {
		try{
			if(StringUtils.isBlank(newRule1) && StringUtils.isBlank(newRule2)){
				return new ReturnData("新增的规则不能都为空");
			}
			//查找id是否存在
			List<Map<String, Object>> listMap =goodsMapper.getAllGoodsCategory();
			boolean flag=false;
			for(Map map:listMap){
				if(null!=map.get("id") && (int)map.get("id")==categoryId){
					flag=true;
				}
				
			}
			if(!flag){
				return new ReturnData("产品分类id不存在");
			}
			
			List<Map<String, Object>> listMap2 =goodsMapper.getAllCommonGoodsCategory();
			for(Map map:listMap2){
				if(null!=map.get("category_id") && (int)map.get("category_id")==categoryId){
					flag=false;
				}
			}
			if(!flag){
				return new ReturnData("该分类规则存在");
			}
			
			int i=backGoodsMapper.addCategoryRule(newRule1,newRule2,categoryId);
			
			return ReturnData.SUCCESS;
		}catch(Exception e){
			Map map=new HashMap();
    		map.put("method", "BackGoodsService.addCategoryRules");
    		map.put("categoryId", categoryId);
    		map.put("newRule1", newRule1);
    		map.put("newRule2", newRule2);
    		new cn.bevol.log.LogException(e,map);
		}
		return ReturnData.FAILURE;
	}

	/**
	 * 产品分类列表
	 * @return
	 */
	public ReturnListData goodsCategoryList() {
		try{
			List<Map<String, Object>> listMap =goodsMapper.getAllGoodsCategory();
			return new ReturnListData(listMap,listMap.size());
		}catch(Exception e){
			Map map=new HashMap();
    		map.put("method", "BackGoodsService.goodsCategoryList");
    		new cn.bevol.log.LogException(e,map);
		}
		return new ReturnListData();
	}
	
	


 }