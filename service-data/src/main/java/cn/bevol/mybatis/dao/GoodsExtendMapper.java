package cn.bevol.mybatis.dao;

import cn.bevol.mybatis.model.GoodsExtend;
import com.io97.utils.db.Paged;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsExtendMapper {
	
	 List<GoodsExtend> getGoodsByMid(@Param("mid") String mid);
	 
	 List<GoodsExtend> findByPage(Paged<GoodsExtend> paged);
	 
	 int selectTotal(Paged<GoodsExtend> paged);
	 int updateByMid(GoodsExtend goodsExtend);
}
