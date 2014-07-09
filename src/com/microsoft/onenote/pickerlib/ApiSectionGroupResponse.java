//----------------------------------------------------------------------------
//  Copyright (c) Microsoft Open Technologies, Inc. All rights reserved.
//  Licensed under the Apache License, Version 2.0.
//  See License.txt in the project root for license information.
//----------------------------------------------------------------------------
package com.microsoft.onenote.pickerlib;

import java.net.URL;

class ApiSectionGroupResponse extends ApiResponse {
    protected URL sectionsUrl;
    protected URL sectionGroupsUrl;

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
