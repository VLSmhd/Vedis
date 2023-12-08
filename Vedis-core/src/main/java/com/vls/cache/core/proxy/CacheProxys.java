package com.vls.cache.core.proxy;


import com.vls.cache.api.ICache;
import com.vls.cache.core.proxy.cglib.CglibProxy;
import com.vls.cache.core.proxy.jdkdynamic.DynamicProxy;
import com.vls.cache.core.proxy.none.NoneProxy;
import com.vls.cache.util.ObjectUtils;

import java.lang.reflect.Proxy;

public class CacheProxys {

    private CacheProxys(){}


    /**
     * @description: 获取对象代理
     * @author VLS
     * @date 2023/12/8 15:15
     * @version 1.0
     */
    @SuppressWarnings("all")
    public static <K,V> ICache<K,V> getProxy(ICache<K,V> cache) {
        if(ObjectUtils.isNull(cache)){
            return (ICache<K,V>) new NoneProxy(cache).proxy();
        }

        Class<? extends ICache> clazz = cache.getClass();

        //判断代理对象是类还是接口
        if(clazz.isInterface() || Proxy.isProxyClass(clazz)){
            return  (ICache<K,V>)new DynamicProxy(cache).proxy();
        }

        return (ICache<K,V>)new CglibProxy(cache).proxy();
    }
}
