package com.xinhuo.ocrdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mingle.widget.LoadingView;
import com.xinhuo.ocrdemo.adapter.MyAdapter;
import com.xinhuo.ocrdemo.adapter.MyWordsBeanAdapter;
import com.xinhuo.ocrdemo.entity.KeyBean;
import com.xinhuo.ocrdemo.entity.WordsResult;
import com.xinhuo.ocrdemo.http.ApiModule;
import com.xinhuo.ocrdemo.utils.AppUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import win.smartown.android.library.certificateCamera.CameraActivity;

public class MainActivity extends Activity {

    private View view;
    private LoadingView loadingView;
    private RecyclerView recyclerView;
    private ImageView ivBack;
    private ImageView ivImage; //显示拍照后的图片
    private TextView tv_txt;
    private LinearLayout linearLayout;

    private boolean isShow = false;

    private MyAdapter mAdapter; // OCR的列表适配器（type=0）

    // 创建容器，将json解析的key和value存入进去
    KeyBean bean ;
    private List<KeyBean> list = new ArrayList<>();
    private MyWordsBeanAdapter myWordsBeanAdapter;
    private static final String TAG = MainActivity.class.getSimpleName();

    public static void gotoMain(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        loadingView = (LoadingView) findViewById(R.id.load_view);
        view = (View) findViewById(R.id.view);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivImage = (ImageView) findViewById(R.id.iv_image);
        tv_txt = findViewById(R.id.tv_txt);
        linearLayout = findViewById(R.id.ll_result);

        loadingView.setLoadingText("正在加载中...");
        loadingView.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MyAdapter(this);
        recyclerView.setAdapter(mAdapter);

//        myWordsBeanAdapter = new MyWordsBeanAdapter(this);
//        recyclerView.setAdapter(myWordsBeanAdapter);

        // 默认进入打开相机
        isShow = true;

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
//        Log.e(TAG, " isShow====>:" + isShow);
        if (isShow)
            takePhoto();
    }

    /**
     * 拍摄照片
     */
    private void takePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0x12);
            return;
        }
        CameraActivity.openCertificateCamera(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CameraActivity.REQUEST_CODE && resultCode == CameraActivity.RESULT_CODE) {
            //获取文件路径，显示图片
            String path = CameraActivity.getResult(data);
            if (!TextUtils.isEmpty(path)) {
                ivImage.setImageBitmap(BitmapFactory.decodeFile(path));
            }
            // 若返回的路径没有file头部，手动添加
            if (!path.contains("file://")) {
                path = "file://" + path;
            }
            Log.i(TAG, "path====:" + path);

            // 开启加载动画
            loadingView.setVisibility(View.VISIBLE);
            isShow = false;

            // file:///data/user/0/com.xinhuo.camerademo/cache/cropImage_1569402893422.jpg
            // /storage/emulated/0/Android/data/com.xinhuo.ocrdemo/cache/companyInfoCrop.jpg
            Bitmap bitmap = AppUtility.decodeUri(this, Uri.parse(path), 340, 1920);
            Log.i(TAG, "bitmap===:" + bitmap);
            if (bitmap == null) {
                AppUtility.showToast(this, "未获取到图片，请重试");
                isShow = true;
                return;
            }
            // 获取到bitmap，转为base64
            Base64Image(bitmap);
        }
    }

    /**
     * 图片转Base64
     */
    private void Base64Image(Bitmap bitmap) {
        final String png = "data:image/png;base64,";
        final String jpeg = "data:image/jpeg;base64,";

//        String base64Data = png + AppUtility.imgToBase64(bitmap);
        String base64Data = jpeg + AppUtility.imgToBase64(bitmap);
        // 把字符串中"/",替换为"_",防止转json后"/"变成"\/"
        String base64Data1 = base64Data.replace("/", "_");
        Log.i(TAG, "base64Data====:" + base64Data);
        if (!bitmap.isRecycled()) bitmap.recycle();

        // OCR 文字识别
        getWordsResult(base64Data1);
        // 身份识别
//        getWordsBean(base64Data1);
    }

    /**
     * "image": "image/jpeg;base64,XXX...", // 图片文件二进制数组经过base64编码后 的字符串，目前支持jpg、tif、png、bmp等多种格式
     * "type": "0" // 目前支持0、1、2、3四种类型，分别表示通 用OCR、身份证、车牌、营业执照
     */
    private void getWordsResult(String img) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "0");
            jsonObject.put("image", img);
            jsonObject.put("token", "b4de50360422ccd2a9655cddddc88888");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String stringJson = jsonObject.toString().replace("_", "/");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), stringJson);
        ApiModule.getInstance().getRetrofitService().getResult(requestBody).enqueue(new Callback<WordsResult>() {
            @Override
            public void onResponse(Call<WordsResult> call, Response<WordsResult> response) {
                loadingView.setVisibility(View.GONE);
                Log.e(TAG, " ===response====>:" + response);
                if (response.code() == 200) {
                    WordsResult wordsResult = response.body();
                    Log.e(TAG, " ===wordsResult====>:" + wordsResult);
                    // 当出现参数缺少等错误，弹出错误信息
                    int errCode = wordsResult.getErrCode();
                    if (errCode != 0) {
                        String errInfo = wordsResult.getErrInfo();
                        AppUtility.showToast(MyApp.getContext(), errInfo);
                    }

                    if (wordsResult.getWords_result() != null) {
                        if (wordsResult.getWords_result().size() == 0) {
                            AppUtility.showToast(MainActivity.this, "未识别出结果，请重试");
                            return;
                        }
                        mAdapter.setDatas(wordsResult.getWords_result());
                    }
                }
            }

            @Override
            public void onFailure(Call<WordsResult> call, Throwable t) {
                loadingView.setVisibility(View.GONE);
                AppUtility.showToast(MainActivity.this, "网络请求超时, 请重试");
                Log.e(TAG, " t====>:" + t.getMessage());
                // 请求失败，重新调相机拍照识别
//                takePhoto();
            }
        });
    }

    /**
     * "image": "image/jpeg;base64,XXX...", // 图片文件二进制数组经过base64编码后 的字符串，目前支持jpg、tif、png、bmp等多种格式
     * "type": "0" // 目前支持0、1、2、3四种类型，分别表示通 用OCR、身份证、车牌、营业执照
     */
    private void getWordsBean(String img) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "1");
            jsonObject.put("image", img);
            jsonObject.put("token", "b4de50360422ccd2a9655cddddc88888");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String stringJson = jsonObject.toString().replace("_", "/");

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), stringJson);
        ApiModule.getInstance().getRetrofitService().getResponseResult(requestBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                loadingView.setVisibility(View.GONE);
                Log.e(TAG, " ===response====>:" + response);
                if (response.code() == 200) {
                    String result = doJson(response.body());
                    Log.e(TAG, " ===wordsBean====>:" + result);
                    //解析数据
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                        int words_result_num = jsonObject.getInt("words_result_num");
                        String words_result = jsonObject.getString("words_result");
                        if (words_result_num == 0) {
                            AppUtility.showToast(MainActivity.this, "未识别出结果，请重试");
                            return;
                        }

                        //解析数据
                        jsonObject = new JSONObject(words_result);
                        Iterator<String> keys = jsonObject.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            String value = jsonObject.optString(key);
                            bean = new KeyBean();
                            bean.setKey(key);
                            bean.setValue(value);
                            list.add(bean);
                        }
//                        myWordsBeanAdapter.setDatas(words_result_num, list);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loadingView.setVisibility(View.GONE);
                AppUtility.showToast(MainActivity.this, "网络请求超时, 请重试");
                Log.e(TAG, " t====>:" + t.getMessage());
                // 请求失败，重新调相机拍照识别
//                takePhoto();
            }
        });
    }

    /**
     * ResponseBody 处理成Json
     */
    private String doJson(ResponseBody responseBody) {
        long contentLength = responseBody.contentLength();
        BufferedSource source = responseBody.source();
        try {
            source.request(Long.MAX_VALUE); // Buffer the entire body.
        } catch (IOException e) {
            e.printStackTrace();
        }
        Buffer buffer = source.buffer();
        Charset charset = UTF8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            try {
                charset = contentType.charset(UTF8);
            } catch (UnsupportedCharsetException e) {
                e.printStackTrace();
            }
        }
        String result = "";
        // 拦截器，
        if (contentLength != 0) {
            result = buffer.clone().readString(charset);
//            Log.e("MainActivity", " doJson====>:" + result);
        }
        return result;
    }

    private static final Charset UTF8 = Charset.forName("UTF-8");


    /** 上次点击返回键的时间 */
    private long lastBackPressed;

    /** 两次点击的间隔时间 */
    private static final int QUIT_INTERVAL = 2000;


    /**
     * 重写onKeyDown()
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            long backPressed = System.currentTimeMillis();
            if (backPressed - lastBackPressed > QUIT_INTERVAL) {
                lastBackPressed = backPressed;
                AppUtility.showToast(MainActivity.this, "再按一次退出");
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 重写onBackPressed()
     */
    @Override
    public void onBackPressed() {
        long backPressed = System.currentTimeMillis();
        super.onBackPressed();
        if (backPressed - lastBackPressed > QUIT_INTERVAL) {
            lastBackPressed = backPressed;
            AppUtility.showToast(MainActivity.this, "再按一次退出");
        } else {
            finish();
            System.exit(0);
        }
    }
}
