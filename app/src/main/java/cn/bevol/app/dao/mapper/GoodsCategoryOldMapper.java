package cn.bevol.app.dao.mapper;

import cn.bevol.app.entity.model.GoodsCategory;
import org.apache.ibatis.annotations.Param;

/**
 * Created by mysens on 16-12-9.
 */
public interface GoodsCategoryOldMapper {
    /*@SuppressWarnings("GoodsCategory查询")
    @Select("select name from hq_goods_category WHERE id = #{id}")
    @ResultMap("BaseResultMap")*/
    GoodsCategory findById(@Param("id")long id);
}
