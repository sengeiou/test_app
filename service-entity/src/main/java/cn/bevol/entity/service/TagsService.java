package cn.bevol.entity.service;

import cn.bevol.mybatis.dao.TagsMapper;
import cn.bevol.mybatis.model.Tags;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by mysens on 17-3-1.
 */

@Service
public class TagsService {
    @Resource
    private TagsMapper tagsMapper;

    public List<Tags> findByTabs(String tabs){
        return tagsMapper.findByTabs(tabs);
    }
}
