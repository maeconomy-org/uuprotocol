package io.recheck.uuidprotocol.common.utils;

import lombok.SneakyThrows;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class ReflectionUtils {

    public static List<List<String>> getFieldCombinations(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<String> fieldNames = Arrays.stream(fields)
                .map(Field::getName)
                .toList();

        List<List<String>> result = new ArrayList<>();
        int n = fieldNames.size();

        // Generate all subsets (including empty)
        for (int mask = 0; mask < (1 << n); mask++) {
            List<String> combination = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    combination.add(fieldNames.get(i));
                }
            }
            result.add(combination);
        }

        return result;
    }

    @SneakyThrows
    public static List<Field> getNonNullFields(Object o) {
        List<Field> nonNullFields = new ArrayList<>();

        List<Field> allFields = ReflectionUtils.getAllFields(o.getClass());
        for (Field field : allFields) {
            Object value = field.get(o);
            if (value != null) { //filter by all fields that has non empty value
                if (value instanceof String) {
                    if (!StringUtils.hasText((CharSequence) value)) {
                        continue;
                    }
                }
                nonNullFields.add(field);
            }
        }
        return nonNullFields;
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
