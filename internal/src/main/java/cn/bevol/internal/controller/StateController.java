package cn.bevol.internal.controller;

import cn.bevol.util.response.ReturnData;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mysens
 * @date 17-12-26 下午3:44
 */
@RestController
public class StateController {

    @RequestMapping("state")
    public ReturnData state(){
        return ReturnData.SUCCESS;
    }

}
