package com.vls.cache.api;

/**
 * @description: 持久化策略接口
 * @author VLS
 * @date 2023/12/5 20:39
 * @version 1.0
 */
public interface ICachePersist<K,V> {

    void persist(ICache<K, V> cache);
}
