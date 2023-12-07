package com.vls.cache.api;

public interface ICacheSlowListener {

    void listen(ICacheSlowListenerContext context);

    /*
     * 慢日志触发阈值
     * @param
     * @return
     */
    long slowerThanMills();
}
