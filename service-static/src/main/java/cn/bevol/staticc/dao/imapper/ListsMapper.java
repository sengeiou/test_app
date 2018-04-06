package cn.bevol.staticc.dao.imapper;

import cn.bevol.staticc.model.entity.Tags;
import cn.bevol.staticc.model.entity.Lists;
import com.io97.utils.db.Paged;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Rc. on 2017/2/22.
 * 清单
 */
public interface ListsMapper {
    List<Tags> findTagByPage(Paged<Lists> paged);
    List<Lists> findByPage(Paged<Lists> paged);
    int selectTotal();
    List<Lists> getListsByTagId(@Param("tagId")Integer tagId, @Param("pagedBegin")Integer page, @Param("pageSize")Integer pageSize);
    int selectTotalByTag(@Param("tagId")Integer tagId);
}
