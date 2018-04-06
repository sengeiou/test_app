package cn.bevol.internal.service;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.io97.cache.redis.RedisCacheProvider;

import cn.bevol.mybatis.dao.BackTagMapper;
import cn.bevol.mybatis.dao.FindMapper;
import cn.bevol.mybatis.dao.GoodsMapper;
import cn.bevol.mybatis.dao.HotListMapper;
import cn.bevol.mybatis.dto.HotList;
import cn.bevol.mybatis.dto.Label;
import cn.bevol.mybatis.model.Find;
import cn.bevol.entity.service.BaseService;
import cn.bevol.log.LogClass;
import cn.bevol.log.LogException;
import cn.bevol.util.ReturnData;
import cn.bevol.util.ReturnListData;

/**
 * 
 * @author chenHaiJian
 *
 */

@Service
@LogClass
public class BackTagService extends BaseService {
	private static Logger logger = LoggerFactory.getLogger(BackTagService.class);
	@Autowired
    RedisCacheProvider cacheProvider;
	
	@Autowired
	FindMapper findMapper;
	
	@Autowired
	GoodsMapper goodsMapper;
	
	@Autowired
	HotListMapper hotListMapper;
	
	
	@Autowired
	BackTagMapper backTagMapper;
	
	public ReturnListData getCommentGoodsTags(String tabs) {
		try{
			List<Map<String, Object>> listMap =backTagMapper.getTagListsByLikeTabls(tabs);
			List allList=new ArrayList();
			List tabList=new ArrayList();
			Map allMap=new HashMap();
			Map<String,Object> m=new HashMap();
			for(Map map:listMap){
				m.put(map.get("tab_id")+"", map.get("tab_id")+"");
			}
			String start="goods_category_goods_comment_";
			String count="count";
			List<Map> list=new ArrayList<Map>();
			for(Map.Entry<String, Object> entry : m.entrySet()){
				Map map=new HashMap();
				map.put("tab_id",entry.getKey());
				for(int i=1;i<=5;i++){
					map.put(start+i,start+i);
					map.put(start+i+count,0);
				}
				list.add(map);
			}
			//分类下的星级的标签的个数
			for(Map map:listMap){
				for(Map entryMap:list){
					if((entryMap.get("tab_id")+"").equals(map.get("tab_id")+"")){
						if(null!=map.get("tag_pid")&&Integer.parseInt(map.get("tag_pid")+"")>0&&0==Integer.parseInt(map.get("hidden")+"")&&(map.get("tabs")+"").equals(entryMap.get(map.get("tabs"))+"")){
							int total=Integer.parseInt(entryMap.get(map.get("tabs")+count)+"");
							total++;
							entryMap.put((map.get("tabs")+"")+count, total);
						}
						//如果都为0
						/*if(0==Integer.parseInt(map.get("hidden")+"")){
							entryMap.put("hidden",map.get("hidden"));
						}*/
						
					}
				}
			}
			
			
			if(null!=list && list.size()>0){
				//字段处理
				List<Map> tagList=new ArrayList();
				List<Map<String,Object>> categoryList =goodsMapper.getAllGoodsCategory();
				//List<Map<String,Object>> categoryList=backTagMapper.getGoodsParentCategory();
				for(int i=0;i<list.size();i++){
					Map tagMap=new HashMap();
					int tabId=Integer.parseInt((list.get(i).get("tab_id")+""));
					tagMap.put("tab_id", tabId);
					if(null==list.get(i).get("hidden")){
						tagMap.put("hidden", list.get(i).get("hidden"));
					}
					tagMap.put("hidden", list.get(i).get("hidden"));
					boolean find=false;
					for(int j=0;!find&&j<categoryList.size();j++){
						int id=Integer.parseInt((categoryList.get(j).get("id")+""));
						if(tabId==id){
							find=true;
							//list.get(i).put("tab_name", categoryList.get(j).get("name"));
							tagMap.put("tab_name", categoryList.get(j).get("name"));
						}
					}
					for(int k=1;k<=5;k++){
						tagMap.put("start_"+k, list.get(i).get(start+k+count));
					}
					tagList.add(tagMap);
				}
				
				for(Map map: listMap){
					if(0==Integer.parseInt(map.get("hidden")+"")){
						String id=map.get("tab_id")+"";
						for(int i=0;i<tagList.size();i++){
						    String tabId=tagList.get(i).get("tab_id")+"";
							if(id.equals(tabId) && null==tagList.get(i).get("hidden")){
								tagList.get(i).put("hidden", 0);
							}
						}
					}
				}
				
				for(int i=0;i<tagList.size();i++){
					if(null==tagList.get(i).get("hidden")){
						tagList.get(i).put("hidden", 1);
					}
				}
				return new ReturnListData(tagList,tagList.size());
			}
			
			return new ReturnListData(list,list.size());
		}catch(Exception e){
			Map map=new HashMap();
    		map.put("method", "BackTagService.getCommentGoodsTags");
    		new cn.bevol.log.LogException(e,map);
		}
		return new ReturnListData();
	}

	public ReturnData getTagCategoryInfo(Integer tabId) {
		List<Map<String, Object>> listMap =backTagMapper.getComemntTagsByCategory(tabId);
		List<Map> list=new ArrayList();
		if(null!=listMap && listMap.size()>0){
			Map m=new HashMap();
			m.put("tab_id", listMap.get(0).get("tab_id"));
			List list1=new ArrayList();
			List list2=new ArrayList();
			List list3=new ArrayList();
			List list4=new ArrayList();
			List list5=new ArrayList();
			String[] types={"1","2","3","4","5"};
			
			for(Map map : listMap){
				String tabs=map.get("tabs")+"";
				String type=tabs.substring(tabs.length()-1,tabs.length());
				Map sm=new HashMap();
				if(Arrays.binarySearch(types, type)!=-1){
					if(null!=map.get("title")){
						sm.put("tagId", map.get("id"));
						sm.put("title", map.get("title"));
						sm.put("tagPid", map.get("tag_pid"));
						
						if("1".equals(type)){
							list1.add(sm);
						} else if("2".equals(type)){
							list2.add(sm);
						} else if("3".equals(type)){
							list3.add(sm);
						} else if("4".equals(type)){
							list4.add(sm);
						} else if("5".equals(type)){
							list5.add(sm);
						}
					}
				}
				
			}
			m.put("start_1", list1);
			m.put("start_2", list2);
			m.put("start_3", list3);
			m.put("start_4", list4);
			m.put("start_5", list5);
			List<Map<String, Object>> categoryList =backTagMapper.getGoodsParentCategory();
			if(null!=categoryList && categoryList.size()>0){
				for(Map map : categoryList){
					if((map.get("id")+"").equals(m.get("tab_id")+"")){
						m.put("tab_name",map.get("name"));
					}
				}
			}
			return new ReturnData(m);
		}
		return null;
	}

	/**
	 * 分类列表
	 * @param type
	 * @return
	 */
	public ReturnData goodsCategoryList(int type) {
		List<Map<String, Object>> categoryList=new ArrayList();
		if(0==type){
			categoryList =goodsMapper.getAllGoodsCategory();
			Map map=new HashMap();
			List<Integer> pidList=new ArrayList<Integer>();
			for(Map m:categoryList){
				if(null!=m.get("parent_id") && "0".equals((m.get("parent_id")+""))){
					pidList.add(Integer.parseInt(m.get("id")+""));
				}
			}
			List<Map<String, Object>> allList=new ArrayList();
			for(int i=0;i<pidList.size();i++){
				int pid=pidList.get(i);
				Map objMap=new HashMap();
				objMap.put("id", pid);
				List subList=new ArrayList();
				for(Map m:categoryList){
					if(pid==Integer.parseInt(m.get("parent_id")+"")){
						subList.add(m);
					}
					if(pid==Integer.parseInt(m.get("id")+"")){
						objMap.put("name", m.get("name"));
					}
				}
				objMap.put("subList", subList);
				allList.add(objMap);
			}
			categoryList=allList;
		}
		
		return new ReturnData(categoryList);
	}

	/**
	 * 隐藏
	 * @param tagId
	 * @param tabId
	 * @return
	 */
	public ReturnData updateCategoryTag(String tagIds, Integer tabId) {
		if(StringUtils.isNotBlank(tagIds)){
			//修改星级
			backTagMapper.updateTagByTagId(tagIds);
			return ReturnData.SUCCESS;
		}else if(null!=tabId && tabId>0){
			//修改分类
			backTagMapper.updateTagByTabId(tabId);
			return ReturnData.SUCCESS;
		}
		return null;
	}

	/*public ReturnData addTags(String tagIds, String tabs, Integer tabId, Integer hidden) {
		if(null!=hidden && null!=tabId){
			//隐藏分类下的所有标签
			backTagMapper.updateTagByTabId(tabId);
			return ReturnData.SUCCESS;
		}

		if(null!=tabId && tabId>0 && StringUtils.isBlank(tagIds)){
			//新增分类和分类下的初始星级
			String start="goods_category_goods_comment_";
			
			for(int i=1;i<=5;i++){
				backTagMapper.saveByTabId(start+i,tabId);
			}
			return new ReturnData(0,"新建分类和初始化星级");
		}
		
		if(null!=tabId && tabId>0&&StringUtils.isNotBlank(tabs)){
			//星级下的标签
			List<Map<String,Object>> listMap=backTagMapper.getNewTagByTabs(tabs);
			if(StringUtils.isBlank(tagIds)){
				//如果给的为空,则清空所有标签
				String ids="";
				for(Map map:listMap){
					ids+=map.get("id")+",";
				}
				ids=ids.substring(0,ids.length()-1);
				backTagMapper.updateTagByTagId(ids);
				return new ReturnData(0,"星级下的标签被清空");
			}
			
			//修改星级下的标签
			String insertIds="";
			String[] pids=tagIds.split(",");
			List sameIds=new ArrayList();
			String beforeTagIds="";
			for(Map map : listMap){
				for(int i=0;i<pids.length;i++){
					//查找相同的标签
					if(pids[i].equals(map.get("tab_pid")+"")){
						sameIds.add(pids[i]);
					}
				}
				beforeTagIds+=map.get("id")+",";
			}
			
			
			if(sameIds.size()==0){
				//没有相同的标签
				//隐藏之前的标签
				if(StringUtils.isNotBlank(beforeTagIds)){
					backTagMapper.updateTagByTagId(beforeTagIds.substring(0,beforeTagIds.length()-1));
				}
				System.out.println("1----");
				System.out.println("pids:"+pids.toString());
				//新增全新的标签
				for(String pid : pids){
					//查出pid的name
					List<Map<String,Object>> pMap=backTagMapper.getPtagById(Integer.parseInt(pid));
					backTagMapper.saveNewTag(Integer.parseInt(pMap.get(0).get("id")+""),pMap.get(0).get("title")+"",tabId,tabs);
				}
				
			}else{
				String[] ids=tagIds.split(",");
				List idsList=Arrays.asList(ids);
				if(idsList.size()>sameIds.size()){
					System.out.println("2---");
					//新增标签
					String addIds="";
					idsList.retainAll(sameIds);
					System.out.println("sameIds:"+sameIds);

					if(null!=idsList && idsList.size()>0){
						for(int i=0;i<idsList.size();i++){
							//查出pid的name
							List<Map<String,Object>> pMap=backTagMapper.getPtagById(Integer.parseInt(idsList.get(i)+""));
							backTagMapper.saveNewTag(Integer.parseInt(pMap.get(0).get("id")+""),pMap.get(0).get("title")+"",tabId,tabs);
						}
					}
					return new ReturnData(0,"新增标签");
				}else if(idsList.size()<sameIds.size()){
					System.out.println("3---");
					//隐藏多余的标签
					String addIds="";
					sameIds.retainAll(idsList);
					System.out.println("sameIds:"+sameIds);

					if(null!=sameIds && sameIds.size()>0){
						for(int i=0;i<sameIds.size();i++){
							//查出pid的name
							List<Map<String,Object>> pMap=backTagMapper.getPtagById(Integer.parseInt(sameIds.get(i)+""));
							//根据pid和tabs隐藏
							backTagMapper.hiddenNewTagsByPid(Integer.parseInt(pMap.get(0).get("id")+""),tabId,tabs);
						}
					}
					return new ReturnData(0,"隐藏多余的标签");
				}
			}
			
		}
		
		return new ReturnData(0,"操作无效");
	}
	*/
	public ReturnData addTags(String tagIds, String tabs, Integer tabId, Integer hidden) {
		try{
			if(null!=hidden && null!=tabId){
				//隐藏分类下的所有标签
				backTagMapper.updateTagByTabId(tabId);
				return ReturnData.SUCCESS;
			}

			if(null!=tabId && tabId>0 && StringUtils.isBlank(tagIds) && StringUtils.isBlank(tabs)){
				//新增分类和分类下的初始星级
				String start="goods_category_goods_comment_";
				
				for(int i=1;i<=5;i++){
					backTagMapper.saveByTabId(start+i,tabId);
				}
				return new ReturnData(0,"新建分类和初始化星级");
			}
			
			//修改或者增加
			if(null!=tabId && tabId>0&&StringUtils.isNotBlank(tabs)){
				//星级下的标签
				List<Map<String,Object>> listMap=backTagMapper.getNewTagByTabsAndTabId(tabs,tabId);
				
				String ids="";
				if(StringUtils.isBlank(tagIds)){
					//如果给的为空,则清空所有标签
					for(Map map:listMap){
						ids+=map.get("id")+",";
					}
					if(StringUtils.isNotBlank(ids)){
						ids=ids.substring(0,ids.length()-1);
						backTagMapper.updateTagByTagId(ids);
					}
					
					return new ReturnData(0,"星级下的标签被清空");
				}
				
				//修改星级下的标签
				String insertIds="";
				String[] pids=tagIds.split(",");
				List sameIds=new ArrayList();
				String beforeTagIds="";
				String beforePtagIds="";
				for(Map map : listMap){
					for(int i=0;i<pids.length;i++){
						//查找相同的标签
						if(pids[i].equals(map.get("tag_pid")+"")){
							sameIds.add(pids[i]);
						}
					}
					beforeTagIds+=map.get("id")+",";
					beforePtagIds+=map.get("tag_pid")+",";
				}
				
				
				if(sameIds.size()==0){
					//没有相同的标签
					//隐藏之前的标签
					if(StringUtils.isNotBlank(beforeTagIds)){
						backTagMapper.updateTagByTagId(beforeTagIds.substring(0,beforeTagIds.length()-1));
					}
					//新增全新的标签
					for(String pid : pids){
						//查出pid的name
						List<Map<String,Object>> pMap=backTagMapper.getPtagById(Integer.parseInt(pid));
						if(null!=pMap&& pMap.size()>0){
							backTagMapper.saveNewTag(Integer.parseInt(pMap.get(0).get("id")+""),pMap.get(0).get("title")+"",tabId,tabs);
						
						}
					}
					return new ReturnData(0,"添加成功,与之前相比没有相同的标签");
				}else{
					//隐藏之前的
					//List allIds=Arrays.asList(beforePtagIds.substring(0,beforePtagIds.length()-1).split(","));
					if(StringUtils.isNotBlank(beforeTagIds)){
						backTagMapper.deleteTagByTagId(beforeTagIds.substring(0,beforeTagIds.length()-1));
					}
					List idsList=null;
					if(null!=pids && pids.length>0){
						idsList=Arrays.asList(pids);
					}
					
					//新增
					if(null!=idsList && idsList.size()>0){
						for(int i=0;i<idsList.size();i++){
							//查出pid的name
							List<Map<String,Object>> pMap=backTagMapper.getPtagById(Integer.parseInt(idsList.get(i)+""));
							backTagMapper.saveNewTag(Integer.parseInt(pMap.get(0).get("id")+""),pMap.get(0).get("title")+"",tabId,tabs);
						}
					}
					
					return new ReturnData(0,"修改标签");
					//todo 
					/*String[] ids=tagIds.split(",");
					List idsList=Arrays.asList(ids);
					//新的标签和相同的标签
					if(idsList.size()>sameIds.size()){
						
						//查找要隐藏的
						List hiddenList=new ArrayList();
						List allIds=Arrays.asList(beforePtagIds.substring(0,beforePtagIds.length()-1).split(","));
						hiddenList.retainAll(idsList);
						//list1.removeAll(list2);
						if(null!=hiddenList && hiddenList.size()>ids){
							
						}
						
						//批量隐藏todo
						for(int i=0;i<hiddenList.size();i++){
							//查出pid的name
							List<Map<String,Object>> pMap=backTagMapper.getPtagById(Integer.parseInt(hiddenList.get(i)+""));
							//根据pid和tabs隐藏
							backTagMapper.hiddenNewTagsByPid(Integer.parseInt(pMap.get(0).get("id")+""),tabId,tabs);
						}
						
						//新增标签
						String addIds="";
						idsList.removeAll(sameIds);
						if(null!=idsList && idsList.size()>0){
							for(int i=0;i<idsList.size();i++){
								//查出pid的name
								List<Map<String,Object>> pMap=backTagMapper.getPtagById(Integer.parseInt(idsList.get(i)+""));
								backTagMapper.saveNewTag(Integer.parseInt(pMap.get(0).get("id")+""),pMap.get(0).get("title")+"",tabId,tabs);
							}
						}
						
						return new ReturnData(0,"新增标签");
					}else if(idsList.size()<sameIds.size()){
						//隐藏多余的标签
						String addIds="";
						sameIds.retainAll(idsList);
						if(null!=sameIds && sameIds.size()>0){
							for(int i=0;i<sameIds.size();i++){
								//查出pid的name
								List<Map<String,Object>> pMap=backTagMapper.getPtagById(Integer.parseInt(sameIds.get(i)+""));
								//根据pid和tabs隐藏
								backTagMapper.hiddenNewTagsByPid(Integer.parseInt(pMap.get(0).get("id")+""),tabId,tabs);
							}
						}
						return new ReturnData(0,"隐藏多余的标签");
					}*/
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return new ReturnData(0,"操作无效");
	}
	
	
	/**
	 * 新增和修改标签
	 * @param id
	 * @param type
	 * @param top
	 * @return
	 */
	public ReturnData addOrUpdateSubTags(Integer pid,Integer id, String tabs, Integer top,Integer hidden) {
		try {
			
			Label newLabel=new Label();
			//一级标签
			Label label=null;
			//二级标签
			Label label2=null;
			if(null!=pid && pid>0){
				//查一级标签库id
				label=backTagMapper.getTagListById(pid);
				if(null==label){
					//一级标签不存在
					return ReturnData.FAILURE;
				}
			}
			if(null!=label){
				//新增
				label2=backTagMapper.getNewTagsByTitle(label.getTitle(), tabs);
				if(null==label2){
					if("user".equals(tabs)){
						//心得的top自增
						Label maxTopLabel=backTagMapper.getNewTagsByMaxTop(tabs);
						int t=0;
						if(null!=maxTopLabel && null!=maxTopLabel.getTop()){
							t=maxTopLabel.getTop();
							top=t+1;
						}
						
					}
					
					//根据id和tabs插入,insert
					newLabel.setTitle(label.getTitle());
					newLabel.setTabs(tabs);
					newLabel.setTop(top);
					newLabel.setId(label.getId());
					backTagMapper.insertNewTag2(newLabel);
					return ReturnData.SUCCESS;
				}
				
			}
			
			//修改二级标签
			if(null!=id && id>0){
				//修改
				List<Map> map=backTagMapper.getNewTagById(id);
				if(null!=map){
					newLabel.setTop(top);
					newLabel.setId(Long.parseLong(id+""));
					if(null!=hidden){
						newLabel.setHidden(hidden);
					}
					backTagMapper.updateNewTagsById(newLabel);
				}
			}
			return ReturnData.SUCCESS;
		} catch (Exception e) {
			Map map = new HashMap();
			map.put("method", "BackTagService.addTags");
			map.put("pid", pid);
			map.put("id", id);
			map.put("tabs", tabs);
			map.put("top", top);
			map.put("hidden", hidden);
			new LogException(e, map);
		}
		
		
		return ReturnData.FAILURE;
	}
	
	/**
	 * 根据标签类型 获取相应的标签列表
	 * @param tabs: 标签类型(find,lists,user_skin)
	 * @return
	 */
	public ReturnData newFindList(String tabs,Integer pager,Integer pageSize) {
		try {
			if(pager>1){
				pager=(pager-1)*pageSize;
			}else{
				pager=0;
			}
			List<Label> labelList=backTagMapper.getNewTagsByTabs2(tabs,pager,pageSize);
			return new ReturnData(labelList);
		} catch (Exception e) {
			Map map = new HashMap();
			map.put("method", "BackTagService.newFindList");
			map.put("pager", pager);
			map.put("pageSize", pageSize);
			map.put("tabs", tabs);
			new LogException(e, map);
		}
		return null;
	}
	
	 /**
     * 查询标签库
     * hq_tag_list,即一级标签库的标签
     * @return
     */
    public ReturnData getTags(int page,int pageSize) {
        try {
        	long start=0;
        	if(page>1){
        		start=(page-1)*pageSize;
        	}
            List<Label> list = backTagMapper.getAllTags(start,pageSize);
            Map map = new HashMap();
            map.put("tagList", list);
            return new ReturnData(map);
        } catch (Exception e) {
			Map map = new HashMap();
			map.put("method", "BackTagService.getTags");
			map.put("page", page);
			map.put("pageSize", pageSize);
			new LogException(e, map);
        }
        return ReturnData.ERROR;
    }
    
    /**
     * 新增一/二级标签
     * 关联hq_tag_lists he_new_tag
     * tabs(goods,user,find,lists)
     * top: 默认0 
     * @return
     */
	public ReturnData addTags(String title,String tabs,Integer tabId) {
		try{
			if(!StringUtils.isBlank(title) && !StringUtils.isBlank(tabs)){
				//增加前判断是否存在(一级标签库)
				Label labels=backTagMapper.getTagListByTitle(title);
				Label label=new Label();
				label.setTitle(title);
				label.setCreateTime(new Date().getTime()/1000);
				if(null==labels){
					findMapper.insertListTags(label);
					labels=backTagMapper.getTagListByTitle(title);
				}
				//关联二级标签库
				String[] tab=tabs.split(",");
				for(int i=0;i<tab.length;i++){
					label.setTabs(tab[i]);
					Label label2=backTagMapper.getNewTagsByTitle(title,tab[i]);
					if(null==label2 && null!=labels){
						backTagMapper.insertNewTag(labels.getId(), title, tab[i],tabId);
					}
				}
			}else{
				return new ReturnData("标签名或者类型不能为空!");
			}
			return ReturnData.SUCCESS;
		}catch (Exception e) {
			Map map = new HashMap();
			map.put("method", "BackTagService.getTags");
			map.put("title", title);
			map.put("tabs", tabs);
			new LogException(e, map);
        }
		return null;
	}
	
	/**
     * 删除标签库中的标签
     * 一级关联二级 
     *
     * @return
     */
	public ReturnData delTags(long id) {
		try{
			Label beforLab=backTagMapper.getTagListById(id);
			if(null!=beforLab){
				//一级
				backTagMapper.delTagList(id);
				//删除所有关联
				backTagMapper.delNewTagsByTitle(beforLab.getTitle());
				
				//删除实体标签
				this.updateFind();
				
			}else{
				return new ReturnData("id不存在");
			}
			
			return ReturnData.SUCCESS;
		}catch (Exception e) {
			Map map = new HashMap();
			map.put("method", "BackTagService.delTags");
			map.put("id", id);
			new LogException(e, map);
        }
		return null;
	}
	
	
	/**
	 * 标签修改或者删除后  同步实体的标签
	 * 只支持清单和文章
	 * @return
	 */
	public ReturnData updateFind() {
		try{
			//查找没有被删除的标签
			String[] tabss=new String[]{"lists","find"};
			for(int k=0;k<tabss.length;k++){
				List<Label> labelList=backTagMapper.getNewTagsByTabs(tabss[k]);
				List<Find> list=new ArrayList<Find>();
				if(tabss[k].equals("lists")){
					List<HotList> hostList=hotListMapper.allList();
					for(HotList hot:hostList){
						String tagIds="";
						if(StringUtils.isNotBlank(hot.getTagIds())){
							tagIds=hot.getTagIds();
						}
						Find find=new Find();
						find.setTagIds(tagIds);
						find.setId(hot.getId());
						list.add(find);
					}
				}else if(tabss[k].equals("find")){
					list=findMapper.getAllFind();
				}
				
				//
				for(int i=0;i<list.size();i++){
					if(StringUtils.isNotBlank(list.get(i).getTagIds())){
						String[] tagIds=list.get(i).getTagIds().split(",");
						String newTagIds="";
						String newTagTitle="";
						for(int j=0;j<tagIds.length;j++){
							for(Label label: labelList){
								//如果标签库中的标签被删除  实体的标签也删除
								if(label.getId()==Integer.parseInt(tagIds[j])){
									newTagIds+=label.getId()+",";
									newTagTitle+=label.getTitle()+",";
								}
							}
						}
						if(!StringUtils.isBlank(newTagIds)){
							newTagTitle=newTagTitle.substring(0,newTagTitle.length()-1);
							newTagIds=newTagIds.substring(0,newTagIds.length()-1);
							if(tabss[k].equals("find")){
								findMapper.updateFindIds2(list.get(i).getId(),newTagIds,newTagTitle);
							}else if(tabss[k].equals("lists")){
								hotListMapper.updateListsIds2(list.get(i).getId(),newTagIds,newTagTitle);
							}
						}
						
					}
				}
			}
			return ReturnData.SUCCESS;
		}catch(Exception e){
			Map map = new HashMap();
			map.put("method", "BackTagService.updateFind");
			new LogException(e, map);
		}
		return null;
	}
	
	
	/**
     * 修改标签库中的标签 ,一级和二级
     *
     * @return
     */
	public ReturnData editTags(Long id,String newTitle,String tabs) {
		try{
			if(null!=id && id>0){
				//一级标签库
				Label beforLab=backTagMapper.getTagListById(id);
				if(null!=beforLab){
					if(!StringUtils.isBlank(newTitle) ){
						Label lab=backTagMapper.getTagListByTitle(newTitle);
						//标签库中存在
						if(null!=lab){
							return new ReturnData("要修改的新标签名已存在");
						}
						//更新一级标签
						Label label=new Label();
						label.setTitle(newTitle);
						label.setId(id);
						backTagMapper.updateListTagById(label);
						//更新二级标签
						//更改全部关联
						backTagMapper.updateNewTags(newTitle,beforLab.getTitle());
						
						//修改实体标签名
						this.updateFind();
					}
					
					//类型
					if(StringUtils.isNotBlank(tabs)){
						String[] tabss=tabs.split(",");
						StringBuffer sql=new StringBuffer("DELETE FROM hq_new_tags WHERE tag_pid="+id);
						for(int i=0;i<tabss.length;i++){
							sql.append(" and tabs<>'"+tabss[i]+"'");
						}
						//心得类型的标签不能去除
						sql.append(" and tabs<>'user'");
						findMapper.excut(sql.toString());
					}
					
				}else{
					return new ReturnData("id不存在");
				}
			}else{
				return new ReturnData("标签不能为空");
			}
			return ReturnData.SUCCESS;
		}catch (Exception e) {
			Map map = new HashMap();
			map.put("method", "FindService.editTags");
			map.put("id", id);
			map.put("newTitle", newTitle);
			map.put("tabs", tabs);
			new LogException(e, map);
        }
		return null;
	}
	
 }