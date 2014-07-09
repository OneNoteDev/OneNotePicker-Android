//----------------------------------------------------------------------------
//  Copyright (c) Microsoft Open Technologies, Inc. All rights reserved.
//  Licensed under the Apache License, Version 2.0.
//  See License.txt in the project root for license information.
//----------------------------------------------------------------------------
package com.microsoft.onenote.pickerlib;

enum ApiRequestEndpoint {
    NOTEBOOKS("Notebooks"), SECTION_GROUPS("SectionGroups"), SECTIONS("Sections");

    private final String string;

    private ApiRequestEndpoint(String s) {
        string = s;
    }

    public String toString() {
        return string;
    }

}
