//----------------------------------------------------------------------------
//  Copyright (c) Microsoft Open Technologies, Inc. All rights reserved.
//  Licensed under the Apache License, Version 2.0.
//  See License.txt in the project root for license information.
//----------------------------------------------------------------------------
package com.microsoft.onenote.pickerlib;

import org.json.JSONException;
import org.json.JSONObject;

class ApiNotebookResponse extends ApiSectionGroupResponse {
    protected boolean isDefault;
    protected String createdBy;
    protected String userRole;
    protected String ownerName;

    public ApiNotebookResponse(JSONObject object) throws JSONException{
        super(object);
    	setIsDefault(object.optBoolean("isDefault"));
        setUserRole(object.optString("userRole"));
        setOwnerName(object.optString("ownerName"));
    }
    
    public boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}
