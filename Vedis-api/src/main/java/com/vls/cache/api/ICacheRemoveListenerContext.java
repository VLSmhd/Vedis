package com.vls.cache.api;

/**
 * @description: 监听器上下文
 * @author VLS
 * @date 2023/12/6 20:21
 * @version 1.0
 */
public interface ICacheRemoveListenerContext<K,V> {

    /*
     * 返回淘汰或者清空的key
     * @param
     * @return
     */
    K key();

    V value();

    /*
     * 清空类型: 过期淘汰 or 内存满了的淘汰
     * @param
     * @return
     */
    String type();
}
