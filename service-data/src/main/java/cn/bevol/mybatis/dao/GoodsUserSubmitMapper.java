package cn.bevol.mybatis.dao;

import cn.bevol.mybatis.model.GoodsUserSubmit;
import org.apache.ibatis.annotations.Param;
import com.io97.utils.db.Paged;

import java.util.List;

/**
 * Created by Rc. on 2017/2/10.
 */
public interface GoodsUserSubmitMapper {

    Integer insert(GoodsUserSubmit record);

    Integer update(GoodsUserSubmit record);

    Integer findByPageCount(Paged<GoodsUserSubmit> paged);

    List<GoodsUserSubmit> findByPage(Paged<GoodsUserSubmit> paged);

    GoodsUserSubmit selectByPrimaryKey(Integer id);

    Integer insertOrUpdate(GoodsUserSubmit goodsUserSubmit);

    Integer submitProduct(GoodsUserSubmit goodsUserSubmit);

    Integer bathUpdate(@Param("ids")String ids,@Param("state") Integer state);
}
