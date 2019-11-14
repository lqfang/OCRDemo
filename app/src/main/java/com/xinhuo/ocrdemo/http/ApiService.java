package com.xinhuo.ocrdemo.http;


import com.xinhuo.ocrdemo.entity.WordsResult;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    // 这个是得到 json字符串
    @Headers({"Content-Type: application/json"})
    @POST("ocr")
    Call<WordsResult> getResult(@Body RequestBody requestBody);

    // 这个是得到 json字符串
    @Headers({"Content-Type: application/json"})
    @POST("ocr")
    Call<ResponseBody> getJson(@Body RequestBody requestBody);
}
