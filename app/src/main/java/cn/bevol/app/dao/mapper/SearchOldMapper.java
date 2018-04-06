package cn.bevol.app.dao.mapper;


import cn.bevol.app.entity.dto.SeachComposition;
import cn.bevol.app.entity.dto.SeachFind;

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
