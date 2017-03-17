//----------------------------------------------------------------------------
//  Copyright (c) Microsoft Open Technologies, Inc. All rights reserved.
//  Licensed under the Apache License, Version 2.0.
//  See License.txt in the project root for license information.
//----------------------------------------------------------------------------
package com.microsoft.onenote.pickerlib;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

class ApiSectionResponse extends ApiResponse {
    private boolean isDefault;
    private URL pagesUrl;

    public ApiSectionResponse(JSONObject object) throws JSONException, MalformedURLException{
        super(object);

        if (object != null) {
            setIsDefault(object.optBoolean("isDefault"));

            if (object.has("pagesUrl")) {
                this.setPagesUrl(new URL(object.getString("pagesUrl")));
            }
        }
    }
    
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
