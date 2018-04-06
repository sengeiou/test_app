package cn.bevol.mybatis.dao;

import cn.bevol.mybatis.model.Tags;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by zhangcheng on 17-2-28.
 */
public interface TagsMapper {
    List<Tags> findByTabs(@Param("tabs")String tabs);
    List<Tags> findByIds(String[] ids);
}
