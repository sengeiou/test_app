package cn.bevol.statics.service;

import cn.bevol.statics.dao.db.Paged;
import cn.bevol.statics.dao.mapper.ListsOldMapper;
import cn.bevol.statics.dao.mapper.TagStatiscalOldMapper;
import cn.bevol.statics.entity.EntityUserPart;
import cn.bevol.statics.entity.UserPart;
import cn.bevol.statics.entity.model.Lists;
import cn.bevol.statics.entity.model.TagStatiscal;
import cn.bevol.statics.entity.model.Tags;
import cn.bevol.util.DateUtils;
import org.apache.http.client.ClientProtocolException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by Rc. on 2017/2/14.
 * 标签统计
 */
@Service
public class TagStatiscalService {

    @Resource
    private StaticFindService staticFindService;
    @Resource
    private GoodsExtendService goodsExtendService;
    @Resource
    private TagStatiscalOldMapper tagStatiscalOldMapper;
    @Resource
    private ListsOldMapper listsOldMapper;
    @Resource
    MongoTemplate mongoTemplate;
    public Integer insertTags(){
        Integer result = 0;
        Map<String,Integer> findMap = staticFindService.findTags();
        for (String s: findMap.keySet()) {

            if(!StringUtils.isEmpty(s)) {
                TagStatiscal tagStatiscal = new TagStatiscal();
                tagStatiscal.setTagId(Integer.valueOf(s));
                tagStatiscal.setFindNum(findMap.get(s));
                tagStatiscal.setUpdateDate(DateUtils.nowInMillis()/1000);
                Integer tmp = tagStatiscalOldMapper.insertOrUpdateById(tagStatiscal);
                result+=tmp;
            }

        }
        Map<String,Integer> listsMap =this.listsTags();
        for (String s: listsMap.keySet()) {

            if(!StringUtils.isEmpty(s)) {
                TagStatiscal tagStatiscal = new TagStatiscal();
                tagStatiscal.setTagId(Integer.valueOf(s));
                tagStatiscal.setListsNum(listsMap.get(s));
                tagStatiscal.setUpdateDate(DateUtils.nowInMillis()/1000);
                Integer tmp = tagStatiscalOldMapper.insertOrUpdateById(tagStatiscal);
                result+=tmp;
            }

        }
        Map<String,Integer> goodsMap =  goodsExtendService.findTags();
        for (String s: goodsMap.keySet()) {

            if(!StringUtils.isEmpty(s)) {
                TagStatiscal tagStatiscal = new TagStatiscal();
                tagStatiscal.setTagId(Integer.valueOf(s));
                tagStatiscal.setGoodsNum(goodsMap.get(s));
                tagStatiscal.setUpdateDate(DateUtils.nowInMillis()/1000);
                Integer tmp = tagStatiscalOldMapper.insertOrUpdateById(tagStatiscal);
                result+=tmp;
            }

        }
        Map<Integer,Integer> parkMap = this.findUserPartTags();
        for (Integer s: parkMap.keySet()) {

            if (!StringUtils.isEmpty(s)) {
                TagStatiscal tagStatiscal = new TagStatiscal();
                tagStatiscal.setTagId(s);
                tagStatiscal.setTalkNum(parkMap.get(s));
                tagStatiscal.setUpdateDate(DateUtils.nowInMillis() / 1000);
                Integer tmp = tagStatiscalOldMapper.insertOrUpdateById(tagStatiscal);
                result += tmp;
            }
        }
        return result;
    }

    public Map<Integer, Integer> findUserPartTags(){
        List<EntityUserPart> list = new ArrayList<EntityUserPart>();
        String actionType="entity_user_part_lists";
        //  Criteria crt= Criteria.where("1").is(1);
        long count = mongoTemplate.count(new Query(), actionType);
        int tatolPage = (int) (count / 100 + 1);
        for (int i=1;i<=tatolPage;i++){
            Query query = new Query().skip((i-1)*100).limit(100);
            List<EntityUserPart> uais = mongoTemplate.find(query, EntityUserPart.class, actionType);
            list.addAll(uais);
        }
        List<Integer> lsTage = new ArrayList<Integer>();
        for(EntityUserPart part:list){
            lsTage.addAll(part.getTags());
        }
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (Integer find : lsTage) {
            int cNum = 1;
                if (map.containsKey(find)) {
                    Integer tNum = map.get(find);
                    ++tNum;
                    cNum = tNum;
                }
                map.put(find, cNum);
            }

            return map;
    }

    public Paged findTagStatiscal(Integer page, Integer pageSize){
        Paged<TagStatiscal> paged = new Paged<TagStatiscal>();
        paged.setCurPage(page);
        paged.setPageSize(pageSize);
        paged.setResult(tagStatiscalOldMapper.findByPage(paged));
        paged.setTotal(tagStatiscalOldMapper.selectTotal(paged));
        return paged;
    }

    /***
     * 根据tagId查询话题
     * @param tagId
     * @param page
     * @param pageSize
     * @return
     */
    public Paged getTagFindList(Integer tagId, Integer page, Integer pageSize) {
        String actionType="entity_user_part_lists";
        Criteria crt= Criteria.where("tags").is(tagId);
        Query query = new Query(crt);
        long count = mongoTemplate.count(query, actionType);
        query.skip((page-1)*pageSize).limit(pageSize);
        List<UserPart> uais = mongoTemplate.find(query, UserPart.class, actionType);
        Paged<UserPart> paged = new Paged<UserPart>();
        paged.setPageSize(pageSize);
        paged.setCurPage(page);
        paged.setTotal((int) count);
        paged.setResult(uais);
        return paged;
    }

    /**
     * 查询所有清单标签
     * @return
     */
    public Map<String, Integer> listsTags() {
        Paged<Lists> paged = new Paged<Lists>();
        List<Tags> ls = new ArrayList<Tags>();
        int totalPage = listsOldMapper.selectTotal() / 200 + 1;
        for (int i = 1; i <= totalPage; i++) {
            paged.setCurPage(i);
            paged.setPageSize(200);
            List<Tags> tmpList = listsOldMapper.findTagByPage(paged);
            ls.addAll(tmpList);
        }
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (Tags find : ls) {
            if (find != null) {
                String tags = find.getId();
                int cNum = 1;
                String[] split = tags.split(",");
                for (String s : split) {
                    if (map.containsKey(s)) {
                        Integer tNum = map.get(s);
                        ++tNum;
                        cNum = tNum;
                    }
                    map.put(s, cNum);
                }
            }
        }
        return map;
    }

    /**
     *根据tagId查询清单
     * @param page
     * @param tagId
     * @return
     */
    public Paged getTagListsList(Integer tagId, Integer page, Integer pageSize) {
        Paged<Lists> paged = new Paged<Lists>();
        paged.setPageSize(pageSize);
       paged.setCurPage(page);
        paged.setTotal( listsOldMapper.selectTotalByTag(tagId));
        paged.setResult(listsOldMapper.getListsByTagId(tagId,page,pageSize));
        return paged;
    }
    public static void main(String[] args) throws ClientProtocolException, FileNotFoundException {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/*.xml");
        TagStatiscalService service = (TagStatiscalService) context.getBean("tagStatiscalService");
       // service.insertTags();
      //  service.getTagListsList(5,1,20);
        service.listsTags();
    }


}
