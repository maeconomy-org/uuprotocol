package io.recheck.uuidprotocol.nodenetwork.node.utils;

import io.recheck.uuidprotocol.common.utils.ReflectionUtils;
import io.recheck.uuidprotocol.domain.node.model.Node;
import io.recheck.uuidprotocol.domain.owner.model.UUIDOwner;
import io.recheck.uuidprotocol.nodenetwork.node.persistence.NodeDataSource;
import io.recheck.uuidprotocol.nodenetwork.owner.UUIDOwnerService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ClassResolver {

    private final UUIDOwnerService uuidOwnerService;
    private final ApplicationContext applicationContext;

    @SneakyThrows
    public <TNode extends Node> Class<TNode> getNodeClassTypeOfUUID(String uuid) {
        UUIDOwner uuidOwner = uuidOwnerService.findByUUID(uuid);
        if (uuidOwner == null || !StringUtils.hasText(uuidOwner.getNodeType())) {
            throw new IllegalArgumentException("No Node Type found for uuid: " + uuid);
        }
        return (Class<TNode>) Class.forName("io.recheck.uuidprotocol.domain.node.model."+uuidOwner.getNodeType());
    }

    public <TNode extends Node> NodeDataSource<TNode> getNodeDataSourceForType(Class<TNode> targetType) {
        Map<String, NodeDataSource> beans = applicationContext.getBeansOfType(NodeDataSource.class);

        for (NodeDataSource<?> bean : beans.values()) {
            Class<?> actualClass = AopUtils.getTargetClass(bean); // handles Spring proxies

            if (ReflectionUtils.isMatchingGenericType(actualClass, targetType)) {
                //noinspection unchecked
                return (NodeDataSource<TNode>) bean;
            }
        }

        throw new IllegalArgumentException("No GenericService found for type: " + targetType);
    }
}