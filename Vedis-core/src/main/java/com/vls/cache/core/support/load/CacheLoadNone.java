package com.vls.cache.core.support.load;

import com.vls.cache.api.ICache;
import com.vls.cache.api.ICacheLoad;

public class CacheLoadNone<K,V> implements ICacheLoad<K,V> {
    @Override
    public void load(ICache<K, V> cache) {

    }
}
