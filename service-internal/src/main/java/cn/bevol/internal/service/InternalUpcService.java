package cn.bevol.internal.service;

import cn.bevol.conf.client.ConfUtils;
import cn.bevol.entity.service.MessageService;
import cn.bevol.entity.service.utils.CommonUtils;
import cn.bevol.mybatis.dao.GoodsMapper;
import cn.bevol.mybatis.dao.UpcMapper;
import cn.bevol.mybatis.dto.UpcDTO;
import cn.bevol.mybatis.dto.UpcSourceDTO;
import cn.bevol.mybatis.model.Goods;
import cn.bevol.util.ReturnData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mysens on 17-5-25.
 */
@Service
public class InternalUpcService {

    @Resource
    UpcMapper upcMapper;
    @Resource
    GoodsMapper goodsMapper;
    
    @Autowired
    MessageService messageService;

    public static Map<String, String> userUpcMsg = new HashMap<String, String>();

    static{
    	userUpcMsg.put("2", "上传的产品审核通过");
    	userUpcMsg.put("3", "上传的产品审核通过");
    	userUpcMsg.put("title", "产品审核信息");
    }
    
    /**
     * 添加条形码
     * @param upcDTO
     * @return
     */
    public ReturnData addUpcRelation(UpcDTO upcDTO){
        try {
            if(upcDTO.getEan() == null){
                return new ReturnData(-1, "条形码不能为空！");
            }
            if(upcDTO.getGoodsId() == null && upcDTO.getGoodsMid() == null){
                return new ReturnData(-1, "产品ID或MID不能全为空！");
            }
            upcDTO = getUpcGoodsInfo(upcDTO);
            if(upcDTO.getGoodsTitle() == null){
                String message = "不存在ID或MID为" + (upcDTO.getGoodsId()==null?(upcDTO.getGoodsMid()+""):(upcDTO.getGoodsId()+"")) + "的产品";
                return new ReturnData(-2, message);
            }
            upcMapper.addUpcRelation(upcDTO);
            return new ReturnData(upcDTO);
        }catch(DuplicateKeyException e) {
            return new ReturnData(-3, "条形码已存在");
        }catch(Exception e){
            return ReturnData.ERROR;
        }
    }

    /**
     * 编辑条形码
     * @param upcDTO
     * @return
     */
    public ReturnData saveUpcRelation(UpcDTO upcDTO){
        try{
            if(upcDTO.getId() == null && upcDTO.getEan() == null){
                return new ReturnData(-1, "条形码数据缺失！");
            }
            if(upcDTO.getGoodsId() != null || upcDTO.getGoodsMid() != null){
                upcDTO = getUpcGoodsInfo(upcDTO);
            }else{
                //如果没有传入goods_id/goods_mid，则不允许修改产品信息
                upcDTO.setGoodsTitle(null);
                upcDTO.setGoodsMid(null);
                upcDTO.setGoodsId(null);
            }
            upcMapper.saveUpcRelation(upcDTO);
            return new ReturnData(upcDTO);
        }catch (Exception e){
            return ReturnData.ERROR;
        }
    }

    public ReturnData saveUpcRelationList(UpcDTO upcDTO, String ids){
        try{
            String idsStringArr[] = ids.split(",");
            Integer[] idsArr = new Integer[idsStringArr.length];
            for(int i=0;i<idsStringArr.length;i++) {
                idsArr[i] = Integer.parseInt(idsStringArr[i]);
            }
            upcDTO.setIdsArr(idsArr);
            //不允许修改产品信息
            upcDTO.setGoodsTitle(null);
            upcDTO.setGoodsMid(null);
            upcDTO.setGoodsId(null);
            upcMapper.saveUpcRelationList(upcDTO);
            return new ReturnData(upcDTO);
        }catch (Exception e){
            return ReturnData.ERROR;
        }
    }

    /**
     * 编辑条形码源
     * @param upcSourceDTO
     * @return
     */
    @Transactional
    public ReturnData saveUpcSource(UpcSourceDTO upcSourceDTO){
        try{
            if(upcSourceDTO.getId() == null || upcSourceDTO.getEan() == null){
                return new ReturnData(-1, "条形码数据缺失！");
            }
            if(upcSourceDTO.getGoodsId() != null){
                Integer state = upcSourceDTO.getState();
                if(state == 2) {
                    //匹配成功
                    UpcDTO upcDTO = new UpcDTO();
                    upcDTO.setEan(upcSourceDTO.getEan());
                    upcDTO.setGoodsId(upcSourceDTO.getGoodsId());
                    ReturnData returnData = this.addUpcRelation(upcDTO);
                    if (returnData.getRet() != 0) {
                        return returnData;
                    }
                    //用户上传的数据,给用户发消息
                   this.userUpcMsg(upcSourceDTO,state);
                }else if(state == 3){
                    //废弃不用
                	//用户上传的数据,给用户发消息
                    this.userUpcMsg(upcSourceDTO,state);
                }
            }
            upcMapper.saveUpcSource(upcSourceDTO);
            return new ReturnData(upcSourceDTO);
        }catch(Exception e){
            return ReturnData.ERROR;
        }
    }
    
    /**
     * 用户上传的upc数据处理后,给用户发消息
     * @param upcSourceDTO
     * @param state: 2通过 3没通过
     */
    public void userUpcMsg(UpcSourceDTO upcSourceDTO,int state){
    	//来源于用户上传的
    	if(null!=upcSourceDTO.getSource() && upcSourceDTO.getSource()==3 && null!=upcSourceDTO.getUserId()){
    		String date=CommonUtils.timeStampCastToDate(Long.parseLong(upcSourceDTO.getCreateTime()));
    		long replyUserId=ConfUtils.getResourceNum("mangeUserId");
    		ReturnData cols=messageService.sendMsgByXxj(replyUserId, String.valueOf(upcSourceDTO.getUserId()),userUpcMsg.get("title"), "你于"+date+userUpcMsg.get(String.valueOf(state)),null,null,null,null);
        }
    }

    public ReturnData saveUpcSourceByBatch(UpcSourceDTO upcSourceDTO, String ids){
        try{
            String idsStringArr[] = ids.split(",");
            Integer[] idsArr = new Integer[idsStringArr.length];
            for(int i=0;i<idsStringArr.length;i++) {
                idsArr[i] = Integer.parseInt(idsStringArr[i]);
            }
            upcSourceDTO.setIdsArr(idsArr);
            upcMapper.saveUpcSourceByBatch(upcSourceDTO);
            return new ReturnData(upcSourceDTO);
        }catch (Exception e){
            return ReturnData.ERROR;
        }
    }

    /**
     * todo  未完成
     * @param list
     * @return
     */
    @Transactional
    public ReturnData addUpcSourceByBatch(List<UpcSourceDTO> list){
        try{
            upcMapper.addUpcSourceByBatch(list);
            return ReturnData.SUCCESS;
        }catch(Exception e){
            return ReturnData.ERROR;
        }
    }

    private UpcDTO getUpcGoodsInfo(UpcDTO upcDTO){
        Goods goods = new Goods();
        if(upcDTO.getGoodsId() != null){
            goods = goodsMapper.getById(upcDTO.getGoodsId());
        }else if(upcDTO.getGoodsMid() != null) {
            goods = goodsMapper.getByGoodsByMid(upcDTO.getGoodsMid());
        }
        if(goods != null){
            upcDTO.setGoodsId(goods.getId());
            upcDTO.setGoodsMid(goods.getMid());
            upcDTO.setGoodsTitle(goods.getTitle());
        }else{
            upcDTO.setGoodsTitle(null);
        }
        return upcDTO;
    }
}
