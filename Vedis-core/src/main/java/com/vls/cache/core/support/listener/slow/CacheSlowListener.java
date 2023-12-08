package com.vls.cache.core.support.listener.slow;

import com.alibaba.fastjson.JSON;
import com.vls.cache.api.ICacheSlowListener;
import com.vls.cache.api.ICacheSlowListenerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheSlowListener implements ICacheSlowListener {


    @Override
    public void listen(ICacheSlowListenerContext context) {
        log.warn("[Slow] methodName: {}, params: {}, cost time: {}",
                context.methodName(), JSON.toJSON(context.params()), context.costTimeMills());
    }

    @Override
    public long slowerThanMills() {
        //超过1s就打印
        return 1;
    }
}
