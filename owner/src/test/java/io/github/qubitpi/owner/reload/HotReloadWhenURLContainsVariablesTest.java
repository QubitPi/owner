/*
 * Copyright (c) 2012-2015, Luigi R. Viggiano
 * All rights reserved.
 *
 * This software is distributable under the BSD license.
 * See the terms of the BSD license in the documentation provided with this software.
 */

package io.github.qubitpi.owner.reload;

import io.github.qubitpi.owner.Config.HotReload;
import io.github.qubitpi.owner.Config.Sources;
import io.github.qubitpi.owner.ConfigFactory;
import io.github.qubitpi.owner.Reloadable;
import io.github.qubitpi.owner.TestConstants;
import io.github.qubitpi.owner.VariablesExpanderForTest;
import io.github.qubitpi.owner.event.ReloadEvent;
import io.github.qubitpi.owner.event.ReloadListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Properties;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static io.github.qubitpi.owner.Config.HotReloadType.ASYNC;
import static io.github.qubitpi.owner.util.UtilTest.fileFromURI;
import static io.github.qubitpi.owner.util.UtilTest.save;
import static org.junit.Assert.assertEquals;

/**
 * @author Luigi R. Viggiano
 */
public class HotReloadWhenURLContainsVariablesTest extends AsyncReloadSupport implements TestConstants {
    private static final String SPEC = "file:${user.dir}/" + RESOURCES_DIR + "/AutoReloadExample.properties";
    private static File target;

    @Sources(SPEC)
    @HotReload(value=10, unit = MILLISECONDS, type = ASYNC)
    interface AutoReloadConfig extends Reloadable {
        @DefaultValue("5")
        Integer someValue();
    }

    @Before
    public void before() throws Throwable {

        // here I need to expand SPEC manually to create the file for the test
        String spec = new VariablesExpanderForTest(new Properties()).expand(SPEC);

        target = fileFromURI(spec);
        save(target, new Properties() {{
            setProperty("someValue", "10");
        }});

        // 1-Jan-1970 (so, the file it's old enough to need reload
        target.setLastModified(0);
    }

    @Test
    public void testReloadWorksWhenURLContainsVariablesToExpand()
            throws Throwable {

        AutoReloadConfig cfg = ConfigFactory.create(AutoReloadConfig.class);
        cfg.addReloadListener(new ReloadListener() {
            public void reloadPerformed(ReloadEvent event) {
                notifyReload();
            }
        });

        assertEquals(new Integer(10), cfg.someValue());

        save(target, new Properties() {{
            setProperty("someValue", "20");
        }});

        waitForReload(1000);

        assertEquals(new Integer(20), cfg.someValue());

    }

    @After
    public void after() throws Throwable {
        target.delete();
    }

}
