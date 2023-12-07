package com.vls.cache.util;

import com.github.houbb.heaven.support.handler.IHandler;
import com.github.houbb.heaven.util.lang.StringUtil;
import com.github.houbb.heaven.util.lang.reflect.ClassTypeUtil;
import com.github.houbb.heaven.util.util.ArrayUtil;
import com.github.houbb.heaven.util.util.CollectionUtil;
import com.github.houbb.heaven.util.util.MapUtil;

import java.lang.reflect.Array;
import java.util.*;

public final class ObjectUtils {
    private ObjectUtils() {
    }

    public static boolean isSameType(Object one, Object two) {
        if (!isNull(one) && !isNull(two)) {
            Class clazzOne = one.getClass();
            return clazzOne.isInstance(two);
        } else {
            return false;
        }
    }

    public static boolean isNotSameType(Object one, Object two) {
        return !isSameType(one, two);
    }

    public static boolean isNull(Object object) {
        return null == object;
    }

    public static boolean isNotNull(Object object) {
        return !isNull(object);
    }

    public static boolean isEmpty(Object object) {
        if (isNull(object)) {
            return true;
        } else if (object instanceof String) {
            String string = (String)object;
            return StringUtil.isEmpty(string);
        } else if (object instanceof Collection) {
            Collection collection = (Collection)object;
            return CollectionUtil.isEmpty(collection);
        } else if (object instanceof Map) {
            Map map = (Map)object;
            return MapUtil.isEmpty(map);
        } else if (object.getClass().isArray()) {
            return Array.getLength(object) == 0;
        } else {
            return false;
        }
    }

    public static boolean isNotEmpty(Object object) {
        return !isEmpty(object);
    }

    public static boolean isEquals(Object except, Object real) {
        if (isNotSameType(except, real)) {
            return false;
        } else {
            Class exceptClass = except.getClass();
            Class realClass = except.getClass();
            if (exceptClass.isPrimitive() && realClass.isPrimitive() && except != real) {
                return false;
            } else if (ClassTypeUtil.isArray(exceptClass) && ClassTypeUtil.isArray(realClass)) {
                Object[] exceptArray = (Object[])((Object[])except);
                Object[] realArray = (Object[])((Object[])real);
                return Arrays.equals(exceptArray, realArray);
            } else if (ClassTypeUtil.isMap(exceptClass) && ClassTypeUtil.isMap(realClass)) {
                Map exceptMap = (Map)except;
                Map realMap = (Map)real;
                return exceptMap.equals(realMap);
            } else {
                return except.equals(real);
            }
        }
    }

    public static boolean isNotEquals(Object except, Object real) {
        return !isEquals(except, real);
    }

    public static String objectToString(Object object) {
        return objectToString(object, (String)null);
    }

    public static String objectToString(Object object, String defaultValue) {
        return isNull(object) ? defaultValue : object.toString();
    }

    public static boolean isNull(Object object, Object... others) {
        if (isNull(object)) {
            if (ArrayUtil.isNotEmpty(others)) {
                Object[] var2 = others;
                int var3 = others.length;

                for(int var4 = 0; var4 < var3; ++var4) {
                    Object other = var2[var4];
                    if (isNotNull(other)) {
                        return false;
                    }
                }

                return true;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public static boolean isEqualsOrNull(Object left, Object right) {
        if (isNull(left, right)) {
            return true;
        } else {
            return !isNull(left) && !isNull(right) ? isEquals(left, right) : false;
        }
    }

    public static <R> List<R> toList(Object object, IHandler<Object, R> handler) {
        if (isNull(object)) {
            return Collections.emptyList();
        } else {
            Class clazz = object.getClass();
            if (ClassTypeUtil.isCollection(clazz)) {
                Collection collection = (Collection)object;
                return CollectionUtil.toList(collection, handler);
            } else if (clazz.isArray()) {
                return ArrayUtil.toList(object, handler);
            } else {
                throw new UnsupportedOperationException("Not support foreach() for class: " + clazz.getName());
            }
        }
    }

    public static Class getClass(Object object) {
        return isNull(object) ? null : object.getClass();
    }
}