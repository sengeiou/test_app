package cn.bevol.config.cache.redis;

import cn.bevol.util.ConfClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 用于批量清缓存
 * 每次扩展cache Nodes 注意重启上层应用
 * todo 后期优化
 */
@Repository
public class RedisCacheNodesRepository {

    public void removeMatch(String keyPattern) {
        try {
            if (connectionPools == null || connectionPools.isEmpty()) {
                initializeDatabaseConnection();
            }

            for (JedisPool pool : connectionPools) {
                Boolean error = true;
                Jedis jedis = pool.getResource();
                try {
                    Set<String> keys = jedis.keys(keyPattern);
                    Iterator<String> i = keys.iterator();
                    while (i.hasNext()) {
                        jedis.del(i.next());
                    }
                    error = false;
                } catch (Exception ex) {

                } finally {//release
                    if (jedis != null) {
                        returnConnection(pool, jedis, error);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected List<JedisPool> connectionPools = new ArrayList<JedisPool>();
    protected List<String> hosts = new ArrayList<String>();

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    protected int port = 16379;
    protected String password = null;

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    protected int timeout = Protocol.DEFAULT_TIMEOUT;

    {
        /**
         * 扩展redis节点后需要更新配置节点值，并重启webapp
         */
        ConfClient confClint = new ConfClient("common");
        String ips = confClint.getResourceString("cache_nodes_ip_list");
        if (!StringUtils.isEmpty(ips)) {
            String[] ip = ips.split(",");
            if (ip.length > 0) {
                for (String everyIP : ip) {
                    hosts.add(everyIP);
                    System.out.println("=============>cache  service  add  nodes " + everyIP + " !<===========");
                }

            }
        } else {
        	 port = 6379;
          hosts.add("127.0.0.1");
            System.out.println("=============>cache  service  without  nodes  conf !<===========");
        }

        initializeDatabaseConnection();
    }


    private void initializeDatabaseConnection() {
        try {
            for (String host : hosts) {
                connectionPools.add(new JedisPool(new JedisPoolConfig(), host,
                        getPort(), getTimeout()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void returnConnection(JedisPool connectionPool, Jedis jedis, Boolean error) {
        if (error) {
            connectionPool.returnBrokenResource(jedis);
        } else {
            connectionPool.returnResource(jedis);
        }
    }


}
