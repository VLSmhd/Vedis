package com.vls.cache.core.support.evict;

import com.vls.cache.api.ICacheEvict;

/**
 * @description: 策略工具类
 * @author VLS
 * @date 2023/12/4 21:27
 * @version 1.0
 */
public final class CacheEvicts {
    //不让外界创建，只提供静态类
    private CacheEvicts(){}


    /*
     * 无策略
     * @param
     * @return  具体策略实现类CacheEvict
     */
    public static <K,V> ICacheEvict<K,V> none(){
        return new CacheEvictNone<>();
    }

    /*
     * 无策略
     * @param
     * @return  具体策略实现类CacheEvict
     */
    public static <K,V> ICacheEvict<K,V> fifo(){
        return new CacheEvictFIFO<>();
    }
}
