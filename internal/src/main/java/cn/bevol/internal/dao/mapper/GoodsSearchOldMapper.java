package cn.bevol.internal.dao.mapper;

import cn.bevol.internal.entity.model.Tags;
import cn.bevol.internal.dao.db.Paged;

import java.util.List;

public interface GoodsSearchOldMapper {
	 List<Tags> findByPageOfTag(Paged<Tags> paged);
	 int selectTotal();
}
