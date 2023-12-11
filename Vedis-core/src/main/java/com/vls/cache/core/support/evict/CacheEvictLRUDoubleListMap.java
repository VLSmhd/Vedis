package com.vls.cache.core.support.evict;

import com.vls.cache.api.ICache;
import com.vls.cache.api.ICacheEntry;
import com.vls.cache.api.ICacheEvictContext;
import com.vls.cache.core.exception.CacheRuntimeException;
import com.vls.cache.core.model.CacheEntry;
import com.vls.cache.core.support.struct.lru.model.DoubleListNode;
import com.vls.cache.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CacheEvictLRUDoubleListMap<K,V> extends AbstractCacheEvict<K,V> {

    private DoubleListNode<K,V> head;

    private DoubleListNode<K,V> tail;

    private Map<K, DoubleListNode<K,V>> map;

    public CacheEvictLRUDoubleListMap() {
        this.head = new DoubleListNode<>();
        this.tail = new DoubleListNode<>();
        this.map = new HashMap<>();

        this.head.next(this.tail);
        this.tail.pre(this.head);
    }


    @Override
    protected ICacheEntry<K,V> doEvict(ICacheEvictContext<K,V> context) {
        ICache<K, V> cache = context.cache();
        ICacheEntry<K, V> removeEntry = null;
        if(map.size() >= context.sizeLimit()){
            removeEntry = removeOldest();

        }

        return removeEntry;
    }


    @Override
    public void updateKey(K key) {
        DoubleListNode<K, V> updateNode = map.get(key);
        if(ObjectUtils.isNull(updateNode)) {
            updateNode = new DoubleListNode<>();
            updateNode.key(key);
        }
        removeKey(key);
        //头插
        updateNode.next(head.next());
        updateNode.pre(head);
        head.next().pre(updateNode);
        head.next(updateNode);

        map.put(key, updateNode);
        log.debug("在 LruMapDoubleList 中更新, key: {}", key);
    }

    @Override
    public void removeKey(K key) {
        DoubleListNode<K, V> removeNode = map.get(key);
        if(ObjectUtils.isNull(removeNode)) {
            return;
        }
        removeNode.pre().next(removeNode.next());
        removeNode.next().pre(removeNode.pre());

        map.remove(key);

    }

    private ICacheEntry<K, V> removeOldest() {
        DoubleListNode<K, V> tailPre = this.tail.pre();
        if(tailPre == this.head){
            throw new CacheRuntimeException("链表中无结点，头结点无法删除!");
        }
        ICacheEntry<K, V> entry = CacheEntry.<K, V>of(tailPre.key(), tailPre.value());
        removeKey(tailPre.key());

        return entry;
    }

}
