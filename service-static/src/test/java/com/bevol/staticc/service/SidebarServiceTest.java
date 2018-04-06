package com.bevol.staticc.service;

import cn.bevol.staticc.service.SidebarService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mysens on 17-7-10.
 */
@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/static-spring-config.xml"})
public class SidebarServiceTest {
    @Autowired
    private SidebarService sidebarService;

    @Test
    public void getProductsByRandomTest(){
        sidebarService.generateSideBarCache();
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap = sidebarService.getSidebar(dataMap);
        Assert.isTrue(dataMap.containsKey("sidebarGoodsList"));
        Assert.isTrue(dataMap.containsKey("sidebarFindList"));
    }
}
