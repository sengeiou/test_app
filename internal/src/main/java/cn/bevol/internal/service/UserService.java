package cn.bevol.internal.service;

import cn.bevol.util.response.ReturnData;
import cn.bevol.internal.cache.redis.RedisCacheProvider;
import cn.bevol.internal.dao.mapper.UserInfoOldMapper;
import cn.bevol.internal.entity.user.UserInfo;
import cn.bevol.internal.entity.vo.SmartUserInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService extends BaseService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    UserInfoOldMapper userInfoOldMapper;

    @Autowired
    RedisCacheProvider cacheProvider;

    @Autowired
    EntityService entityService;

    @Autowired
    MessageService messageService;


    @Autowired
    AliyunService aliyunService;

    @Autowired
    BackUserService backUserService;

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * 获取用户基本信息
     * @param ids
     * @return
     */
    public List<SmartUserInfo> findSmartUserInfoByIds2(List<Long> ids) {
        // TODO Auto-generated method stub
        try {
            String idss= StringUtils.join(ids,",");
            List<UserInfo> userInfo = userInfoOldMapper.findUserinfoByIds(idss);
            if (userInfo == null) {
                userInfo = new ArrayList();
            }
            List<SmartUserInfo> sis = new ArrayList<SmartUserInfo>();
            for (UserInfo ui : userInfo) {
                SmartUserInfo su = new SmartUserInfo();
                su.setHeadimgurl(ui.getHeadimgurl());
                su.setId(ui.getId());
                su.setNickname(ui.getNickname());
                sis.add(su);
            }
            return sis;
        } catch (Exception e) {
            logger.error("method:findSmartUserInfoByIds2 arg:{\"ids\":\"" + ids + "\"}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return null;
    }





    public enum ScoreOpt {
        /**
         * finalskin
         * 完成肤质测试
         */
        FINISHEDSKIN {
            public int getVal() {
                return 50;
            }
        },
        /**
         * 发送产品评论评论
         */
        SENDCOMMNEGOODS {
            public int getVal() {
                return 5;
            }
        },
        /**
         * 发送成分评论
         */
        SENDCOMMNECOMPOSITION {
            public int getVal() {
                return 5;
            }
        },
        /**
         * 发送发现评论
         */
        SENDCOMMENTFIND {
            public int getVal() {
                return 2;
            }
        },
        /**
         * 点评加精
         */
        COMMENTESSENCE {
            public int getVal() {
                return 20;
            }
        };

        public abstract int getVal();
    }

    /**
     * 增加用户积分
     */
    public void addScore(long userId, ScoreOpt opt) {
        mongoTemplate.findAndModify(new Query(Criteria.where("id").is(userId)), new Update().inc("score", opt.getVal()), new FindAndModifyOptions().returnNew(true).upsert(true), UserInfo.class, "user_info");
    }




    /**
     * 查询用户信息
     *
     * @param id
     * @return
     */
    public ReturnData<UserInfo> getUserById(long id) {
        try {
            UserInfo userInfo = userInfoOldMapper.findFeild("id", id);
            //获取消息数量

            if (userInfo != null)
                return new ReturnData<UserInfo>(userInfo);
        } catch (Exception e) {
            logger.error("method:getUserById arg:{\"id\":\"" + id + "\"}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
        }
        return ReturnData.ERROR;
    }

}


