/*
 * Copyright (c) 2012-2015, Luigi R. Viggiano
 * All rights reserved.
 *
 * This software is distributable under the BSD license.
 * See the terms of the BSD license in the documentation provided with this software.
 */
package io.github.qubitpi.owner.java8;

import io.github.qubitpi.owner.Config;

/**
 * @author Luigi R. Viggiano
 */
public interface ConfigWithJava8Features extends Config {

    @DefaultValue("100")
    int oneHundred();

    default Integer sum(Integer a, Integer b) {
        return Integer.sum(a, b);
    }

    static int min(int a, int b) {
        return Integer.min(a, b);
    }
}
