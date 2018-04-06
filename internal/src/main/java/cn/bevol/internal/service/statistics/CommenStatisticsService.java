package cn.bevol.internal.service.statistics;

import cn.bevol.util.response.ReturnData;
import cn.bevol.util.ComReqModel;
import cn.bevol.util.DateUtils;
import cn.bevol.util.statistics.StatisticsI;
import flexjson.JSONDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

//import com.io97.utils.DateUtils;

/**
 * Created by owen on 16-7-13.
 */
@Service
public class CommenStatisticsService implements StatisticsI {


    @Autowired
    MongoTemplate mongoTemplate;

    public void increseUserDailyActive(HttpServletRequest request, String actionType) {
        try {
            String uuid = request.getParameter(KEY_UUID);
            String machineModel = request.getParameter(KEY_MODEL) == null ? "" : request.getParameter(KEY_MODEL);
            int date4Today = DateUtils.timeStampParseInt();
            String platform = request.getParameter(KEY_PLATFORM);
            String version = request.getParameter(KEY_VERSION);
            String sys_v = request.getParameter(KEY_SYS_V);
            String channel = request.getParameter(KEY_CHANEL);

            
            if (StringUtils.isEmpty(platform)) {
                platform = "other";
            }
            if(StringUtils.isEmpty(sys_v)) {
            	sys_v="";
            }
            if (StringUtils.isEmpty(version)) {
                version = "1.x";
            }

            Integer userId;
            try {
                userId = Integer.parseInt(request.getParameter(KEY_USER_ID));
            } catch (Exception ex) {
                userId = 0;
            }

            if (!StringUtils.isEmpty(uuid)) {
                mongoTemplate.upsert(
                        new Query(Criteria.where("date").is(date4Today).where(FIELD_UUID).is(uuid)),
                        new Update().set(FIELD_USER_ID, userId).set(FIELD_VERSION, version.toLowerCase())
                                .set(FIELD_PLATFORM, platform.toLowerCase())
                                .set(FIELD_MODEL, machineModel.toLowerCase())
                                .set(KEY_SYS_V, sys_v.toLowerCase())
                                .set(KEY_CHANEL, channel)
                                .inc(FIELD_TOTAL_NUM, 1),
                        actionType + date4Today);
            }


        } catch (Exception ex) {
            //todo  log
        }
    }
    
    /**
     * tname限制
     */
    public static final Map<String,String> TNAME_MAP = new HashMap<String,String>();
    static{
    	TNAME_MAP.put("goods", "1");
    	TNAME_MAP.put("composition", "1");
    };
    /**
     * 搜索统计
     * @param request.tname 实体类型 对应TNAME_MAP 
     * 		  request。o=pc weixin 来源
     * 	      	
     * @return
     */
    public ReturnData userSearchActive(HttpServletRequest request) {
        try {
            String uuid = request.getParameter(KEY_UUID);
            String machineModel = request.getParameter(KEY_MODEL) == null ? "" : request.getParameter(KEY_MODEL);
            String platform = request.getParameter(KEY_PLATFORM);
            String version = request.getParameter(KEY_VERSION) ;
            String sys_v = request.getParameter(KEY_SYS_V);
            String channel = request.getParameter(KEY_CHANEL);

            if (StringUtils.isEmpty(platform)) {
                platform = "other";
            }
            if(StringUtils.isEmpty(sys_v)) {
            	sys_v="";
            }

            Integer userId;
            try {
                userId = Integer.parseInt(request.getParameter(KEY_USER_ID));
            } catch (Exception ex) {
                userId = 0;
            }

            String tname = request.getParameter(FIELD_TNAME);
            String keywords = request.getParameter(FIELD_KEYWORDS);

            if(TNAME_MAP.get(tname)!=null&&!org.apache.commons.lang3.StringUtils.isBlank(keywords)) {
            	//统计
            	Map map=new HashMap();
            	
            	map.put(KEY_CHANEL, channel);
            	map.put(FIELD_USER_ID, userId);
            	map.put(FIELD_UUID, uuid);
            	if(!org.apache.commons.lang3.StringUtils.isBlank(version))
            	map.put(FIELD_VERSION, version.toLowerCase());
            	map.put(FIELD_PLATFORM, platform.toLowerCase());
            	map.put(FIELD_MODEL, machineModel.toLowerCase());
            	map.put(KEY_SYS_V, sys_v.toLowerCase());

        		try {
        			keywords= URLDecoder.decode(keywords, "utf-8");
        		} catch (Exception e) {
        			// TODO Auto-generated catch block
        			ReturnData error = ReturnData.ERROR;
        			return error;
        		};
    			Map<String,Object> jsonNode=  new JSONDeserializer<Map<String,Object>>().deserialize(keywords, HashMap.class);
            	map.put(FIELD_KEYWORDS, jsonNode);
            	map.put("createStamp", DateUtils.nowInMillis()/1000);
            	mongoTemplate.insert(map,COLLECTION_NOSEARCH_PRE+tname);
            }
        } catch (Exception ex) {
            //todo  log
        }
        return ReturnData.SUCCESS;
    }
    
    /**
     * 邮箱注册用户统计
			注册统计表user_reg
			uid 用户id
			account      账号
			accountType  类型1手机 2邮箱
			createStamp注册时间
			regType=1 微信绑定的
			regType=2 直接注册的
			 @return
     */
    public ReturnData userRegActive(HttpServletRequest request, long userId, String account, int accountType, int type) {
        try {
            String uuid = request.getParameter(KEY_UUID);
            
            String channel = request.getParameter(KEY_CHANEL);

            String machineModel = request.getParameter(KEY_MODEL) == null ? "" : request.getParameter(KEY_MODEL);
            String platform = request.getParameter(KEY_PLATFORM);
            String version = request.getParameter(KEY_VERSION) ;
            
            if (StringUtils.isEmpty(platform)) {
                platform = "other";
            }

            	//统计   todo  del
    	Map map=new HashMap();
    	map.put(FIELD_USER_ID, userId);
    	
    	map.put("account", account);
    	map.put("accountType", accountType);
    	map.put("regType", type);
    	map.put(KEY_CHANEL, channel);

    	map.put(FIELD_UUID, uuid);
    	if(!org.apache.commons.lang3.StringUtils.isBlank(version))
    	map.put(FIELD_VERSION, version.toLowerCase());
    	map.put(FIELD_PLATFORM, platform.toLowerCase());
    	map.put(FIELD_MODEL, machineModel.toLowerCase());
    	
    	map.put("createStamp", DateUtils.nowInMillis()/1000);
    	mongoTemplate.insert(map,COLLECTION_USR_REG);
        } catch (Exception ex) {
            //todo  log
        }
        return ReturnData.SUCCESS;
    }
    
    
    /**
     * 通用日志保存方法
     * @param col 日志表
     * @param logky 当前的key
     * @return
     */
    public void mongoLog(String col, Map logky) {
    	try{
    		Map map= ComReqModel.getReqComAgrs();
    		map.putAll(logky);
    		mongoTemplate.insert(map,col);
        } catch (Exception ex) {
            //todo  log
        }
    }


}
