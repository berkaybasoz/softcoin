package com.audacityit.finder.util;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.audacityit.finder.util.Constants.URL_APB_API;

/**
 * @author Audacity IT Solutions Ltd.
 * @class ApiHandler
 * @brief Easy implementation of OkHttp library
 */
public class ApiHandler {

    public static final int REQUEST_GET = 0;
    public static final int REQUEST_POST = 1;
    private Context context;
    private ContentValues contentValues;
    private String urlKey;
    private ApiHandlerListener apiHandlerListener;
    private boolean isShowProgressBar = true;
    private ProgressDialog progressDialog;

    /**
     * @brief default constructor
     */
    public ApiHandler() {

    }

    /**
     * @param urlKey
     * @brief constructor for only printing the url
     */
    public ApiHandler(String urlKey) {
        this.urlKey = urlKey;
    }

    /**
     * @param context
     * @param urlKey
     * @brief constructor for initialize from Activity
     */
    public ApiHandler(Context context, String urlKey) {
        this.context = context;
        this.urlKey = urlKey;
        apiHandlerListener = (ApiHandlerListener) context;
    }

    /**
     * @param context       application context
     * @param urlKey        suffix part of the url
     * @param contentValues values with key value pair
     * @brief constructor for initialize from Activity
     */
    public ApiHandler(Context context, String urlKey, ContentValues contentValues) {
        this.context = context;
        this.urlKey = urlKey;
        this.contentValues = contentValues;
        apiHandlerListener = (ApiHandlerListener) context;
    }

    /**
     * @param fragment fragment object
     * @param urlKey   suffix part of the url
     * @brief constructor for initialize from Fragment
     */
    public ApiHandler(Fragment fragment, String urlKey) {
        this.context = fragment.getActivity();
        this.urlKey = urlKey;
        apiHandlerListener = (ApiHandlerListener) fragment;
    }

    /**
     * @param fragment      fragment object
     * @param urlKey        suffix part of the url
     * @param contentValues values with key value pair
     * @brief constructor for initialize from Fragment
     */
    public ApiHandler(Fragment fragment, String urlKey, ContentValues contentValues) {
        this.context = fragment.getActivity();
        this.urlKey = urlKey;
        this.contentValues = contentValues;
        apiHandlerListener = (ApiHandlerListener) fragment;
    }

    /**
     * @param isShow true or false
     * @brief methods for setting progress bar show or hide
     */
    public void showProgressBar(boolean isShow) {
        isShowProgressBar = isShow;
    }

    /**
     * @param requestType \c REQUEST_GET or \c REQUEST_POST
     * @brief methods for API call
     */
    public void doApiRequest(int requestType) {

        if (isShowProgressBar) {
            progressDialog = ProgressDialog.show(context, null, "Please wait...", true, false);
        }
        OkHttpClient client = new OkHttpClient();

        if (requestType == REQUEST_GET) {

            Request request = new Request.Builder()
                    .url(URL_APB_API + urlKey)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    if (isShowProgressBar) {
                        progressDialog.dismiss();
                    }
                    apiHandlerListener.onFailureResponse(urlKey);
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if (isShowProgressBar) {
                        progressDialog.dismiss();
                    }
                    apiHandlerListener.onSuccessResponse(urlKey, response.body().string());
                }
            });

        } else if (requestType == REQUEST_POST) {

            Set<String> keys = contentValues.keySet();
            List<String> keyList = new ArrayList<String>(keys);
            FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
            RequestBody formBody;

            for (String key : keyList) {
//            Log.v("logCheck", key + " -->" + contentValues.getAsString(key));
                formEncodingBuilder.add(key, contentValues.getAsString(key));
            }

            printUrl(keyList);

            formBody = formEncodingBuilder.build();
            Request request = new Request.Builder()
                    .url(URL_APB_API + urlKey)
                    .post(formBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Request request, IOException e) {
                    if (isShowProgressBar) {
                        progressDialog.dismiss();
                    }
                    apiHandlerListener.onFailureResponse(urlKey);
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String jsonString = response.body().string();
                    if (isShowProgressBar) {
                        progressDialog.dismiss();
                    }
                    apiHandlerListener.onSuccessResponse(urlKey, jsonString);
                }
            });

        } else {

        }

    }

    /**
     * @param keyList the list of keys passing with the url
     * @brief print the coming values along with url
     */
    private void printUrl(List<String> keyList) {
        String url = URL_APB_API + urlKey + "?";
        for (int i = 0; i < keyList.size(); i++) {
            url = url + keyList.get(i) + "=" + contentValues.getAsString(keyList.get(i));
            if (i != keyList.size() - 1) {
                url = url + "&";
            }
        }
        Log.v("printUrl", url);
    }

    /**
     * @param values \c contentValues with key value pair
     * @brief print the coming values along with url
     */
    public void printUrl(ContentValues values) {
        Set<String> keys = values.keySet();
        List<String> keyList = new ArrayList<String>(keys);
        String url = URL_APB_API + urlKey + "?";
        for (int i = 0; i < keyList.size(); i++) {
            url = url + keyList.get(i) + "=" + values.getAsString(keyList.get(i));
            if (i != keyList.size() - 1) {
                url = url + "&";
            }
        }
        Log.v("printUrl", url);
    }

    /**
     * @brief interface
     */
    public static interface ApiHandlerListener {
        void onSuccessResponse(String tag, String jsonString);

        void onFailureResponse(String tag);
    }
}
