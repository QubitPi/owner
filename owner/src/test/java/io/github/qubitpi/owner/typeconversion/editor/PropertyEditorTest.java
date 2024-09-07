/*
 * Copyright (c) 2012-2015, Luigi R. Viggiano
 * All rights reserved.
 *
 * This software is distributable under the BSD license.
 * See the terms of the BSD license in the documentation provided with this software.
 */

package io.github.qubitpi.owner.typeconversion.editor;

import io.github.qubitpi.owner.Config;
import io.github.qubitpi.owner.ConfigFactory;
import org.junit.Before;
import org.junit.Test;

import java.beans.PropertyEditorManager;
import java.util.List;

import static io.github.qubitpi.owner.typeconversion.editor.PropertyEditorTestUtil.assumePropertyEditorIsEnabled;
import static org.junit.Assert.assertEquals;

/**
 * @author Luigi R. Viggiano
 */
public class PropertyEditorTest {

    private MyAppConfig cfg;

    public interface MyAppConfig extends Config {
        @DefaultValue("admin")
        User user();

        @DefaultValue("admin,root")
        List<User> users();
    }

    @Before
    public void setUp() {
        assumePropertyEditorIsEnabled();
        PropertyEditorManager.registerEditor(User.class, UserPropertyEditor.class);
        cfg = ConfigFactory.create(MyAppConfig.class);
    }

    @Test
    public void testPropertyEditor() {
        assertEquals("admin", cfg.user().getUsername());
    }

    @Test
    public void testPropertyEditorWithList() {
        List<User> users = cfg.users();
        assertEquals("admin", users.get(0).getUsername());
        assertEquals("root", users.get(1).getUsername());
    }

}
