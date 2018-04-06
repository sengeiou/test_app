package cn.bevol.internal.dao.mapper;

import cn.bevol.internal.entity.model.GoodsUserSubmit;
import org.apache.ibatis.annotations.Param;
import cn.bevol.internal.dao.db.Paged;

import java.util.List;

/**
 * Created by Rc. on 2017/2/10.
 */
public interface GoodsUserSubmitOldMapper {

    Integer insert(GoodsUserSubmit record);

    Integer update(GoodsUserSubmit record);

    Integer findByPageCount(Paged<GoodsUserSubmit> paged);

    List<GoodsUserSubmit> findByPage(Paged<GoodsUserSubmit> paged);

    GoodsUserSubmit selectByPrimaryKey(Integer id);

    Integer insertOrUpdate(GoodsUserSubmit goodsUserSubmit);

    Integer submitProduct(GoodsUserSubmit goodsUserSubmit);

    Integer bathUpdate(@Param("ids") String ids, @Param("state") Integer state);
}
