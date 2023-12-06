package com.vls.cache.core.support.persist;

import com.vls.cache.api.ICachePersist;

public class CachePersists {

    private CachePersists(){}

    public static <K,V> ICachePersist<K,V> fileJson(String filename){
        return new CachePersistFileJson<>(filename);
    }


    public static <K,V> ICachePersist<K,V> none(){
        return new CachePersistNone<>();
    }
}
