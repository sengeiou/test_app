package cn.bevol.internal.dao.mapper;

import cn.bevol.internal.entity.model.Qrcode;
import cn.bevol.internal.dao.db.Paged;

import java.util.List;

/**
 * Created by Rc. on 2017/1/5.
 */
public interface QrcodeOldOldMapper {

    Integer insertOrUpdate(Qrcode record);

    int findByPageCount(Paged<Qrcode> paged);

    List<Qrcode> findByPage(Paged<Qrcode> paged);

    Qrcode selectByPrimaryKey(Integer id);
}
