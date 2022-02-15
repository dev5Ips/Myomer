package com.myomer.myomer.util.oldProjectFiles.webservice;

import com.google.gson.JsonElement;

/**
 * Interface for Network Api Callbacks Handle
 */
public interface ServiceCallBack {

    /**
     * Method for Success Handler of network api
     * @param requestTag -  Api TAG
     * @param data - Response Data
     */
    void onSuccess(String requestTag, JsonElement data);

    /**
     * Method for Failure Handler of network api
     * @param requestTag - Api TAG
     * @param message - Failure mesage
     * @param errorcode
     */
    void onFailure(String requestTag, String message, String errorcode);





}
