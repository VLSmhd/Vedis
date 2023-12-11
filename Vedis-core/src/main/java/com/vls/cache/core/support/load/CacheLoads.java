package com.vls.cache.core.support.load;

import com.vls.cache.api.ICacheLoad;

public class CacheLoads {
    private CacheLoads(){}

    public static <K,V> ICacheLoad<K,V> none(){
        return new CacheLoadNone<K,V>();
    }

    public static <K,V> ICacheLoad<K,V> fileJson(String filename){
        return new CacheLoadFileJson<K,V>(filename);
    }


    public static <K,V> ICacheLoad<K,V> aof(String filename){
        return new CacheLoadAof<>(filename);
    }
}
