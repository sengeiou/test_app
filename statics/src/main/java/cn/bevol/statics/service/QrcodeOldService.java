package cn.bevol.statics.service;

import cn.bevol.statics.dao.mapper.QrcodeOldOldMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Rc. on 2017/2/23.
 */
@Service
public class QrcodeOldService {
    @Resource
    private QrcodeOldOldMapper qrcodeOldOldMapper;
    public Integer updateTotal(Integer id){
        return qrcodeOldOldMapper.updateTotal(id);
    }
}
