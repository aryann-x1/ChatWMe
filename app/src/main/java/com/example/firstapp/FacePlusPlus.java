/*package com.example.firstapp;


import java.io.ByteArrayOutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;// Define the API interface
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

// Initialize Retrofit
Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("https://api-us.faceplusplus.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();

FacePlusPlusAPI api = retrofit.create(FacePlusPlusAPI.class);

// Convert bitmap to Base64
ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
byte[] byteArray = byteArrayOutputStream.toByteArray();
String imageBase64 = Base64.encodeToString(byteArray, Base64.NO_WRAP);

// Make the API call
Call<ResponseBody> call = api.detectEmotion("YOUR_API_KEY", "YOUR_API_SECRET", imageBase64, "emotion");
call.enqueue(new Callback<ResponseBody>() {
    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        if (response.isSuccessful()) {
            // Parse and display emotion results
        } else {
            // Handle error
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        // Handle failure
    }
});*/
