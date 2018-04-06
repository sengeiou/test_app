package cn.bevol.statics.service;

import cn.bevol.statics.dao.mapper.TagsOldMapper;
import cn.bevol.statics.entity.model.Tags;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by mysens on 17-3-1.
 */

@Service
public class TagsService {
    @Resource
    private TagsOldMapper tagsOldMapper;

    public List<Tags> findByTabs(String tabs){
        return tagsOldMapper.findByTabs(tabs);
    }
}
