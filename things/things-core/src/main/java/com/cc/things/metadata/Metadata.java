package com.cc.things.metadata;

import java.util.Map;
import java.util.Optional;

/**
 * 物 元数据模型
 * wcc 2022/6/4
 */
public interface Metadata {
    /**
     * 唯一标识别
     *
     * @return ID
     */
    String getId();

    /**
     * 名称
     *
     * @return 名称
     */
    String getName();

    /**
     * 说明
     *
     * @return 说明
     */
    String getDescription();

    /**
     * @return 其他拓展配置
     */
    Map<String, Object> getExpands();

    /**
     * 根据key获取拓展配置值
     *
     * @param key key
     * @return 拓展配置值
     */
    default Optional<Object> getExpand(String key) {
        return Optional.ofNullable(getExpands())
                .map(map -> map.get(key));
    }

    /**
     * 修改拓展配置
     *
     * @param expands 拓展配置
     */
    default void setExpands(Map<String, Object> expands) {
    }

    /**
     * 修改名称
     *
     * @param name 新的名称
     */
    default void setName(String name) {

    }

    /**
     * 修改说明
     *
     * @param description 说明
     */
    default void setDescription(String description) {

    }
}
