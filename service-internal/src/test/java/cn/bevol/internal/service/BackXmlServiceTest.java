package cn.bevol.internal.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by mysens on 17-6-27.
 */
@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/internal-spring-config.xml"})
public class BackXmlServiceTest {
    @Autowired
    private InternalXmlService backXmlService;

    @Test
    public void generateAladdinProductXmlTest(){
        backXmlService.generateAladdinProductXml();
    }
}
