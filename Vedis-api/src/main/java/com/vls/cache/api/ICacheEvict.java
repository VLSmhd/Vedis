package com.vls.cache.api;

/**
 * @author VLS
 * @version 1.0
 * @description: TODO
 * @date 2023/12/4 17:37
 */
public interface ICacheEvict<K,V> {

    /*
     * 定义  驱逐淘汰方法
     * @param
     * @return
     */
    ICacheEntry<K,V> evict(ICacheEvictContext<K,V> context);
}
