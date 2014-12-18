//----------------------------------------------------------------------------
//  Copyright (c) Microsoft Open Technologies, Inc. All rights reserved.
//  Licensed under the Apache License, Version 2.0.
//  See License.txt in the project root for license information.
//----------------------------------------------------------------------------
package com.microsoft.onenote.pickerlib;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

class ApiRequestAsyncTask extends
        AsyncTask<Void, Void, ApiNotebookResponse[]> {
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
    protected ApiNotebookResponse[] doInBackground(Void... voids) {
        List<ApiNotebookResponse> responseList = new ArrayList<ApiNotebookResponse>();
        for (ApiRequest request : mRequests) {
        	ApiNotebookResponse[] apiResponses;
            try {
                apiResponses = requestResource(request);
            } catch (Exception ex) {
                // Handle ApiExceptions and other exceptions in one place
                mCaughtException = ex;
                return null;
            }
            responseList.addAll(Arrays.asList(apiResponses));
        }
        return responseList.toArray(new ApiNotebookResponse[responseList.size()]);
    }

    private ApiNotebookResponse[] requestResource(ApiRequest request) throws Exception {
        JSONObject responseJSON = getJSONResponse(API_ROOT + request.getEndpointURL());

        List<ApiNotebookResponse> notebookResponses = new ArrayList<ApiNotebookResponse>();
        if (responseJSON != null) {
            // Determine if values were returned or an error occurred
            if (responseJSON.has("value")) {
                // Get response values
                JSONArray notebookObjects = responseJSON.getJSONArray("value");
                // Iterate over all values, and convert JSON objects into library objects
                for (int i = 0; i < notebookObjects.length(); i++) {
                	JSONObject notebookObject = notebookObjects.getJSONObject(i);
                	
                    ApiNotebookResponse notebook = new ApiNotebookResponse(notebookObject);
                    notebook.sections = getSections(notebookObject);
                    notebook.sectionGroups = getSectionGroups(notebookObject);
                    
                    notebookResponses.add(notebook);
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

        return notebookResponses.toArray(new ApiNotebookResponse[notebookResponses.size()]);
    }
    
    private List<ApiSectionGroupResponse> getSectionGroups(JSONObject parentObject) throws JSONException, MalformedURLException {
        List<ApiSectionGroupResponse> sectionGroups = new ArrayList<ApiSectionGroupResponse>();
        JSONArray sectionGroupObjects = parentObject.getJSONArray("sectionGroups");
        
        for (int k = 0; k < sectionGroupObjects.length(); k++)
        {
        	JSONObject sectionGroupObject = sectionGroupObjects.getJSONObject(k);
        	ApiSectionGroupResponse sectionGroup = new ApiSectionGroupResponse(sectionGroupObject);
        	sectionGroup.sectionGroups = getSectionGroups(sectionGroupObject);
        	sectionGroup.sections = getSections(sectionGroupObject);
        	sectionGroups.add(sectionGroup);
        }
        
        return sectionGroups;
	}

	private List<ApiSectionResponse> getSections(JSONObject parentObject) throws JSONException, MalformedURLException
    {
        List<ApiSectionResponse> sections = new ArrayList<ApiSectionResponse>();
        JSONArray sectionObjects = parentObject.getJSONArray("sections");
        for (int k = 0; k < sectionObjects.length(); k++)
        {
        	sections.add(new ApiSectionResponse(sectionObjects.getJSONObject(k)));
        }
        return sections;
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
    protected void onPostExecute(ApiNotebookResponse[] responses) {
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