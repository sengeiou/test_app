package cn.bevol.internal.controller;

import cn.bevol.internal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author mysens
 * @date 17-12-25 下午4:11
 */
public class BaseController {

    @Autowired
    private UserService userService;



}
