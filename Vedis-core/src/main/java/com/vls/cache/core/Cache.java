package com.vls.cache.core;

import com.vls.cache.api.*;
import com.vls.cache.core.exception.CacheRuntimeException;
import com.vls.cache.core.support.evict.CacheEvictContext;
import com.vls.cache.core.support.expire.CacheExpire;
import com.vls.cache.core.support.load.CacheLoads;
import com.vls.cache.core.support.persist.CachePersistNone;
import com.vls.cache.core.support.persist.CachePersists;
import com.vls.cache.core.support.persist.InnerCachePersist;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @description: 缓存信息实现类
 * @author VLS
 * @date 2023/12/4 17:47
 * @version 1.0
 */
public class Cache<K,V> implements ICache<K,V> {

    private final Map<K, V> map;

    private final int sizeLimit;

    private final ICacheEvict<K,V> cacheEvict;

    private ICacheExpire<K,V> cacheExpire;

    private ICacheLoad<K,V> load;

    private ICachePersist<K,V> persist;

    public Cache(ICacheContext<K,V> context) {
        this.map = context.map();
        this.sizeLimit = context.sizeLimit();
        this.cacheEvict = context.cahceEvict();
        this.load = CacheLoads.none();
        this.persist = CachePersists.none();
    }

    public ICacheExpire<K, V> getCacheExpire() {
        return cacheExpire;
    }


    public void init(){
        this.cacheExpire = new CacheExpire<>(this);
        this.load.load(this);

        if(this.persist != null && !(this.persist instanceof CachePersistNone)){
            new InnerCachePersist(this, persist);
        }
    }

    /**
     * @description: 当前缓存中存储元素的个数
     * @author VLS
     * @date 2023/12/4 17:50
     */
    @Override
    public int size() {
        return map.size();
    }


    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }


    @Override
    public boolean containsKey(Object key) {
        refreshExpireAllKeys();
        return map.containsKey(key);
    }


    @Override
    public boolean containsValue(Object value) {
        refreshExpireAllKeys();
        return map.containsValue(value);
    }


    @Override
    public V get(Object key) {
        K genericKey = (K) key;
        this.cacheExpire.lazyRefresh( Collections.singletonList(genericKey));
        return map.get(key);
    }

    @Override
    public V put(K key, V value) {
        //1. 尝试淘汰内存,初始化一个CacheEvictContext 服务于策略
        CacheEvictContext<K, V> context = new CacheEvictContext<>();
        context.key(key).sizeLimit(sizeLimit).cache(this);

        cacheEvict.evict(context);

        //2. 淘汰后判断能否添加
        if(isSizeLimited()){
            throw new CacheRuntimeException("当前缓存已满，数据添加失败！");
        }

        return map.put(key, value);
    }

    private boolean isSizeLimited(){
        final int curSize = this.size();//加final防止并发
        return curSize >= sizeLimit;
    }



    @Override
    public V remove(Object key) {
        return map.remove(key);
    }


    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }


    @Override
    public void clear() {
        map.clear();
    }


    @Override
    public Set<K> keySet() {
        refreshExpireAllKeys();
        return map.keySet();
    }


    @Override
    public Collection<V> values() {
        refreshExpireAllKeys();
        return map.values();
    }


    @Override
    public Set<Entry<K, V>> entrySet() {
        refreshExpireAllKeys();
        return map.entrySet();
    }


    /*
     * 多少时间后过期
     * @param
     * @return
     */
    @Override
    public ICache<K, V> expire(K key, long timeout, TimeUnit unit) {
        long mills = unit.toMillis(timeout);

        long timeoutAt = System.currentTimeMillis() + mills;
        return this.expireAt(key, timeoutAt);
    }

    @Override
    public ICache<K, V> expireAt(K key, long timeoutAt) {
        this.cacheExpire.expire(key, timeoutAt);
        return this;
    }

    @Override
    public ICacheExpire<K, V> expire() {
        return this.cacheExpire;
    }

    @Override
    public ICacheLoad<K, V> load() {
        return this.load;
    }

    /*
     * 设置加载策略
     * @param
     * @return
     */
    public void setLoad(ICacheLoad<K,V> cacheLoad) {
        this.load = cacheLoad;
    }


    @Override
    public ICachePersist<K, V> persist() {
        return this.persist;
    }

    public void setPersist(ICachePersist<K,V> cachePersist){
        this.persist = cachePersist;
    }


    /*
     * 刷新懒过期处理所有的 keys
     * @param
     * @return
     */
    private void refreshExpireAllKeys() {
        this.cacheExpire.lazyRefresh(map.keySet());
    }
}
