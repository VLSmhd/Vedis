package com.vls.cache.core.support.intercepter;

import com.vls.cache.api.ICacheInterceptor;
import com.vls.cache.api.ICacheSlowListener;
import com.vls.cache.core.support.intercepter.aof.CacheInterceptorAof;
import com.vls.cache.core.support.intercepter.cost.CacheInterceptorCost;
import com.vls.cache.core.support.intercepter.evict.CacheInterceptorEvict;

import java.util.ArrayList;
import java.util.List;

public class CacheInterceptors {


    public static ICacheInterceptor aof(){
        return new CacheInterceptorAof();
    }

    public static ICacheInterceptor evict(){
        return new CacheInterceptorEvict();
    }

    public static List<ICacheInterceptor> defaultCosts(){
        List<ICacheInterceptor> list = new ArrayList<>();
        list.add(new CacheInterceptorCost());
        return list;
    }

}
