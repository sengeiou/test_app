package cn.bevol.mybatis.dao;

import cn.bevol.mybatis.model.TagStatiscal;
import com.io97.utils.db.Paged;

import java.util.List;

/**
 * Created by Rc. on 2017/2/14.
 */
public interface TagStatiscalMapper {
    int insertOrUpdateById(TagStatiscal record);
    int insertOrUpdateByName(TagStatiscal record);
    int selectTotal(Paged<TagStatiscal> paged);
    List<TagStatiscal> findByPage(Paged<TagStatiscal> paged);
}
