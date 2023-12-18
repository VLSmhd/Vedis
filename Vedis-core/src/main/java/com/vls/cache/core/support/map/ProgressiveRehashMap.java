package com.vls.cache.core.support.map;

import com.github.houbb.heaven.support.tuple.impl.Pair;
import com.github.houbb.heaven.util.util.CollectionUtil;
import com.vls.cache.core.exception.CacheRuntimeException;
import com.vls.cache.util.HashUtils;
import com.vls.cache.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class ProgressiveRehashMap<K,V> extends AbstractMap<K,V> implements Map<K,V>{

    private int capacity;

    private int size;

    private double factor = 1;

    private List<List<Entry<K,V>>> table;

    private List<List<Entry<K,V>>> rehashTable;

    private int rehashIdx;

    //处于 rehash 状态的  新哈希表容量
    private int rehashCapacity;

    private boolean debugMode = false;

    private double reduceFactor = 0.5;

    private static int MIN_CAPACITY = 2;

    static class DefaultEntry<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        V value;
        ProgressiveRehashMap.DefaultEntry<K,V> next;

        DefaultEntry(int hash, K key, V value, ProgressiveRehashMap.DefaultEntry<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        DefaultEntry(K key, V value, int hash) {
            this.key = key;
            this.value = value;
            this.hash = hash;
        }
        @Override
        public final K getKey()         { return key; }
        @Override
        public final V getValue()      { return value; }
        @Override
        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }
        @Override
        public final V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        @Override
        @SuppressWarnings(value = "all")
        public final boolean equals(Object o) {
            if (o == this)
                return true;
            if (o instanceof Map.Entry) {
                Map.Entry<?,?> e = (Map.Entry<?,?>)o;
                if (Objects.equals(key, e.getKey()) &&
                        Objects.equals(value, e.getValue()))
                    return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return "{" +key +
                    ": " + value +
                    '}';
        }
    }

    public ProgressiveRehashMap() {
        this(8);
    }

    public ProgressiveRehashMap(int capacity) {
        this(capacity, false);
    }

    public ProgressiveRehashMap(int capacity, boolean debugMode) {
        if(capacity < MIN_CAPACITY) {
            capacity = MIN_CAPACITY;
        }
        this.capacity = capacity;
        table = new ArrayList<>(capacity);
        for (int i = 0; i < capacity; i++) {
            table.add(i, new LinkedList<Entry<K,V>>());
        }
        this.debugMode = debugMode;

        rehashIdx = -1;
        rehashCapacity = -1;
        rehashTable = null;
    }

    @Override
    public V put(K key, V value) {
        //判断是否需要更新
        if(isInRehash()) {
            if(debugMode) {
                log.debug("当前处于渐进式 rehash 阶段，额外执行一次渐进式 rehash 的动作");
            }
            rehashToNewTable();

            //原table更新
            Pair<Boolean, V> pair1 = update(key, value, table, capacity);
            if(pair1.getValueOne()) {
                V oldVal = pair1.getValueTwo();
                if(debugMode) {
                    log.debug("此次为更新 table 操作。key: {}, value: {}", key, value);
                    printTable(this.table);
                }
                return oldVal;
            }
            //新table更新
            Pair<Boolean, V> pair2 = update(key, value, rehashTable, rehashCapacity);
            if(pair2.getValueOne()) {
                V oldVal = pair2.getValueTwo();
                if(debugMode) {
                    log.debug("此次为更新 rehashTable 操作。key: {}, value: {}", key, value);
                    printTable(this.table);
                }
                return oldVal;
            }

        }else {
            //原table更新
            Pair<Boolean, V> pair1 = update(key, value, table, capacity);
            if(pair1.getValueOne()) {
                V oldVal = pair1.getValueTwo();
                if(debugMode) {
                    log.debug("此次为更新 table 操作。key: {}, value: {}", key, value);
                    printTable(this.table);
                }
                return oldVal;
            }
        }
        return createNewEntry(key, value);
    }


    /*
     *
     * @param
     * @return  Pair二元组  1. 为true就是更新操作，否则就是新增操作
     */
    private Pair<Boolean, V> update(K key, V value, List<List<Entry<K,V>>> table, int capacity) {
        int hash = HashUtils.hash(key);
        int idx = (capacity - 1) & hash;

        List<Entry<K, V>> entries = table.get(idx);
        //遍历链表，查看key是否已存在
        for (Entry<K, V> entry : entries) {
            K entryKey = entry.getKey();
            V oldValue = entry.getValue();
            if(ObjectUtils.isNull(key, entryKey)
                    || key.equals(entryKey)) {
                entry.setValue(value);
                if(debugMode) {
                    log.debug("put 为替换元素，table 信息为：");
                    printTable(table);
                }
                return Pair.of(true, oldValue);
            }
        }
        return Pair.of(false, null);
    }

    private boolean isInRehash() {
        return rehashIdx != -1;
    }

    /*
     *
     * @param  新增结点到map中
     * @return
     */
    private V createNewEntry(K key, V value) {
        int hash = HashUtils.hash(key);
        Entry<K, V> entry = new DefaultEntry<>(key, value, hash);
        if(isInRehash()) {
            //新结点添加到新table
            addNewEntryToRehashTable(hash,  entry);
        }
        //不在rehash阶段
        else if(isNeedExpand()) {
            //旧的table已经不够了
            rehash(capacity << 1);
            addNewEntryToRehashTable(hash, entry);
        }else {
            addNewEntryToTable(hash, entry);
        }
        size++;
        return value;
    }

    private void addNewEntryToTable(int hash, Entry<K,V> entry) {
        int idx = (capacity - 1) & hash;
        List<Entry<K, V>> entries = table.get(idx);
        entries.add(entry );
        if(debugMode) {
            log.debug("目前不处于 rehash 中，元素直接插入到 table 中。");
            printTable(this.table);
        }
    }

    private void addNewEntryToRehashTable(int hash, Entry<K, V> entry) {
        int idx = (rehashCapacity - 1) & hash;
        List<Entry<K, V>> entries = rehashTable.get(idx);
        entries.add(entry);
        if (debugMode) {
            log.debug("目前处于 rehash 中，元素直接插入到 rehashTable 中。");
            printTable(this.rehashTable);
        }
    }

    private boolean isNeedExpand() {
        return (size * 1.0 / capacity) >= factor && !isInRehash();
    }

    private void printTable(List<List<Entry<K,V>>> table) {
        for(List<Entry<K, V>> list : table) {
            for(Entry<K,V> entry : list) {
                System.out.print(entry + " ") ;
            }
            System.out.println();
        }
    }

    private void printAllTable() {
        log.info("[printAllTable] 打印table -----------------");
        for(List<Entry<K, V>> list : this.table) {
            for(Entry<K,V> entry : list) {
                System.out.print(entry + " ") ;
            }
            System.out.println();
        }
        log.info("[printAllTable] 打印rehashTable -----------");
        for(List<Entry<K, V>> list : this.rehashTable) {
            for(Entry<K,V> entry : list) {
                System.out.print(entry + " ") ;
            }
            System.out.println();
        }
    }

    private void rehash(int rehashCapacity) {
        //1. 判断是否处于rehash阶段  处于，直接返回
        if(isInRehash()) {
            if(debugMode) {
                log.debug("当前处于渐进式 rehash 阶段，不能重复进行 rehash!");
            }
            return;
        }
        //2. 创建新的rehashtable
        this.rehashCapacity = rehashCapacity;
        rehashTable = new ArrayList<>(this.rehashCapacity);
        for (int i = 0; i < rehashCapacity; i++) {
            rehashTable.add(i,new LinkedList<>());
        }
        //3. 先将table[0]的数据  rehash到 新表中   然后table[0]设置为空
        rehashToNewTable();
    }


    private void rehashToNewTable() {
        if(!isInRehash()) {
            return;
        }
        //添加到新table
        rehashIdx++;
        List<Entry<K, V>> entries = table.get(rehashIdx);
        for (Entry<K, V> entry : entries) {
            int hash = HashUtils.hash(entry.getKey());
            int idx = hash & (rehashCapacity - 1);


            List<Entry<K, V>> entryList = rehashTable.get(idx);
            entryList.add(entry);
            rehashTable.set(idx, entryList);
        }
        //清空旧table
        table.remove(rehashIdx);
        //判断是否结束rehash
        if(rehashIdx == table.size() - 1) {
            this.capacity = this.rehashCapacity;
            this.rehashCapacity = -1;
            this.rehashIdx = -1;
            this.table = rehashTable;
            this.rehashTable = null;

            if(debugMode) {
                log.debug("渐进式 rehash 已经完成。");
                printTable(this.table);
            }
        } else {
            if(debugMode) {
                log.debug("渐进式 rehash 处理中, 目前 index：{} 已完成", rehashIdx);
                printAllTable();
            }
        }
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        if(isInRehash()) {
            log.debug("调用entrySet，rehash中......");
            rehashToNewTable();
        }
        Set<Entry<K, V>> set = new HashSet<>();
        if(CollectionUtil.isEmpty(table) && CollectionUtil.isEmpty(rehashTable)) {
            return set;
        }
        for (List<Entry<K, V>> entries : table) {
            if(CollectionUtil.isNotEmpty(entries)) {
                set.addAll(entries);
            }
        }
        for (List<Entry<K, V>> entries : rehashTable) {
            if(CollectionUtil.isNotEmpty(entries)) {
                set.addAll(entries);
            }
        }
        return set;
    }

    @Override
    public V get(Object key) {
        if(isInRehash()) {
            if(debugMode) {
                log.debug("当前处于渐进式 rehash 状态，额外执行一次操作");
            }
            rehashToNewTable();
        }
        //从第一个table找
        V value = getValue((K) key, table, capacity);
        if(value != null) {
            log.info("[get] 从table中找到对应的key : {}，返回value : {}", key,value);
            return value;
        }
        //从rehashtable找
        if (isInRehash()) {
            V value1 = getValue((K) key, rehashTable, rehashCapacity);
            if(value1 != null) {
                log.info("[get] 从rehashTable中找到对应的key : {}，返回value : {}", key,value1);
                return value;
            }
        }

        return null;
    }

    private V getValue(K key, List<List<Entry<K,V>>> table, int capacity) {
        int hash = HashUtils.hash(key);
        int idx1 = (capacity - 1) & hash;
        List<Entry<K, V>> entryList = table.get(idx1);
        for (Entry<K, V> entry : entryList) {
            K entryKey = entry.getKey();
            if(ObjectUtils.isNull(key, entryKey) || key.equals(entryKey)) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public V remove(Object key) {
        if(isInRehash()) {
            if(debugMode) {
                log.debug("当前处于渐进式 rehash 状态，额外执行一次操作");
            }
            rehashToNewTable();

            V removeValue = removeFromTable((K) key, table, capacity);
            if(removeValue != null) {
                log.info("[remove] 从table中找到对应的key : {}，删除value : {}", key,removeValue);
                return removeValue;
            }

            removeValue = removeFromTable((K) key, rehashTable, rehashCapacity);
            if(removeValue != null) {
                log.info("[remove] 从rehashTable中找到对应的key : {}，删除value : {}", key,removeValue);
                return removeValue;
            }

        }
        else if(isNeedReduce()) {
            if (debugMode) {
                log.debug("触发缩容操作");
            }
            capacity = capacity >> 1;
            rehash(capacity);

        } else {
            V removeValue = removeFromTable((K) key, table, capacity);
            if(removeValue != null) {
                log.info("[remove] 从table中找到对应的key : {}，删除value : {}", key,removeValue);
                return removeValue;
            }
        }
        return null;
    }

    private V removeFromTable(K key, List<List<Entry<K,V>>> table, int capacity) {
        int hash = HashUtils.hash(key);
        int idx = (capacity - 1) & hash;

        List<Entry<K, V>> entries = table.get(idx);
        if(CollectionUtil.isEmpty(entries)) {
            throw new CacheRuntimeException("[remove] 哈希桶为空，要删除的元素不存在！");
        }
        V removeValue = null;
        for (Entry<K, V> entry : entries) {
            K entryKey = entry.getKey();
            if(ObjectUtils.isNull(key, entryKey) || key.equals(entryKey)) {
                entries.remove(entry);
                removeValue = entry.getValue();
                if (debugMode) {
                    log.debug("移除元素，table 信息为：{}", entry);
                    printTable(table);
                }
            }
        }

        return removeValue;
    }

    private boolean isNeedReduce() {
        return (size * 1.0 / capacity) <= reduceFactor && (capacity >= MIN_CAPACITY) && !isInRehash();
    }
}
