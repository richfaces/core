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

import com.google.common.base.Function;
import com.google.common.base.Objects;

/**
 * @author Nick Belaevski
 *
 */
public final class ResourceKey {
    public static final Function<String, ResourceKey> FACTORY = new Function<String, ResourceKey>() {
        public ResourceKey apply(String from) {
            return create(from);
        }

        ;
    };
    private final String resourceName;
    private final String libraryName;

    public ResourceKey(String resourceName, String libraryName) {
        super();
        this.resourceName = resourceName;
        this.libraryName = libraryName;
    }

    public static ResourceKey create(String resourceQualifier) {
        return new ResourceKey(extractResourceName(resourceQualifier), extractLibraryName(resourceQualifier));
    }

    public static ResourceKey create(String resourceName, String libraryName) {
        return new ResourceKey(resourceName, libraryName);
    }

    private static String extractResourceName(String resourceQualifier) {
        int idx = resourceQualifier.lastIndexOf(':');
        if (idx < 0) {
            return resourceQualifier;
        }

        return resourceQualifier.substring(idx + 1);
    }

    private static String extractLibraryName(String resourceQualifier) {
        int idx = resourceQualifier.lastIndexOf(':');
        if (idx < 0) {
            return null;
        }

        return resourceQualifier.substring(0, idx);
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getLibraryName() {
        return libraryName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((libraryName == null) ? 0 : libraryName.hashCode());
        result = prime * result + ((resourceName == null) ? 0 : resourceName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ResourceKey other = (ResourceKey) obj;
        if (libraryName == null) {
            if (other.libraryName != null) {
                return false;
            }
        } else if (!libraryName.equals(other.libraryName)) {
            return false;
        }
        if (resourceName == null) {
            if (other.resourceName != null) {
                return false;
            }
        } else if (!resourceName.equals(other.resourceName)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("resourceName", resourceName).add("libraryName", libraryName).toString();
    }
}
