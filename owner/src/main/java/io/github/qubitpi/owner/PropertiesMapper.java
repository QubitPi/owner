/*
 * Copyright (c) 2012-2015, Luigi R. Viggiano
 * All rights reserved.
 *
 * This software is distributable under the BSD license.
 * See the terms of the BSD license in the documentation provided with this software.
 */

package io.github.qubitpi.owner;

import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Maps methods to properties keys and defaultValues. Maps a class to default property values.
 *
 * @author Luigi R. Viggiano
 */
final class PropertiesMapper {

    /** Don't let anyone instantiate this class */
    private PropertiesMapper() {}

    static boolean isEncryptedValue( Method method ) {
        return ( method.getAnnotation( Config.EncryptedValue.class ) ) != null;
    }

    static String key(Method method) {
        Config.Key key = method.getAnnotation(Config.Key.class);
        return (key == null) ? method.getName() : key.value();
    }

    static String defaultValue(Method method) {
        Config.DefaultValue defaultValue = method.getAnnotation(Config.DefaultValue.class);
        return defaultValue != null ? defaultValue.value() : null;
    }

    static void defaults(Properties properties, Class<? extends Config> clazz) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            String key = key(method);
            String value = defaultValue(method);
            if (value != null)
                properties.put(key, value);
        }
    }

}
