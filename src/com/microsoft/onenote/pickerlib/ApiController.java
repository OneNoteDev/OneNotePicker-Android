//----------------------------------------------------------------------------
//  Copyright (c) Microsoft Open Technologies, Inc. All rights reserved.
//  Licensed under the Apache License, Version 2.0.
//  See License.txt in the project root for license information.
//----------------------------------------------------------------------------
package com.microsoft.onenote.pickerlib;

import java.util.LinkedList;

abstract class ApiController implements ApiAsyncResponse {
    final LinkedList<ApiRequestAsyncTask> mQueuedTasks = new LinkedList<ApiRequestAsyncTask>();
    private final String mAccessToken;
    ApiRequestAsyncTask mTopPriorityTask = null;


    public ApiController(String accessToken) {
        mAccessToken = accessToken;
    }

    @Override
    public synchronized void onApiResponse(ApiResponse[] responses, Exception error) {
        if (error != null) {
            onApiError(error);
            return;
        }
        handleApiResponse(responses);
        executeNext();
    }

    protected synchronized void executeNext() {
        if (mTopPriorityTask == null) {
            ApiRequestAsyncTask task = mQueuedTasks.poll();
            if (task != null) {
                task.execute();
            }
        } else {
            mTopPriorityTask.execute();
            mTopPriorityTask = null;
        }
    }

    private void handleApiResponse(ApiResponse[] responses) {
        for (ApiResponse response : responses) {
            ApiRequestAsyncTask task = buildApiRequestTask(response);
            if (task != null) {
                mQueuedTasks.add(task);
            }
        }
    }

    private ApiRequestAsyncTask buildApiRequestTask(ApiResponse resultData) {
        ApiRequestEndpoint primaryEndpoint = null;
        if (resultData instanceof ApiNotebookResponse) {
            primaryEndpoint = ApiRequestEndpoint.NOTEBOOKS;
        } else if (resultData instanceof ApiSectionGroupResponse) {
            primaryEndpoint = ApiRequestEndpoint.SECTION_GROUPS;
        }
        if (primaryEndpoint != null) {
            ApiRequestAsyncTask task = new ApiRequestAsyncTask(mAccessToken,
                    new ApiRequest(primaryEndpoint, resultData.getId(),
                            ApiRequestEndpoint.SECTIONS),
                    new ApiRequest(primaryEndpoint, resultData.getId(),
                            ApiRequestEndpoint.SECTION_GROUPS)
            );
            task.addDelegate(this);
            task.addDelegate(getApiResponseDelegate(resultData));
            return task;
        } else {
            return null;
        }
    }

    public void begin(ApiAsyncResponse initialDelegate) {
        ApiRequestAsyncTask initialTask = new ApiRequestAsyncTask(mAccessToken,
                new ApiRequest(ApiRequestEndpoint.NOTEBOOKS));
        initialTask.addDelegate(this);
        initialTask.addDelegate(initialDelegate);

        mQueuedTasks.add(initialTask);
        executeNext();
    }

    public void prioritize(ApiResponse resultData) {
        mTopPriorityTask = buildApiRequestTask(resultData);
        // Remove prioritized task from the queue
        mQueuedTasks.remove(mTopPriorityTask);
    }

    protected abstract ApiAsyncResponse getApiResponseDelegate(ApiResponse response);

    protected abstract void onApiError(Exception error);

}
