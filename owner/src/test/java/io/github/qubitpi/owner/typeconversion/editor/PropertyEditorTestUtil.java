/*
 * Copyright (c) 2012-2015, Luigi R. Viggiano
 * All rights reserved.
 *
 * This software is distributable under the BSD license.
 * See the terms of the BSD license in the documentation provided with this software.
 */

package io.github.qubitpi.owner.typeconversion.editor;

import io.github.qubitpi.owner.util.Reflection;

import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

/**
 * @author Luigi R. Viggiano
 */
public class PropertyEditorTestUtil {

    public static final String PROPERTY_EDITOR_DISABLED = "io.github.qubitpi.owner.property.editor.disabled";

    static void assumePropertyEditorIsEnabled() {
        assumeFalse(Boolean.getBoolean(PROPERTY_EDITOR_DISABLED));
        assumeTrue(Reflection.isClassAvailable("java.beans.PropertyEditorManager"));
    }
}
