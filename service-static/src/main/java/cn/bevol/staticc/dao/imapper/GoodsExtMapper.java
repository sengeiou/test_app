package cn.bevol.staticc.dao.imapper;

import cn.bevol.staticc.model.entity.GoodsExt;

/**
 * Created by mysens on 17-3-15.
 */
public interface GoodsExtMapper {
    int addGoodsExtInfo(GoodsExt goodsExt);

    int saveGoodsExtCps(GoodsExt goodsExt);
}
