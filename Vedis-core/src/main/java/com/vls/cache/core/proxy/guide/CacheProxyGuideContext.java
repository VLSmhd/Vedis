package com.vls.cache.core.proxy.guide;

import com.vls.cache.annotation.CacheInterceptor;
import com.vls.cache.api.ICache;

import java.lang.reflect.Method;

public class CacheProxyGuideContext implements ICacheProxyGuideContext{

    private ICache target;

    private Object[] params;

    private Method method;

    private CacheInterceptor interceptor;

    public static CacheProxyGuideContext newInstance(){
        return new CacheProxyGuideContext();
    }

    @Override
    public ICache target() {
        return target;
    }
    public CacheProxyGuideContext setTarget(ICache target) {
        this.target = target;
        return this;
    }

    @Override
    public CacheInterceptor interceptor() {
        return interceptor;
    }

    @Override
    public CacheProxyGuideContext target(ICache cache) {
        this.target = cache;
        return this;
    }

    @Override
    public Object[] params() {
        return params;
    }

    public CacheProxyGuideContext setParams(Object[] params) {
        this.params = params;
        return this;
    }

    @Override
    public Method method() {
        return method;
    }
    public CacheProxyGuideContext setMethod(Method method) {
        //这里的method，是那些被增强的方法，所以Interceptor的set方法在这set
        this.method = method;
        this.interceptor = method.getAnnotation(CacheInterceptor.class);
        return this;
    }


    @Override
    public Object invoke() throws Throwable {
        return method.invoke(target, params);
    }
}
