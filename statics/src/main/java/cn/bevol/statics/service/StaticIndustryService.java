package cn.bevol.statics.service;

import cn.bevol.statics.dao.db.Paged;
import cn.bevol.statics.dao.mapper.IndustryOldMapper;
import cn.bevol.statics.entity.model.Industry;
import cn.bevol.statics.service.iservice.StaticInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by mysens on 17-7-4.
 */
@Service
public class StaticIndustryService implements StaticInfoService {
    @Resource
    private IndustryOldMapper industryOldMapper;
    private static int curPage;
    private static int pageSize;
    private static int total;

    public static int getTotal() {
        return total;
    }

    public static void setTotal(int total) {
        StaticIndustryService.total = total;
    }

    public static int getPageSize() {
        return pageSize;
    }

    public static void setPageSize(int pageSize) {
        StaticIndustryService.pageSize = pageSize;
    }

    public static int getCurPage() {
        return curPage;
    }

    public static void setCurPage(int curPage) {
        StaticIndustryService.curPage = curPage;
    }

    public void getLocalCache(){
        int total = industryOldMapper.selectTotal();
        setTotal(total);
        setPageSize(20);
    }

    @Override
    public Map<String, Object> getStaticInfo(Map<String, Object> dataMap){
        if(total == 0 || pageSize == 0){
            getLocalCache();
        }
        dataMap.put("total", total);
        dataMap.put("pageSize", pageSize);
        return dataMap;
    }

    @Override
    public Map<String, Object> getStaticLoopInfo(Map<String, Object> dataMap, Integer curPage) {
        dataMap = getStaticInfo(dataMap);
        if(curPage != 0) {
            Paged<Industry> paged = new Paged<Industry>();
            paged.setPageSize(pageSize);
            paged.setCurPage(curPage);
            List<Industry> lists = industryOldMapper.industryByPage(paged);
            dataMap.put("lists", lists);
            dataMap.put("curPage", curPage);
        }
        return dataMap;
    }

}
