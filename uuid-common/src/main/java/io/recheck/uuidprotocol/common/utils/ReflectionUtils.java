package io.recheck.uuidprotocol.common.utils;

import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class ReflectionUtils {

    @SneakyThrows
    public static <T> T getValueAnnotationPresent(Class<? extends Annotation> annotationClass, Object object) {
        Optional<Field> optionalField = ReflectionUtils.findAnnotationPresent(annotationClass, object.getClass());
        if (optionalField.isPresent()) {
            return (T) optionalField.get().get(object);
        }
        return null;
    }

    @SneakyThrows
    public static void setValueAnnotationPresent(Class<? extends Annotation> annotationClass, Object object, String value) {
        Optional<Field> idFieldOptional = ReflectionUtils.findAnnotationPresent(annotationClass, object.getClass());
        if (idFieldOptional.isPresent()) {
            idFieldOptional.get().set(object, value);
        }
    }


    public static Optional<Field> findAnnotationPresent(Class<? extends Annotation> annotationClass, Class<?> clazz) {
        return getAllFields(clazz).stream().filter(field -> field.isAnnotationPresent(annotationClass)).findFirst();
    }

    public static List<Field> getAllFields(Class clazz) {
        if (clazz == null) {
            return Collections.emptyList();
        }

        List<Field> result = new ArrayList<>(getAllFields(clazz.getSuperclass()));
        List<Field> fields = Arrays.stream(clazz.getDeclaredFields())
                .peek(field -> field.setAccessible(true))
                .toList();
        result.addAll(fields);
        return result;
    }

    public static boolean isMatchingGenericType(Class<?> clazz, Class<?> targetType) {
        // Traverse class hierarchy
        while (clazz != null && clazz != Object.class) {
            // Check superclass
            Type genericSuperclass = clazz.getGenericSuperclass();
            if (typeMatches(genericSuperclass, targetType)) return true;

            // Check interfaces
            for (Type genericInterface : clazz.getGenericInterfaces()) {
                if (typeMatches(genericInterface, targetType)) return true;
            }

            clazz = clazz.getSuperclass();
        }
        return false;
    }

    private static boolean typeMatches(Type genericType, Class<?> targetType) {
        if (!(genericType instanceof ParameterizedType pt)) return false;

        Type[] typeArgs = pt.getActualTypeArguments();
        if (typeArgs.length == 0) return false;

        Type actual = typeArgs[0];

        if (actual instanceof Class<?> actualClass) {
            return actualClass.equals(targetType);
        } else if (actual instanceof ParameterizedType parameterizedType) {
            Type rawType = parameterizedType.getRawType();
            return rawType instanceof Class<?> && rawType.equals(targetType);
        }
        return false;
    }

}
