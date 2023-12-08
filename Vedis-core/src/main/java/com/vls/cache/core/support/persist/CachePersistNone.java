package com.vls.cache.core.support.persist;

import com.vls.cache.api.ICache;
import com.vls.cache.api.ICachePersist;

import java.util.concurrent.TimeUnit;

public class CachePersistNone<K,V> implements ICachePersist<K,V> {

    @Override
    public void persist(ICache<K, V> cache) {

    }

    @Override
    public long delay() {
        return 0;
    }

    @Override
    public long period() {
        return 0;
    }

    @Override
    public TimeUnit timeUnit() {
        return null;
    }
}
