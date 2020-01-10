package com.xinhuo.ocrdemo.http;

import android.os.Looper;
import android.util.Log;

import com.xinhuo.ocrdemo.MainActivity;
import com.xinhuo.ocrdemo.MyApp;
import com.xinhuo.ocrdemo.utils.AppUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiModule {

    // 若下面域名访问不了，两种方法：1换此域名http://192.168.2.132:8081/  2：wifi换xinhuo4G或5G
    private static final String BASE_URL = "http://112.30.110.198:28901/";

    private static ApiModule apiHelper;
    private ApiService apiService;

    public static ApiModule getInstance() {
        return apiHelper == null ? apiHelper = new ApiModule() : apiHelper;
    }

    private ApiModule() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)//设置连接超时时间
                .readTimeout(60, TimeUnit.SECONDS)//设置读取超时时间
//                    .addHeader("Content-Type", "application/json")
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException { // 拦截器
                        Request request = chain.request()
                                .newBuilder()
                                .build();
                        Response response = chain.proceed(request);
                        ResponseBody responseBody = response.body();
                        long contentLength = responseBody.contentLength();
                        if (!bodyEncoded(response.headers())) {
                            BufferedSource source = responseBody.source();
                            source.request(Long.MAX_VALUE); // Buffer the entire body.
                            Buffer buffer = source.buffer();

                            Charset charset = UTF8;
                            MediaType contentType = responseBody.contentType();
                            if (contentType != null) {
                                try {
                                    charset = contentType.charset(UTF8);
                                } catch (UnsupportedCharsetException e) {
                                    return response;
                                }
                            }
                            // 拦截器，
                            if (!isPlaintext(buffer)) return response;
                            if (contentLength != 0) {
                                String result = buffer.clone().readString(charset);
                                Log.e("MainActivity", " response====>:" + result);

//                                //解析数据
//                                JSONObject jsonObject = null;
//                                try {
//                                    jsonObject = new JSONObject(result);
//                                    int errCode = jsonObject.getInt("errCode");
//                                    String errInfo = jsonObject.getString("errInfo");
//                                    if(errCode != 0){
//                                        // 子线程 Toast需要创建Looper对象
//                                        Looper.prepare();
//                                        AppUtility.showToast(MyApp.getContext(), errInfo);
//                                        Looper.loop();
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
                            }
                        }
                        return chain.proceed(request);
                    }

                })
                .build();

        // 初始化Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // json解析
                .client(httpClient)
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public ApiService getRetrofitService() {
        return apiService;
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }

    private static final Charset UTF8 = Charset.forName("UTF-8");

    static boolean isPlaintext(Buffer buffer) throws EOFException {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }
}
