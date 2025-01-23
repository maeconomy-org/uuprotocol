package io.recheck.uuidprotocol.common.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

public class ReflectionUtils {

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

}
