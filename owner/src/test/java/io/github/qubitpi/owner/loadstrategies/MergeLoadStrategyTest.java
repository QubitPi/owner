/*
 * Copyright (c) 2012-2015, Luigi R. Viggiano
 * All rights reserved.
 *
 * This software is distributable under the BSD license.
 * See the terms of the BSD license in the documentation provided with this software.
 */

package io.github.qubitpi.owner.loadstrategies;

import static io.github.qubitpi.owner.Config.LoadType.MERGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import io.github.qubitpi.owner.Config;
import io.github.qubitpi.owner.Config.LoadPolicy;
import io.github.qubitpi.owner.Config.Sources;
import io.github.qubitpi.owner.ConfigFactory;
import org.junit.Test;

/**
 * @author Luigi R. Viggiano
 */
public class MergeLoadStrategyTest {
    @Test
    public void testPropertyMerge() {
        MergeConfig cfg = ConfigFactory.create(MergeConfig.class);
        assertEquals("first", cfg.foo());
        assertEquals("second", cfg.bar());
        assertEquals("first", cfg.foo());
        assertEquals("third", cfg.qux());
        assertNull(cfg.quux());
        assertEquals("theDefaultValue", cfg.fubar());
    }

    @Sources({"classpath:io/github/qubitpi/owner/first.properties",
              "classpath:foo/bar/thisDoesntExists.properties",
              "classpath:io/github/qubitpi/owner/second.properties",
              "file:${user.dir}/src/test/resources/foo/bar/thisDoesntExists.properties",
              "file:${user.dir}/src/test/resources/io/github/qubitpi/owner/third.properties"})
    @LoadPolicy(MERGE)
    public static interface MergeConfig extends Config {
        @DefaultValue("this should be ignored")
        String foo();
        @DefaultValue("this should be ignored")
        String bar();
        @DefaultValue("this should be ignored")
        String baz();
        @DefaultValue("this should be ignored")
        String qux();

        String quux(); // this should return null;
        @DefaultValue("theDefaultValue")
        String fubar();
    }

    @Sources("httpz://foo.bar.baz")
    @LoadPolicy(MERGE)
    interface InvalidURLConfig extends Config {

    }

    @Test(expected = UnsupportedOperationException.class)
    public void testWhenURLIsInvalid() {
        ConfigFactory.create(InvalidURLConfig.class);
    }

}
