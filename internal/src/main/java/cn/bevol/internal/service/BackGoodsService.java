package cn.bevol.internal.service;


import cn.bevol.internal.cache.redis.RedisCacheProvider;
import cn.bevol.internal.dao.*;
import cn.bevol.internal.dao.mapper.*;
import cn.bevol.internal.entity.dto.Doyen;
import cn.bevol.internal.entity.dto.EssenceComment;
import cn.bevol.internal.entity.entityAction.Comment;
import cn.bevol.internal.entity.model.GoodsRule;
import cn.bevol.internal.entity.user.UserInfo;
import cn.bevol.internal.entity.vo.GoodsTagVO;
import cn.bevol.util.DateUtils;
import cn.bevol.util.Log.LogException;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	RedisCacheProvider cacheProvider;

	@Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsOldMapper goodsOldMapper;

    @Autowired
    private BackGoodsOldMapper backGoodsOldMapper;

	@Autowired
	private GoodsTagMapper goodsTagMapper;

	@Autowired
	private GoodsTagRuleMapper goodsTagRuleMapper;

	@Autowired
    private GoodsTagCompositionMapper goodsTagCompositionMapper;

	@Autowired
    private GoodsTagResultMapper goodsTagResultMapper;

    @Autowired
    private GoodsPolyCategoryMapper goodsPolyCategoryMapper;

	@Autowired
    private GoodsPloyCategoryComplexMapper goodsPloyCategoryComplexMapper;

	@Autowired
	IndexOldMapper indexOldMapper;



	@Autowired
	DoyenOldMapper doyenOldMapper;

	@Autowired
	UserService userService;


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
				goodsTag.setName(tagName);
				goodsTag.setCreateStamp(DateUtils.nowInSeconds());
				result = goodsTagMapper.insertSelective(goodsTag);
				if(result<=0){
					return ReturnData.FAILURE;
				}
				//获取插入的数据
				GoodsTagExample goodsTagExample = new GoodsTagExample();
				goodsTagExample.createCriteria().andNameEqualTo(tagName);
				List<GoodsTag> goodsTags =goodsTagMapper.selectByExample(goodsTagExample);
				if(null!=goodsTags && goodsTags.size()==1 && goodsTags.get(0).getId()!=null){
					//添加标签对应的规则
					GoodsTagRule goodsTagRule = new GoodsTagRule();
					goodsTagRule.setTagId(goodsTag.getId());
					goodsTagRule.setRule1(rules);
					goodsTagRule.setStatus(true);
					goodsTagRule.setCreateStamp(goodsTag.getCreateStamp());
					result = goodsTagRuleMapper.insertSelective(goodsTagRule);
				}
				if(result<=0){
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
			new LogException(e,map);
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
				GoodsTag goodsTag = new GoodsTag();
				goodsTag.setName(tagName);
				goodsTag.setId(Integer.parseInt(tagId+""));
				result=goodsTagMapper.updateByPrimaryKeySelective(goodsTag);
				if(result>1){
					return ReturnData.SUCCESS;
				}
			}
			if(StringUtils.isNotBlank(rules)){
				//修改标签规则
				GoodsTagRule goodsTagRule = new GoodsTagRule()
                        .withTagId(Integer.parseInt(tagId+""))
                        .withRule1(rules);
				result=goodsTagRuleMapper.updateByPrimaryKeySelective(goodsTagRule);
				if(result>1){
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
			new LogException(e,map);
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
			int start=0;
			if(pager>1){
				start = (pager-1)*pageSize;
			}

			GoodsTagExample goodsTagExample = new GoodsTagExample();
			goodsTagExample.createCriteria().andStatusEqualTo(false);
			RowBounds rowBounds = new RowBounds(start, pageSize);
			List<GoodsTag> gtList = goodsTagMapper.selectByExampleWithRowbounds(goodsTagExample, rowBounds);

			List<GoodsRule> grList=goodsService.getAllGoodsRule();

			List<GoodsTagVO> resultList = new ArrayList<>();
			for(int i=0;i<gtList.size();i++){
				List list=new ArrayList();
				long tagId=gtList.get(i).getId();
				for(GoodsRule gr:grList){
					if(gr.getTagId()==tagId){
						list.add(gr);
					}
				}
				//转为VO实体
				GoodsTagVO goodsTagVO = new GoodsTagVO();
				BeanUtils.copyProperties(gtList.get(i), goodsTagVO);
				goodsTagVO.setRuleList(list);
				resultList.add(goodsTagVO);
			}

			long total=resultList.size();
			return new ReturnListData(resultList,total);
		} catch (Exception e) {
			Map map=new HashMap();
			map.put("method", "BackGoodsService.findGoodsTag");
			map.put("pager", pager);
			map.put("pageSize", pageSize);
			new LogException(e,map);
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
	public ReturnListData findCpsByTagId(Long tagId,Boolean isMain,int pager,int pageSize){
		try{
			if(null!=tagId && tagId!=0){
				int start=0;
				List<GoodsTagComposition> gtcList=null;
				if(pager>1){
					start=(pager-1)*pageSize;
				}
				//根据is_main和tagId进行查询
                GoodsTagCompositionExample goodsTagCompositionExample = new GoodsTagCompositionExample();
				goodsTagCompositionExample.createCriteria().andTagIdEqualTo(Integer.parseInt(tagId+""))
                        .andIsMainEqualTo(isMain).andStatusEqualTo(false);
                RowBounds rowBounds = new RowBounds(start, pageSize);
                gtcList=goodsTagCompositionMapper.selectByExampleWithRowbounds(goodsTagCompositionExample, rowBounds);
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
			new LogException(e,map);
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
	public ReturnData addTagComposition(String tagIds,String compositionIds, boolean isMain){
		try {
			if(!StringUtils.isBlank(tagIds) && !StringUtils.isBlank(compositionIds)){
				List<GoodsTagComposition> allGtcList= getAllGoodsTagComposition();
				String[] tag_idss=tagIds.split(",");
				String[] composition_idss=compositionIds.split(",");
				List<GoodsTagComposition> listMap=new ArrayList<GoodsTagComposition>();
				for(int i=0;i<tag_idss.length;i++){
					List<Long> existCpsList=new ArrayList<Long>();
					List<GoodsTagComposition> gtcList=new ArrayList<GoodsTagComposition>();
					for(GoodsTagComposition gtc:allGtcList){
						if(gtc.getTagId().equals(Integer.valueOf(tag_idss[i]))){
							//避免误操作,表中存在的标签才能添加成分
							gtcList.add(gtc);
						}
					}

					//查找存在的标签,用于排除
					if(gtcList.size()>0){
						for(GoodsTagComposition gtc:gtcList){
							for(int j=0;j<composition_idss.length;j++){
								if(gtc.getCompositionId().equals(Integer.valueOf(composition_idss[j]))){
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
							GoodsTagComposition goodsTagComposition = new GoodsTagComposition();
							//排除存在的成分
							if(!existCpsList.contains(Long.valueOf(composition_idss[j]))){
								goodsTagComposition.withTagId(Integer.parseInt(tag_idss[i]))
										.withCompositionId(Integer.parseInt(composition_idss[j]))
										.withStatus(true)
										.withCreateStamp(DateUtils.nowInSeconds())
										.withIsMain(isMain);
								if(isMain){
									goodsTagComposition.setMainName("Y");
								}else{
									goodsTagComposition.setMainName("");
								}
								listMap.add(goodsTagComposition);
							}
						}
						//要添加的成分都是可以直接添加的
					}else if(gtcList.size()>0 && (existCpsList.size()==0 || null==existCpsList)){
						for(int j=0;j<composition_idss.length;j++){
							GoodsTagComposition goodsTagComposition = new GoodsTagComposition();
							goodsTagComposition.withTagId(Integer.parseInt(tag_idss[i]))
									.withCompositionId(Integer.parseInt(composition_idss[j]))
									.withStatus(true)
									.withCreateStamp(DateUtils.nowInSeconds())
									.withIsMain(isMain);
							if(isMain){
								goodsTagComposition.setMainName("Y");
							}else{
								goodsTagComposition.setMainName("");
							}
							listMap.add(goodsTagComposition);
						}
					}
				}
				if(listMap.size()>0){
				    goodsTagCompositionMapper.batchInsert(listMap);
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
			new LogException(e,map);
		}
		return ReturnData.ERROR;
	}


	public List<GoodsTagComposition> getAllTagComposition(){
        GoodsTagCompositionExample goodsTagCompositionExample = new GoodsTagCompositionExample();
        goodsTagCompositionExample.createCriteria().andStatusEqualTo(false);
        return goodsTagCompositionMapper.selectByExample(goodsTagCompositionExample);
    }

	/**
	 * 获取所有的非隐藏产品成分标签
	 * @return
	 */
	public List<GoodsTagComposition> getAllGoodsTagComposition(){
		try {
			List<GoodsTagComposition> gtcList=getAllTagComposition();
			return gtcList;
		} catch (Exception e) {
			Map map=new HashMap();
			map.put("method", "BackGoodsService.getAllGoodsTagComposition");
			new LogException(e,map);
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

				List<Integer> compositionIdsList = new ArrayList<>();
				String[] compositionIdsArr = compositionIds.split(",");
				for(String str:compositionIdsArr){
					compositionIdsList.add(Integer.parseInt(str));
				}

				String[] tagIdss=tagIds.split(",");
				for(int i=0;i<tagIdss.length;i++){
					GoodsTagCompositionExample goodsTagCompositionExample = new GoodsTagCompositionExample();
					goodsTagCompositionExample.createCriteria().andTagIdEqualTo(Integer.parseInt(tagIdss[i]))
							.andCompositionIdIn(compositionIdsList);
					goodsTagCompositionMapper.updateByExampleSelective(new GoodsTagComposition().withStatus(true) ,goodsTagCompositionExample);
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
			new LogException(e,map);
		}
		return ReturnData.ERROR;
	}

	public GoodsTagResult findTagResultByGoodsId(Long goodsId){
        GoodsTagResultExample goodsTagResultExample = new GoodsTagResultExample();
        goodsTagResultExample.createCriteria().andGoodsIdEqualTo(Integer.parseInt(goodsId+""));
        return goodsTagResultMapper.selectOneByExample(goodsTagResultExample);
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
				GoodsTagExample goodsTagExample = new GoodsTagExample();
				goodsTagExample.createCriteria().andStatusEqualTo(false);
				List<GoodsTag> gtList= goodsTagMapper.selectByExample(goodsTagExample);

				for(int i=0;i<goodsIdss.length;i++){
					//根据goodsId找到tagId
                    GoodsTagResultExample goodsTagResultExample = new GoodsTagResultExample();
                    goodsTagResultExample.createCriteria().andGoodsIdEqualTo(Integer.parseInt(goodsIdss[i]));
					GoodsTagResult gtr= goodsTagResultMapper.selectOneByExample(goodsTagResultExample);
					boolean flag=false;

					//表中存在就修改
					if(null!=gtr){
						String madeTagIds="";
						String madeTagNames="";
						for(GoodsTag gt:gtList){
							for(int j=0;j<tagIdss.length;j++){
								if(Integer.valueOf(tagIdss[j]).equals(gt.getId())){
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
							gtr.setMadeDelete(false);
							flag=true;
						}

					}
					if(flag){
					    GoodsTagResultExample goodsTagResultExample1 = new GoodsTagResultExample();
					    goodsTagResultExample1.createCriteria().andGoodsIdEqualTo(gtr.getGoodsId());
					    gtr.setGoodsId(null);
					    goodsTagResultMapper.updateByExampleSelective(gtr, goodsTagResultExample1);
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
			new LogException(e,map);
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
            List<Integer> idsList = new ArrayList<>();
			for(GoodsTagResult gtr:gtrList){
				//获取产品id
                idsList.add(gtr.getGoodsId());
			}
			List<Goods> glist=new ArrayList<Goods>();
			if(idsList.size()>0){
				//获取产品列表
                GoodsExample goodsExample = new GoodsExample();
                goodsExample.createCriteria().andIdIn(idsList);
				glist=goodsMapper.selectByExample(goodsExample);
			}
			return new ReturnListData(glist,glist.size());
		} catch (Exception e) {
			Map map=new HashMap();
			map.put("method", "BackGoodsService.method:findGoodsByTagId");
			map.put("tagId", tagId);
			map.put("size", size);
			map.put("pageSize", pageSize);
			new LogException(e,map);
		}
		return ReturnListData.ERROR;
	}


	public List<GoodsTag> getAllTag(){
        GoodsTagExample goodsTagExample = new GoodsTagExample();
        goodsTagExample.createCriteria().andStatusEqualTo(false);
        return goodsTagMapper.selectByExample(goodsTagExample);
    }

	/**
	 * 删除含有某个标签的产品 --批量隐藏产品(hq_goods_tag_result)
	 * @param tagId: 产品标签id
	 * @param size
	 * @param pageSize
	 */
	public ReturnData delGoodsByTagId(Long tagId){
		try {
            List<GoodsTag> tlist= getAllTag();
			boolean flag=false;
			for(int i=0;!flag && i<tlist.size();i++){
				if(tlist.get(i).getId()==tagId.intValue()){
					flag=true;
				}
			}
			if(flag){
			    //删除
			    GoodsTagResultExample goodsTagResultExample = new GoodsTagResultExample();
                goodsTagResultExample.createCriteria().andTagIdsLike("%,"+tagId+",%");
                GoodsTagResultExample.Criteria criteria2 = goodsTagResultExample.createCriteria();
                criteria2.andTagIdsLike("%,"+tagId+"%");
                GoodsTagResultExample.Criteria criteria3 = goodsTagResultExample.createCriteria();
                criteria3.andTagIdsLike("%"+tagId+",%");
                goodsTagResultExample.or(criteria2);
                goodsTagResultExample.or(criteria3);
			    goodsTagResultMapper.updateByExampleSelective(new GoodsTagResult().withStatus(true) ,goodsTagResultExample);
			}else{
				return ReturnData.FAILURE;
			}
			return ReturnData.SUCCESS;
		} catch (Exception e) {
			Map map=new HashMap();
			map.put("method", "BackGoodsService.delGoodsByTagId");
			map.put("tagId", tagId);
			new LogException(e,map);
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
			int start=0;
			if(size>1){
				start=(size-1)*pageSize;
			}
			List<GoodsTagResult> gtrList=selectGoodsTagResultInTagId(tagId,start,pageSize);
			return gtrList;
		} catch (Exception e) {
			Map map=new HashMap();
			map.put("method", "BackGoodsService.getGoodsByTagId");
			map.put("tagId", tagId);
			map.put("size", size);
			map.put("pageSize", pageSize);
			new LogException(e,map);
		}
		return new ArrayList();
	}

	public List<GoodsTagResult> selectGoodsTagResultInTagId(Long tagId, int start, int pageSize){
        GoodsTagResultExample goodsTagResultExample = new GoodsTagResultExample();
        goodsTagResultExample.createCriteria().andTagIdsLike("%,"+tagId+",%");
        GoodsTagResultExample.Criteria criteria2 = goodsTagResultExample.createCriteria();
        criteria2.andTagIdsLike("%,"+tagId+"%");
        GoodsTagResultExample.Criteria criteria3 = goodsTagResultExample.createCriteria();
        criteria3.andTagIdsLike("%"+tagId+",%");
        goodsTagResultExample.or(criteria2);
        goodsTagResultExample.or(criteria3);
        return goodsTagResultMapper.selectByExampleWithRowbounds(
                goodsTagResultExample,
                new RowBounds(start, pageSize)
        );
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
				List<GoodsTag> gtList=getAllTag();
				String[] goodsIdss=goodsIds.split(",");
				String[] tagIdss=tagIds.split(",");
				Map<Long,String> map=new HashMap<Long,String>();
				for(int i=0;i<goodsIdss.length;i++){
					//查找数据,是否存在
					GoodsTagResult goodsTagResult=findTagResultByGoodsId(Long.valueOf(goodsIdss[i]));
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
								map.put(gt.getId().longValue(), gt.getName());
							}
						}
					}

					goodsTagResult.setCreateStamp(DateUtils.nowInSeconds());
					//表中没有数据,创建一条
					if(!flag && !StringUtils.isBlank(madeTagIds)){
						madeTagIds=madeTagIds.substring(0,madeTagIds.length()-1);
						madeTagNames=madeTagNames.substring(0,madeTagNames.length()-1);
						goodsTagResult.setGoodsId(Integer.valueOf(goodsIdss[i]));
						goodsTagResult.setMadeTagIds(madeTagIds);
						goodsTagResult.setMadeTagNames(madeTagNames);
						goodsTagResult.setTagIds(madeTagIds);
						goodsTagResult.setTagNames(madeTagNames);
						if(!StringUtils.isBlank(goodsTagResult.getTagIds())){
						    goodsTagResultMapper.insert(goodsTagResult);
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
						goodsTagResult.setMadeDelete(false);
						goodsTagResultMapper.updateByExampleSelective(
						        goodsTagResult,
                                new GoodsTagResultExample().createCriteria()
                                        .andGoodsIdEqualTo(goodsTagResult.getGoodsId())
                                        .example()
                        );
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
			new LogException(e,map);
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
					GoodsTagResult goodsTagResult=findTagResultByGoodsId(Long.valueOf(goodsIdss[i]));
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
							goodsTagResult.setMadeDelete(true);
							flag2=true;
						}
					}

					if(flag2){
						goodsTagResult.setMadeTagIds(newTagIds);
						goodsTagResult.setMadeTagNames(newTagNames);
						goodsTagResult.setTagIds(newTagIds);
						goodsTagResult.setTagNames(newTagNames);
						goodsTagResult.setUpdateTime(DateUtils.nowInSeconds());
                        goodsTagResultMapper.updateByExampleSelective(
                                goodsTagResult,
                                new GoodsTagResultExample().createCriteria()
                                        .andGoodsIdEqualTo(goodsTagResult.getGoodsId())
                                        .example()
                        );
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
			new LogException(e,map);
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
						map.put("updateTime", DateUtils.nowInSeconds());
						map.put("existCategoryIds", 0);
						newCategoryList.add(map);
                        goodsPloyCategoryComplexMapper.updatePloyCategory(newCategoryList);
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
			new LogException(e,map);
		}
		return ReturnData.ERROR;
	}

	/**
	 * 获取单个产品的多分类数据
	 * @param goodsIds
	 * @return
	 */
	public List<Map<String,Object>>  getPolyCategoryBygoodsIdsLocal(String goodsIds) {
		try {
            List<GoodsPolyCategory> goodsPolyCategoryList=goodsPolyCategoryMapper.selectByExample(
                    new GoodsPolyCategoryExample().createCriteria()
                            .andGoodsIdEqualTo(Integer.parseInt(goodsIds))
                            .example()
            );
            List<Map<String,Object>> geu = new ArrayList<>();
            for(GoodsPolyCategory goodsPolyCategory:goodsPolyCategoryList){
                if (goodsPolyCategory != null) {
                    Map<String, Object> map = Maps.newHashMap();
                    BeanMap beanMap = BeanMap.create(goodsPolyCategory);
                    for (Object key : beanMap.keySet()) {
                        map.put(key+"", beanMap.get(key));
                    }
                    geu.add(map);
                }
            }
			return geu;
		} catch (Exception e) {
			Map map=new HashMap();
			map.put("method", "BackGoodsService.getPolyCategoryBygoodsIdsLocal");
			map.put("goodsIds", goodsIds);
			new LogException(e,map);
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
				listMap=goodsOldMapper.getAllCommonGoodsCategory();
			}else if(type==2){
				listMap=goodsOldMapper.getAllSpecialCategory();
			}

			//获取产品分类
			List<Map<String,Object>> categoryList=goodsOldMapper.getAllGoodsCategory();
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
			new LogException(e,map);
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
			List<Map<String, Object>> listMap =backGoodsOldMapper.getCommonGoodsCategoryById(id);
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
                backGoodsOldMapper.updateCommonGoodsCategoryById(id,nowRule1,nowRule2);

				return ReturnData.SUCCESS;
			}
			return ReturnData.FAILURE;
		}catch(Exception e){
			Map map=new HashMap();
			map.put("method", "BackGoodsService.addeCategoryRules");
			map.put("newRule1", newRule1);
			map.put("id", id);
			map.put("newRule2", newRule2);
			new LogException(e,map);
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
			List<Map<String, Object>> listMap =backGoodsOldMapper.getCommonGoodsCategoryById(id);
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
            backGoodsOldMapper.updateCommonGoodsCategoryById(id,nowRule1,nowRule2);

			return ReturnData.SUCCESS;
		}catch(Exception e){
			Map map=new HashMap();
			map.put("method", "BackGoodsService.updateCategoryRules");
			map.put("newRule1", newRule1);
			map.put("id", id);
			map.put("newRule2", newRule2);
			new LogException(e,map);
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
			List<Map<String, Object>> listMap =goodsOldMapper.getAllGoodsCategory();
			boolean flag=false;
			for(Map map:listMap){
				if(null!=map.get("id") && (int)map.get("id")==categoryId){
					flag=true;
				}

			}
			if(!flag){
				return new ReturnData("产品分类id不存在");
			}

			List<Map<String, Object>> listMap2 =goodsOldMapper.getAllCommonGoodsCategory();
			for(Map map:listMap2){
				if(null!=map.get("category_id") && (int)map.get("category_id")==categoryId){
					flag=false;
				}
			}
			if(!flag){
				return new ReturnData("该分类规则存在");
			}

			int i=backGoodsOldMapper.addCategoryRule(newRule1,newRule2,categoryId);

			return ReturnData.SUCCESS;
		}catch(Exception e){
			Map map=new HashMap();
			map.put("method", "BackGoodsService.addCategoryRules");
			map.put("categoryId", categoryId);
			map.put("newRule1", newRule1);
			map.put("newRule2", newRule2);
			new LogException(e,map);
		}
		return ReturnData.FAILURE;
	}

	/**
	 * 产品分类列表
	 * @return
	 */
	public ReturnListData goodsCategoryList() {
		try{
			List<Map<String, Object>> listMap =goodsOldMapper.getAllGoodsCategory();
			return new ReturnListData(listMap,listMap.size());
		}catch(Exception e){
			Map map=new HashMap();
			map.put("method", "BackGoodsService.goodsCategoryList");
			new LogException(e,map);
		}
		return new ReturnListData();
	}

	/**
	 * 后台添加首页精选点评
	 * @param type: 1修行说 2精选点评
	 * @param typeId: type=1,typeId:修行说id; type=2,typeId:评论id
	 * @param publishTime: 发布时间
	 * @return
	 */
	public ReturnData addEssenceComment(int type, long typeId, long publishTime) {
		try {

			String actionType = "entity_comment_goods";
			String entityName="user_info";
			//1修行说 2精选点评
			if(type==1){
				Doyen doyen=doyenOldMapper.getDoyenById2(typeId);
				long goodsId=doyen.getGoodsId();
				long userId=doyen.getUserId();
				cn.bevol.internal.entity.model.Goods goods=goodsOldMapper.getById(goodsId);
				//mongo评论  查doyen(goods_id user_id)
				EssenceComment ec=new EssenceComment();
				ec.setUserId(userId);
				ec.setType(type);
				ec.setTypeId(typeId);
				ec.setContent(doyen.getDoyenComment());
				ReturnData rd=userService.getUserById(userId);
				UserInfo userInfo=(UserInfo)rd.getResult();
				ec.setNickname(userInfo.getNickname());
				ec.setHeadimgurl(userInfo.getHeadimgurl());
				ec.setUserDescz(userInfo.getDescz());
				ec.setSkinResults(userInfo.getSkinResults());
				ec.setSkin(userInfo.getResult());
				ec.setGoodsId(goodsId);
				ec.setGoodsImage(goods.getImage());
				ec.setGoodsMid(goods.getMid());
				ec.setGoodsTitle(goods.getTitle());
				ec.setPublishTime(publishTime);
				ec.setIsEssence(0);
				ec.setHiddenStatus(0);
				ec.setLikeNum(0L);
				ec.setCreateTime(DateUtils.nowInMillis()/1000);
				//insert hq_essence_comment
				int i=indexOldMapper.addEssenceComment(ec);
				if(i!=1){
					return ReturnData.FAILURE;
				}
				mongoTemplate.updateFirst(new Query(Criteria.where("id").is(userInfo.getId())), new Update().inc("xxsNum", 1), UserInfo.class,entityName);
				return ReturnData.SUCCESS;
			}else if(type==2){
				//根据typeId到mongodb查goodsId userId
				Query query = new Query(Criteria.where("id").is(typeId));
				Comment comment = mongoTemplate.findOne(query, Comment.class, actionType);
				EssenceComment ec=new EssenceComment();
				ec.setUserId(comment.getUserId());
				ec.setType(type);
				ec.setTypeId(typeId);
				//转码
				ec.setContent(StringEscapeUtils.escapeJava(comment.getContent()));
				ec.setSkinResults(comment.getSkinResults());
				ec.setSkin(comment.getSkin());
				ec.setIsEssence(comment.getIsEssence());
				ec.setLikeNum(comment.getLikeNum());
				ec.setHiddenStatus(comment.getHidden());
				//user_id
				ReturnData rd=userService.getUserById(comment.getUserId());
				UserInfo userInfo=(UserInfo)rd.getResult();
				ec.setNickname(userInfo.getNickname());
				ec.setHeadimgurl(userInfo.getHeadimgurl());
				ec.setUserDescz(userInfo.getDescz());
				//goods_id
				ec.setGoodsId(comment.getEntityId());
				cn.bevol.internal.entity.model.Goods goods2=goodsOldMapper.getById(comment.getEntityId());
				ec.setGoodsImage(goods2.getImage());
				ec.setGoodsMid(goods2.getMid());
				ec.setGoodsTitle(goods2.getTitle());
				ec.setPublishTime(publishTime);
				ec.setCreateTime(DateUtils.nowInMillis()/1000);
				ec.setLikeNum(0L);
				//insert hq_essence_comment
				int i=indexOldMapper.addEssenceComment(ec);
				if(i!=1){
					return ReturnData.FAILURE;
				}
				mongoTemplate.updateFirst(new Query(Criteria.where("id").is(userInfo.getId())), new Update().inc("essenceCommentNum", 1), UserInfo.class,entityName);
				return ReturnData.SUCCESS;
			}
			return ReturnData.FAILURE;
		} catch (Exception e) {
			Map map=new HashMap();
			map.put("method", "BackGoodsService.addEssenceComment");
			map.put("type", type);
			map.put("typeId", typeId);
			map.put("publishTime", publishTime);
			new LogException(e,map);
			return ReturnData.FAILURE;
		}
	}



}