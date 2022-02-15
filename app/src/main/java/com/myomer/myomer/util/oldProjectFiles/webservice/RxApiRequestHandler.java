package com.myomer.myomer.util.oldProjectFiles.webservice;

import android.content.Context;

import com.google.gson.JsonObject;
import com.myomer.myomer.utilty.Constants;

/**
 * Created by Sagar on 05/04/2018.
 */

public class RxApiRequestHandler {


    public static void postUserData(Context context, ServiceCallBack callBack, String email, String first_name, String last_name) {

        try {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("email", email);
            jsonObject.addProperty("first_name", first_name);
            jsonObject.addProperty("last_name", last_name);

            RXRequestHandler handler = new RXRequestHandler();
            handler.makeApiRequest(context, "users/login", Constants.ApiTags.USER_DATA, callBack, jsonObject, null, true, Constants.ApiType.POST, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
