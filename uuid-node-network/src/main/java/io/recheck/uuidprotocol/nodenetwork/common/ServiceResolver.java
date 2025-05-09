package io.recheck.uuidprotocol.nodenetwork.common;

import io.recheck.uuidprotocol.nodenetwork.datasource.NodeDataSource;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

@Component
public class ServiceResolver {

    @Autowired
    private ApplicationContext applicationContext;

    public NodeDataSource<?> getServiceForType(Class<?> targetType) {
        Map<String, NodeDataSource> beans = applicationContext.getBeansOfType(NodeDataSource.class);

        for (NodeDataSource<?> bean : beans.values()) {
            Class<?> actualClass = AopUtils.getTargetClass(bean); // handles Spring proxies

            if (isMatchingGenericType(actualClass, targetType)) {
                //noinspection unchecked
                return bean;
            }
        }

        throw new IllegalArgumentException("No GenericService found for type: " + targetType);
    }

    private boolean isMatchingGenericType(Class<?> clazz, Class<?> targetType) {
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

    private boolean typeMatches(Type genericType, Class<?> targetType) {
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