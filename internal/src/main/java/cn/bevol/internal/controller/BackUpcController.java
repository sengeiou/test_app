package cn.bevol.internal.controller;

import cn.bevol.internal.entity.dto.UpcDTO;
import cn.bevol.internal.entity.dto.UpcSourceDTO;
import cn.bevol.internal.service.InternalUpcService;
import cn.bevol.util.response.ReturnData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by mysens on 17-5-25.
 */
@Controller
public class BackUpcController {
    @Resource
    InternalUpcService internalUpcService;

    /**
     * 新增条形码匹配
     * ean 条形码 （必填）
     * goodsId 产品ID （必填）
     * goodsMid 产品MID
     * state 状态
     * @param upcDTO
     * @return
     */
    @RequestMapping(value="/back/upc/add")
    @ResponseBody
    public ReturnData addUpcRelation(UpcDTO upcDTO){
        return internalUpcService.addUpcRelation(upcDTO);
    }

    /**
     * 编辑条形码匹配
     * id  主键（必填）
     * ean 条形码
     * goodsId 产品ID
     * goodsMid 产品MID
     * state 状态
     * hiddenStatus
     * deletedStatus
     * @param upcDTO
     * @return
     */
    @RequestMapping(value="/back/upc/save")
    @ResponseBody
    public ReturnData saveUpcRelation(UpcDTO upcDTO){
        return internalUpcService.saveUpcRelation(upcDTO);
    }

    /**
     * 批量修改条形码匹配
     * 只能修改hidden_status/deleted_status
     * @param upcDTO
     * @param ids
     * @return
     */
    @RequestMapping(value="/back/upc/list")
    @ResponseBody
    public ReturnData saveUpcRelationList(UpcDTO upcDTO, String ids){
        return internalUpcService.saveUpcRelationList(upcDTO, ids);
    }

    /**
     * upc源匹配或更新状态
     * id
     * goodsId
     * state
     * deleteStatus
     * @param upcSourceDTO
     * @return
     */
    @RequestMapping(value="/back/upc/source/save")
    @ResponseBody
    public ReturnData saveUpcSource(UpcSourceDTO upcSourceDTO){
        return internalUpcService.saveUpcSource(upcSourceDTO);
    }

    @RequestMapping(value="back/upc/source/list")
    @ResponseBody
    public ReturnData saveUpcSourceList(UpcSourceDTO upcSourceDTO, String ids){
        return internalUpcService.saveUpcSourceByBatch(upcSourceDTO, ids);
    }

}
