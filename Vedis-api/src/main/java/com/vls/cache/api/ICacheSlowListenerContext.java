package com.vls.cache.api;

public interface ICacheSlowListenerContext {

    /*
     * 方法/操作 名
     * @param
     * @return
     */
    String methodName();

    /*
     * 方法参数
     * @param
     * @return
     */
    Object[] params();

    /*
     * 方法返回值
     * @param
     * @return
     */
    Object result();

    long startTimeMills();

    long endTimeMills();

    long costTimeMills();
}
