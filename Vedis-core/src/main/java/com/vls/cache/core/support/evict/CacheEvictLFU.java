package com.vls.cache.core.support.evict;

import com.github.houbb.heaven.util.util.CollectionUtil;
import com.vls.cache.api.ICache;
import com.vls.cache.api.ICacheEntry;
import com.vls.cache.api.ICacheEvictContext;
import com.vls.cache.core.exception.CacheRuntimeException;
import com.vls.cache.core.model.CacheEntry;
import com.vls.cache.core.support.struct.lfu.model.FreqNode;
import com.vls.cache.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

@Slf4j
public class CacheEvictLFU<K,V> extends AbstractCacheEvict<K,V> {

    private Map<K, FreqNode<K,V>> keyMap;

    private Map<Integer, LinkedHashSet<FreqNode<K,V>>> freqMap;

    private int minFreq;

    public CacheEvictLFU() {
        keyMap = new HashMap<>();
        freqMap = new HashMap<>();

        minFreq = 1;
    }

    @Override
    protected ICacheEntry<K, V> doEvict(ICacheEvictContext<K, V> context) {
        ICache<K, V> cache = context.cache();
        ICacheEntry<K,V> result = null;
        if(cache.size() >= context.sizeLimit()){
            FreqNode<K, V> evictNode = getMinFreqNode();
            K key = evictNode.key();
            V value = cache.remove(key);
            this.removeKey(key);
            log.debug("淘汰最小频率信息, key: {}, value: {}, freq: {}",
                    key, value, evictNode.frequency());
            result = CacheEntry.of(key, value);
        }

        return result;
    }

    private FreqNode<K,V> getMinFreqNode() {
        LinkedHashSet<FreqNode<K, V>> minFreqSet = freqMap.get(minFreq);
        if(CollectionUtil.isNotEmpty(minFreqSet)){
            //返回第一个元素
            return minFreqSet.iterator().next();
        }
        throw new CacheRuntimeException("未发现最小频率的 Key");
    }



    @Override
    public void updateKey(K key) {
        FreqNode<K, V> freqNode = keyMap.get(key);
        if(ObjectUtils.isNotEmpty(freqNode)){
            int oldFrequency = freqNode.frequency();
            LinkedHashSet<FreqNode<K, V>> preSet = freqMap.get(oldFrequency);
            preSet.remove(freqNode);
            //更新min
            if(minFreq == oldFrequency && preSet.isEmpty()){
                minFreq++;
                log.debug("minFreq 增加为：{}", minFreq);
            }
            addToFreqMap(oldFrequency + 1 , freqNode);
            freqNode.frequency(oldFrequency + 1);
        }else{
            freqNode = new FreqNode<>(key);
            keyMap.put(key, freqNode);
            addToFreqMap(1, freqNode);
            //更新min
            minFreq = 1;
        }

    }

    private void addToFreqMap(int frequency, FreqNode<K, V> freqNode) {
        LinkedHashSet<FreqNode<K, V>> set = freqMap.get(frequency);
        if(set == null){
            set = new LinkedHashSet<>();
        }
        set.add(freqNode);
        freqMap.put(frequency, set);
        log.debug("[freqMap]: freq={} 添加元素节点：{}", frequency, freqNode);
    }

    @Override
    public void removeKey(K key) {
        FreqNode<K, V> removeNode = keyMap.remove(key);
        int frequency = removeNode.frequency();
        LinkedHashSet<FreqNode<K, V>> freqNodeLinkedHashSet = freqMap.get(frequency);
        freqNodeLinkedHashSet.remove(removeNode);
        //更新min
        if(freqNodeLinkedHashSet.isEmpty() && minFreq == frequency){
            minFreq--;
            log.debug("minFreq 减少为：{}", minFreq);
        }
    }
}
