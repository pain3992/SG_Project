package com.graduate.seoil.sg_projdct.Fragments;

import com.graduate.seoil.sg_projdct.Notification.MyResponse;
import com.graduate.seoil.sg_projdct.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAoS8WkXQ:APA91bF9R2rPrUFwJhk_FANAQhj8DAUl8d0bBi_u6NvCCLEa5bXRpIJmy-fxHdRbNhDdlv7otkCjPpOpOXOTW13q6BDmzPdwqLgRgwPxTTPOa7b-VOOh1pTvmF5xLAhIDU1v21pBb9RK"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
