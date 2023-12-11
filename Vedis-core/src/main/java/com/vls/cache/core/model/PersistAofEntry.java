package com.vls.cache.core.model;

import jdk.nashorn.internal.objects.annotations.Constructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@ToString
public class PersistAofEntry {

    private String methodName;

    private Object[] params;

    private PersistAofEntry(){

    }


    private PersistAofEntry(String methodName, Object[] params) {
        this.methodName = methodName;
        this.params = params;
    }

    public static PersistAofEntry of(String methodName, Object[] params){
        return new PersistAofEntry(methodName, params);
    }
}
