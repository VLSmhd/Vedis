package com.vls.cache.core;

import com.vls.cache.api.ICache;
import com.vls.cache.api.ICacheContext;
import com.vls.cache.api.ICacheEvict;
import com.vls.cache.core.exception.CacheRuntimeException;
import com.vls.cache.core.support.evict.CacheEvictContext;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

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

    public Cache(ICacheContext<K,V> context) {
        this.map = context.map();
        this.sizeLimit = context.sizeLimit();
        cacheEvict = context.cahceEvict();
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
        return map.containsKey(key);
    }


    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }


    @Override
    public V get(Object key) {
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
        return map.keySet();
    }


    @Override
    public Collection<V> values() {
        return map.values();
    }


    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }
}
