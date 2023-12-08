package com.vls.cache.core.proxy.jdkdynamic;

import com.vls.cache.api.ICache;
import com.vls.cache.core.proxy.ICacheProxy;
import com.vls.cache.core.proxy.guide.CacheProxyGuide;
import com.vls.cache.core.proxy.guide.CacheProxyGuideContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DynamicProxy implements ICacheProxy, InvocationHandler {

    private final ICache target;

    public DynamicProxy(ICache target) {
        this.target = target;
    }

    @Override
    public Object proxy() {
        // 我们要代理哪个真实对象，就将该对象传进去，最后是通过该真实对象来调用其方法的
        InvocationHandler invocationHandler = new DynamicProxy(target);

        return Proxy.newProxyInstance
                (invocationHandler.getClass().getClassLoader(), target.getClass().getInterfaces(), invocationHandler);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        CacheProxyGuideContext context = CacheProxyGuideContext.newInstance()
                .setMethod(method).setParams(args).target(this.target);

        return CacheProxyGuide.newInstance().setContext(context).execute();
    }
}
