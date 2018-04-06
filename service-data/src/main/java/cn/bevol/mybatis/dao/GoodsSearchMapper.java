package cn.bevol.mybatis.dao;

import cn.bevol.mybatis.model.Tags;
import com.io97.utils.db.Paged;

import java.util.List;

public interface GoodsSearchMapper {
	 List<Tags> findByPageOfTag(Paged<Tags> paged);
	 int selectTotal();
}
