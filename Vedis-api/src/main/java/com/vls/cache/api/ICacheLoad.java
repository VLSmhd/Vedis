package com.vls.cache.api;

/**
 * @description: 持久化加载接口
 * @author VLS
 * @date 2023/12/5 20:48
 * @version 1.0
 */
public interface ICacheLoad<K, V> {

    /*
     * 加载上次关机前的缓存信息
     * @param
     * @return
     */
    void load(ICache<K,V> cache);
}
