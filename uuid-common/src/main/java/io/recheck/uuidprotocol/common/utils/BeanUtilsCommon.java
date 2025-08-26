package io.recheck.uuidprotocol.common.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class BeanUtilsCommon {

    public static void copyMatchingPropertiesDeep(Object source, Object target) {
        copyMatchingPropertiesDeep(source, target, new IdentityHashMap<>());
    }

    private static void copyMatchingPropertiesDeep(Object source, Object target,
                                                   Map<Object, Object> visited) {
        if (source == null || target == null) {
            return;
        }

        // 🚨 Cycle detection
        if (visited.containsKey(source)) {
            return; // already copied this source object
        }
        visited.put(source, target);

        Set<String> sourceProps = getPropertyNames(source);
        Set<String> targetProps = getPropertyNames(target);

        // only properties present in both
        Set<String> common = new HashSet<>(sourceProps);
        common.retainAll(targetProps);

        for (String prop : common) {
            try {
                BeanWrapper srcWrap = new BeanWrapperImpl(source);
                BeanWrapper tgtWrap = new BeanWrapperImpl(target);

                Object srcVal = srcWrap.getPropertyValue(prop);
                Class<?> propType = tgtWrap.getPropertyType(prop);

                if (isSimpleValueType(propType)) {
                    // ✅ Direct assign for simple values
                    tgtWrap.setPropertyValue(prop, srcVal);

                } else if (Collection.class.isAssignableFrom(propType)) {
                // ✅ Handle Collections
                Collection<?> srcCol = (Collection<?>) srcVal;
                Collection<Object> tgtCol = (Collection<Object>) tgtWrap.getPropertyValue(prop);

                if (tgtCol == null) {
                    tgtCol = instantiateCollection(propType);
                    tgtWrap.setPropertyValue(prop, tgtCol);
                } else {
                    tgtCol.clear();
                }

                // Try to resolve element type from target property
                Class<?> elemType = tgtWrap.getPropertyTypeDescriptor(prop).getResolvableType().getGeneric(0).resolve(Object.class);

                for (Object item : srcCol) {
                    if (item == null) continue;
                    if (isSimpleValueType(item.getClass())) {
                        tgtCol.add(item);
                    } else {
                        Object tgtItem = visited.containsKey(item)
                                ? visited.get(item)
                                : BeanUtils.instantiateClass(elemType);
                        copyMatchingPropertiesDeep(item, tgtItem, visited);
                        tgtCol.add(tgtItem);
                    }
                }

            } else if (Map.class.isAssignableFrom(propType)) {
                // ✅ Handle Maps
                Map<?, ?> srcMap = (Map<?, ?>) srcVal;
                Map<Object, Object> tgtMap = (Map<Object, Object>) tgtWrap.getPropertyValue(prop);

                if (tgtMap == null) {
                    tgtMap = instantiateMap(propType);
                    tgtWrap.setPropertyValue(prop, tgtMap);
                } else {
                    tgtMap.clear();
                }

                // Resolve key and value types
                Class<?> keyType = tgtWrap.getPropertyTypeDescriptor(prop).getResolvableType().getGeneric(0).resolve(Object.class);
                Class<?> valType = tgtWrap.getPropertyTypeDescriptor(prop).getResolvableType().getGeneric(1).resolve(Object.class);

                for (Map.Entry<?, ?> entry : srcMap.entrySet()) {
                    Object key = entry.getKey();
                    Object val = entry.getValue();

                    if (val == null) continue;

                    Object tgtKey = key; // usually keys are simple
                    Object tgtVal;
                    if (isSimpleValueType(val.getClass())) {
                        tgtVal = val;
                    } else {
                        tgtVal = visited.containsKey(val)
                                ? visited.get(val)
                                : BeanUtils.instantiateClass(valType);
                        copyMatchingPropertiesDeep(val, tgtVal, visited);
                    }
                    tgtMap.put(tgtKey, tgtVal);
                }

            } else if (propType.isArray()) {
                // ✅ Handle Arrays
                int length = Array.getLength(srcVal);
                Class<?> compType = propType.getComponentType();

                Object tgtArray = Array.newInstance(compType, length);

                for (int i = 0; i < length; i++) {
                    Object srcElem = Array.get(srcVal, i);
                    if (srcElem == null) {
                        Array.set(tgtArray, i, null);
                        continue;
                    }

                    if (isSimpleValueType(srcElem.getClass())) {
                        Array.set(tgtArray, i, srcElem);
                    } else {
                        Object tgtElem = visited.containsKey(srcElem)
                                ? visited.get(srcElem)
                                : BeanUtils.instantiateClass(compType);
                        copyMatchingPropertiesDeep(srcElem, tgtElem, visited);
                        Array.set(tgtArray, i, tgtElem);
                    }
                }

                tgtWrap.setPropertyValue(prop, tgtArray);
            } else {
                    // ✅ Nested bean
                    Object tgtVal = tgtWrap.getPropertyValue(prop);
                    if (tgtVal == null) {
                        if (!propType.isInterface() && !Modifier.isAbstract(propType.getModifiers())) {
                            tgtVal = BeanUtils.instantiateClass(propType);
                            tgtWrap.setPropertyValue(prop, tgtVal);
                        } else {
                            continue; // cannot instantiate
                        }
                    }
                    copyMatchingPropertiesDeep(srcVal, tgtVal, visited);
                }

            } catch (Exception e) {
                // ignore and continue with next property
            }
        }
    }

    private static Set<String> getPropertyNames(Object bean) {
        BeanWrapper w = new BeanWrapperImpl(bean);
        return Arrays.stream(w.getPropertyDescriptors())
                .map(PropertyDescriptor::getName)
                .filter(p -> !"class".equals(p))
                .collect(Collectors.toSet());
    }

    private static boolean isSimpleValueType(Class<?> clazz) {
        return BeanUtils.isSimpleValueType(clazz);
    }

    private static Collection<Object> instantiateCollection(Class<?> type) {
        if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
            if (List.class.isAssignableFrom(type)) return new ArrayList<>();
            if (Set.class.isAssignableFrom(type)) return new HashSet<>();
            return new ArrayList<>(); // default
        }
        return (Collection<Object>) BeanUtils.instantiateClass(type);
    }

    private static Map<Object, Object> instantiateMap(Class<?> type) {
        if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
            if (SortedMap.class.isAssignableFrom(type)) return new TreeMap<>();
            return new HashMap<>(); // default
        }
        return (Map<Object, Object>) BeanUtils.instantiateClass(type);
    }
}
