package cn.bevol.app.dao.mapper;

import cn.bevol.app.entity.model.Tags;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by zhangcheng on 17-2-28.
 */
public interface TagsOldMapper {
    List<Tags> findByTabs(@Param("tabs") String tabs);
    List<Tags> findByIds(String[] ids);
}
