package com.vls.cache.core.support.expire;

import com.github.houbb.heaven.util.util.CollectionUtil;
import com.vls.cache.api.ICache;
import com.vls.cache.api.ICacheExpire;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @description: 排序策略 过期删除实现类
 * @author VLS
 * @date 2023/12/5 16:26
 * @version 1.0
 */
@Slf4j
public class CacheExpireSort<K,V> implements ICacheExpire<K,V> {
    //存储设置了过期时间的缓存信息
    private final Map<Long, List<K>> sortedExpireMap;

    private ICache<K,V> cache;

    //每次清除的个数限制
    private static int CLEAN_LIMIT;

    public CacheExpireSort(ICache<K,V> cache) {
        this.sortedExpireMap = new TreeMap<>((o1, o2) -> (int)(o1 - o2));
        this.cache = cache;
        this.init();
    }

    //单线程
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    /*
     * 初始化轮询任务
     * @param
     * @return
     */
    private void init(){
        EXECUTOR_SERVICE.scheduleAtFixedRate(new ExpireThread(),1, 1, TimeUnit.SECONDS);
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
            if(sortedExpireMap == null || sortedExpireMap.size() == 0){
                return;
            }
            int count = 0;
            for (Map.Entry<Long, List<K>> entry : sortedExpireMap.entrySet()) {
                final Long expireAt = entry.getKey();
                List<K> keys = entry.getValue();

                if(CollectionUtil.isEmpty(keys)){
                    return;
                }

                if(count > CLEAN_LIMIT){
                    return;
                }

                long currentTimeMillis = System.currentTimeMillis();

                if(currentTimeMillis >= expireAt){
                    Iterator<K> keysIterator = keys.iterator();
                    while (keysIterator.hasNext()){
                        K key = keysIterator.next();
                        log.debug("删除的缓存key为{}", key);
                        keysIterator.remove();
                        sortedExpireMap.remove(key);

                        cache.remove(key);
                        count++;
                    }
                }else {
                    //剪枝操作，因为是有序遍历，当遍历到过期时间在未来的集合，直接返回，因为下面的过期时间只会更久远
                    return;
                }
            }

        }
    }


    @Override
    public void expire(K key, long expireAt) {
        List<K> keys = sortedExpireMap.get(expireAt);
        if(keys == null){
            keys = new ArrayList<>();
        }
        keys.add(key);
        sortedExpireMap.put(expireAt, keys);
    }

    @Override
    public void lazyRefresh(Collection<K> keys) {
        return;
    }
}
