package com.vls.cache.core.support.struct.lfu.model;

import java.util.Objects;

/**
 * @description: 带有频次的结点
 * @author VLS
 * @date 2023/12/12 16:47
 * @version 1.0
 */
public class FreqNode<K,V> {

    private K key;

    private V value;

    private int frequency;

    public FreqNode(K key) {
        this.key = key;
        this.frequency = 1;
    }

    public K key() {
        return key;
    }

    public FreqNode<K, V> key(K key) {
        this.key = key;
        return this;
    }

    public V value() {
        return value;
    }

    public FreqNode<K, V> value(V value) {
        this.value = value;
        return this;
    }

    public int frequency() {
        return frequency;
    }

    public FreqNode<K, V> frequency(int frequency) {
        this.frequency = frequency;
        return this;
    }

    @Override
    public String toString() {
        return "FreqNode{" +
                "key=" + key +
                ", value=" + value +
                ", frequency=" + frequency +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FreqNode<?, ?> freqNode = (FreqNode<?, ?>) o;
        return frequency == freqNode.frequency &&
                Objects.equals(key, freqNode.key) &&
                Objects.equals(value, freqNode.value);
    }


//    @Override
//    public int hashCode() {
//        return Objects.hash(key, value, frequency);
//    }
}
