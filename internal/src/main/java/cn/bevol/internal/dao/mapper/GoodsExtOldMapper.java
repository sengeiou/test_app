package cn.bevol.internal.dao.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.bevol.internal.entity.model.GoodsExt;

/**
 * @author cps 扩展
 *
 */
public interface GoodsExtOldMapper {
	
	GoodsExt getExtByGoodsId(@Param("id") Long id);

	List<GoodsExt> getExtByGoodsIds(@Param("ids") List<Long> ids);

    /**
     * @param key 需要更新的字段名
     * @param val  需要更新的字段值
     * @param feild 条件
     * @param fval  条件值
     * @return
     */
    int updateField(@Param("key") String key, @Param("val") Object val, @Param("feild") String feild, @Param("fval") Object fval);

    int addGoodsExtInfo(GoodsExt goodsExt);

    int saveGoodsExtCps(GoodsExt goodsExt);
}
