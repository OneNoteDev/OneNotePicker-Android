//----------------------------------------------------------------------------
//  Copyright (c) Microsoft Open Technologies, Inc. All rights reserved.
//  Licensed under the Apache License, Version 2.0.
//  See License.txt in the project root for license information.
//----------------------------------------------------------------------------
package com.microsoft.onenote.pickerlib;

abstract class ApiController implements ApiAsyncResponse {
    private final String mAccessToken;

    public ApiController(String accessToken) {
        mAccessToken = accessToken;
    }

    public void begin() {
        ApiRequestAsyncTask initialTask = new ApiRequestAsyncTask(mAccessToken, new ApiRequest(ApiRequestEndpoint.EXPAND));
        initialTask.addDelegate(this);
        initialTask.execute();
    }
}
