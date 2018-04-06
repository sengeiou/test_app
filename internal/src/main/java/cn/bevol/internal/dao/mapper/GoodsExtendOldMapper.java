package cn.bevol.internal.dao.mapper;

import cn.bevol.internal.entity.model.GoodsExtend;
import cn.bevol.internal.dao.db.Paged;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsExtendOldMapper {
	
	 List<GoodsExtend> getGoodsByMid(@Param("mid") String mid);
	 
	 List<GoodsExtend> findByPage(Paged<GoodsExtend> paged);
	 
	 int selectTotal(Paged<GoodsExtend> paged);
	 int updateByMid(GoodsExtend goodsExtend);
}
