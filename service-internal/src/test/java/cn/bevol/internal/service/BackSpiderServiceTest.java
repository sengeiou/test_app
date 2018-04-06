package cn.bevol.internal.service;

import cn.bevol.util.ReturnData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

/**
 * Created by mysens on 17-6-19.
 */
@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/internal-spring-config.xml"})
public class BackSpiderServiceTest {
    @Autowired
    private InternalSpiderService backSpiderService;

    @Test
    public void statisticSpiderIntoTest(){
        ReturnData result =  backSpiderService.statisticSpiderInto();
        Assert.isTrue(result.getRet() == 0);
    }

}
