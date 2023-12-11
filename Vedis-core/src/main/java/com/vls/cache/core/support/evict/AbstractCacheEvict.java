package com.vls.cache.core.support.evict;

import com.vls.cache.api.ICacheEntry;
import com.vls.cache.api.ICacheEvict;
import com.vls.cache.api.ICacheEvictContext;

public abstract class AbstractCacheEvict<K,V> implements ICacheEvict<K,V> {

    @Override
    public ICacheEntry<K, V> evict(ICacheEvictContext<K, V> context) {
        return doEvict(context);
    }

    protected abstract ICacheEntry<K,V> doEvict(ICacheEvictContext<K, V> context);

    @Override
    public void updateKey(K key) {

    }

    @Override
    public void removeKey(K key) {

    }
}
