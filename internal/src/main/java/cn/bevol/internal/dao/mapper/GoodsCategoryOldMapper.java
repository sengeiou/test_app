package cn.bevol.internal.dao.mapper;

import cn.bevol.internal.entity.model.GoodsCategory;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

/**
 * Created by mysens on 16-12-9.
 */
public interface GoodsCategoryOldMapper {
    @SuppressWarnings("GoodsCategory查询")
    @Select("select name from hq_goods_category WHERE id = #{id}")
    @ResultMap("BaseResultMap")
    GoodsCategory findById(long id);
}
