package cn.bevol.mybatis.dao;



import cn.bevol.model.entity.EntityFind;
import cn.bevol.mybatis.model.Find;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * @author chenhaijian
 *
 */
public interface ArticleListMapper {

	List<EntityFind> articleList(@Param("ids") String ids);
}
