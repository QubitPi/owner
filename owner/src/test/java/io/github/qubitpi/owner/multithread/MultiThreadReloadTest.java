/*
 * Copyright (c) 2012-2015, Luigi R. Viggiano
 * All rights reserved.
 *
 * This software is distributable under the BSD license.
 * See the terms of the BSD license in the documentation provided with this software.
 */

package io.github.qubitpi.owner.multithread;

import io.github.qubitpi.owner.Config;
import io.github.qubitpi.owner.Config.Sources;
import io.github.qubitpi.owner.ConfigFactory;
import io.github.qubitpi.owner.Reloadable;
import io.github.qubitpi.owner.TestConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Properties;

import static io.github.qubitpi.owner.util.UtilTest.fileFromURI;
import static io.github.qubitpi.owner.util.UtilTest.newArray;
import static io.github.qubitpi.owner.util.UtilTest.save;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Luigi R. Viggiano
 */
public class MultiThreadReloadTest extends MultiThreadTestBase implements TestConstants {
    private static final String SPEC = "file:" + RESOURCES_DIR + "/ReloadableConfig.properties";
    private static File target;
    private ReloadableConfig reloadableConfig;

    @BeforeClass
    public static void beforeClass() throws URISyntaxException {
        target = fileFromURI(SPEC);
    }

    @Before
    public void before() throws Throwable {
        synchronized (target) {
            save(target, new Properties() {{
                setProperty("someValue", "10");
            }});

            reloadableConfig = ConfigFactory.create(ReloadableConfig.class);
        }
    }

    @Sources(SPEC)
    public interface ReloadableConfig extends Config, Reloadable {
        Integer someValue();
    }

    @Test
    public void multiThreadedReloadTest() throws Throwable {
        Object lock = new Object();

        ReaderThread[] readers = newArray(20, new ReaderThread(reloadableConfig, lock, 100));
        WriterThread[] writers = newArray(5, new WriterThread(reloadableConfig, lock, 70));

        start(readers, writers);

        notifyAll(lock);

        join(readers, writers);

        assertNoErrors(readers);
        assertNoErrors(writers);
    }

    private class ReaderThread extends ThreadBase<ReloadableConfig> {
        ReaderThread(ReloadableConfig cfg, Object lock, int loops) {
            super(cfg, lock, loops);
        }

        @Override
        void execute() throws Throwable {
            yield();
            Integer value = cfg.someValue();
            assertNotNull(value);
            assertTrue(value == 10 || value == 20);
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return new ReaderThread(cfg, lock, loops);
        }
    }

    private class WriterThread extends ThreadBase<ReloadableConfig> {
        public WriterThread(ReloadableConfig cfg, Object lock, int loops) {
            super(cfg, lock, loops);
        }

        @Override
        void execute() throws Throwable {
            synchronized (target) {
                save(target, new Properties() {{
                    setProperty("someValue", "20");
                }});

                cfg.reload();
            }
            yield();

            synchronized (target) {
                save(target, new Properties() {{
                    setProperty("someValue", "10");
                }});

                cfg.reload();
            }
            yield();
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return new WriterThread(cfg, lock, loops);
        }
    }

    @After
    public void after() throws Throwable {
        synchronized (target) {
            target.delete();
        }
    }
}
