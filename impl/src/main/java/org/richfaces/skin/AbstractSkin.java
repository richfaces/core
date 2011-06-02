/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.richfaces.skin;

import java.awt.Color;

import javax.faces.context.FacesContext;

import org.ajax4jsf.util.HtmlColor;
import org.richfaces.renderkit.util.HtmlDimensions;

/**
 * @author Nick Belaevski
 *
 */
public abstract class AbstractSkin implements Skin {
    protected Integer decodeColor(Object value) {
        if (value instanceof Color) {
            return ((Color) value).getRGB();
        } else if (value instanceof Integer) {
            return ((Integer) value).intValue();
        } else {
            String stringValue = (String) value;
            if (stringValue != null && stringValue.length() != 0) {
                return Integer.valueOf(HtmlColor.decode(stringValue).getRGB());
            } else {
                return null;
            }
        }
    }

    protected Integer decodeInteger(Object value) {
        if (value instanceof Number) {
            return Integer.valueOf(((Number) value).intValue());
        } else {
            String stringValue = (String) value;
            if (stringValue != null && stringValue.length() != 0) {
                return (int) HtmlDimensions.decode(stringValue).doubleValue();
            } else {
                return null;
            }
        }
    }

    public Integer getColorParameter(FacesContext context, String name) {
        return decodeColor(getParameter(context, name));
    }

    public Integer getColorParameter(FacesContext context, String name, Object defaultValue) {
        return decodeColor(getParameter(context, name, defaultValue));
    }

    public Integer getIntegerParameter(FacesContext context, String name) {
        return decodeInteger(getParameter(context, name));
    }

    public Integer getIntegerParameter(FacesContext context, String name, Object defaultValue) {
        return decodeInteger(getParameter(context, name, defaultValue));
    }
}
