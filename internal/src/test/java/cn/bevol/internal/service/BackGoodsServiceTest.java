package cn.bevol.internal.service;

import cn.bevol.internal.dao.GoodsTagRule;
import cn.bevol.internal.dao.mapper.GoodsTagRuleMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @author mysens
 * @date 17-12-27 上午10:32
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BackGoodsServiceTest {

    @Autowired
    private GoodsTagRuleMapper goodsTagRuleMapper;


    @Test
    public void addTagRule() {
        GoodsTagRule goodsTagRule = new GoodsTagRule();
        goodsTagRule.setTagId(888);
        goodsTagRule.setCreateStamp(11111111);
        goodsTagRule.setRule1("45sdfsdfsfd");
        int result = goodsTagRuleMapper.insert(goodsTagRule);
        System.out.println(result);
        assertTrue(result > 0);
    }
}