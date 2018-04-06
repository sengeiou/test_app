package cn.bevol.internal.service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.bevol.entity.service.BaseService;
import cn.bevol.entity.service.EntityService;
import cn.bevol.util.ReturnData;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

/**
 * 
 * @author ksy
 * 小程序后台banner设置
 */

@Service
public class BackMBannerService extends BaseService {
	private static Logger logger = LoggerFactory.getLogger(BackMBannerService.class);
	
	@Resource
	private EntityService entityService;

	public ReturnData addOrUpdateMBanner(List<Map> bannerList) {
		
		if(bannerList != null && bannerList.size() > 0) {
			//读取后台设置的小程序banner数据
			List<Map> listMap = entityService.getConfigArray("mg_index_banners");
			if(listMap == null) {
				entityService.addConfig("mg_index_banners", bannerList);
			}else {
				JSONArray jsonObject = JSONArray.fromObject(bannerList);  
				String value = jsonObject.toString();
				entityService.updateValue("mg_index_banners", value);
			}
			
		}
		
		return ReturnData.SUCCESS;
	}
	
	
	
	
 }