package com.vls.cache.core.support.load;

import com.vls.cache.api.ICache;
import com.vls.cache.api.ICacheLoad;

//真正实现的时候，要把泛型具体化
public class MyCacheLoad implements ICacheLoad<String,String> {

    @Override
    public void load(ICache<String, String> cache) {
        cache.put("mhd", "mhd");
        cache.put("cml", "cml");
    }
}
