package com.vls.cache.core;

import com.vls.cache.api.ICacheContext;
import com.vls.cache.api.ICacheEvict;

import java.util.Map;

/**
 * @description: 缓存上下文信息
 * @author VLS
 * @date 2023/12/4 21:15
 * @version 1.0
 */
public class CacheContext<K,V> implements ICacheContext<K,V> {

    private Map<K, V> map;

    private int sizeLimit;

    private ICacheEvict<K,V> cacheEvict;


    @Override
    public Map<K,V> map() {
        return map;
    }

    public CacheContext<K, V> map(Map<K, V> map) {
        this.map = map;
        return this;
    }

   /*
    * 获取容器大小限制
    * @param
    * @return
    */
    @Override
    public int sizeLimit() {
        return sizeLimit;
    }

    public CacheContext<K, V> sizeLimit(int sizeLimit) {
        this.sizeLimit = sizeLimit;
        return this;
    }

    /*
     * 淘汰策略
     * @param
     * @return
     */
    @Override
    public ICacheEvict<K,V> cahceEvict() {
        return cacheEvict;
    }

    public CacheContext<K, V> cacheEvict(ICacheEvict<K, V> cacheEvict) {
        this.cacheEvict = cacheEvict;
        return this;
    }
}
