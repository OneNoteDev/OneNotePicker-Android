//----------------------------------------------------------------------------
//  Copyright (c) Microsoft Open Technologies, Inc. All rights reserved.
//  Licensed under the Apache License, Version 2.0.
//  See License.txt in the project root for license information.
//----------------------------------------------------------------------------
package com.microsoft.onenote.pickerlib;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class ApiResponse {
    protected String id;
    protected String name;
    protected Date createdTime;
    protected Date modifiedTime;
    protected String lastModifiedBy;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = DateFormatParser.tryParse(createdTime);
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(String modifiedTime) {
        this.modifiedTime = DateFormatParser.tryParse(modifiedTime);
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    private static class DateFormatParser {
        private static SimpleDateFormat ISODateTimeMillisecondsFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'");
        private static SimpleDateFormat ISODateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        public static Date tryParse(String s) {
            try {
                return ISODateTimeMillisecondsFormat.parse(s);
            } catch (ParseException ex) {
                try {
                    return ISODateTimeFormat.parse(s);
                } catch (ParseException ex_) {
                    return null;
                }
            }
        }
    }
}
