package cn.bevol.statics.dao.mapper;


import cn.bevol.statics.entity.dto.SkinInterpretation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * SELECT id,left(right(REPLACE(CONCAT(",",tags,","),",11,",""),LENGTH(tags)+1),LENGTH(tags)) lm  FROM `hq_find` where tags is not null and CONCAT(",",tags,",") like '%,11,%'

 * @author Administrator
 *
 */
public interface SkinInterpretationOldMapper {

	SkinInterpretation findskinInterpretation(@Param("skin") String skin);

	void deleteLikeGood();

	List<SkinInterpretation> getAll();

	String getTypeId(@Param("id") Long id);

	void updateLikeGood(@Param("id") Long id, @Param("like_good") String like_good);

	SkinInterpretation selectByPrimaryKey(SkinInterpretation test);

	int insert(SkinInterpretation test);

	int delete(SkinInterpretation test);
}
