package cn.bevol.statics.dao.mapper;

import cn.bevol.statics.entity.dto.UpcDTO;
import cn.bevol.statics.entity.dto.UpcSourceDTO;
import cn.bevol.statics.entity.model.Goods;

import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * Created by mysens on 17-5-25.
 */
public interface UpcOldMapper {
    int addUpcRelation(UpcDTO upcDTO);
    int saveUpcRelation(UpcDTO upcDTO);
    int saveUpcRelationList(UpcDTO upcDTO);
    int addUpcSourceByBatch(List<UpcSourceDTO> upcSourceDTOs);
    int saveUpcSource(UpcSourceDTO upcSourceDTO);
    int saveUpcSourceByBatch(UpcSourceDTO upcSourceDTO);
    //条形码查询产品
	List selectGoods(@Param("field") String field);
	//查询去零条形码
	List selectGoodsNewEan(@Param("ean") String ean);
	void addNewUpc(@Param("ean") String ean, @Param("str") String str);
    
}
