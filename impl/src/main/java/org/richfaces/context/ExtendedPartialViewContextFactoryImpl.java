/**
 * License Agreement.
 *
 * Rich Faces - Natural Ajax for Java Server Faces (JSF)
 *
 * Copyright (C) 2007 Exadel, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */
package org.richfaces.context;

import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextFactory;

/**
 * @author Nick Belaevski
 * @since 4.0
 */
public class ExtendedPartialViewContextFactoryImpl extends PartialViewContextFactory {
    private PartialViewContextFactory parentFactory;

    public ExtendedPartialViewContextFactoryImpl(PartialViewContextFactory parentFactory) {
        super();
        this.parentFactory = parentFactory;
    }

    @Override
    public PartialViewContext getPartialViewContext(final FacesContext context) {
        return new ExtendedPartialViewContextImpl(parentFactory.getPartialViewContext(context), context);
    }

    @Override
    public PartialViewContextFactory getWrapped() {
        return parentFactory;
    }
}
