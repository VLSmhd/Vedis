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

    /*
     * 服务于lru，将key添加到链表头
     * @param
     * @return
     */
    void updateKey(K key);

    /*
     * 服务于lru，将key移除
     * @param
     * @return
     */
    void removeKey(K key);

}
