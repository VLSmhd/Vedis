package com.vls.cache.core.support.evict;

import com.vls.cache.api.ICache;
import com.vls.cache.api.ICacheEvictContext;

public class CacheEvictContext<K,V> implements ICacheEvictContext<K,V> {

    private K key;

    private ICache<K,V> cache;

    private int sizeLimit;


    /**
     * 新加的 key
     *  后跟set方法，返回值保证流式编程
     * @return key
     */
    @Override
    public K key() {
        return key;
    }

    public CacheEvictContext<K, V> key(K key) {
        this.key = key;
        return this;
    }

    /**
     * cache 实现
     *
     * @return map
     */
    @Override
    public ICache<K, V> cache() {
        return cache;
    }


    public CacheEvictContext<K, V> cache(ICache<K, V> cache) {
        this.cache = cache;
        return this;
    }

    /**
     * 获取大小
     *
     * @return 大小
     */
    @Override
    public int sizeLimit() {
        return sizeLimit;
    }


    public CacheEvictContext<K, V> sizeLimit(int sizeLimit) {
        this.sizeLimit = sizeLimit;
        return this;
    }
}
