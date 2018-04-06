package cn.bevol.internal.dao.mapper;



import cn.bevol.internal.entity.dto.ShareEntity;

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
