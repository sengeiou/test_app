package cn.bevol.app.dao.mapper;

import cn.bevol.app.entity.model.WxArtile;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Rc. on 2017/3/31.
 */
public interface WxArtileOldMapper {
    List<WxArtile> findByContent(@Param("title") String title);
}
