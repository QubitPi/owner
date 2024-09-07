/*
 * Copyright (c) 2012-2015, Luigi R. Viggiano
 * All rights reserved.
 *
 * This software is distributable under the BSD license.
 * See the terms of the BSD license in the documentation provided with this software.
 */

package io.github.qubitpi.owner.typeconversion.arrays;

import io.github.qubitpi.owner.Tokenizer;

/**
 * @author Luigi R. Viggiano
 */
public class CustomDashTokenizer implements Tokenizer {
    public String[] tokens(String values) {
        return values.split("-", -1);
    }
}
