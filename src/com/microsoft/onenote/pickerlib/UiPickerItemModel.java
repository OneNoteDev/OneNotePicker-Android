//----------------------------------------------------------------------------
//  Copyright (c) Microsoft Open Technologies, Inc. All rights reserved.
//  Licensed under the Apache License, Version 2.0.
//  See License.txt in the project root for license information.
//----------------------------------------------------------------------------
package com.microsoft.onenote.pickerlib;


class UiPickerItemModel {
    private UiPickerItemType type;
    private String text;

    private ApiResponse resultData;

    public UiPickerItemModel(UiPickerItemType type, String text, ApiResponse resultData) {
        this.type = type;
        this.text = text;
        this.resultData = resultData;
    }

    public String getText() {
        return text;
    }

    public UiPickerItemType getType() {
        return type;
    }

    public ApiResponse getResultData() {
        return resultData;
    }

}
