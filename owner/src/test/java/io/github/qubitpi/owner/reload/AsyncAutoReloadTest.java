/*
 * Copyright (c) 2012-2015, Luigi R. Viggiano
 * All rights reserved.
 *
 * This software is distributable under the BSD license.
 * See the terms of the BSD license in the documentation provided with this software.
 */

package io.github.qubitpi.owner.reload;

import io.github.qubitpi.owner.Config;
import io.github.qubitpi.owner.Config.HotReload;
import io.github.qubitpi.owner.Config.Sources;
import io.github.qubitpi.owner.ConfigFactory;
import io.github.qubitpi.owner.Reloadable;
import io.github.qubitpi.owner.TestConstants;
import io.github.qubitpi.owner.event.ReloadEvent;
import io.github.qubitpi.owner.event.ReloadListener;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Properties;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static io.github.qubitpi.owner.Config.HotReloadType.ASYNC;
import static io.github.qubitpi.owner.util.UtilTest.delete;
import static io.github.qubitpi.owner.util.UtilTest.fileFromURI;
import static io.github.qubitpi.owner.util.UtilTest.save;
import static org.junit.Assert.assertEquals;

/**
 * @author Luigi R. Viggiano
 */
public class AsyncAutoReloadTest extends AsyncReloadSupport implements TestConstants {
    private static final String PROPERTY_FILE_NAME = "AsyncAutoReloadConfig.properties";
    private static final int DELAY = 1000;

    private static final String SPEC = "file:"+ RESOURCES_DIR + "/" + PROPERTY_FILE_NAME;

    private static File target;

    @BeforeClass
    public static void beforeClass() throws URISyntaxException {
        target = fileFromURI(SPEC);
    }

    @Sources(SPEC)
    @HotReload(value=10, unit = MILLISECONDS, type = ASYNC)
    interface AsyncAutoReloadConfig extends Config, Reloadable {
        @DefaultValue("5")
        Integer someValue();
    }

    @Test
    public void testReload() throws Throwable {

        save(target, new Properties() {{
            setProperty("someValue", "10");
        }});

        AsyncAutoReloadConfig cfg = ConfigFactory.create(AsyncAutoReloadConfig.class);
        final int[] reloadCount = {0};
        cfg.addReloadListener(new ReloadListener() {
            public void reloadPerformed(ReloadEvent event) {
                reloadCount[0]++;
            }
        });
        cfg.addReloadListener(new ReloadListener() {
            public void reloadPerformed(ReloadEvent event) {
                notifyReload();
            }
        });

        assertEquals(Integer.valueOf(10), cfg.someValue());

        assertEquals(0, reloadCount[0]);
        assertEquals(Integer.valueOf(10), cfg.someValue());

        delete(target);
        waitForReload(DELAY);

        assertEquals(1, reloadCount[0]);
        assertEquals(Integer.valueOf(5), cfg.someValue());

        save(target, new Properties() {{
            setProperty("someValue", "20");
        }});
        waitForReload(DELAY);

        assertEquals(2, reloadCount[0]);
        assertEquals(Integer.valueOf(20), cfg.someValue());

        delete(target);
        waitForReload(DELAY);

        assertEquals(3, reloadCount[0]);
        assertEquals(Integer.valueOf(5), cfg.someValue());

        save(target, new Properties() {{
            setProperty("someValue", "30");
        }});
        waitForReload(DELAY);

        assertEquals(4, reloadCount[0]);
        assertEquals(Integer.valueOf(30), cfg.someValue());
    }

    @HotReload(value=10, unit = MILLISECONDS, type = ASYNC)
    interface OnlyHotReloadAnnotationIsSpecified extends Config, Reloadable {
        @DefaultValue("5")
        Integer someValue();
    }

    @Test
    public void testShouldNotCauseNullPex() {
        OnlyHotReloadAnnotationIsSpecified cfg = ConfigFactory.create(OnlyHotReloadAnnotationIsSpecified.class);
        assertEquals(Integer.valueOf(5), cfg.someValue());
    }

}
