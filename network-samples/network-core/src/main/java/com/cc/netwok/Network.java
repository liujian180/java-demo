package com.cc.netwok;

/**
 * 网络组件：所有网络相关实例根接口
 * wcc 2022/4/26
 */
public interface Network {
    /**
     * ID唯一标识
     *
     * @return ID
     */
    String getId();

    /**
     * @return 网络类型
     * @see DefaultNetworkType
     */
    NetworkType getType();

    /**
     * 关闭网络组件
     */
    void shutdown();

    /**
     * @return 是否存活
     */
    boolean isAlive();

    /**
     * 当{@link Network#isAlive()}为false是,是否自动重新加载.
     *
     * @return 是否重新加载
     * @see NetworkProvider#reload(Network, Object)
     */
    boolean isAutoReload();

    /**
     * @return 最后一次错误信息
     */
    default String getLastError() {
        return null;
    }
}
