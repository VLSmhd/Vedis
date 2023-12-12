package com.vls.cache.core.support.evict;

import com.vls.cache.api.ICache;
import com.vls.cache.api.ICacheEntry;
import com.vls.cache.api.ICacheEvictContext;
import com.vls.cache.core.model.CacheEntry;
import com.vls.cache.core.support.struct.lru.impl.LruMapDoubleList;

import java.util.LinkedList;
import java.util.Queue;

public class CacheEvictLRU2Q<K,V> extends AbstractCacheEvict<K,V> {

    private Queue<K> fifoQueue;

    private static final int QUEUE_LIMIT = 1024;

    private LruMapDoubleList<K,V> lruMapDoubleList;

    public CacheEvictLRU2Q() {
        this.fifoQueue = new LinkedList<>();
        this.lruMapDoubleList = new LruMapDoubleList<>();
    }

    @Override
    protected ICacheEntry<K, V> doEvict(ICacheEvictContext<K, V> context) {
        ICache<K, V> cache = context.cache();
        K evictKey = null;
        ICacheEntry<K, V> result = null;
        if(cache.size() >= context.sizeLimit()){
            if(fifoQueue != null){
                evictKey = fifoQueue.poll();
            }else{
                ICacheEntry<K, V> removeEntry = lruMapDoubleList.removeOldest();
                evictKey = removeEntry.key();
            }
            V remove = cache.remove(evictKey);
            result = CacheEntry.of(evictKey, remove);
        }


        return result;
    }

    @Override
    public void removeKey(K key) {
        //自顶向下
        if (lruMapDoubleList.containsKey(key)) {
            lruMapDoubleList.removeKey(key);
        }else{
            //O(n)的时间复杂度
            fifoQueue.remove(key);
        }
    }

    @Override
    public void updateKey(K key) {
        if (lruMapDoubleList.containsKey(key) || fifoQueue.contains(key)) {
            removeKey(key);
            lruMapDoubleList.updateKey(key);
        }

        fifoQueue.offer(key);
    }
}
