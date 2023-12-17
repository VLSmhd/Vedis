package com.vls.cache.core.support.expire;

import com.github.houbb.heaven.util.util.CollectionUtil;
import com.vls.cache.api.ICache;
import com.vls.cache.api.ICacheExpire;
import com.vls.cache.api.ICacheRemoveListener;
import com.vls.cache.constant.enums.CacheRemoveType;
import com.vls.cache.core.support.listener.remove.CacheRemoveListenerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @description:  基础 过期删除实现类
 * @author VLS
 * @date 2023/12/5 15:41
 * @version 1.0
 */
@Slf4j
public class CacheExpire<K,V> implements ICacheExpire<K,V> {

    //存储设置了过期时间的缓存信息
    private Map<K,Long> expireMap = new HashMap<>();

    private ICache<K,V> cache;

    //每次清除的个数限制
    private static int CLEAN_LIMIT;

    //单线程
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    public CacheExpire(ICache<K, V> cache) {
        this.cache = cache;
        this.init();
    }

    /*
     * 初始化轮询任务
     * @param
     * @return
     */
    private void init(){
        EXECUTOR_SERVICE.scheduleAtFixedRate(new ExpireThread(),100, 100, TimeUnit.MILLISECONDS);
        CLEAN_LIMIT = 100;
    }

    /**
     * @description: 定义执行清理任务的单线程
     * @author VLS
     * @date 2023/12/5 16:07
     * @version 1.0
     */
    private class ExpireThread implements Runnable {

        @Override
        public void run() {
            if(expireMap == null || expireMap.size() == 0){
                return;
            }
            int count = 0;
            for (K key : expireMap.keySet()) {
                if(count >= CLEAN_LIMIT){
                    return;
                }
                cleanExpireKey(key);
                count++;
            }
        }
    }

    private void cleanExpireKey(K key){
        Long expireAtMs = expireMap.get(key);

        long currentTimeMillis = System.currentTimeMillis();

        if(currentTimeMillis >= expireAtMs){
            log.debug("删除的缓存key为{}", key);
            expireMap.remove(key);
            V removeValue = cache.remove(key);

            //过期监听器
            CacheRemoveListenerContext<K, V> cacheRemoveListenerContext = CacheRemoveListenerContext.<K, V>newInstance()
                    .setKey(key)
                    .setValue(removeValue)
                    .setType(CacheRemoveType.EXPIRE.getCode());

            for (ICacheRemoveListener<K, V> removeListener : cache.removeListeners()) {
                removeListener.listen(cacheRemoveListenerContext);
            }

        }
    }

    @Override
    public void expire(K key, long expireAt) {
        expireMap.put(key, expireAt);
    }

    @Override
    public void lazyRefresh(Collection<K> keys) {
        //要清理两个集合，一个是传入的key，另一个是当前expireMap
        if(CollectionUtil.isEmpty(keys)){
            return;
        }
        if(keys.size() <= expireMap.size()){
            for (K key : keys) {
                cleanExpireKey(key);
            }
        }else{
            for (K key : expireMap.keySet()) {
                cleanExpireKey(key);
            }
        }
    }

    @Override
    public Long expireTime(K key) {
        return expireMap.get(key);
    }
}
