package com.vls.cache.core.support.evict;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class CacheEvictLruLinkedHashMap<K,V> extends LinkedHashMap<K,V> {

    private int capacity;


    public CacheEvictLruLinkedHashMap(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return super.size() >= capacity;
    }
}
