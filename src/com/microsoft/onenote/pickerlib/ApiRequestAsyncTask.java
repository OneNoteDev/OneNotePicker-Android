//----------------------------------------------------------------------------
//  Copyright (c) Microsoft Open Technologies, Inc. All rights reserved.
//  Licensed under the Apache License, Version 2.0.
//  See License.txt in the project root for license information.
//----------------------------------------------------------------------------
package com.microsoft.onenote.pickerlib;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

class ApiRequestAsyncTask extends
        AsyncTask<Void, Void, ApiResponse[]> {
    private final static String API_ROOT = "https://www.onenote.com/api/v1.0/";
    public List<ApiAsyncResponse> delegates = new ArrayList<ApiAsyncResponse>(2);
    public ApiRequest[] mRequests;
    public Exception mCaughtException = null;
    private String mAccessToken = null;

    public ApiRequestAsyncTask(String mAccessToken, ApiRequest... requests) {
        this.mAccessToken = mAccessToken;
        this.mRequests = requests;
    }

    public void addDelegate(ApiAsyncResponse delegate) {
        delegates.add(delegate);
    }

    @Override
    protected ApiResponse[] doInBackground(Void... voids) {
        List<ApiResponse> responseList = new ArrayList<ApiResponse>();
        for (ApiRequest request : mRequests) {
            ApiResponse[] apiResponses;
            try {
                apiResponses = requestResource(request);
            } catch (Exception ex) {
                // Handle ApiExceptions and other exceptions in one place
                mCaughtException = ex;
                return null;
            }
            responseList.addAll(Arrays.asList(apiResponses));
        }
        return responseList.toArray(new ApiResponse[responseList.size()]);
    }

    private ApiResponse[] requestResource(ApiRequest request) throws Exception {
        JSONObject responseJSON = getJSONResponse(API_ROOT + request.getEndpointURL());

        List<ApiResponse> apiResponses = new ArrayList<ApiResponse>();
        if (responseJSON != null) {
            // Determine if values were returned or an error occurred
            if (responseJSON.has("value")) {
                // Get response values
                JSONArray values = responseJSON.getJSONArray("value");
                // Iterate over all values, and convert JSON objects into library objects
                for (int i = 0; i < values.length(); i++) {
                    JSONObject object = values.getJSONObject(i);
                    apiResponses.add(buildApiResponse(request, object));
                }
            } else if (responseJSON.has("error")) {
                // Process and throw an API error
                JSONObject error = responseJSON.getJSONObject("error");
                String code = error.optString("code");
                String message = error.optString("message");
                String urlString = error.optString("url");
                URL url = null;
                if (urlString.length() != 0) {
                    url = new URL(urlString);
                }
                throw new ApiException("API responded with an error.", code, message, url);
            } else {
                throw new ApiException("Unrecognized JSON response.",
                        null, "Unrecognized JSON response.", null);
            }
        }

        return apiResponses.toArray(new ApiResponse[apiResponses.size()]);
    }

    private ApiResponse buildApiResponse(ApiRequest request, JSONObject object) throws Exception {
        ApiResponse apiResponse = null;
        // Get links
        JSONObject links = object.optJSONObject("links");
        if (request.getPrimaryEndpoint() == ApiRequestEndpoint.NOTEBOOKS
                && !request.hasResourceId()
                && !request.hasSecondaryEndpoint()) {
            // Build notebook response
            apiResponse = new ApiNotebookResponse();
            ((ApiNotebookResponse) apiResponse).setIsDefault(
                    object.optBoolean("isDefault"));
            ((ApiNotebookResponse) apiResponse).setUserRole(
                    object.optString("userRole"));
            ((ApiNotebookResponse) apiResponse).setOwnerName(
                    object.optString("ownerName"));
        } else if (request.getSecondaryEndpoint() == ApiRequestEndpoint.SECTIONS) {
            // Build section response
            apiResponse = new ApiSectionResponse();
            ((ApiSectionResponse) apiResponse).setIsDefault(
                    object.optBoolean("isDefault"));
            if (links != null) {
                ((ApiSectionResponse) apiResponse).setPagesUrl(
                        new URL(links.getJSONObject("pagesUrl").getString("href")));
            } else {
                ((ApiSectionResponse) apiResponse).setPagesUrl(
                        new URL(object.getString("pagesUrl")));
            }

        } else if (request.getSecondaryEndpoint() == ApiRequestEndpoint.SECTION_GROUPS) {
            // Build section groups response
            apiResponse = new ApiSectionGroupResponse();
        }
        if (apiResponse != null) {
            // Apply properties common to all response types
            apiResponse.setId(object.getString("id"));
            apiResponse.setName(object.getString("name"));
            apiResponse.setCreatedTime(object.optString("createdTime"));
            apiResponse.setModifiedTime(object.optString("lastModifiedTime"));
            apiResponse.setLastModifiedBy(object.optString("lastModifiedBy"));
        }
        return apiResponse;
    }


    private JSONObject getJSONResponse(String endpoint) throws Exception {
        HttpsURLConnection mUrlConnection = (HttpsURLConnection) (new URL(endpoint)).openConnection();
        mUrlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent") + " Android OneNotePicker");
        if (mAccessToken != null) {
            mUrlConnection.setRequestProperty("Authorization", "Bearer " + mAccessToken);
        }
        mUrlConnection.connect();

        int responseCode = mUrlConnection.getResponseCode();
        String responseMessage = mUrlConnection.getResponseMessage();
        String responseBody = null;
        boolean responseIsJson = mUrlConnection.getContentType().contains("application/json");
        JSONObject responseObject;
        if (responseCode ==  HttpsURLConnection.HTTP_UNAUTHORIZED) {
            mUrlConnection.disconnect();
            throw new ApiException(Integer.toString(responseCode) + " " + responseMessage,
                    null, "Invalid or missing access token.", null);
        }
        if (responseIsJson) {
            InputStream is = null;
            try {
                if (responseCode == HttpsURLConnection.HTTP_INTERNAL_ERROR) {
                    is = mUrlConnection.getErrorStream();
                } else {
                    is = mUrlConnection.getInputStream();
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line = null;
                String lineSeparator = System.getProperty("line.separator");
                StringBuffer buffer = new StringBuffer();
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                    buffer.append(lineSeparator);
                }
                responseBody = buffer.toString();
            } finally {
                if (is != null) {
                    is.close();
                }
                mUrlConnection.disconnect();
            }

            responseObject = new JSONObject(responseBody);
        } else {
            throw new ApiException(Integer.toString(responseCode) + " " + responseMessage,
                    null, "Unrecognized server response", null);
        }

        return responseObject;
    }

    @Override
    protected void onPostExecute(ApiResponse[] responses) {
        super.onPostExecute(responses);
        for (ApiAsyncResponse delegate : delegates) {
            delegate.onApiResponse(responses, mCaughtException);
        }
        mCaughtException = null;
    }

    // Custom equals and hashCode implementations are needed to properly compare alike instances
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApiRequestAsyncTask that = (ApiRequestAsyncTask) o;

        if (!delegates.equals(that.delegates)) return false;
        if (!mAccessToken.equals(that.mAccessToken)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mAccessToken.hashCode();
        result = 31 * result + delegates.hashCode();
        return result;
    }
}