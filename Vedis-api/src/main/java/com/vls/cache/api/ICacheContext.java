package com.vls.cache.api;

import java.util.Map;

/**
 * @author VLS
 * @version 1.0
 * @description: TODO
 * @date 2023/12/4 17:35
 */
public interface ICacheContext<K,V> {

    /*
     *  获取容器
     * @param
     * @return
     */
    Map<K,V> map();

    /*
     * 获取容器大小限制
     * @param
     * @return
     */
    int sizeLimit();

    /*
     * 淘汰策略
     * @param
     * @return
     */
    ICacheEvict<K,V> cacheEvict();

}
