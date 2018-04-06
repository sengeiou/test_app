package cn.bevol.entity.service.weixin;


import cn.bevol.cache.CACHE_NAME;
import cn.bevol.entity.service.weixin.Handler.AccessToken;
import cn.bevol.entity.service.weixin.Handler.OAuthAccessToken;
import cn.bevol.entity.service.weixin.Handler.Ticket;
import cn.bevol.entity.service.weixin.Handler.WxApi;
import cn.bevol.model.items.MpAccount;
import com.io97.cache.CacheKey;
import com.io97.cache.CacheableTemplate;
import com.io97.cache.redis.RedisCacheProvider;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Rc. on 2017/2/24.
 */
@Service
public class WxCscheService {
    @Resource
    private RedisCacheProvider cacheProvider;

    /***
     * 添加ACCESSTOKEN到缓存中
     * @param
     * @return
     */
    AccessToken getAccessToken(final MpAccount mpAccount) {
//
//        return (AccessToken)token;
        return new CacheableTemplate<AccessToken>(cacheProvider) {
            @Override
            protected AccessToken getFromRepository() {
                return WxApi.getAccessToken(mpAccount.getAppid(),mpAccount.getAppsecret());
            }
            @Override
            protected boolean canPutToCache(AccessToken returnValue) {
                return (returnValue != null);
            }
        }.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE,  CACHE_NAME.ACCESS_TOKEN), true);
    }

    Ticket getTicket(final String token){
        return new CacheableTemplate<Ticket>(cacheProvider){
            @Override
            protected Ticket getFromRepository(){
                return WxApi.getTicket(token);
            }
            @Override
            protected boolean canPutToCache(Ticket returnValue){
                return (returnValue != null);
            }
        }.execute(new CacheKey(CACHE_NAME.NAMESPACE, CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE, CACHE_NAME.TICKET), true);
    }

    /**
     * 从缓存中取 OAuthAccessToken
     * @return
     */
    OAuthAccessToken getSingleOAuthAccessToken(){
        CacheKey key = new CacheKey(CACHE_NAME.NAMESPACE,CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE, CACHE_NAME.OAUTH_ACCESS_TOKEN);
        return (OAuthAccessToken) cacheProvider.get(key);
    }

    /**
     * 添加OAuth的Token到缓存中
     * @param token
     * @return
     */
    AccessToken addOAuthAccessToken(OAuthAccessToken token){
        CacheKey key = new CacheKey(CACHE_NAME.NAMESPACE,CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE, CACHE_NAME.OAUTH_ACCESS_TOKEN);
        cacheProvider.put(key,token);
        return token;
    }

//    public MpAccount getSingleMpAccount() {
//        CacheKey key = new CacheKey(CACHE_NAME.NAMESPACE,CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE, CACHE_NAME.OAUTH_ACCESS_TOKEN);
//        cacheProvider.put(key,token);
//        return token;
//    }
//    public MpAccount addSingleMpAccount() {
//        CacheKey key = new CacheKey(CACHE_NAME.NAMESPACE,CACHE_NAME.FIVE_MINUTE_CACHE_QUEUE, CACHE_NAME.OAUTH_ACCESS_TOKEN);
//        cacheProvider.put(key,token);
//        return token;
//    }

}
