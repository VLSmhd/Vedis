package com.vls.cache.api;

public interface ICacheEntry<K,V> {
    K key();

    V value();
}
