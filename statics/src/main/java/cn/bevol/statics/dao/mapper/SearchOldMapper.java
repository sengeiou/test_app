package cn.bevol.statics.dao.mapper;


import cn.bevol.statics.entity.dto.SeachComposition;
import cn.bevol.statics.entity.dto.SeachFind;

import java.util.List;

/**
 *
 * @author ruanchen
 *
 */
public interface SearchOldMapper {

	 List<SeachComposition> ruleOutComposition();
	 List<SeachComposition> ruleOutCompositionByInit4();

	List<SeachFind> ruleOutGoods();

	int insertComposition(SeachComposition test);

	int deleteComposition(SeachComposition test);
}
