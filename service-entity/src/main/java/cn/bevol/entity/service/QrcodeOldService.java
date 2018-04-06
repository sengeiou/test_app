package cn.bevol.entity.service;

import cn.bevol.mybatis.dao.QrcodeOldMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Rc. on 2017/2/23.
 */
@Service
public class QrcodeOldService {
    @Resource
    private QrcodeOldMapper qrcodeOldMapper;
    public Integer updateTotal(Integer id){
        return qrcodeOldMapper.updateTotal(id);
    }
}
