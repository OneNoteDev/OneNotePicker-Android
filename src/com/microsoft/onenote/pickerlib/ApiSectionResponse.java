//----------------------------------------------------------------------------
//  Copyright (c) Microsoft Open Technologies, Inc. All rights reserved.
//  Licensed under the Apache License, Version 2.0.
//  See License.txt in the project root for license information.
//----------------------------------------------------------------------------
package com.microsoft.onenote.pickerlib;

import java.net.URL;

class ApiSectionResponse extends ApiResponse {
    private boolean isDefault;
    private URL pagesUrl;

    public boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public URL getPagesUrl() {
        return pagesUrl;
    }

    public void setPagesUrl(URL pagesUrl) {
        this.pagesUrl = pagesUrl;
    }
}
