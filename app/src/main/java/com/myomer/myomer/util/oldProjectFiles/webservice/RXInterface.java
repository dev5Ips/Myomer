package com.myomer.myomer.util.oldProjectFiles.webservice;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.OPTIONS;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface RXInterface {

    @POST("{url}")
    Observable<JsonElement> makePostRequest( @Path(value = "url", encoded = true) String url, @Body JsonObject data);

    @POST("{url}")
    Observable<JsonElement> makePostRequest_Query(@Header("Authorization") String auth, @Path(value = "url", encoded = true) String url, @QueryMap Map<String, String> options, @Body JsonObject data, @Header("LanguageId") String languageID);

    @Multipart
    @POST("{url}")
    Observable<JsonElement> makePostRequest_media(@Header("Authorization") String auth, @Path(value = "url", encoded = true) String url, @Part MultipartBody.Part filePart, @Header("LanguageId") String languageID);

    @Multipart
    @PUT("{url}")
    Observable<JsonElement> makePutRequest_media(@Header("Authorization") String auth, @Path(value = "url", encoded = true) String url, @Part MultipartBody.Part filePart, @Header("LanguageId") String languageID);

    @PUT("{url}")
    Observable<JsonElement> makePutRequest(@Header("Authorization") String auth, @Path("url") String url, @Body JsonObject data, @Header("LanguageId") String languageID);

    @PATCH("{url}")
    Observable<JsonElement> makePatchRequest(@Header("Authorization") String auth, @Path("url") String url, @Body JsonObject data, @Header("LanguageId") String languageID);

    @OPTIONS("{url}")
    Observable<JsonElement> makeOptionsRequest(@Header("Authorization") String auth, @Path("url") String url, @Body JsonObject data, @Header("LanguageId") String languageID);

    @GET("{url}")
    Observable<JsonElement> makeGetRequest(@Header("Authorization") String auth, @Path(value = "url", encoded = true) String url, @Header("LanguageId") String languageID);

    @GET("{url}")
    Observable<JsonElement> makeGetRequest_Query(@Header("Authorization") String auth, @Path(value = "url", encoded = true) String url, @QueryMap Map<String, String> options, @Header("LanguageId") String languageID);

    @DELETE("{url}")
    Observable<JsonElement> makeDeleteRequest(@Header("Authorization") String auth, @Path(value = "url", encoded = true) String url, @Header("LanguageId") String languageID);



}
