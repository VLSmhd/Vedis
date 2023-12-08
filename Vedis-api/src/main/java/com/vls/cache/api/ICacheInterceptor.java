package com.vls.cache.api;

/**
 * @description: 拦截器接口
 * @author VLS
 * @date 2023/12/7 17:50
 * @version 1.0
 */
public interface ICacheInterceptor<K,V> {


    void before(ICacheInterceptorContext<K,V> context);

    void after(ICacheInterceptorContext<K,V> context);
}
