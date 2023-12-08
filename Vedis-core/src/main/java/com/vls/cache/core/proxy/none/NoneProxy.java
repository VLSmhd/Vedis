package com.vls.cache.core.proxy.none;


import com.vls.cache.core.proxy.ICacheProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class NoneProxy implements ICacheProxy, InvocationHandler {

    private Object target;

    public NoneProxy(Object target) {
        this.target = target;
    }

    @Override
    public Object proxy() {
        return target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(proxy, args);
    }
}
