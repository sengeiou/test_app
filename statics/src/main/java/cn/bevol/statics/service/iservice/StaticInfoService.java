package cn.bevol.statics.service.iservice;

import java.util.Map;

/**
 * Created by zhangcheng on 17-2-27.
 */
public interface StaticInfoService {
    Map<String, Object> getStaticInfo(Map<String, Object> dataMap);
    Map<String, Object> getStaticLoopInfo(Map<String, Object> dataMap, Integer curPage);
}
