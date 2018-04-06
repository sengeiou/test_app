package cn.bevol.mybatis.dao;

import cn.bevol.mybatis.model.WxArtile;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Rc. on 2017/3/31.
 */
public interface WxArtileMapper {
    List<WxArtile> findByContent(@Param("title")String title);
}
