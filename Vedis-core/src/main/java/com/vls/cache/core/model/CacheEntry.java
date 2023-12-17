package com.vls.cache.core.model;

import com.vls.cache.api.ICacheEntry;

public class CacheEntry<K,V> implements ICacheEntry<K,V> {

    private K key;

    private V value;

    public CacheEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public static <K,V> ICacheEntry<K,V> of(K key, V value){
        return new CacheEntry<>(key, value);
    }

    @Override
    public K key() {
        return key;
    }
    public K key(K key) {
        return key;
    }

    @Override
    public V value() {
        return value;
    }
    public V value(V value) {
        return value;
    }

    @Override
    public String toString() {
        return "CacheEntry {" +
                "key=" + key +
                ", value=" + value +
                '}';
    }
}
