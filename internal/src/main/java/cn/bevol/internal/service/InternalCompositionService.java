package cn.bevol.internal.service;

import cn.bevol.model.entity.EntityComposition;
import cn.bevol.util.response.ReturnData;
import cn.bevol.util.response.ReturnListData;
import cn.bevol.internal.dao.mapper.CompositionOldMapper;
import cn.bevol.internal.entity.dto.CompositionDTO;
import cn.bevol.internal.entity.dto.DirtyCompositionDTO;
import cn.bevol.internal.entity.vo.CompositionName;
import cn.bevol.util.DateUtils;
import cn.bevol.util.MD5Utils;
import cn.bevol.util.StringUtil;
import flexjson.JSONDeserializer;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by mysens on 17-5-9.
 */
@Service
public class InternalCompositionService {
    private static Logger logger = LoggerFactory.getLogger(InternalCompositionService.class);

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private CompositionOldMapper compositionOldMapper;

    @Resource
    private StaticClientService staticClientService;

    @Resource
    private CacheService cacheService;

    private final static String ENTITY_COMPOSITION_COLLECTION = "entity_composition";

    public ReturnData addNewComposition(CompositionDTO compositionDTO){
        try{
            //插入到成分表
            compositionDTO = addCompositionInfo(compositionDTO);
            //生成产品mid
            compositionDTO = saveCompositionMid(compositionDTO);
            //插入mongo数据
            addCompositionMongoData(compositionDTO);
            //加入seo
            addComposition2Seo(compositionDTO);
            //重新静态化
            compositionStatic(compositionDTO);
        }catch(Exception e){
            e.printStackTrace();
            logger.debug("Class BackCompositionService,Method addNewComposition:" + ExceptionUtils.getStackTrace(e));
            return ReturnData.ERROR;
        }
        return new ReturnData(compositionDTO);
    }

    /**
     * 单个修改
     * @param compositionDTO
     * @return
     */
    public ReturnData saveCompositionInfo(CompositionDTO compositionDTO){
        try{
            if(compositionDTO.getId() != null){
                if(compositionDTO.getName() != null){
                    compositionDTO.setCmName(compositionDTO.getName());
                }
                if(compositionDTO.getEnglish() != null){
                    compositionDTO.setCmEnglish(compositionDTO.getEnglish());
                }
                compositionOldMapper.saveCompositionInfo(compositionDTO);
                saveCompositionMongoData(compositionDTO);
                //重新静态化
                compositionStatic(compositionDTO);
            }else{
                return ReturnData.ERROR;
            }
        }catch(Exception e){
            e.printStackTrace();
            logger.debug("Class CmsGoodsService Method saveBaseGoodsInfo:"+ExceptionUtils.getStackTrace(e));
            return ReturnData.ERROR;
        }
        return ReturnData.SUCCESS;
    }

    /**
     * 批量修改 （只更改hidden,deleted字段）
     * @param compositionDTO
     * @param ids
     * @return
     */
    public ReturnData saveCompositionList(CompositionDTO compositionDTO, String ids){
        try{
            String idsStringArr[] = ids.split(",");
            Integer[] idsArr = new Integer[idsStringArr.length];
            for(int i=0;i<idsStringArr.length;i++) {
                idsArr[i] = Integer.parseInt(idsStringArr[i]);
            }
            compositionDTO.setIdsArr(idsArr);
            compositionOldMapper.saveCompositionList(compositionDTO);
            saveCompositionMongoData(compositionDTO, idsArr);
        }catch(Exception e){
            e.printStackTrace();
            logger.debug("Class CmsGoodsService Method saveBaseGoodsList:"+ExceptionUtils.getStackTrace(e));
            return ReturnData.ERROR;
        }
        return ReturnData.SUCCESS;
    }

    private CompositionDTO addCompositionInfo(CompositionDTO compositionDTO){
        compositionOldMapper.addCompositionInfo(compositionDTO);
        return compositionDTO;
    }

    private CompositionDTO saveCompositionMid(CompositionDTO compositionDTO){
        Long compositionId = compositionDTO.getId();
        String salt = "2016bevol";
        String mid = MD5Utils.encode(salt + compositionId + "composition");
        compositionOldMapper.saveCompositionMid(compositionId, mid);
        compositionDTO.setMid(mid);
        return compositionDTO;
    }

    private void addCompositionMongoData(CompositionDTO compositionDTO){
        EntityComposition entityComposition = new EntityComposition();
        entityComposition.setId(compositionDTO.getId());

        entityComposition.setMid(compositionDTO.getMid());
        entityComposition.setTitle(compositionDTO.getName());
        entityComposition.setCommentNum(0L);
        entityComposition.setLikeNum(0L);
        entityComposition.setNotLikeNum(0L);
        entityComposition.setDeleted(0);
        entityComposition.setHidden(0);
        entityComposition.setCreateStamp(System.currentTimeMillis()/1000);
        entityComposition.setUpdateStamp(System.currentTimeMillis()/1000);
        mongoTemplate.insert(entityComposition, ENTITY_COMPOSITION_COLLECTION);
    }

    private void saveCompositionMongoData(CompositionDTO compositionDTO){
        Update update=new Update();
        if(compositionDTO.getName() != null) {
            update.set("title", compositionDTO.getName());
        }
        if(compositionDTO.getDeleted() != null){
            update.set("deleted", compositionDTO.getDeleted());
        }
        if(compositionDTO.getHidden() != null){
            update.set("hidden", compositionDTO.getHidden());
        }
        update.set("updateStamp", DateUtils.nowInSeconds());
        mongoTemplate.findAndModify(new Query(Criteria.where("id").is(compositionDTO.getId())), update, EntityComposition.class, ENTITY_COMPOSITION_COLLECTION);
    }

    private void saveCompositionMongoData(CompositionDTO compositionDTO, Integer[] ids){
        Update update=new Update();
        if(compositionDTO.getDeleted() != null){
            update.set("deleted", compositionDTO.getDeleted());
        }
        if(compositionDTO.getHidden() != null){
            update.set("hidden", compositionDTO.getHidden());
        }
        update.set("updateStamp", DateUtils.nowInSeconds());
        mongoTemplate.updateMulti(new Query(Criteria.where("id").in(ids)), update, ENTITY_COMPOSITION_COLLECTION);
    }

    private void addComposition2Seo(CompositionDTO compositionDTO){
        String mid = compositionDTO.getMid();
        staticClientService.seoCompositionAdd(mid);
    }

    private void compositionStatic(CompositionDTO compositionDTO)
    {
        String mid = compositionDTO.getMid();
        Long id = compositionDTO.getId();
        if(mid == null){
            mid = compositionOldMapper.getMidById(id);
        }
        compositionStatic(mid, id);
    }

    private void compositionStatic(String mid, Long id){
        //清理缓存
        cacheService.cleanComposition(mid, id);
        //静态化
        staticClientService.compositionStatic(mid);
    }


    /***
     * 单一成分查询
     * @param names
     * @return
     */
    public Object findComposition(String names) {
        //TODO 方法未实现
        return null;
    }

    public List<CompositionName> findCompositionByIds(String ids) {
        String[] arrayIds = ids.split(",");
        return compositionOldMapper.findCompositionByIds(arrayIds);
    }

    public String findFormatCps(String ids){
        String[] arrayIds = ids.split(",");
        List<String> cpsList = compositionOldMapper.findFormatCps(arrayIds);
        return StringUtils.join(cpsList, ",");
    }

    public ReturnData addDirtyComposition(DirtyCompositionDTO dirtyCompositionDTO){
        try {
            if (dirtyCompositionDTO.getName() != null) {
                compositionOldMapper.addDirtyComposition(dirtyCompositionDTO);
            }else{
                return new ReturnData(-1, "脏成分名不能为空");
            }
            return new ReturnData(dirtyCompositionDTO);
        }catch(Exception e){
            e.printStackTrace();
            return ReturnData.ERROR;
        }
    }

    public ReturnData saveDirtyComposition(DirtyCompositionDTO dirtyCompositionDTO){
        try{
            if(dirtyCompositionDTO.getId() == null){
                return new ReturnData(-1, "数据缺失");
            }
            //匹配后重置删除状态
            dirtyCompositionDTO.setDeleteStatus(0);
            compositionOldMapper.saveDirtyComposition(dirtyCompositionDTO);
            return new ReturnData(dirtyCompositionDTO);
        }catch(Exception e){
            e.printStackTrace();
            return ReturnData.ERROR;
        }
    }

    public ReturnData saveDirtyCompositionList(DirtyCompositionDTO dirtyCompositionDTO, String ids){
        try{
            String idsStringArr[] = ids.split(",");
            Integer[] idsArr = new Integer[idsStringArr.length];
            for(int i=0;i<idsStringArr.length;i++) {
                idsArr[i] = Integer.parseInt(idsStringArr[i]);
            }
            dirtyCompositionDTO.setIdsArr(idsArr);
            compositionOldMapper.saveDirtyCompositionList(dirtyCompositionDTO);
            return ReturnData.SUCCESS;
        }catch(Exception e){
            e.printStackTrace();
            return ReturnData.ERROR;
        }
    }

    /**
     * 检查成分是否存在（调度）
     * @param compositions
     * @return
     */
    public ReturnListData getCompositionInfoByNames(String compositions){
        try {
            List<String> unClearedCompositionArr = new ArrayList<>(Arrays.asList(compositions.split(",")));
            for (int k = 0; k < unClearedCompositionArr.size(); k++) {
                String tmpString = StringUtil.sanitizedName(unClearedCompositionArr.get(k));
                //判断是否空字符串
                if(!"".equals(tmpString)) {
                    //处理含有逗号的成分，如1,3丙二醇，逗号前只有一个字符且为数字判定为成分内的逗号
                    if (unClearedCompositionArr.size() != k + 1 && tmpString.length() == 1 && StringUtils.isNumeric(tmpString)) {
                        unClearedCompositionArr.remove(k);
                        tmpString = tmpString + "," + StringUtil.trim(unClearedCompositionArr.get(k).replaceAll(" ", " ").trim(), ".");
                    }
                    //将###的占位符换成逗号
                    tmpString = tmpString.replaceAll("###", ",");
                    unClearedCompositionArr.set(k, tmpString);
                }else{
                    //去除空字符串
                    unClearedCompositionArr.remove(k);
                }
            }

            CompositionDTO[] compositionDTOS = getCompositionInfoByNames(unClearedCompositionArr);

            //未匹配上的成分过滤后再次匹配
            List<String> unClearedCompositionArr2 = new ArrayList<String>();
            for(CompositionDTO compositionDTO: compositionDTOS){
                if(compositionDTO.getId() == null){
                    unClearedCompositionArr2.add(compositionNameFilter(compositionDTO.getName()));
                }
            }
            if(unClearedCompositionArr2.size() > 0){
                CompositionDTO[] compositionDTOS2 = getCompositionInfoByNames(unClearedCompositionArr2);
                for(CompositionDTO compositionDTO2 : compositionDTOS2){
                    if(compositionDTO2.getId() != null){
                        for(CompositionDTO compositionDTO : compositionDTOS){
                            if(compositionNameFilter(compositionDTO.getName()).equals(compositionDTO2.getName())){
                                compositionDTO.setId(compositionDTO2.getId());
                            }
                        }
                    }
                }
            }

            return new ReturnListData(Arrays.asList(compositionDTOS), compositionDTOS.length);
        }catch (Exception e){
            logger.error("caused by =============="+compositions+"===============");
            e.printStackTrace();
            return ReturnListData.ERROR;
        }
    }

    /**
     * 检查成分是否存在
     * @param unClearedCompositionArr
     * @return
     */
    private CompositionDTO[] getCompositionInfoByNames(List<String> unClearedCompositionArr){
        int compositionNum = unClearedCompositionArr.size();
        CompositionDTO[] compositionDTOS = new CompositionDTO[compositionNum];
        for (int k = 0; k < compositionNum; k++) {
            CompositionDTO compositionDTO = new CompositionDTO();
            compositionDTO.setName(unClearedCompositionArr.get(k));
            compositionDTOS[k] = compositionDTO;
        }
        String[] dirtyCompositionArr = new String[compositionNum];
        Boolean dirtyCompositionArrInit = false;
        for (int i = 0; i < compositionNum; i++) {
            String tmp = StringUtil.formatStandardName(unClearedCompositionArr.get(i));
            if(!"".equals(tmp)){
                dirtyCompositionArrInit = true;
                dirtyCompositionArr[i] = tmp;
            }
        }

        if(dirtyCompositionArrInit){
            List<String> checkedComposition = new ArrayList<String>();
            List<CompositionDTO> compositionDTOList =
                    compositionOldMapper.getCompositionInfoByNames(dirtyCompositionArr);
            for (CompositionDTO compositionDTO : compositionDTOList) {
                for (int i = 0; i < compositionNum; i++) {
                    if ( !checkedComposition.contains(dirtyCompositionArr[i]) && (
                            (!"".equals(compositionDTO.getCmName()) &&
                            compositionDTO.getCmName().equals(StringUtil.formatStandardName(compositionDTOS[i].getName())) ) ||
                            (!"".equals(compositionDTO.getEnglish()) &&
                            compositionDTO.getCmEnglish().equals(StringUtil.formatStandardName(compositionDTOS[i].getName())) ) )
                        ) {
                        compositionDTOS[i].setId(compositionDTO.getId());
                        checkedComposition.add(dirtyCompositionArr[i]);
                    }
                }
            }

            //处理重复的成分
            for(int i = 0; i < compositionNum; i++){
                for(int j=0; j < compositionNum; j++){
                    if(dirtyCompositionArr[i] != null && dirtyCompositionArr[i].equals(dirtyCompositionArr[j]) && compositionDTOS[i].getId() != null && compositionDTOS[i].getId()>0){
                        compositionDTOS[j].setId(compositionDTOS[i].getId());
                    }
                }
            }

            int checkedCompositionNum = 0;
            for(int i = 0; i < compositionNum; i++){
                Long id = compositionDTOS[i].getId();
                if(id != null && id > 0){
                    checkedCompositionNum++;
                }
            }

            if (compositionNum - checkedCompositionNum > 0) {
                unClearedCompositionArr.removeAll(checkedComposition);
                if(unClearedCompositionArr.size() >0 ) {
                    String[] compositionArr = new String[unClearedCompositionArr.size()];
                    for (int i = 0; i < unClearedCompositionArr.size(); i++) {
                        compositionArr[i] = StringUtil.formatStandardName(unClearedCompositionArr.get(i));
                    }
                    List<String> checkedDirtyComposition = new ArrayList<String>();
                    List<CompositionDTO> dirtyCompositionDTOList =
                            compositionOldMapper.getDirtyCompositionInfoByNames(compositionArr);
                    for (CompositionDTO compositionDTO : dirtyCompositionDTOList) {
                        for (int i = 0; i < compositionNum; i++) {
                            if (!"".equals(compositionDTO.getCmName()) &&
                                    !checkedDirtyComposition.contains(compositionDTO.getCmName()) &&
                                    compositionDTO.getCmName().equals(StringUtil.formatStandardName(compositionDTOS[i].getName()))) {
                                compositionDTOS[i].setId(compositionDTO.getId());
                                checkedDirtyComposition.add(compositionDTO.getCmName());
                            }
                        }
                    }
                }
            }
        }

        return compositionDTOS;
    }

    /**
     * 成分过滤
     * @param str
     * @return
     */
    private static String compositionNameFilter(String str) {
        String sp=str;

        //去除百分数
        sp = sp.replaceAll("\\d+\\.?\\d*\\%", "");
        //
        boolean ischs=StringUtil.isChineseChar(sp);
        String key1="{'replace_after':['*','.','。'],'replace_space':['organic','*','　','。','.'],'replace_common':{},'replace_chs_char':{'!':'！','\\'':'’','`':'`','(':'（',')':'）','.':'。',',':'，',';':'；',']':'】',':':'：','\':'、','}':'}','|':'|','{':'{','[':'【'}}";
        Map<String,Object> jsonNode=  new JSONDeserializer<Map<String,Object>>().deserialize(key1, HashMap.class);
        //空格匹配规则
        List<String> replaceSpaceList=(List<String>) jsonNode.get("replace_space");
        //中英文匹配规则
        Map<String,String> replaceChsChar=(Map<String,String>) jsonNode.get("replace_chs_char");
        //其他匹配规则
        Map<String,String> replaceCommon=(Map<String,String>) jsonNode.get("replace_common");

        List<String> replaceAfter=(List<String>) jsonNode.get("replace_after");
        for(String a:replaceAfter) {
            if(StringUtils.isNotBlank(sp)) {
                //非*开头
                //hualong 2017 03 24 * ddd 星号开头的不去掉
                if(!(sp.trim().charAt(0)+"").equals(a)) {
                    sp= StringUtils.substringBefore(sp, a);
                }
            }
        }

        if(replaceChsChar!=null)
            for(String key : replaceChsChar.keySet()){
                if(ischs) {
                    //英文换中文
                    sp= StringUtils.replace(sp, key, replaceChsChar.get(key));
                } else {
                    //中文换英文
                    sp= StringUtils.replace(sp, replaceChsChar.get(key), key);
                }
            }

        //通用替换
        if(replaceCommon!=null&&!replaceCommon.isEmpty()) {
            for (String key : replaceCommon.keySet()) {
                sp = StringUtils.replace(sp, key, replaceCommon.get(key));
            }
        }

        //空格替换
        if(replaceSpaceList!=null) {
            for (String key : replaceSpaceList) {
                sp = StringUtils.replace(sp, key, "");
            }
        }

        //2、公共处理
        sp=StringUtil.strCleanMark(sp);


        return sp;
    }
}
