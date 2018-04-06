package cn.bevol.statics.service;

import cn.bevol.statics.dao.mapper.GoodsOldMapper;
import cn.bevol.statics.dao.mapper.UserInfoOldMapper;
import cn.bevol.statics.entity.model.Goods;
import cn.bevol.statics.entity.user.UserInfo;
import cn.bevol.util.CommonUtils;
import cn.bevol.util.Log.LogMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StaticShareGoodsService{

	@Autowired
    MongoTemplate mongoTemplate;
	@Resource
	private GoodsOldMapper goodsOldMapper;
	@Resource
	private UserInfoOldMapper userInfoOldMapper;
	private static Logger logger = LoggerFactory.getLogger(SinglePageService.class);

	/**
	 * 准备分享产品静态化的数据
	 *  @param commentId
	 */
	@LogMethod
	public Map getShareGoods(Integer id){
		Map<String,Object> dataMap=new HashMap<String,Object>();
		Query query=new Query(Criteria.where("id").is(id.intValue()));
		String actionType="entity_comment_goods";
		Map commentMap=mongoTemplate.findOne(query,HashMap.class,actionType);
		//评论内容
		dataMap.put("content", commentMap.get("content"));
		dataMap.put("score", commentMap.get("score"));

		String userId=commentMap.get("userId")+"";
		List<UserInfo> user=userInfoOldMapper.findUserinfoByIds(userId);
		//用户昵称和头像
		String nickName=user.get(0).getNickname();
		String headImgUrl=user.get(0).getHeadimgurl();
		dataMap.put("nickName", nickName);
		dataMap.put("headImgUrl", headImgUrl);

		String skinResults=(String) commentMap.get("skinResults");
		if(skinResults!=null){
			String[] skinMap = SinglePageService.getSkinDesc(skinResults);
			dataMap.put("skin", skinMap);
		}
		//产品图片
		String entityId=commentMap.get("entityId")+"";
		Goods goods=goodsOldMapper.getById(Long.parseLong(entityId));
		dataMap.put("title", goods.getTitle());
		String alias=goods.getAlias();
		if(alias!=null){
			dataMap.put("alias", alias);
		}
		String imgSrc= CommonUtils.getImageSrc("goods", (String)goods.getImage());
		dataMap.put("imgSrc", imgSrc);
		return dataMap;
	}
	//产品分享页面静态化
	public Boolean staticShareGoods(Integer id, Integer switchOfcreate){
		Map<String,Object> dataMap=getShareGoods(id);
		if(dataMap == null){
			logger.error("staticGeneralPage:" +":dataMap为null数据读取出错！");
			return false;
		}else{
			String uploadPath = "commentGoods/share/" + id + ".html";
			Boolean flag=ifCreate(id);
			if(!flag || 1==switchOfcreate){
				try {
					return SinglePageService.staticGeneralPage(dataMap, "mobile", "goods_info", uploadPath);
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("静态化mobile产品分享页面出错"+id);
				}

			}else{
				return true;
			}
		}
		return false;
	}


	/**
	 * 判断是否生成评论模板
	 * @param id
	 * @return  true 已生成  false 未生成
	 */
	public Boolean ifCreate(Integer id){
		String key = "commentGoods/share/"+id+ ".html";
		return OSSService.isExist(OSSService.getMClient(), OSSService.getMBucketName(), key);
	}
}
