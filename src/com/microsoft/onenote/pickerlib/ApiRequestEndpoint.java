//----------------------------------------------------------------------------
//  Copyright (c) Microsoft Open Technologies, Inc. All rights reserved.
//  Licensed under the Apache License, Version 2.0.
//  See License.txt in the project root for license information.
//----------------------------------------------------------------------------
package com.microsoft.onenote.pickerlib;

enum ApiRequestEndpoint {
    EXPAND("Notebooks?$expand=sections,sectionGroups($expand=sections,sectionGroups($expand=sections;$levels=max))");

    private final String string;

    private ApiRequestEndpoint(String s) {
        string = s;
    }

    public String toString() {
        return string;
    }
}
