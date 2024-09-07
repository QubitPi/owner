/*
 * Copyright (c) 2012-2015, Luigi R. Viggiano
 * All rights reserved.
 *
 * This software is distributable under the BSD license.
 * See the terms of the BSD license in the documentation provided with this software.
 */

package io.github.qubitpi.owner.typeconversion.arrays;

import io.github.qubitpi.owner.Config;
import io.github.qubitpi.owner.ConfigFactory;
import io.github.qubitpi.owner.Tokenizer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Luigi R. Viggiano
 */
public class InvalidAnnotationTest {
    private InvalidAnnotationConfig cfg;

    @Before
    public void before() {
        cfg = ConfigFactory.create(InvalidAnnotationConfig.class);
    }

    public static interface InvalidAnnotationConfig extends Config {
        // it throws an exception since the Tokenizer class is declared as private
        @TokenizerClass(NonInstantiableTokenizer.class)
        @DefaultValue("1,2,3")
        public int[] nonInstantiableTokenizer();
    }

    // it's private, it cannot be instantiated by the OWNER library
    private static class NonInstantiableTokenizer extends CustomCommaTokenizer implements Tokenizer {
    }

    @Test
    public void testNonInstantiableTokenizer() throws Exception {
        try {
            cfg.nonInstantiableTokenizer();
            fail("UnsupportedOperationException expected");
        } catch (UnsupportedOperationException ex) {
            // since NonInstantiableTokenizer is private and IllegalAccessException is expected.
            assertTrue(ex.getCause() instanceof IllegalAccessException);
        }
    }
}
