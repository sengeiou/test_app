package cn.bevol.statics.dao.mapper;


import cn.bevol.statics.entity.EntityGoods;
import cn.bevol.statics.entity.dto.Doyen;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ruanchen
 *
 */
public interface DoyenOldMapper {
	List<Doyen> getDoyenById(@Param("id") String id, @Param("start") long start, @Param("size") int size);

	Doyen getDoyenById2(@Param("id") long id);

	Doyen getDoyenByGoodsId(@Param("goodsId") long goodsId);

	int insertDoyen(Doyen test);

	int deleteDoyen(Doyen test);

	long countByGoodsId(@Param("id") String id);

	List<EntityGoods> findByGoodsIds(@Param("ids") String ids);
}
