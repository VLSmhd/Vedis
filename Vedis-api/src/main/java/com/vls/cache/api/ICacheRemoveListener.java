package com.vls.cache.api;

/**
 * @description: 监听器删除接口
 * @author VLS
 * @date 2023/12/6 20:07
 * @version 1.0
 */
public interface ICacheRemoveListener<K,V> {

    /**
     * @description: 监听动作
     * @author VLS
     * @date 2023/12/6 20:19
     * @version 1.0
     */
    void listen(ICacheRemoveListenerContext<K,V> removeListenerContext);
}
