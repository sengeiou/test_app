package cn.bevol.statics.dao.mapper;

import cn.bevol.statics.entity.model.Tags;
import cn.bevol.statics.dao.db.Paged;

import java.util.List;

public interface GoodsSearchOldMapper {
	 List<Tags> findByPageOfTag(Paged<Tags> paged);
	 int selectTotal();
}
