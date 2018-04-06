package cn.bevol.app.service;

import cn.bevol.app.cache.redis.RedisCacheProvider;
import cn.bevol.app.dao.mapper.UploadGoodsOldMapper;
import cn.bevol.app.entity.dto.UploadGoods;
import cn.bevol.app.entity.dto.UserSubmitGoods;
import cn.bevol.app.entity.model.Goods;
import cn.bevol.model.user.UserInfo;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UploadGoodsService extends BaseService {
	 private static Logger logger = LoggerFactory.getLogger(UploadGoodsService.class);

	    @Autowired
        MongoTemplate mongoTemplate;
 
	    @Autowired
		RedisCacheProvider cacheProvider;

	    @Autowired
	    private UploadGoodsOldMapper uploadGoodsMapper;
	    @Autowired
	    private GoodsService goodsService;
	    
	    /**
	     * 图片信息保存
	     *
	     * @return
	     */
	    public ReturnData<UploadGoods> uploadGoods(String goodsId,String image,UserInfo userInfo) {
	    	try {
	    		String nickname=userInfo.getNickname();
	    		long userId=userInfo.getId();
	    		Long id=0L;
	    		try {id=Long.parseLong(goodsId);}catch(Exception e){};
	    		Goods goods=null;
	    		//检验产品是否存在
	    		if(id==0) {
	    			goods=goodsService.getGoodsByMid(goodsId);
	    		} else {
		    		goods=goodsService.getGoodsById(id);
	    		}
	    		if(goods==null) new ReturnData<>(-2, "id不正确");
	    		String title=goods.getTitle();
                String mid=goods.getMid();
                id=goods.getId();
                UploadGoods fileUploads2=new UploadGoods(title,image,nickname,userId,mid,id);
                //保存到mysql
                int i=uploadGoodsMapper.uploadGoods(fileUploads2);
                
	    		return ReturnData.SUCCESS;
            } catch (Exception e) {
                logger.error("method:uploadGoods arg:{goodsId:" + goodsId + "}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
                return ReturnData.ERROR;
            }
	    }
	    
	    
	    
	    /**
	     * 上传的图片列表
	     *
	     * @return
	     */
	    public ReturnListData<UploadGoods> mySubmitGoods(int used, int pager, int pageSize, long userId){
	    	try {
	    		long start=0;
                if (pager > 1) {
                    start = Long.valueOf(((pager-1) * pageSize) + "");
                }  
                List<UploadGoods> efs = uploadGoodsMapper.myUploadGoods(used,start,pageSize,userId);
               
                long total = 0;
                if (efs.size() > 0) {
                    total = uploadGoodsMapper.count(used,userId);
                }else{
                	return ReturnListData.SUCCESS;
                }
                return new ReturnListData(efs, total);
            } catch (Exception e) {
                logger.error("method:mySubmitGoods arg:{pager:" + pager + ",pageSize:" + pageSize + "}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
                return ReturnListData.ERROR;
            }
		}


	    /**
	     * 获取uuid
	     * @return
	     */
		public ReturnData getUUIDFileName(Long userId) {
			// TODO Auto-generated method stub
			String fileName = UUID.randomUUID().toString();
			if(null!=userId){
				fileName = userId + "_" +fileName;
			}
			return new ReturnData<String>(fileName);
		}



		public ReturnListData mySubmitGoodsList(int state, int pager, int pageSize, long userId) {
			try {
	    		long start=0;
                if (pager > 1) {
                    start = Long.valueOf(((pager-1) * pageSize) + "");
                }  
                List<UserSubmitGoods> efs = uploadGoodsMapper.mySubmitGoods(state,start,pageSize,userId);
                
                long total = 0;
                if (efs.size() > 0) {
                    total = uploadGoodsMapper.submitGoodsCount(state,userId);
                }else{
                	return ReturnListData.SUCCESS;
                }
                return new ReturnListData(efs, total);
            } catch (Exception e) {
                logger.error("method:UploadGoodsService.mySubmitGoodsList arg:{state:" + state+",userId:"+userId+ "}" + "   desc:" +  ExceptionUtils.getStackTrace(e));
                return ReturnListData.ERROR;
            }
		}
}
