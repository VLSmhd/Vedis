package com.vls.cache.api;

import java.util.Collection;

/**
 * @description: 过期删除接口
 * @author VLS
 * @date 2023/12/5 15:41
 * @version 1.0
 */
public interface ICacheExpire<K,V> {

    /*
     * 设置过期函数
     * @param expireAt: 在什么时候过期
     * @return
     */
    void expire(K key, long expireAt);



    /*
     * 惰性删除策略
     * @param 需要处理的key集合
     * @return
     */
    void lazyRefresh(Collection<K> keys);
}
