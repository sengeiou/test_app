package cn.bevol.staticc.api.servlet;

import cn.bevol.entity.service.AdvertisementLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Rc. on 2017/4/12.
 * servlet-mapping 的方式也可以初始化启动，现在用的是Listener方式
 */
@Deprecated
public class AdInitServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AdInitServlet.class);
    @Override
    public void init() throws ServletException {
        AdvertisementLogService s = new AdvertisementLogService();
        s.initADLog();
        logger.info("===========日志监听开启================");
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doGet(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
