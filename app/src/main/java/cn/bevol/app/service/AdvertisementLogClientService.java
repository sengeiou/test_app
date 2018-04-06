package cn.bevol.app.service;

import cn.bevol.util.Log.LogException;
import cn.bevol.util.client.AdClient;
import cn.bevol.util.response.ReturnData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rc. on 2017/3/24.
 * 广告日志记录
 */
@Service
public class AdvertisementLogClientService extends BaseService{
    private static Logger logger = LoggerFactory.getLogger(AdvertisementLogClientService.class);
	private final static AdClient adClient=new AdClient("ad");

    public Map<String,Object> findAd(Integer name,String type,Integer classifyId,String positionType){
      Map<String,Object>  map= adClient.findAd(name,type,classifyId,positionType);
        return  map;
    }
    public void addADLog(Integer id,String positionType){
        adClient.addADLog(id,positionType);
    }
    
    /**
     * 广告日志统计
     * @param adId
     * @param positionType
     * @return
     */
	public ReturnData adLog(String adId, String positionType) {
		try{
			if(StringUtils.isEmpty(positionType) || ("null".equals(positionType)) || ("undefined".equals(positionType))){
				positionType="";
			}
			if (!StringUtils.isEmpty(adId)&&!("null".equals(adId))&&!("undefined".equals(adId))) {
				Integer id = Integer.valueOf(adId);
				this.addADLog(id,positionType);
			}
			return ReturnData.SUCCESS;
		}catch (Exception e) {
			Map map=new HashMap();
			map.put("adId", adId);
			map.put("positionType", positionType);
			map.put("method", "AdvertisementLogService.adLog");
			new LogException(e, map);
		}
		return null;
	}
}

