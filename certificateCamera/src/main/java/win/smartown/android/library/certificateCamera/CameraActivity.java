package win.smartown.android.library.certificateCamera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by smartown on 2018/2/24 11:46.
 * <br>
 * Desc:
 * <br>
 * 拍照界面
 */
public class CameraActivity extends Activity implements View.OnClickListener {

    public final static int REQUEST_CODE = 0X13;
    public final static int RESULT_CODE = 0X14;


    public static void openCertificateCamera(Activity activity) {
        Intent intent = new Intent(activity, CameraActivity.class);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * @return 结果文件路径
     */
    public static String getResult(Intent data) {
        if (data != null) {
            return data.getStringExtra("result");
        }
        return "";
    }

    private CameraPreview cameraPreview;
    private View containerView;
    private ImageView cropView;
    private ImageView flashImageView;
    private View optionView;
    private View resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera);
        cameraPreview = (CameraPreview) findViewById(R.id.camera_surface);

        //获取屏幕最小边，设置为cameraPreview较窄的一边
        float screenMinSize = Math.min(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
        //根据screenMinSize，计算出cameraPreview的较宽的一边，长宽比为标准的16:9
        float maxSize = screenMinSize / 9.0f * 16.0f;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) screenMinSize, (int) maxSize);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        cameraPreview.setLayoutParams(layoutParams);

        containerView = findViewById(R.id.camera_crop_container);
        cropView = (ImageView) findViewById(R.id.camera_crop);
        float width = (int) (screenMinSize * 0.8);
//        float height = (int) (width * 43.0f / 30.0f)-400;
        float height = width - 100;
        Log.i("MainActivity", "width===:" + width);
        Log.i("MainActivity", "height===:" + height);
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) height);
        containerView.setLayoutParams(containerParams);
        LinearLayout.LayoutParams cropParams = new LinearLayout.LayoutParams((int) width, (int) height);
        cropView.setLayoutParams(cropParams);

        flashImageView = (ImageView) findViewById(R.id.camera_flash);
        optionView = findViewById(R.id.camera_option);
        resultView = findViewById(R.id.camera_result);
        cameraPreview.setOnClickListener(this);
        findViewById(R.id.camera_close).setOnClickListener(this);
        findViewById(R.id.camera_take).setOnClickListener(this);
        flashImageView.setOnClickListener(this);
        findViewById(R.id.camera_result_ok).setOnClickListener(this);
        findViewById(R.id.camera_result_cancel).setOnClickListener(this);

        // 进入相机自动聚焦
        cameraPreview.focus();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.camera_surface) {
            // 手动触摸聚焦
            cameraPreview.focus();
        } else if (id == R.id.camera_close) {
            // 关闭相机
            finish();
        } else if (id == R.id.camera_take) {
            // 点击拍照
            takePhoto();
        } else if (id == R.id.camera_flash) {
            // 打开关闭闪光灯
            boolean isFlashOn = cameraPreview.switchFlashLight();
            flashImageView.setImageResource(isFlashOn ? R.mipmap.camera_flash_on : R.mipmap.camera_flash_off);
        } else if (id == R.id.camera_result_ok) {
            // 返回到拍照
            goBack();
        } else if (id == R.id.camera_result_cancel) {
            optionView.setVisibility(View.VISIBLE);
            cameraPreview.setEnabled(true);
            resultView.setVisibility(View.GONE);
            cameraPreview.startPreview();
        }
    }

    private void takePhoto() {
        optionView.setVisibility(View.GONE);
        cameraPreview.setEnabled(false);
        cameraPreview.takePhoto(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(final byte[] data, Camera camera) {
                camera.stopPreview();
                //子线程处理图片，防止ANR
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            File originalFile = getOriginalFile();
                            FileOutputStream originalFileOutputStream = new FileOutputStream(originalFile);
                            originalFileOutputStream.write(data);
                            originalFileOutputStream.close();

                            Bitmap bitmap = BitmapFactory.decodeFile(originalFile.getPath());

                            //计算裁剪位置
                            float left, top, right, bottom;
                            left = (float) cropView.getLeft() / (float) cameraPreview.getWidth();
                            top = ((float) containerView.getTop() - (float) cameraPreview.getTop()) / (float) cameraPreview.getHeight();
                            right = (float) cropView.getRight() / (float) cameraPreview.getWidth();
                            bottom = (float) containerView.getBottom() / (float) cameraPreview.getHeight();
                            //裁剪及保存到文件
                            Bitmap cropBitmap = Bitmap.createBitmap(bitmap,
                                    (int) (left * (float) bitmap.getWidth()),
                                    (int) (top * (float) bitmap.getHeight()),
                                    (int) ((right - left) * (float) bitmap.getWidth()),
                                    (int) ((bottom - top) * (float) bitmap.getHeight()));

                            final File cropFile = getCropFile();
                            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(cropFile));
                            cropBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                            bos.flush();
                            bos.close();
                            // Todo 拍照完成后直接返回主页
                            goBack();
                            return;
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                optionView.setVisibility(View.VISIBLE);
                                cameraPreview.setEnabled(true);
                            }
                        });
                    }
                }).start();

            }
        });
    }

    /**
     * @return 拍摄图片原始文件
     * <p>
     * Uri destination = Uri.fromFile(new File(getCacheDir(), SAMPLE_CROPPED_IMAGE_NAME));
     */
    String SAMPLE_CROPPED_IMAGE_NAME = "cropImage_" + System.currentTimeMillis() + ".jpg";

    private File getOriginalFile() {
//        return new File(getExternalCacheDir(), "picture.jpg");
        Log.e("cacheDir", ">>>>---:"+ getCacheDir());
        return new File(getCacheDir(), SAMPLE_CROPPED_IMAGE_NAME);
    }

    /**
     * @return 拍摄图片裁剪文件
     */
    private File getCropFile() {
        // 拍照裁剪存储内存
        return new File(getCacheDir(), SAMPLE_CROPPED_IMAGE_NAME);
        // 拍照裁剪后存储本地
//        return new File(getDiskCacheDir(this), SAMPLE_CROPPED_IMAGE_NAME);
    }

    /**
     * 获取缓存地址
     */
    public String getDiskCacheDir(Context context) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    /**
     * 点击对勾，使用拍照结果，返回对应图片路径
     */
    private void goBack() {
        Intent intent = new Intent();
        intent.putExtra("result", getCropFile().getPath());
        setResult(RESULT_CODE, intent);
        finish();
    }

}
