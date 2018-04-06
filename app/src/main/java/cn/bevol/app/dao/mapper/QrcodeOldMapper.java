package cn.bevol.app.dao.mapper;

import cn.bevol.app.entity.model.Qrcode;
import cn.bevol.app.dao.db.Paged;

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
