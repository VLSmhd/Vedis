package com.vls.cache.core.support.evict;

import com.vls.cache.api.ICache;
import com.vls.cache.api.ICacheEntry;
import com.vls.cache.api.ICacheEvictContext;
import com.vls.cache.core.model.CacheEntry;
import com.vls.cache.core.support.struct.lru.ILruMap;
import com.vls.cache.core.support.struct.lru.impl.LruMapDoubleList;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheEvictLRU2<K,V> extends AbstractCacheEvict<K,V> {

    private ILruMap<K,V> firstVisitLruMap;


    private ILruMap<K,V> moreVisitLruMap;


    public CacheEvictLRU2(){
        firstVisitLruMap = new LruMapDoubleList<>();
        moreVisitLruMap = new LruMapDoubleList<>();
    }

    @Override
    protected ICacheEntry<K, V> doEvict(ICacheEvictContext<K, V> context) {
        ICache<K, V> cache = context.cache();
        ICacheEntry<K, V> result = null;
        if(cache.size() >= context.sizeLimit()) {
            ICacheEntry<K, V> removeEntry = null;
            if(!firstVisitLruMap.isEmpty()){
                removeEntry = firstVisitLruMap.removeOldest();
                log.debug("[lru-2]: 从 firstVisitLruMap 中淘汰数据 key：{}", removeEntry.key());
            }else{
                removeEntry = moreVisitLruMap.removeOldest();
                log.debug("[lru-2]: 从 moreVisitLruMap 中淘汰数据 key：{}", removeEntry.key());
            }
            result = removeEntry;
            cache.remove(result.key());
        }
        return result;
    }

    @Override
    public void removeKey(K key) {
        if(moreVisitLruMap.containsKey(key)) {
            moreVisitLruMap.removeKey(key);
            log.debug("[lru-2]: 从 moreVisitLruMap 中移除 key：{}", key);
        } else{
            firstVisitLruMap.removeKey(key);
            log.debug("[lru-2]: 从 firstVisitLruMap 中移除 key：{}", key);
        }
    }

    @Override
    public void updateKey(K key) {
        if(moreVisitLruMap.containsKey(key) || firstVisitLruMap.containsKey(key)){
            removeKey(key);
            moreVisitLruMap.updateKey(key);
            log.debug("key: {} 多次访问，加入到 moreLruMap 中", key);
        }else{
            firstVisitLruMap.updateKey(key);
            log.debug("key: {} 为第一次访问，加入到 firstLruMap 中", key);
        }

    }
}
