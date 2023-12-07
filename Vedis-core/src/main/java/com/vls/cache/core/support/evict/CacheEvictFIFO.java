package com.vls.cache.core.support.evict;

import com.vls.cache.api.ICache;
import com.vls.cache.api.ICacheEntry;
import com.vls.cache.api.ICacheEvict;
import com.vls.cache.api.ICacheEvictContext;
import com.vls.cache.core.model.CacheEntry;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @description: FIFO淘汰策略
 * @author VLS
 * @date 2023/12/4 20:57
 * @version 1.0
 */
public class CacheEvictFIFO<K,V> implements ICacheEvict<K,V> {

    private Queue<K> queue;

    public CacheEvictFIFO() {
        this.queue = new LinkedList<>();
    }

    @Override
    public ICacheEntry<K,V> evict(ICacheEvictContext<K, V> context) {
        ICache<K, V> cache = context.cache();
        ICacheEntry<K,V> result = null;
        if(cache.size() >= context.sizeLimit()){
            K removeKey = queue.remove();
            V removeValue = cache.remove(removeKey);
            result = new CacheEntry<>(removeKey, removeValue);
        }
        //这种final可加可不加，这是提高可读性的一种方式
        final K key = context.key();
        queue.add(key);
        return result;
    }
}
