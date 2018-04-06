package cn.bevol.statics.dao.mapper;


import org.apache.ibatis.annotations.Select;

/**
 *
 */
public interface InitOldMapper {

    @Select("select count(id) from hq_user where result <> '' ;")
    int countTestResult();

    @Select("select count(*) from hq_user ;")
    int countRegUser();

    @Select("select count(id) from hq_goods where deleted =0  and  hidden =0;")
    int countProduct();


    @Select("select count(id) from hq_composition where deleted =0  and  hidden =0;")
    int countComposition();
	
    
    
    @Select("select count(id) from hq_config limit 0,1")
    int collectTest();


}
