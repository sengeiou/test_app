package cn.bevol.mybatis.dao;


import cn.bevol.mybatis.model.SeoLinks;

import java.util.List;

/**
 * Created by mysens on 17-7-19.
 */
public interface SeoLinksMapper {
    List<SeoLinks> selectAll();
}
