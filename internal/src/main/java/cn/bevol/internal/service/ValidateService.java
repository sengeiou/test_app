package cn.bevol.internal.service;

import cn.bevol.model.entity.MongoBase;
import cn.bevol.util.ConfUtils;
import cn.bevol.util.DateUtils;
import cn.bevol.util.response.ReturnData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;



/**
 * 反馈处理
 * @author Administrator
 *
 */
@Service
public class ValidateService extends BaseService {
    private static Logger logger = LoggerFactory.getLogger(ValidateService.class);

    /**
     * 查看自己上一条记录的发送时间
     * @param userId
     * @param tname
     * @return
     */
    public ReturnData vSendTime(long userId, String tname) {
        long time= DateUtils.nowInMillis()/1000- ConfUtils.getResourceNum("send_min_time");
 		MongoBase firstTime=mongoTemplate.findOne(new Query(Criteria.where("userId").is(userId).and("createStamp").gt(time)), MongoBase.class, tname);
        if(firstTime!=null) return new ReturnData(-3,"发送的太快了！");
        return new ReturnData();
	}
    
}
