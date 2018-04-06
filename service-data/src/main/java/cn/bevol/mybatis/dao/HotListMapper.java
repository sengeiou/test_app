package cn.bevol.mybatis.dao;


import cn.bevol.mybatis.dto.HotList;

import cn.bevol.mybatis.dto.HotListGood;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * @author ruanchen
 *
 */
public interface HotListMapper { 

    List<HotList> list(@Param("start") long start, @Param("size") int size);

    List<HotList> partList(@Param("start") long start, @Param("size") int size);


    long count();

    HotList findDetail(@Param("id") Integer id);

    HotList findDetail2(@Param("id") long id);

    List<HotListGood> findDetailGoods(@Param("goods_ids") String goods_ids, @Param("id") Long id);

    int insertHotList(HotList test);

    int deleteHotList(HotList test);

    int insertHotListGood(HotListGood test);

    int deleteHotListGood(HotListGood test);

    Integer updateGoodById(@Param("id") Integer id, @Param("goodsIds") Integer good_id, @Param("content") String content);

    Integer findListId(HotList test);

    List<HotList> allList();

	int updateListsIds(@Param("id") Long id, @Param("ids") String ids);

	int updateListsIds2(@Param("id") Long id, @Param("tagIds") String tagIds, @Param("tags") String tags);
 
}
