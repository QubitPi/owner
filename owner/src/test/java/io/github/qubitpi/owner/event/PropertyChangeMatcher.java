/*
 * Copyright (c) 2012-2015, Luigi R. Viggiano
 * All rights reserved.
 *
 * This software is distributable under the BSD license.
 * See the terms of the BSD license in the documentation provided with this software.
 */

package io.github.qubitpi.owner.event;

import org.mockito.ArgumentMatcher;

import java.beans.PropertyChangeEvent;

import static io.github.qubitpi.owner.util.UtilTest.eq;

/**
 * @author Luigi R. Viggiano
 */
class PropertyChangeMatcher {
    static ArgumentMatcher<PropertyChangeEvent> matches(final PropertyChangeEvent expectedEvent) {
        return new ArgumentMatcher<PropertyChangeEvent>() {
            @Override
            public boolean matches(PropertyChangeEvent argument) {
                return expectedEvent.getSource() == argument.getSource() &&
                        eq(expectedEvent.getOldValue(), argument.getOldValue()) &&
                        eq(expectedEvent.getNewValue(), argument.getNewValue()) &&
                        eq(expectedEvent.getPropertyName(), argument.getPropertyName());
            }

            @Override
            public String toString() {
                return String.valueOf(expectedEvent);
            }
        };
    }
}
