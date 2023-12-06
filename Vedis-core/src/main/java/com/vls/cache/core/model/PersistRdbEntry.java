package com.vls.cache.core.model;

import lombok.Data;

/**
 * @description: 用于RDB持久化实体类
 * @author VLS
 * @date 2023/12/5 21:06
 * @version 1.0
 */
@Data
public class PersistRdbEntry<K,V> {

    K key;


    V value;

    Long expire;

}
