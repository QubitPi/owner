/*
 * Copyright (c) 2012-2016, Luigi R. Viggiano
 * All rights reserved.
 *
 * This software is distributable under the BSD license.
 * See the terms of the BSD license in the documentation provided with this software.
 */

package io.github.qubitpi.owner.converters;

import io.github.qubitpi.owner.util.bytesize.ByteSize;
import io.github.qubitpi.owner.util.bytesize.ByteSizeUnit;
import io.github.qubitpi.owner.Converter;

import java.lang.reflect.Method;
import java.math.BigDecimal;

/**
 * @author Stefan Freyr Stefansson
 */
public class ByteSizeConverter implements Converter<ByteSize> {

    public ByteSize convert(Method method, String input) {
        return parse(input);
    }

    private static ByteSize parse(String input){
        String[] parts = ConverterUtil.splitNumericAndChar(input);
        String value = parts[0];
        String unit = parts[1];

        BigDecimal bdValue = new BigDecimal(value);
        ByteSizeUnit bsuUnit = ByteSizeUnit.parse(unit);

        if (bsuUnit == null){
            throw new IllegalArgumentException("Invalid unit string: '" + unit + "'");
        }

        return new ByteSize(bdValue, bsuUnit);
    }
}
