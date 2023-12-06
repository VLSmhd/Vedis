package com.vls.cache.api;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author VLS
 * @version 1.0
 * @description: 定义最基本的map接口
 * @date 2023/12/4 15:31
 */
public interface ICache<K,V> extends Map<K,V> {

    ICache<K,V> expire(K key, long timeout, TimeUnit unit);


    ICache<K,V> expireAt(K key, long timeoutAt);

    /*
     * 获取缓存对应过期处理的策略类
     * @param
     * @return
     */
    ICacheExpire<K,V> expire();

    ICacheLoad<K,V> load();

    ICachePersist<K,V> persist();
}
