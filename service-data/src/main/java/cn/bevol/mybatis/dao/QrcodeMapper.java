package cn.bevol.mybatis.dao;

import cn.bevol.mybatis.model.Qrcode;
import com.io97.utils.db.Paged;

import java.util.List;

/**
 * Created by Rc. on 2017/1/5.
 */
public interface QrcodeMapper {

    Integer insertOrUpdate(Qrcode record);

    int findByPageCount(Paged<Qrcode> paged);

    List<Qrcode> findByPage(Paged<Qrcode> paged);

    Qrcode selectByPrimaryKey(Integer id);
}
