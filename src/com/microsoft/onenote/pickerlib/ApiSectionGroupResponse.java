//----------------------------------------------------------------------------
//  Copyright (c) Microsoft Open Technologies, Inc. All rights reserved.
//  Licensed under the Apache License, Version 2.0.
//  See License.txt in the project root for license information.
//----------------------------------------------------------------------------
package com.microsoft.onenote.pickerlib;

import java.net.URL;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

class ApiSectionGroupResponse extends ApiResponse {
    protected URL sectionsUrl;
    protected URL sectionGroupsUrl;

    public ApiSectionGroupResponse(JSONObject object) throws JSONException{
    	super(object);
    }
    
    public List<ApiSectionResponse> sections;
    public List<ApiSectionGroupResponse> sectionGroups;
    
    public URL getSectionsUrl() {
        return sectionsUrl;
    }

    public void setSectionsUrl(URL sectionsUrl) {
        this.sectionsUrl = sectionsUrl;
    }

    public URL getSectionGroupsUrl() {
        return sectionGroupsUrl;
    }

    public void setSectionGroupsUrl(URL sectionGroupsUrl) {
        this.sectionGroupsUrl = sectionGroupsUrl;
    }
}
