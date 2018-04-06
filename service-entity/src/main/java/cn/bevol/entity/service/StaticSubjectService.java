package cn.bevol.entity.service;

import cn.bevol.mybatis.dao.SubjectMapper;
import cn.bevol.mybatis.model.Subject;
import cn.bevol.mybatis.model.SubjectList;
import cn.bevol.util.ReturnData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mysens on 17-7-6.
 */
@Service
public class StaticSubjectService {
    private static Logger logger = LoggerFactory.getLogger(StaticSubjectService.class);
    @Resource
    private SubjectMapper subjectMapper;
    private Integer pageSize = 20;

    /**
     * 获取静态数据
     * @param dataMap
     * @param id
     * @return
     */
    public Map<String, Object> getStaticInfo(Map<String, Object> dataMap, Integer id){
        int total = subjectMapper.selectTotal(id);
        SubjectList subjectListInfo = subjectMapper.findSubjectListById(id);
        dataMap.put("title", subjectListInfo.getSeoTitle());
        dataMap.put("keywords", subjectListInfo.getSeoKeywords());
        dataMap.put("description", subjectListInfo.getSeoDescription());
        dataMap = SinglePageService.getStaticInfo(dataMap, "pc", "zt");
        dataMap.put("total", total);
        dataMap.put("pageSize", pageSize);
        return dataMap;
    }

    /**
     * 获取list数据
     * @param id
     * @param curPage
     * @return
     */
    private Map<String, Object> prepareStaticInfo(Map<String, Object> dataMap, Integer id, Integer curPage){
        if(curPage != 0) {
            Integer pagedBegin = (curPage - 1) * pageSize;
            List<Subject> lists = subjectMapper.subjectByPage(pagedBegin, pageSize, id);
            dataMap.put("lists", lists);
            dataMap.put("curPage", curPage);
        }
        return dataMap;
    }

    /**
     * 生成静态化专题内容页面
     * @param id
     * @return
     */
    public ReturnData staticSubjectPage(Integer id){
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap = getStaticInfo(dataMap, id);
        int total = (int) dataMap.get("total");
        int pageSize = (int) dataMap.get("pageSize");
        int pageNum =  (int) Math.ceil((float) total/pageSize);
        dataMap.put("pageNum", pageNum);
        for(int i=0; i<pageNum; i++){
            dataMap = prepareStaticInfo(dataMap, id, i+1);
            String path;
            if(i>0){
                path = "zt/"+id+"_"+(i+1);
            }else{
                path = "zt/"+id;
            }
            try {
                SinglePageService.staticGeneralPage(dataMap, "pc", "pc_subject", path);
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("生成专题内容页面出错:"+path);
                return ReturnData.ERROR;
            }
        }
        return ReturnData.SUCCESS;
    }
}
