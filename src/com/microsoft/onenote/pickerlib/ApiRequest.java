//----------------------------------------------------------------------------
//  Copyright (c) Microsoft Open Technologies, Inc. All rights reserved.
//  Licensed under the Apache License, Version 2.0.
//  See License.txt in the project root for license information.
//----------------------------------------------------------------------------
package com.microsoft.onenote.pickerlib;

class ApiRequest {
    ApiRequestEndpoint primaryEndpoint = null;
    String resourceId = null;
    ApiRequestEndpoint secondaryEndpoint = null;

    public ApiRequest(ApiRequestEndpoint endpoint) {
        this.primaryEndpoint = endpoint;
    }

    public ApiRequest(ApiRequestEndpoint primaryEndpoint, String resourceId, ApiRequestEndpoint secondEndpoint) {
        this.primaryEndpoint = primaryEndpoint;
        this.resourceId = resourceId;
        this.secondaryEndpoint = secondEndpoint;
    }

    public ApiRequestEndpoint getPrimaryEndpoint() {
        return primaryEndpoint;
    }

    public String getResourceId() {
        return resourceId;
    }

    public ApiRequestEndpoint getSecondaryEndpoint() {
        return secondaryEndpoint;
    }

    public boolean hasResourceId() {
        return resourceId != null;
    }

    public boolean hasSecondaryEndpoint() {
        return secondaryEndpoint != null;
    }

    public String getEndpointURL() {
        return primaryEndpoint.toString() + "/"
                + (hasResourceId() ? resourceId + "/" : "")
                + (hasSecondaryEndpoint() ? secondaryEndpoint.toString() + "/" : "");
    }
}
