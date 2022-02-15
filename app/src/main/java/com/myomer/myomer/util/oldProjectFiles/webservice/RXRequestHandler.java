package com.myomer.myomer.util.oldProjectFiles.webservice;

import android.content.Context;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.myomer.myomer.R;
import com.myomer.myomer.util.oldProjectFiles.Constants;
import com.myomer.myomer.util.oldProjectFiles.Utilty;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import retrofit2.http.Part;


public class RXRequestHandler {

    private static final String TAG = "RxRequestHandler";
    private TransparentProgressDialog mProgressDialog;


    public void makeApiRequest(final Context context, String url, final String requestTag, final ServiceCallBack listener, JsonObject requestObject, Map<String, String> query, boolean isProgressNeeded, String apiType, @Part MultipartBody.Part filePart) {


        if (Utilty.isConnectingToInternet(context)) {

            RXInterface apiService = RxApiClient.getClient().create(RXInterface.class);
            Observable<JsonElement> observableResponse = null;

            if (isProgressNeeded) {
                showProgressDialog(context);
            }


            if (apiType.equalsIgnoreCase(Constants.ApiType.POST)) {
                observableResponse = apiService.makePostRequest( url, requestObject)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());

            }

            if (observableResponse != null) {
                observableResponse.subscribe(new Observer<JsonElement>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull JsonElement jsonElement) {
                        hideProgressDialog(context);
                        if (listener != null) {
                            listener.onSuccess(requestTag, jsonElement);
                        }
                    }


                    @Override
                    public void onError(@NonNull Throwable e) {
                        hideProgressDialog(context);
                        try {
                            readerror(context, e, context.getResources().getString(R.string.server_error), listener, requestTag);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }

                    @Override
                    public void onComplete() {
                    }
                });


            }
        }
    }


    /**
     * Show Progress bar
     * @param ctx
     */
    private void showProgressDialog(final Context ctx) {

        try {

            if (mProgressDialog == null) {
                mProgressDialog = new TransparentProgressDialog(ctx, R.drawable.loading_img);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();

            } else {

                mProgressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Hide Progress bar
     *
     * @param ctx
     */
    private void hideProgressDialog(final Context ctx) {

        try {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void readerror(Context context, Throwable e, String errorstring, ServiceCallBack listener, String requestTag) {
        String errorcode = "";

        if (listener != null) {
            listener.onFailure(requestTag, errorstring, errorcode);
        }
    }

}
