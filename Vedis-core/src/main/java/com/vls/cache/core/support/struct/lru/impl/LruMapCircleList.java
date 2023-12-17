package com.vls.cache.core.support.struct.lru.impl;

import com.vls.cache.api.ICacheEntry;
import com.vls.cache.core.exception.CacheRuntimeException;
import com.vls.cache.core.model.CacheEntry;
import com.vls.cache.core.support.struct.lru.ILruMap;
import com.vls.cache.core.support.struct.lru.model.CircleListNode;
import com.vls.cache.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class LruMapCircleList<K,V> implements ILruMap<K,V> {


    private CircleListNode<K,V> head;

    private CircleListNode<K,V> pre;

    private Map<K,CircleListNode<K,V>> indexMap;

    public LruMapCircleList() {
        head = new CircleListNode<>(null);
        indexMap = new HashMap<>();
        pre = head;

        this.head.next(this.head);
        this.head.pre(this.head);
    }

    @Override
    public ICacheEntry<K, V> removeOldest() {
        if(isEmpty()){
            log.error("当前列表为空，无法进行删除");
            throw new CacheRuntimeException("不可删除头结点!");
        }
        CircleListNode<K, V> cur = this.pre;
        while (cur.next() != head){
            cur = cur.next();
            if(cur.accessFlag()){
                cur.accessFlag(false);
            }else{
                pre = cur.next();
                removeKey(cur.key());
                return CacheEntry.of(cur.key(), cur.value());
            }
        }
        //循环一圈都没找到，降级为FIFO
        pre = head;
        CircleListNode<K,V> firstNode = this.head.next();
        removeKey(head.next().key());
        return CacheEntry.of(firstNode.key(), firstNode.value());
    }

    @Override
    public void updateKey(K key) {
        CircleListNode<K, V> node = indexMap.get(key);
        if(ObjectUtils.isNotEmpty(node)) {
            node.accessFlag(true);
            log.debug("节点已存在，设置节点访问标识为 true, key: {}", key);
        }else {
            node = new CircleListNode<>(key);
            CircleListNode<K, V> tail = head.pre();
            node.next(head);
            node.pre(tail);
            tail.next(node);
            head.pre(node);
            indexMap.put(key, node);
            log.debug("节点不存在，新增节点到链表中：{}", key);
        }
    }

    @Override
    public void removeKey(K key) {
        CircleListNode<K, V> removeNode = indexMap.get(key);
        if(ObjectUtils.isEmpty(removeNode)) {
            log.warn("对应的删除信息不存在：{}", key);
            return;
        }
        CircleListNode<K, V> pre = removeNode.pre();
        CircleListNode<K, V> next = removeNode.next();

        pre.next(next);
        next.pre(pre);
        indexMap.remove(key);
        log.debug("Key: {} 从循环链表中移除", key);
    }

    @Override
    public boolean containsKey(K key) {
        return indexMap.containsKey(key);
    }

    @Override
    public boolean isEmpty() {
        return indexMap.isEmpty();
    }
}
