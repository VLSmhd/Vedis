package com.vls.cache.core.support.evict;

import com.vls.cache.api.ICache;
import com.vls.cache.api.ICacheEntry;
import com.vls.cache.api.ICacheEvictContext;
import com.vls.cache.core.model.CacheEntry;
import com.vls.cache.core.support.struct.lru.impl.LruMapCircleList;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheEvictClock<K,V> extends AbstractCacheEvict<K,V> {

    private LruMapCircleList<K,V> circleList;

    public CacheEvictClock() {
        circleList = new LruMapCircleList<>();
    }

    @Override
    protected ICacheEntry<K, V> doEvict(ICacheEvictContext<K, V> context) {
        CacheEntry<K,V> result = null;
        ICache<K, V> cache = context.cache();

        if(cache.size() >= context.sizeLimit()) {
            result = (CacheEntry<K, V>) circleList.removeOldest();
            V removeValue = cache.remove(result.key());
            log.debug("基于 clock 算法淘汰 key：{}, value: {}", result.key(), removeValue);
            result.value(removeValue);
        }

        return result;
    }

    @Override
    public void removeKey(K key) {
        circleList.removeKey(key);
    }


    @Override
    public void updateKey(K key) {
        circleList.updateKey(key);
    }
}
