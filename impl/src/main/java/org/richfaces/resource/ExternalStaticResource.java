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
package org.richfaces.resource;

import static org.richfaces.application.configuration.ConfigurationServiceHelper.getStringConfigurationValue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import javax.faces.application.Resource;
import javax.faces.context.FacesContext;

import org.richfaces.application.CoreConfiguration;
import org.richfaces.skin.SkinFactory;
import org.richfaces.util.FastJoiner;

/**
 * @author Nick Belaevski
 *
 */
public class ExternalStaticResource extends Resource {
    public static final String STATIC_RESOURCE_LOCATION_VARIABLE = "resourceLocation";
    private static final FastJoiner RESOURCE_PATH_JOINER = FastJoiner.on('/');
    private String location;
    private boolean skinDependent;

    public ExternalStaticResource(String location, boolean skinDependent) {
        super();
        this.location = location;
        this.skinDependent = skinDependent;
    }

    private String getResourceLocation(FacesContext facesContext) {
        String skinName = null;

        if (skinDependent) {
            SkinFactory skinFactory = SkinFactory.getInstance(facesContext);
            skinName = skinFactory.getSkin(facesContext).getName();
        }

        return RESOURCE_PATH_JOINER.join(skinName, location);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, String> getResponseHeaders() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRequestPath() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map<String, Object> requestMap = facesContext.getExternalContext().getRequestMap();
        Object resourceVarValue = requestMap.get(STATIC_RESOURCE_LOCATION_VARIABLE);
        try {
            String resourceLocation = getResourceLocation(facesContext);

            requestMap.put(STATIC_RESOURCE_LOCATION_VARIABLE, resourceLocation);

            // TODO pass via ViewHandler?
            return getStringConfigurationValue(facesContext, CoreConfiguration.Items.staticResourceLocation);
        } finally {
            requestMap.remove(STATIC_RESOURCE_LOCATION_VARIABLE);
            if (resourceVarValue != null) {
                requestMap.put(STATIC_RESOURCE_LOCATION_VARIABLE, resourceVarValue);
            }
        }
    }

    @Override
    public URL getURL() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean userAgentNeedsUpdate(FacesContext context) {
        throw new UnsupportedOperationException();
    }
}
