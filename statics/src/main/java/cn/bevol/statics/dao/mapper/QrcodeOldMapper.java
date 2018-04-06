package cn.bevol.statics.dao.mapper;

import cn.bevol.statics.entity.model.Qrcode;
import cn.bevol.statics.dao.db.Paged;

import java.util.List;

/**
 * Created by Rc. on 2017/1/5.
 */
public interface QrcodeOldMapper {

    Integer insertOrUpdate(Qrcode record);

    int findByPageCount(Paged<Qrcode> paged);

    List<Qrcode> findByPage(Paged<Qrcode> paged);

    Qrcode selectByPrimaryKey(Integer id);
}
