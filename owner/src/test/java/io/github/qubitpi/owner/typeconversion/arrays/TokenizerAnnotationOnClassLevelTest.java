/*
 * Copyright (c) 2012-2015, Luigi R. Viggiano
 * All rights reserved.
 *
 * This software is distributable under the BSD license.
 * See the terms of the BSD license in the documentation provided with this software.
 */

package io.github.qubitpi.owner.typeconversion.arrays;

import io.github.qubitpi.owner.Config;
import io.github.qubitpi.owner.Config.TokenizerClass;
import io.github.qubitpi.owner.ConfigFactory;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Luigi R. Viggiano
 */
public class TokenizerAnnotationOnClassLevelTest {

    private ArrayConfigWithTokenizerAnnotationOnClassLevel cfg;

    @Before
    public void before() {
        cfg = ConfigFactory.create(ArrayConfigWithTokenizerAnnotationOnClassLevel.class);
    }

    @TokenizerClass(CustomDashTokenizer.class)
    public static interface ArrayConfigWithTokenizerAnnotationOnClassLevel extends Config {

        @TokenizerClass(CustomCommaTokenizer.class) //should override the class-level @TokenizerClass
        @DefaultValue("1,2,3,4")
        public int[] commaSeparated();

        @Separator(";")  // overrides class level @TokenizerClass
        @DefaultValue("1; 2; 3; 4")
        public int[] semicolonSeparated();

        @DefaultValue("1-2-3-4")
        public int[] dashSeparated(); // class level @TokenizerClass applies
    }

    @Test
    public void testTokenizerClassAnnotationOnMethodLevelOverridingTokenizerClassAnnotationOnClassLevel() {
        assertThat(cfg.commaSeparated(), is(new int[]{1, 2, 3, 4}));
    }

    @Test
    public void testSeparatorAnnotationOnMethodLevelOverridingTokenizerClassAnnotationOnClassLevel() {
        assertThat(cfg.semicolonSeparated(), is(new int[]{1, 2, 3, 4}));
    }

    @Test
    public void testTokenizerClassAnnotationOnClassLevelAndNoOverridingOnMethodLevel() {
        assertThat(cfg.dashSeparated(), is(new int[]{1, 2, 3, 4}));
    }

}
