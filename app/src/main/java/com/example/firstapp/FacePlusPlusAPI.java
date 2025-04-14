package com.example.firstapp;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FacePlusPlusAPI {
    @FormUrlEncoded
    @POST("facepp/v3/detect")
    Call<ResponseBody> detectEmotion(
            @Field("api_key") String apiKey,
            @Field("api_secret") String apiSecret,
            @Field("image_base64") String imageBase64,
            @Field("return_attributes") String returnAttributes
    );
}
