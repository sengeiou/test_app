package cn.bevol.internal.dao.mapper;

import cn.bevol.internal.entity.model.TagStatiscal;
import cn.bevol.internal.dao.db.Paged;

import java.util.List;

/**
 * Created by Rc. on 2017/2/14.
 */
public interface TagStatiscalOldMapper {
    int insertOrUpdateById(TagStatiscal record);
    int insertOrUpdateByName(TagStatiscal record);
    int selectTotal(Paged<TagStatiscal> paged);
    List<TagStatiscal> findByPage(Paged<TagStatiscal> paged);
}
