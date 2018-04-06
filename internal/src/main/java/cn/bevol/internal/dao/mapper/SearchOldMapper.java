package cn.bevol.internal.dao.mapper;


import cn.bevol.internal.entity.dto.SeachComposition;
import cn.bevol.internal.entity.dto.SeachFind;

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
