package com.io97.redis;

import org.redisson.Config;
import org.redisson.Redisson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Creator: Owen
 * Date: 2015/2/27
 */
@Component
public class RedissonUtil {

    private static final Logger logger = LoggerFactory.getLogger(RedissonUtil.class);

    private static Redisson redisson = null;

    private String host;
    private Integer port;

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public synchronized Redisson getRedisson() {
        if (redisson == null) {
            Config config = getConfig();
            redisson = Redisson.create(config);
        }
        return redisson;
    }

    private Config getConfig() {
        Config config = new Config();
        config.useSingleServer().setAddress(host + ":" + port);
        logger.info("get redisson config, " + host + ":" + port);
        return config;
    }

}
