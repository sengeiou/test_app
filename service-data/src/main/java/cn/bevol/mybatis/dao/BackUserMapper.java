package cn.bevol.mybatis.dao;




import org.apache.ibatis.annotations.Param;

import cn.bevol.mybatis.model.GoodsRule;
import cn.bevol.mybatis.model.GoodsTag;
import cn.bevol.mybatis.model.GoodsTagComposition;
import cn.bevol.mybatis.model.GoodsTagResult;
import cn.bevol.mybatis.model.UserBlackList;

import java.util.List;
import java.util.Map;

/**
 *  
 * @author chenHaiJian
 *
 */
public interface BackUserMapper { 
	
	List<UserBlackList> getUserBlackList();

	int insertUserBlackList(UserBlackList userBlackList);

	int insertUserBlackList2(UserBlackList userBlackList);

	int deleteBlackList(@Param("userId") long userId);

	int updateBlackList(UserBlackList userBlackList);

	UserBlackList getUserBlackById(@Param("userId") long userId);
}
