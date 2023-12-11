package com.vls.cache.core;

import com.vls.cache.annotation.CacheInterceptor;
import com.vls.cache.api.*;
import com.vls.cache.constant.enums.CacheRemoveType;
import com.vls.cache.core.exception.CacheRuntimeException;
import com.vls.cache.core.support.evict.CacheEvictContext;
import com.vls.cache.core.support.expire.CacheExpire;
import com.vls.cache.core.support.listener.remove.CacheRemoveListenerContext;
import com.vls.cache.core.support.listener.remove.CacheRemoveListeners;
import com.vls.cache.core.support.load.CacheLoads;
import com.vls.cache.core.support.persist.CachePersistNone;
import com.vls.cache.core.support.persist.CachePersists;
import com.vls.cache.core.support.persist.InnerCachePersist;
import com.vls.cache.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @description: 缓存信息实现类
 * @author VLS
 * @date 2023/12/4 17:47
 * @version 1.0
 */
public class Cache<K,V> implements ICache<K,V> {

    private Map<K, V> map;

    private int sizeLimit;

    private ICacheEvict<K,V> cacheEvict;

    private ICacheExpire<K,V> cacheExpire;

    private ICacheLoad<K,V> load;

    private ICachePersist<K,V> persist;

    private List<ICacheRemoveListener<K,V>> removeListeners;

    private List<ICacheSlowListener> slowListeners;

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

    public void setMap(Map<K, V> map) {
        this.map = map;
    }

    public void setSizeLimit(int sizeLimit) {
        this.sizeLimit = sizeLimit;
    }

    public void setCacheEvict(ICacheEvict<K, V> cacheEvict) {
        this.cacheEvict = cacheEvict;
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
    @CacheInterceptor(aof = true, evict = true)
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
    @CacheInterceptor(aof = true, evict = true)
    public V get(Object key) {
        K genericKey = (K) key;
        this.cacheExpire.lazyRefresh( Collections.singletonList(genericKey));
        return map.get(key);
    }

    @Override
    @CacheInterceptor(aof = true, evict = true)
    public V put(K key, V value) {
        //1. 尝试淘汰内存,初始化一个CacheEvictContext 服务于策略
        CacheEvictContext<K, V> context = new CacheEvictContext<>();
        context.key(key).sizeLimit(sizeLimit).cache(this);

        //添加拦截器
        ICacheEntry<K, V> evictEntry = cacheEvict.evict(context);
        if(ObjectUtils.isNotEmpty(evictEntry)){
            CacheRemoveListenerContext<K, V> cacheRemoveListenerContext = CacheRemoveListenerContext.<K, V>newInstance()
                    .setKey(evictEntry.key())
                    .setValue(evictEntry.value())
                    .setType(CacheRemoveType.EVICT.getCode());

            for (ICacheRemoveListener<K, V> removeListener : this.removeListeners) {
                removeListener.listen(cacheRemoveListenerContext);
            }

        }

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
    @CacheInterceptor(aof = true, evict = true)
    public V remove(Object key) {
        return map.remove(key);
    }


    @Override
    @CacheInterceptor(aof = true)
    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }


    @Override
    @CacheInterceptor(aof = true)
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
    @CacheInterceptor(aof = true)
    public ICache<K, V> expireAt(K key, long timeoutAt) {
        this.cacheExpire.expire(key, timeoutAt);
        return this;
    }

    @Override
    public ICacheEvict<K, V> evict() {
        return this.cacheEvict;
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

    @Override
    public List<ICacheRemoveListener<K, V>> removeListeners() {
        return this.removeListeners;
    }

    @Override
    public List<ICacheSlowListener> slowListeners() {
        return slowListeners;
    }
    public ICache<K,V> slowListeners(List<ICacheSlowListener> slowListeners) {
        this.slowListeners = slowListeners;
        return this;
    }

    public ICache<K,V> removeListeners(List<ICacheRemoveListener<K, V>> removeListeners){
        this.removeListeners = removeListeners;
        return this;
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
