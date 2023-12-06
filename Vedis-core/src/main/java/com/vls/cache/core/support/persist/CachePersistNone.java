package com.vls.cache.core.support.persist;

import com.vls.cache.api.ICache;
import com.vls.cache.api.ICachePersist;

public class CachePersistNone<K,V> implements ICachePersist<K,V> {

    @Override
    public void persist(ICache<K, V> cache) {

    }
}
