package com.vls.cache.api;

import java.util.concurrent.TimeUnit;

/**
 * @description: 持久化策略接口
 * @author VLS
 * @date 2023/12/5 20:39
 * @version 1.0
 */
public interface ICachePersist<K,V> {

    void persist(ICache<K, V> cache);

    //aof + rdb 共同工作   需要以下参数
    long delay();

    long period();

    TimeUnit timeUnit();
}
