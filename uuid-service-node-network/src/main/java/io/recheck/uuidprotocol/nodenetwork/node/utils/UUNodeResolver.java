package io.recheck.uuidprotocol.nodenetwork.node.utils;

import io.recheck.uuidprotocol.common.utils.ReflectionUtils;
import io.recheck.uuidprotocol.domain.node.model.Node;
import io.recheck.uuidprotocol.domain.registrar.model.UUIDRecord;
import io.recheck.uuidprotocol.nodenetwork.node.persistence.NodeDataSource;
import io.recheck.uuidprotocol.nodenetwork.registrar.UUIDRegistrarService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class UUNodeResolver {

    private final UUIDRegistrarService uuidRegistrarService;
    private final ApplicationContext applicationContext;

    @SneakyThrows
    public <TNode extends Node> Class<TNode> getNodeClassTypeOfUUID(String uuid) {
        UUIDRecord uuidRecord = uuidRegistrarService.findByUUID(uuid);
        if (uuidRecord == null || !StringUtils.hasText(uuidRecord.getUuidRecordMeta().getNodeType())) {
            throw new IllegalArgumentException("No Node Type found for uuid: " + uuid);
        }
        return (Class<TNode>) Class.forName("io.recheck.uuidprotocol.domain.node.model."+uuidRecord.getUuidRecordMeta().getNodeType());
    }

    public <TNode extends Node> NodeDataSource<TNode> getNodeDataSourceForNodeType(Class<TNode> nodeType) {
        Map<String, NodeDataSource> beans = applicationContext.getBeansOfType(NodeDataSource.class);

        for (NodeDataSource<?> bean : beans.values()) {
            Class<?> actualClass = AopUtils.getTargetClass(bean); // handles Spring proxies

            if (ReflectionUtils.isMatchingGenericType(actualClass, nodeType)) {
                //noinspection unchecked
                return (NodeDataSource<TNode>) bean;
            }
        }

        throw new IllegalArgumentException("No NodeDataSource found for node type: " + nodeType);
    }
}