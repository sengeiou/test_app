package cn.bevol.statics.dao.mapper;



import cn.bevol.statics.entity.dto.ShareEntity;

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
