package cn.bevol.statics.service;

import cn.bevol.statics.dao.db.Paged;
import cn.bevol.statics.dao.mapper.GoodsUserSubmitOldMapper;
import cn.bevol.statics.entity.model.GoodsUserSubmit;
import cn.bevol.util.ConfUtils;
import cn.bevol.util.DateUtils;
import cn.bevol.util.http.HttpUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * Created by Rc. on 2017/2/10.
 */
@Service
public class GoodsUserSubmitService {
    @Resource
    private GoodsUserSubmitOldMapper goodsUserSubmitOldMapper;

    public Object submitProduct(GoodsUserSubmit goodsUserSubmit){
        goodsUserSubmit.setAddDate(DateUtils.nowInMillis()/1000);
        goodsUserSubmit.setUpdateDate(DateUtils.nowInMillis()/1000);
        return  goodsUserSubmitOldMapper.submitProduct(goodsUserSubmit);
    }
    /**
     *
     * @param imgId
     * @param name
     * @param source
     * @param userId
     * @param compositionIds
     *@param compositionNames
     * @param compositionNo @return
     */
    public Object insertOrUpdate(String imgId, String name, String source, Integer state, String userId, String compositionIds, String compositionNames, String compositionNo) {
        GoodsUserSubmit goodsUserSubmit = new GoodsUserSubmit();
        if(!StringUtils.isEmpty(imgId)){
            goodsUserSubmit.setImgId(imgId);
        }
        if(!StringUtils.isEmpty(name)){
            goodsUserSubmit.setName(name);
        }
        if(!StringUtils.isEmpty(source)){
            goodsUserSubmit.setSource(source);
        }
        if(!StringUtils.isEmpty(compositionIds)){
            goodsUserSubmit.setCompositionIds(compositionIds);
        }
        if((!StringUtils.isEmpty(compositionNames))){
            goodsUserSubmit.setCompositionNames(compositionNames);
        }
        if((!StringUtils.isEmpty(compositionNo))) {
            goodsUserSubmit.setCompositionNo(compositionNo);
        }
        /* if(compositionNo.length>0){
             StringBuffer compositionNoBuf = new StringBuffer();
             for (String id:compositionNo) {
                 compositionNoBuf.append(id).append(",");
             }
             compositionNoBuf.deleteCharAt(compositionNoBuf.length()-1);
             goodsUserSubmit.setCompositionNo(compositionNoBuf.toString());
         }*/
        if(!StringUtils.isEmpty(userId)){
            goodsUserSubmit.setUserId(userId);
        }
        if(state>0){
            goodsUserSubmit.setState(state);
        }
        goodsUserSubmit.setAddDate(DateUtils.nowInMillis()/1000);
        goodsUserSubmit.setUpdateDate(DateUtils.nowInMillis()/1000);
        return  goodsUserSubmitOldMapper.insertOrUpdate(goodsUserSubmit);
    }

    public Paged findByPage(Integer state, String name, Integer page){
        GoodsUserSubmit goods = new GoodsUserSubmit();
        if(state!=null){
            goods.setState(state);
        }
        if(!StringUtils.isEmpty(name)){
            goods.setName(name);
        }
        Paged<GoodsUserSubmit> paged = new Paged<GoodsUserSubmit>();
        paged.setWheres(goods);
        paged.setCurPage(page);
        paged.addOrderBy("add_date", "DESC");
        paged.setResult(goodsUserSubmitOldMapper.findByPage(paged));
        paged.setTotal(this.goodsUserSubmitOldMapper.findByPageCount(paged));
        return paged;
    }

    public Object upFile(MultipartFile file){
        String url = ConfUtils.getResourceString("url");
        String dir ="Goods/userupload";
        String upFileUrl = url+"/auth/upfile/"+dir;
        String goodsJson = HttpUtils.post(url+"/auth/upfile/",file);

        return  goodsJson;
    }


    public Object updateGoodsOfState(Integer id, Integer state) {
        GoodsUserSubmit goodsUserSubmit = new GoodsUserSubmit();
        goodsUserSubmit.setId(id);
        goodsUserSubmit.setState(state);
        goodsUserSubmit.setUpdateDate(DateUtils.nowInMillis()/1000);
        return goodsUserSubmitOldMapper.update(goodsUserSubmit);
    }
    public Object bathUpdateGoodsOfState(String ids, Integer state) {

        return goodsUserSubmitOldMapper.bathUpdate(ids,state);
    }

}
