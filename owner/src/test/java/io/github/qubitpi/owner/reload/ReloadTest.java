/*
 * Copyright (c) 2012-2015, Luigi R. Viggiano
 * All rights reserved.
 *
 * This software is distributable under the BSD license.
 * See the terms of the BSD license in the documentation provided with this software.
 */

package io.github.qubitpi.owner.reload;

import io.github.qubitpi.owner.Config;
import io.github.qubitpi.owner.Config.Sources;
import io.github.qubitpi.owner.ConfigFactory;
import io.github.qubitpi.owner.Reloadable;
import io.github.qubitpi.owner.TestConstants;
import io.github.qubitpi.owner.event.ReloadEvent;
import io.github.qubitpi.owner.event.ReloadListener;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.internal.hamcrest.HamcrestArgumentMatcher;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Properties;

import static io.github.qubitpi.owner.util.UtilTest.fileFromURI;
import static io.github.qubitpi.owner.util.UtilTest.save;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Luigi R. Viggiano
 */
@RunWith(MockitoJUnitRunner.class)
public class ReloadTest implements TestConstants {
    private static final String SPEC = "file:" + RESOURCES_DIR + "/ReloadableConfig.properties";
    private static File target;
    @Mock
    ReloadListener listener;

    @BeforeClass
    public static void beforeClass() throws URISyntaxException {
        target = fileFromURI(SPEC);
    }

    @Before
    public void before() throws Throwable {
        save(target, new Properties() {{
            setProperty("minimumAge", "18");
        }});
    }

    @Sources(SPEC)
    public interface ReloadableConfig extends Config, Reloadable {
        Integer minimumAge();
    }

    @Test
    public void testReload() throws Throwable {
        ReloadableConfig cfg = ConfigFactory.create(ReloadableConfig.class);

        assertEquals(Integer.valueOf(18), cfg.minimumAge());

        save(target, new Properties() {{
            setProperty("minimumAge", "21");
        }});

        cfg.reload();
        assertEquals(Integer.valueOf(21), cfg.minimumAge());
    }

    public interface ReloadImportConfig extends Config, Reloadable {
        Integer minimumAge();
    }

    @Test
    public void testReloadWithImportedProperties() throws Throwable {
        Properties props = new Properties() {{
            setProperty("minimumAge", "18");
        }};

        ReloadImportConfig cfg = ConfigFactory.create(ReloadImportConfig.class, props);
        assertEquals(Integer.valueOf(18), cfg.minimumAge());

        props.setProperty("minimumAge", "21"); // changing props doesn't reflect to cfg immediately
        assertEquals(Integer.valueOf(18), cfg.minimumAge());

        cfg.reload(); // the config gets reloaded, so the change in props gets reflected
        assertEquals(Integer.valueOf(21), cfg.minimumAge());
    }

    @After
    public void after() throws Throwable {
        target.delete();
    }

    @Test
    public void testReloadListener() throws Throwable {
        ReloadableConfig cfg = ConfigFactory.create(ReloadableConfig.class);
        cfg.addReloadListener(listener);
        cfg.reload();
        cfg.reload();
        cfg.reload();
        verify(listener, times(3)).reloadPerformed(argThat(isReloadListnerWithSource(cfg)));
    }

    @Test
    public void testReloadListenerRemoved() throws Throwable {
        ReloadableConfig cfg = ConfigFactory.create(ReloadableConfig.class);
        cfg.addReloadListener(listener);
        cfg.reload();
        cfg.reload();
        cfg.removeReloadListener(listener);
        cfg.reload();
        verify(listener, times(2)).reloadPerformed(argThat(isReloadListnerWithSource(cfg)));
    }

    private ArgumentMatcher<ReloadEvent> isReloadListnerWithSource(final ReloadableConfig cfg) {
        return new HamcrestArgumentMatcher<ReloadEvent>(
                new BaseMatcher<ReloadEvent>() {
                    public boolean matches(Object o) {
                        ReloadEvent given = (ReloadEvent) o;
                        return given.getSource() == cfg;
                    }

                    public void describeTo(Description description) {
                        description.appendText("does not match");
                    }
                });
    }

}
