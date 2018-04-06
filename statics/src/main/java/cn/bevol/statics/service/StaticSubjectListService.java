package cn.bevol.statics.service;

import cn.bevol.statics.dao.db.Paged;
import cn.bevol.statics.dao.mapper.SubjectOldMapper;
import cn.bevol.statics.entity.model.SubjectList;
import cn.bevol.statics.service.iservice.StaticInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by mysens on 17-7-6.
 */
@Service
public class StaticSubjectListService implements StaticInfoService {
    private static Logger logger = LoggerFactory.getLogger(StaticSubjectListService.class);
    @Resource
    private SubjectOldMapper subjectOldMapper;
    private static int curPage;
    private static int pageSize;
    private static int total;

    public static int getTotal() {
        return total;
    }

    public static void setTotal(int total) {
        StaticSubjectListService.total = total;
    }

    public static int getPageSize() {
        return pageSize;
    }

    public static void setPageSize(int pageSize) {
        StaticSubjectListService.pageSize = pageSize;
    }

    public static int getCurPage() {
        return curPage;
    }

    public static void setCurPage(int curPage) {
        StaticSubjectListService.curPage = curPage;
    }

    public void getLocalCache(){
        int total = subjectOldMapper.selectListTotal();
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
            Paged<SubjectList> paged = new Paged<SubjectList>();
            paged.setPageSize(pageSize);
            paged.setCurPage(curPage);
            List<SubjectList> lists = subjectOldMapper.subjectListByPage(paged);
            dataMap.put("lists", lists);
            dataMap.put("curPage", curPage);
        }
        return dataMap;
    }
}
