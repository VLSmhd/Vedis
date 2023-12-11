package com.vls.cache.core.guide;

import com.github.houbb.heaven.util.common.ArgUtil;
import com.vls.cache.api.*;
import com.vls.cache.core.Cache;
import com.vls.cache.core.CacheContext;
import com.vls.cache.core.proxy.CacheProxys;
import com.vls.cache.core.support.evict.CacheEvicts;
import com.vls.cache.core.support.listener.remove.CacheRemoveListeners;
import com.vls.cache.core.support.listener.slow.CacheSlowListeners;
import com.vls.cache.core.support.load.CacheLoads;
import com.vls.cache.core.support.persist.CachePersists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 指导类
 * @author VLS
 * @date 2023/12/4 21:35
 * @version 1.0
 */
public final class CacheGuide<K,V> {
    //缓存变量, 真正创建map的地方
    private Map<K,V> map = new HashMap<>();

    private int sizeLimit = Integer.MAX_VALUE;

    private ICacheEvict<K,V> cacheEvict = CacheEvicts.none();

    private ICachePersist<K,V> persist = CachePersists.none();

    private ICacheLoad<K,V> load = CacheLoads.none();

    private List<ICacheRemoveListener<K,V>> removeListeners = CacheRemoveListeners.defaults();

    private List<ICacheSlowListener> slowListeners = CacheSlowListeners.none();


    private CacheGuide(){};

    /*
     * 单例创建对象实例
     * @param
     * @return
     */
    public static <K,V> CacheGuide<K,V> newInstance(){
        return new CacheGuide<>();
    }

    /*
     * 建造者模式，流式编程
     * @param
     * @return
     */
    public CacheGuide<K,V> map(Map<K,V> map){
        ArgUtil.notNull(map, "map");
        this.map = map;
        return this;
    }
    public CacheGuide<K,V> sizeLimit(int sizeLimit){
        ArgUtil.notNegative(sizeLimit, "sizeLimit");
        this.sizeLimit = sizeLimit;
        return this;
    }
    public CacheGuide<K,V> cacheEvict(ICacheEvict<K,V> evict){
        ArgUtil.notNull(evict, "evict");
        this.cacheEvict = evict;
        return this;
    }
    public CacheGuide<K,V> load(ICacheLoad<K,V> load){
        ArgUtil.notNull(load, "load");
        this.load = load;
        return this;
    }
    public CacheGuide<K,V> persist(ICachePersist<K,V> persist){
        ArgUtil.notNull(persist, "persist");
        this.persist = persist;
        return this;
    }
    public CacheGuide<K,V> addSlowListener(ICacheSlowListener slowListener){
        ArgUtil.notNull(slowListener, "slowListener");
        this.slowListeners.add(slowListener);
        return this;
    }
    public CacheGuide<K,V> evict(ICacheEvict<K,V> evict){
        ArgUtil.notNull(evict, "evict");
        this.cacheEvict = evict;
        return this;
    }



    public ICache<K,V> build(){
        Cache<K,V> cache = new Cache<>();
        cache.setMap(map);
        cache.setCacheEvict(cacheEvict);
        cache.setSizeLimit(sizeLimit);
        cache.setLoad(load);
        cache.setPersist(persist);
        cache.removeListeners(removeListeners);
        cache.slowListeners(slowListeners);
        cache.init();
        return CacheProxys.getProxy(cache);
    }
}
