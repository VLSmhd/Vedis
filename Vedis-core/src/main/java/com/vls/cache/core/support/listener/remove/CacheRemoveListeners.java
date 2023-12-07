package com.vls.cache.core.support.listener.remove;

import com.vls.cache.api.ICacheRemoveListener;

import java.util.ArrayList;
import java.util.List;

public class CacheRemoveListeners {

    private CacheRemoveListeners(){}


    public static  <K,V> List<ICacheRemoveListener<K,V>> defaults(){
        List<ICacheRemoveListener<K,V>> list = new ArrayList<>();
        list.add(new CacheRemoveListener<>());
        return list;
    }

}
