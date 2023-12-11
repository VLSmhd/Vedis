package com.vls.cache.core.support.struct.lru.model;

public class DoubleListNode<K,V> {

    private K key;


    private V value;

    private DoubleListNode<K,V> pre;

    private DoubleListNode<K,V> next;

    public K key() {
        return key;
    }

    public DoubleListNode() {
    }

    public DoubleListNode<K, V> key(K key) {
        this.key = key;
        return this;
    }

    public V value() {
        return value;
    }

    public DoubleListNode<K, V> value(V value) {
        this.value = value;
        return this;
    }

    public DoubleListNode<K, V> pre() {
        return pre;
    }

    public DoubleListNode<K, V> pre(DoubleListNode<K, V> pre) {
        this.pre = pre;
        return this;
    }

    public DoubleListNode<K, V> next() {
        return next;
    }

    public DoubleListNode<K, V> next(DoubleListNode<K, V> next) {
        this.next = next;
        return this;
    }
}