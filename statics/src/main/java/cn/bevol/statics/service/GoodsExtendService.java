package cn.bevol.statics.service;

import cn.bevol.statics.dao.db.Paged;
import cn.bevol.statics.dao.mapper.GoodsExtendOldMapper;
import cn.bevol.statics.dao.mapper.GoodsSearchOldMapper;
import cn.bevol.statics.entity.model.GoodsExtend;
import cn.bevol.statics.entity.model.Tags;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by Rc. on 2017/2/8.
 */
@Service
public class GoodsExtendService {

    @Resource
    private GoodsExtendOldMapper goodsExtendOldMapper;
    @Resource
    private GoodsSearchOldMapper goodsSearchOldMapper;

    public List<GoodsExtend> findByPage(Integer page) {
        GoodsExtend goods = new GoodsExtend();
        goods.setState(0);//没有操作过
        Paged<GoodsExtend> paged = new Paged<GoodsExtend>();
        paged.setCurPage(page);
        paged.setPageSize(200);
        paged.setWheres(goods);
        return goodsExtendOldMapper.findByPage(paged);
    }

    public int updateByMid(GoodsExtend goodsExtend) {
        goodsExtend.setNum(goodsExtend.getNum()+1);
        goodsExtend.setState(1);
        goodsExtend.setUpdateDate(new Date());
        return goodsExtendOldMapper.updateByMid(goodsExtend);
    }

    /**
     * 查询所有标签
     * @return
     */
    public Map<String, Integer> findTags() {
        Paged<Tags> paged = new Paged<Tags>();
        List<Tags> ls = new ArrayList<Tags>();
        int totalPage = goodsSearchOldMapper.selectTotal() / 500 + 1;
        for (int i = 1; i <= totalPage; i++) {
            paged.setCurPage(i);
            paged.setPageSize(500);
            List<Tags> tmpList = goodsSearchOldMapper.findByPageOfTag(paged);
            ls.addAll(tmpList);
        }
        Map<String, Integer> map = new HashMap<String, Integer>();

        for (Tags tag : ls) {
            if (tag != null) {
                String tags = tag.getId();
                int cNum = 1;
                String[] split = tags.split(",");
                for (String s : split) {
                    if (map.containsKey(s)) {
                        Integer tNum = map.get(s);
                        ++tNum;
                        cNum = tNum;
                    }
                    map.put(s, cNum);
                }
            }
        }
        return map;
    }
}
