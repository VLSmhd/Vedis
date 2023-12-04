package com.vls.cache.api;

/**
 * @author VLS
 * @version 1.0
 * @description: TODO
 * @date 2023/12/4 17:41
 */
public interface ICacheEvictContext<K,V> {

    /**
     * 新加的 key，为具体缓存策略提供key
     * @return key
     */
    K key();

    /**
     * cache 实现
     * @return map
     */
    ICache<K, V> cache();

    /**
     * 获取大小限制
     * @return 大小
     */
    int sizeLimit();

}
