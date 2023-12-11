package com.vls.cache.util;

import com.github.houbb.heaven.annotation.reflect.Param;
import com.github.houbb.heaven.response.exception.CommonRuntimeException;
import com.github.houbb.heaven.support.handler.IHandler;
import com.github.houbb.heaven.util.common.ArgUtil;
import com.github.houbb.heaven.util.guava.Guavas;
import com.github.houbb.heaven.util.lang.ObjectUtil;
import com.github.houbb.heaven.util.lang.StringUtil;
import com.github.houbb.heaven.util.lang.reflect.ClassUtil;
import com.github.houbb.heaven.util.lang.reflect.TypeUtil;
import com.github.houbb.heaven.util.util.ArrayUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

public final class ReflectMethodUtils {
    private static final List<String> IGNORE_METHOD_LIST;

    private ReflectMethodUtils() {
    }

    public static List<String> getIgnoreMethodList() {
        return IGNORE_METHOD_LIST;
    }

    public static boolean isIgnoreMethod(String methodName) {
        return getIgnoreMethodList().contains(methodName);
    }

    public static List<String> getParamTypeNames(Method method) {
        ArgUtil.notNull(method, "method");
        Class<?>[] paramTypes = method.getParameterTypes();
        return ArrayUtil.toList(paramTypes, new IHandler<Class<?>, String>() {
            @Override
            public String handle(Class<?> aClass) {
                return aClass.getName();
            }
        });
    }

    public static List<String> getParamNames(Method method) {
        ArgUtil.notNull(method, "method");
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        return getParamNames(parameterAnnotations);
    }

    public static List<String> getParamNames(Annotation[][] parameterAnnotations) {
        if (ArrayUtil.isEmpty(parameterAnnotations)) {
            return Collections.emptyList();
        } else {
            int paramSize = parameterAnnotations.length;
            List<String> resultList = Guavas.newArrayList(paramSize);

            for(int i = 0; i < paramSize; ++i) {
                Annotation[] annotations = parameterAnnotations[i];
                String paramName = getParamName(i, annotations);
                resultList.add(paramName);
            }

            return resultList;
        }
    }

    private static String getParamName(int index, Annotation[] annotations) {
        String defaultName = "arg" + index;
        if (ArrayUtil.isEmpty(annotations)) {
            return defaultName;
        } else {
            Annotation[] var3 = annotations;
            int var4 = annotations.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Annotation annotation = var3[var5];
                if (annotation.annotationType().equals(Param.class)) {
                    Param param = (Param)annotation;
                    return param.value();
                }
            }

            return defaultName;
        }
    }

    public static Class getReturnGenericType(Method method, int index) {
        Type returnType = method.getGenericReturnType();
        if (returnType instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType)returnType;
            Type[] typeArguments = type.getActualTypeArguments();
            return (Class)typeArguments[index];
        } else {
            return null;
        }
    }

    public static Class getParamGenericType(Method method, int paramIndex, int genericIndex) {
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        Type genericParameterType = genericParameterTypes[paramIndex];
        if (genericParameterType instanceof ParameterizedType) {
            ParameterizedType aType = (ParameterizedType)genericParameterType;
            Type[] parameterArgTypes = aType.getActualTypeArguments();
            return (Class)parameterArgTypes[genericIndex];
        } else {
            return null;
        }
    }

    public static Optional<Method> getMethodOptional(Class tClass, Class<? extends Annotation> annotationClass) {
        Method[] methods = tClass.getMethods();
        if (ArrayUtil.isEmpty(methods)) {
            return Optional.empty();
        } else {
            Method[] var3 = methods;
            int var4 = methods.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Method method = var3[var5];
                if (method.isAnnotationPresent(annotationClass)) {
                    return Optional.of(method);
                }
            }

            return Optional.empty();
        }
    }

    public static Object invoke(Object instance, Method method, Object... args) {
        ArgUtil.notNull(method, "method");

        try {
            return method.invoke(instance, args);
        } catch (InvocationTargetException | IllegalAccessException var4) {
            throw new CommonRuntimeException(var4);
        }
    }

    public static Object invoke(Object instance, String methodName, Object... args) {
        ArgUtil.notEmpty(methodName, "methodName");

        try {
            if (ArrayUtil.isEmpty(args)) {
                return invokeNoArgsMethod(instance, methodName);
            } else {
                Class clazz = instance.getClass();
                Class<?>[] paramTypes = new Class[args.length];

                for(int i = 0; i < args.length; ++i) {
                    Object param = args[i];
                    paramTypes[i] = param.getClass();
                }

                Method method = ClassUtil.getMethod(clazz, methodName, paramTypes);
                return method.invoke(instance, args);
            }
        } catch (InvocationTargetException | IllegalAccessException var7) {
            throw new CommonRuntimeException(var7);
        }
    }

    public static Object invokeNoArgsMethod(Object instance, Method method) {
        if (ObjectUtil.isNull(method)) {
            return null;
        } else {
            String methodName = method.getName();
            Class<?>[] paramTypes = method.getParameterTypes();
            if (ArrayUtil.isNotEmpty(paramTypes)) {
                throw new CommonRuntimeException(methodName + " must be has no params.");
            } else {
                return invoke(instance, method);
            }
        }
    }

    public static Object invokeNoArgsMethod(Object instance, String methodName) {
        ArgUtil.notNull(instance, "instance");
        Class clazz = instance.getClass();
        Method method = ClassUtil.getMethod(clazz, methodName, new Class[0]);
        return invokeNoArgsMethod(instance, method);
    }

    public static Object invokeFactoryMethod(Class clazz, Method factoryMethod) {
        ArgUtil.notNull(clazz, "clazz");
        ArgUtil.notNull(factoryMethod, "factoryMethod");
        String methodName = factoryMethod.getName();
        Class<?>[] paramTypes = factoryMethod.getParameterTypes();
        if (ArrayUtil.isNotEmpty(paramTypes)) {
            throw new CommonRuntimeException(methodName + " must be has no params.");
        } else if (!Modifier.isStatic(factoryMethod.getModifiers())) {
            throw new CommonRuntimeException(methodName + " must be static.");
        } else {
            Class returnType = factoryMethod.getReturnType();
            if (!returnType.isAssignableFrom(clazz)) {
                throw new CommonRuntimeException(methodName + " must be return " + returnType.getName());
            } else {
                return invoke((Object)null, (Method)factoryMethod);
            }
        }
    }

    public static Class getGenericReturnParamType(Method method, int paramIndex) {
        ArgUtil.notNull(method, "method");
        ArgUtil.notNegative(paramIndex, "paramIndex");
        Type returnType = method.getGenericReturnType();
        return ObjectUtil.isNull(returnType) ? null : TypeUtil.getGenericParamType(returnType, paramIndex);
    }

    public static void invokeSetterMethod(Object instance, String propertyName, Object value) {
        ArgUtil.notNull(instance, "instance");
        ArgUtil.notNull(propertyName, "propertyName");
        if (!ObjectUtil.isNull(value)) {
            Class<?> clazz = instance.getClass();
            String setMethodName = buildSetMethodName(propertyName);
            Class paramType = value.getClass();

            try {
                Method method = clazz.getMethod(setMethodName, paramType);
                method.invoke(instance, value);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException var7) {
                throw new CommonRuntimeException(var7);
            }
        }
    }

    public static Object invokeGetterMethod(Object instance, String fieldName, Class fieldType) {
        ArgUtil.notNull(instance, "instance");
        ArgUtil.notNull(fieldType, "fieldType");
        ArgUtil.notEmpty(fieldName, "fieldName");
        Class<?> clazz = instance.getClass();
        String getMethodName = buildGetMethodName(fieldType, fieldName);

        try {
            Method method = clazz.getMethod(getMethodName);
            return method.invoke(instance);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException var6) {
            throw new CommonRuntimeException(var6);
        }
    }

    public static Object invokeGetterMethod(Object instance, String fieldName) {
        return invokeGetterMethod(instance, fieldName, String.class);
    }

    public static Object invokeGetterMethod(Object instance, Field field) {
        Class<?> fieldType = field.getType();
        String fieldName = field.getName();
        return invokeGetterMethod(instance, fieldName, fieldType);
    }

    public static String buildSetMethodName(String propertyName) {
        ArgUtil.notEmpty(propertyName, "propertyName");
        return "set" + StringUtil.firstToUpperCase(propertyName);
    }

    public static String buildGetMethodName(Class fieldType, String propertyName) {
        ArgUtil.notNull(fieldType, "fieldType");
        ArgUtil.notEmpty(propertyName, "propertyName");
        return Boolean.TYPE.equals(fieldType) ? "is" + StringUtil.firstToUpperCase(propertyName) : "get" + StringUtil.firstToUpperCase(propertyName);
    }

    public static String buildGetMethodName(String propertyName) {
        return buildGetMethodName(String.class, propertyName);
    }

    static {
        Set<String> methodNameSet = new HashSet(64);
        Method[] var1 = Object.class.getMethods();
        int var2 = var1.length;

        int var3;
        Method method;
        for(var3 = 0; var3 < var2; ++var3) {
            method = var1[var3];
            methodNameSet.add(method.getName());
        }

        var1 = Class.class.getMethods();
        var2 = var1.length;

        for(var3 = 0; var3 < var2; ++var3) {
            method = var1[var3];
            methodNameSet.add(method.getName());
        }

        IGNORE_METHOD_LIST = new ArrayList(methodNameSet);
    }
}
