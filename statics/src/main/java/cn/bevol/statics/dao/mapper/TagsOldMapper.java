package cn.bevol.statics.dao.mapper;

import cn.bevol.statics.entity.model.Tags;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by zhangcheng on 17-2-28.
 */
public interface TagsOldMapper {
    List<Tags> findByTabs(@Param("tabs") String tabs);
    List<Tags> findByIds(String[] ids);
}
