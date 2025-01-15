package org.jrl.tools.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * SPI帮助类
 *
 * @author JerryLong
 */
public class JrlSpiUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(JrlSpiUtil.class);

    private static Map<Class<?>, Object> objectInstanceMap = new ConcurrentHashMap<>();

    /**
     * 获取SPI实例，如果重复，则按加载顺序获取第一个
     *
     * @param clazz 类型
     * @param def   默认实现
     * @param <T>   泛型
     * @return 实例
     */
    public static <T> T getInstanceOrDefault(Class<T> clazz, Supplier<T> def) {
        try {
            final Iterator<? extends T> iterator = ServiceLoader.load(clazz).iterator();
            if (iterator.hasNext()) {
                final T next = iterator.next();
                return (T) objectInstanceMap.computeIfAbsent(next.getClass(), e -> next);
            } else {
                return def.get();
            }
        } catch (Throwable e) {
            LOGGER.error(" SdkSpiUtil fail to load spi , class : {}", clazz.getSimpleName(), e);
            return def.get();
        }
    }

    /**
     * 获取SPI实例，如果找不到则抛异常
     *
     * @param clazz 类型
     * @param <T>   泛型
     * @return 实例
     */
    public static <T> T getInstance(Class<T> clazz) {
        final Iterator<? extends T> iterator = ServiceLoader.load(clazz).iterator();
        if (iterator.hasNext()) {
            final T next = iterator.next();
            return (T) objectInstanceMap.computeIfAbsent(next.getClass(), e -> next);
        } else {
            throw new RuntimeException(" SdkSpiUtil fail to load spi , class : " + clazz.getSimpleName());
        }
    }
}