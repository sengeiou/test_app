package cn.bevol.app.service;

import cn.bevol.model.Base;
import cn.bevol.util.ConfUtils;
import cn.bevol.util.response.ReturnData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 反馈处理
 * @author Administrator
 *
 */
@Service
public class ValidateService extends BaseService{
    private static Logger logger = LoggerFactory.getLogger(ValidateService.class);

    /**
     * 查看自己上一条记录的发送时间
     * @param userId
     * @param tname
     * @return
     */
    public ReturnData vSendTime(long userId, String tname) {
        long time=new Date().getTime()/1000- ConfUtils.getResourceNum("send_min_time");
 		Base firstTime=mongoTemplate.findOne(new Query(Criteria.where("userId").is(userId).and("createStamp").gt(time)), Base.class, tname);
        if(firstTime!=null) return new ReturnData<>(-3,"发送的太快了！");
        return new ReturnData();
	}
    
    /**
     * 用于评论
     * 查询设定时间内是否有带有评论内容的评论
     * @param userId
     * @param tname
     * @return
     */
    public ReturnData commentSendTime(long userId, String tname) {
        long time=new Date().getTime()/1000-ConfUtils.getResourceNum("send_min_time");
 		Base firstTime=mongoTemplate.findOne(new Query(Criteria.where("userId").is(userId).and("createStamp").gt(time).and("content").exists(true)), Base.class, tname);
        if(firstTime!=null) return new ReturnData(-3,"发送的太快了！");
        return new ReturnData();
	}
    
    
    /**
     * 用于对比的支持
     * 查询设定时间内是否有支持过
     * @param userId
     * @param tname
     * @return
     */
    public ReturnData comparegGoodsLikeSendTime(long userId,String sid, String tname) {
        long time=new Date().getTime()/1000-ConfUtils.getResourceNum("compareGoods_like_send_min_time");
 		Base firstTime=mongoTemplate.findOne(new Query(Criteria.where("userId").is(userId).and("sid").is(sid).and("updateStamp").gt(time)), Base.class, tname);
        if(firstTime!=null) return new ReturnData(-3,"支持发送的太快了！");
        return new ReturnData();
	}

}
