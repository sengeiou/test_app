package cn.bevol.entity.service;

import cn.bevol.log.LogMethod;
import cn.bevol.model.metadata.Tag;
import cn.bevol.model.user.UserInfo;
import cn.bevol.mybatis.dao.GoodsMapper;
import cn.bevol.mybatis.dao.UserInfoMapper;
import cn.bevol.mybatis.dao.VerificationCodeMapper;
import cn.bevol.util.ReturnData;
import com.io97.cache.redis.RedisCacheProvider;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SkinFlowService extends BaseService {

    @Autowired
    MongoTemplate mongoTemplate; 

    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    VerificationCodeMapper verificationCodeMapper;

    @Autowired
    RedisCacheProvider cacheProvider;

    @Autowired
    EntityService entityService;

    @Autowired
    MessageService messageService;


    @Autowired
	AliyunService aliyunService;
    
	 @Autowired
	 GoodsMapper goodsMapper;
	 
	    @Autowired
	    UserService userService;


    private static Logger logger = LoggerFactory.getLogger(SkinFlowService.class);
 
    private static String sourceDir="goods_upload/images";
    
    /**
     * 添加用户感兴趣标签
     * @param userInfo 用户信息
     * @param skinTags 感兴趣的标签
     * @return
     */
    @LogMethod
	public ReturnData addOrUpdateSkinTags(UserInfo userInfo,String skinTags) {
		try {
			//userInfo = mongoTemplate.findOne(new Query(Criteria.where("id").is(userInfo.getId())),  UserInfo.class);
			if(StringUtils.isBlank(skinTags))  return ReturnData.ERROR;
			JSONArray  jsonSkinTags  =null;
			if(StringUtils.isNotBlank(skinTags)){
				jsonSkinTags=JSONArray.fromObject(skinTags);
				if(jsonSkinTags== null) return ReturnData.ERROR;;
			} 
		 	
			List<Tag> tags=new ArrayList<Tag>();
			if(jsonSkinTags.size()>7) return ReturnData.ERROR;
			
			//是否更新标签
			boolean tagFlag=false;
			//解析标签
			for(int i=0;i<jsonSkinTags.size();i++) {
				JSONObject obj=(JSONObject) jsonSkinTags.get(i);
				if(obj.getLong("id")>0&&StringUtils.isNotBlank(obj.getString("title"))) {
					tags.add(new Tag(obj.getLong("id"),obj.getString("title")));
					tagFlag=true;
				}
			}
			
			Update update=new Update();
		 	if(tagFlag) {
		 		update.set("skinTags", tags);
		 	}

		 	//重新加载用户
		 	mongoTemplate.updateFirst(new Query(Criteria.where("id").is(userInfo.getId())), update, UserInfo.class);
		 	return ReturnData.SUCCESS;
		}catch(Exception e) {
			Map map=new HashMap();
    		map.put("method", "SkinFlowService.addOrUpdateSkinTags");
    		new cn.bevol.log.LogException(e,map);
		}
		return ReturnData.ERROR;
	}

 

 	
}


