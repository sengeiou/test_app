package cn.bevol.app.dao.mapper;



import cn.bevol.model.entity.EntityFind;

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
