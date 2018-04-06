package cn.bevol.mybatis.dao;



import cn.bevol.mybatis.dto.ShareEntity;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * @author chenHaiJian
 *
 */
public interface ShareEntityMapper {

	List<ShareEntity> shareList();
}
