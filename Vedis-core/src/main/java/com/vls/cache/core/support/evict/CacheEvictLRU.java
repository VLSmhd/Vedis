package com.vls.cache.core.support.evict;

import com.vls.cache.api.ICache;
import com.vls.cache.api.ICacheEntry;
import com.vls.cache.api.ICacheEvictContext;
import com.vls.cache.core.model.CacheEntry;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class CacheEvictLRU<K,V> extends AbstractCacheEvict<K,V>{

    private List<K> list = new LinkedList<>();

    @Override
    protected ICacheEntry<K, V> doEvict(ICacheEvictContext<K, V> context) {
        ICache<K, V> cache = context.cache();
        K key = context.key();
        int sizeLimit = context.sizeLimit();
        ICacheEntry<K, V> removeEntry = null;

        if(cache.size() >= sizeLimit){
            //获取最后一个结点的key
            K removeKey = list.remove(list.size() - 1);
            V removeValue = cache.remove(removeKey);
             removeEntry = CacheEntry.of(removeKey, removeValue);
        }
        return removeEntry;
    }

    @Override
    public void removeKey(K key) {
        this.list.remove(key);
    }

    @Override
    public void updateKey(K key) {
        this.list.remove(key);
        //头插
        this.list.add(0,key);

    }
}
