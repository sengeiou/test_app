package com.bevol.staticc.service;

import cn.bevol.staticc.service.StaticGoodsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/static-spring-config.xml"})
public class StaticGoodsServiceTest {
    @Autowired
    private StaticGoodsService staticGoodsService;

    @Test
    public void initStaticTest(){
        staticGoodsService.initStatic(3);
    }
}
