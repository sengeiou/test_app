package cn.bevol.app.dao.mapper;

import cn.bevol.app.entity.model.Tags;
import cn.bevol.app.dao.db.Paged;

import java.util.List;

public interface GoodsSearchOldMapper {
	 List<Tags> findByPageOfTag(Paged<Tags> paged);
	 int selectTotal();
}
