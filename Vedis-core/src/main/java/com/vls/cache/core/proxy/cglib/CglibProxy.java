package com.vls.cache.core.proxy.cglib;

import com.vls.cache.api.ICache;
import com.vls.cache.core.proxy.ICacheProxy;

import com.vls.cache.core.proxy.guide.CacheProxyGuide;
import com.vls.cache.core.proxy.guide.CacheProxyGuideContext;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibProxy implements ICacheProxy, MethodInterceptor {

    private final ICache target;

    public CglibProxy(ICache target) {
        this.target = target;
    }


    @Override
    public Object proxy() {
        Enhancer enhancer = new Enhancer();
        //要代理的目标对象
        enhancer.setSuperclass(target.getClass());

        enhancer.setCallback(this);
        return enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] params, MethodProxy methodProxy) throws Throwable {
        CacheProxyGuideContext context = CacheProxyGuideContext.newInstance()
                .setMethod(method).setParams(params).target(this.target);

        return CacheProxyGuide.newInstance().setContext(context).execute();
    }
}
