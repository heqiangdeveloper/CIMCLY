package com.cimcitech.cimcly.activity.home.annount;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.WebBackForwardList;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.bean.announce.AnnounceDetailVo;
import com.cimcitech.cimcly.utils.Config;
import com.cimcitech.cimcly.utils.DateTool;
import com.cimcitech.cimcly.utils.GjsonUtil;
import com.cimcitech.cimcly.utils.ToastUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class AnnounceDetailActivity extends AppCompatActivity {

    @Bind(R.id.back_rl)
    RelativeLayout backRl;
    @Bind(R.id.title_tv)
    TextView titleTv;
    @Bind(R.id.time_tv)
    TextView timeTv;
    //@Bind(R.id.content_tv)
    //TextView contentTv;

    @Bind(R.id.webview)
    WebView wv;

    private String HTML = "";
    private int annId;
    private AnnounceDetailVo detailVo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annount_detail);
        ButterKnife.bind(this);
        annId = this.getIntent().getIntExtra("annId", 0);
        getData();
    }

    @OnClick(R.id.back_rl)
    public void onclick() {
        finish();
    }

    public void getData() {
        OkHttpUtils
                .post()
                .url(Config.announceDetail)
                .addHeader("checkTokenKey", Config.loginback.getToken())
                .addHeader("sessionKey", Config.loginback.getUserId() + "")
                .addParams("annId", annId + "")
                .build()
                .execute(
                        new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                ToastUtil.showNetError();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                //ToastUtil.showToast(response);
                                detailVo = GjsonUtil.parseJsonWithGson(response, AnnounceDetailVo.class);
                                if (detailVo != null)
                                    if (detailVo.isSuccess()) {
                                        titleTv.setText(detailVo.getData().getAnntitle());
                                        timeTv.setText(DateTool.getDateStr(detailVo.getData().getCreatedate()));
                                        //contentTv.setText(Html.fromHtml(detailVo.getData().getAnncontent()));
                                        HTML = detailVo.getData().getAnncontent();
                                        //showHtmlToString(HTML, contentTv);
                                        if(wv.getSettings() != null){
                                            wv.getSettings().setJavaScriptEnabled(false);
                                            wv.setVerticalScrollBarEnabled(false);
                                            wv.setHorizontalScrollBarEnabled(false);
                                            wv.getSettings().setUseWideViewPort(true);//设置webview显示屏幕宽度 不能滑动
                                            wv.getSettings().setLoadWithOverviewMode(true);
                                            //是否支持缩放
                                            wv.getSettings().setSupportZoom(true);
                                            wv.getSettings().setBuiltInZoomControls(true);
                                            wv.getSettings().setDisplayZoomControls(false);
                                            wv.getSettings().setTextSize(WebSettings.TextSize.LARGEST);
                                        }
                                        wv.loadDataWithBaseURL(null, HTML, "text/html",
                                                "utf-8", null);
                                    }
                            }
                        }
                );
    }

    //public void showHtmlToString(final String html,final TextView textView) {

        //解析html
        /*Html.ImageGetter imgGetter = new Html.ImageGetter() {
            public Drawable getDrawable(String source) {
                Log.i("RG", "source---?>>>" + source);
                Drawable drawable = null;
                URL url;
                try {
                    url = new URL(source);
                    Log.i("RG", "url---?>>>" + url);
                    // 获取网路图片
                    drawable = Drawable.createFromStream(url.openStream(), "");
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                Log.i("RG", "url---?>>>" + url);
                return drawable;
            }
        };
        textView.setText(Html.fromHtml(html, imgGetter, null));*/
    //}

    //mTextView03.setText(Html.fromHtml(htmlFor03, mNetWorkImageGetter, null));

    private NetWorkImageGetter mNetWorkImageGetter = new NetWorkImageGetter();

    class NetWorkImageGetter implements Html.ImageGetter {
        @Override
        public Drawable getDrawable(String source) {
            Log.i("heqann","source is: " + source);
            Drawable drawable = null;
            final String imgName = source.substring(source.lastIndexOf("/") + 1);
            Log.i("heqann","imgName is: " + imgName);
            File file = new File(Environment.getExternalStorageDirectory(), imgName);
            if (file.exists()) {
                drawable = Drawable.createFromPath(file.getAbsolutePath());
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth() * 2, drawable.getIntrinsicHeight() * 2);

                return  drawable;
                //得到图片的分辨率，获取宽度
                /*DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                int mScreenWidth = dm.widthPixels;// 获取屏幕分辨率宽度
                int mScreenHeight = dm.heightPixels;

                if(drawable.getIntrinsicWidth() * 2 > mScreenWidth){
                    //加载图片
                    Bitmap bitmap = drawableToBitmap(drawable);
                    int bitmapWidth = bitmap.getWidth();
                    int bitmapHeight = bitmap.getHeight();
                    //需要判断屏幕宽度和图片宽度的大小，来决定是放大还是缩小，如果是放大，应该还需要加上图片本身宽度，即：（倍数+1）为缩放倍数float num = ((float)bitmapWidth/mScreenWidth)+1.0f;
                    //得到图片宽度比
                    float num = 2 * mScreenWidth / (float)bitmapWidth;

                    Matrix matrix = new Matrix();
                    matrix.postScale(num, num);
                    // 产生缩放后的Bitmap对象
                    Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth,
                            bitmapHeight, matrix, true);

                }else {
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth() * 2, drawable.getIntrinsicHeight() * 2);
                }*/

            } else {
                getNetworkImg(source,imgName);
            }
            return drawable;
        }

        Bitmap drawableToBitmap(Drawable drawable) // drawable 转换成 bitmap
        {
            int width = drawable.getIntrinsicWidth();   // 取 drawable 的长宽
            int height = drawable.getIntrinsicHeight();
            Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888:Bitmap.Config.RGB_565;         // 取 drawable 的颜色格式
            Bitmap bitmap = Bitmap.createBitmap(width, height, config);     // 建立对应 bitmap
            Canvas canvas = new Canvas(bitmap);         // 建立对应 bitmap 的画布
            drawable.setBounds(0, 0, width, height);
            drawable.draw(canvas);      // 把 drawable 内容画到画布中
            return bitmap;
        }
    }

    public void showHtmlToString(String html, final TextView textView){
        textView.setText(Html.fromHtml(html, mNetWorkImageGetter, null));
    }


    private void getNetworkImg(final String url,final String imgName) {

        //显示图片的配置
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        ImageLoader.getInstance().loadImage(url, options, new ImageLoadingListener() {


            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                //contentTv.setText(Html.fromHtml(imageUri, null, null));
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                saveMyBitmap(imgName, loadedImage);
                //contentTv.setText(Html.fromHtml(HTML, mNetWorkImageGetter, null));
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                //解析html
                Html.ImageGetter imgGetter = new Html.ImageGetter() {
                    public Drawable getDrawable(String source) {
                        Log.i("RG", "source---?>>>" + source);
                        Drawable drawable = null;
                        URL url;
                        try {
                            url = new URL(source);
                            Log.i("RG", "url---?>>>" + url);
                            // 获取网路图片
                            drawable = Drawable.createFromStream(url.openStream(), "");
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth()*2, drawable
                                .getIntrinsicHeight()*2);
                        Log.i("RG", "url---?>>>" + url);
                        return drawable;
                    }
                };
            }
        }).start();*/

    }


    /**
     * 保存获取到的网络图片到sdcard
     * @param bitName
     * @param mBitmap
     */
    public void saveMyBitmap(String bitName, Bitmap mBitmap) {
        File f = new File("/sdcard/" + bitName);
        try {
            f.createNewFile();
        } catch (IOException e) {
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
