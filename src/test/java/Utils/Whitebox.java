package Utils;

import hudson.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Whitebox {

    public static void setInternalState(Object targetClass, String fieldName, Object argValue) {
        Class<?> aClass = targetClass instanceof Class ? (Class) targetClass : targetClass.getClass();
        Field field = ReflectionUtils.findField(aClass, fieldName);
        field.setAccessible(true);
        ReflectionUtils.setField(field, targetClass, argValue);
    }

    public static <T> T getInternalState(Object targetClass, String fieldName) {
        Class<?> aClass = targetClass instanceof Class ? (Class) targetClass : targetClass.getClass();
        Field field = ReflectionUtils.findField(aClass, fieldName);
        field.setAccessible(true);
        return (T) ReflectionUtils.getField(field, targetClass);
    }

    public static <T> T invokeMethod(Object targetClass, String methodName) throws Exception {
        Class<?> aClass = targetClass instanceof Class ? (Class) targetClass : targetClass.getClass();
        Method method = ReflectionUtils.findMethod(aClass, methodName);
        method.setAccessible(true);

        try {
            if (Modifier.isStatic(method.getModifiers())) {
                return (T) method.invoke(null, new Object[0]);
            }
            return (T) method.invoke(targetClass, new Object[0]);
        } catch (InvocationTargetException e) {
            handleInvocationTargetException(e);
        }
        return null;
    }

    public static <T> T invokeMethod(Object targetClass, String methodName, Object... args) throws Exception {
        return invokeMethod(targetClass, methodName, getTypes(args), args);
    }

    public static <T> T invokeMethod(Object targetClass, String methodName, Class type, Object arg) throws Exception {
        Class<?> aClass = targetClass instanceof Class ? (Class) targetClass : targetClass.getClass();
        Method method = ReflectionUtils.findMethod(aClass, methodName, type);
        method.setAccessible(true);
        try {
            if (Modifier.isStatic(method.getModifiers())) {
                return (T) method.invoke(null, arg);
            }
            return (T) method.invoke(targetClass, arg);
        } catch (InvocationTargetException e) {
            handleInvocationTargetException(e);
        }
        return null;
    }

    public static <T> T invokeMethod(Object targetClass, String methodName, Class[] types, Object... args) throws
            Exception {
        Class<?> aClass = targetClass instanceof Class ? (Class) targetClass : targetClass.getClass();
        Method method = ReflectionUtils.findMethod(aClass, methodName, types);
        method.setAccessible(true);
        try {
            if (Modifier.isStatic(method.getModifiers())) {
                return (T) method.invoke(null, args);
            }
            return (T) method.invoke(targetClass, args);
        } catch (InvocationTargetException e) {
            handleInvocationTargetException(e);
        }
        return null;
    }

    private static Class<?>[] getTypes(Object[] arguments) {
        Class<?>[] classes = new Class<?>[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            classes[i] = getType(arguments[i]);
        }
        return classes;
    }

    private static Class<?> getType(Object object) {
        Class<?> type = null;
        if (object instanceof Class<?>) {
            type = (Class<?>) object;
        } else if (object != null) {
            type = object.getClass();
        }
        return type;
    }

    private static void handleInvocationTargetException(InvocationTargetException ex) throws Exception {
        Throwable targetException = ex.getCause();
        if (targetException instanceof Exception) {
            throw (Exception) targetException;
        }
        if (targetException instanceof Error) {
            throw (Error) targetException;
        }
        throw ex;
    }
}
