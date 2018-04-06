package cn.bevol.app.dao.mapper;

import cn.bevol.app.entity.model.TagStatiscal;
import cn.bevol.app.dao.db.Paged;

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
