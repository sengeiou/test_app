package cn.bevol.mybatis.dao;


import cn.bevol.mybatis.dto.SeachComposition;
import cn.bevol.mybatis.dto.SeachFind;

import java.util.List;

/**
 *
 * @author ruanchen
 *
 */
public interface SearchMapper {

	 List<SeachComposition> ruleOutComposition();
	 List<SeachComposition> ruleOutCompositionByInit4();

	List<SeachFind> ruleOutGoods();

	int insertComposition(SeachComposition test);

	int deleteComposition(SeachComposition test);
}
