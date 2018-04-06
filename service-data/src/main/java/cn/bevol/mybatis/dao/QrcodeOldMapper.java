package cn.bevol.mybatis.dao;

import org.apache.ibatis.annotations.Param;

/**
 * Created by Rc. on 2017/2/23.
 */
public interface QrcodeOldMapper {

    Integer updateTotal(@Param("id")Integer id);
}
