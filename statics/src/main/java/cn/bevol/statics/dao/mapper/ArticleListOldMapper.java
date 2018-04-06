package cn.bevol.statics.dao.mapper;



import cn.bevol.statics.entity.EntityFind;
import cn.bevol.statics.entity.model.Find;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * @author chenhaijian
 *
 */
public interface ArticleListOldMapper {

	List<EntityFind> articleList(@Param("ids") String ids);
}
