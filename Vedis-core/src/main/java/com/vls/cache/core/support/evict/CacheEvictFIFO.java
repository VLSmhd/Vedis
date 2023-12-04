package com.vls.cache.core.support.evict;

import com.vls.cache.api.ICache;
import com.vls.cache.api.ICacheEvict;
import com.vls.cache.api.ICacheEvictContext;

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
    public void evict(ICacheEvictContext<K, V> context) {
        ICache<K, V> cache = context.cache();
        if(cache.size() >= context.sizeLimit()){
            K removeKey = queue.remove();
            cache.remove(removeKey);
        }
        //这种final可加可不加，这是提高可读性的一种方式
        final K key = context.key();
        queue.add(key);

    }
}
