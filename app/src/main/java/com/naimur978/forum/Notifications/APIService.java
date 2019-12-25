package com.naimur978.forum.Notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAmM8ekS0:APA91bEqXkfsLyF4jnL5C16meUMvcmH0KqhUT2DTmDECPdfibkcyOWvHeKdi9R8OnXZBHBcclBIuIfYPNl8g0xlsbZckDVichHQ73wkqRaQK-lJFxkvJNGL3qCGl4GCPc93UvK5KBcuU"
            }
    )

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
