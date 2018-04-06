package cn.bevol.app.dao.mapper;



import cn.bevol.app.entity.dto.ShareEntity;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * @author chenHaiJian
 *
 */
public interface ShareEntityOldMapper {

	List<ShareEntity> shareList();
}
