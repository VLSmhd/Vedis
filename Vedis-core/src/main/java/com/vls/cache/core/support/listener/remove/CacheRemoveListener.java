package com.vls.cache.core.support.listener.remove;


import com.vls.cache.api.ICacheRemoveListener;
import com.vls.cache.api.ICacheRemoveListenerContext;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;

@Slf4j
public class CacheRemoveListener<K,V> implements ICacheRemoveListener<K,V> {


    /**
     * @param removeListenerContext
     * @description: 监听动作
     * @author VLS
     * @date 2023/12/6 20:19
     * @version 1.0
     */
    @Override
    public void listen(ICacheRemoveListenerContext<K, V> removeListenerContext) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        log.debug("Remove key: {}, value: {}, type: {}, timeAt: {}",
                removeListenerContext.key(),
                removeListenerContext.value(),
                removeListenerContext.type(),
                formatter.format(System.currentTimeMillis()));
    }
}
