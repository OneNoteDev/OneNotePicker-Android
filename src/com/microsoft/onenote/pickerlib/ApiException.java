//----------------------------------------------------------------------------
//  Copyright (c) Microsoft Open Technologies, Inc. All rights reserved.
//  Licensed under the Apache License, Version 2.0.
//  See License.txt in the project root for license information.
//----------------------------------------------------------------------------
package com.microsoft.onenote.pickerlib;

import java.net.URL;

class ApiException extends Exception {
    private String errorCode;
    private String errorMessage;
    private URL errorUrl;

    public ApiException(String detailMessage, String errorCode, String errorMessage, URL errorUrl) {
        super(detailMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorUrl = errorUrl;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public URL getErrorUrl() {
        return errorUrl;
    }
}
