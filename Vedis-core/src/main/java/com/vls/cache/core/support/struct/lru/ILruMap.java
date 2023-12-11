package com.vls.cache.core.support.struct.lru;

import com.vls.cache.api.ICacheEntry;

public interface ILruMap<K,V> {

    ICacheEntry<K,V> removeOldest();

    void updateKey(K key);

    void removeKey(K key);

    boolean containsKey(K key);

    boolean isEmpty();
}
