package com.vls.cache.core.proxy.guide;

import com.vls.cache.annotation.CacheInterceptor;
import com.vls.cache.api.ICache;

import java.lang.reflect.Method;

public interface ICacheProxyGuideContext {
    /*
     * 代理的对象
     * @param
     * @return
     */
    ICache target();

    CacheInterceptor interceptor();


    /*
     * 代理对象的代理增强类
     * @param
     * @return
     */
    ICacheProxyGuideContext target(ICache cache);


    Object[] params();

    Method method();

    Object invoke() throws Throwable;
}
