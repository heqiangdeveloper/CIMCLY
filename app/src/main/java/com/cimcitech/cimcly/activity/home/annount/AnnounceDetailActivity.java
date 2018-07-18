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
import android.widget.LinearLayout;
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
    @Bind(R.id.title_tv)
    TextView titleTv;
    @Bind(R.id.time_tv)
    TextView timeTv;

    @Bind(R.id.webview)
    WebView wv;

    @Bind(R.id.more_tv)
    TextView more_Tv;
    @Bind(R.id.title_ll)
    LinearLayout title_Ll;
    @Bind(R.id.titleName_tv)
    TextView titleName_Tv;

    private String HTML = "";
    private int annId;
    private AnnounceDetailVo detailVo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annount_detail2);
        ButterKnife.bind(this);
        initTitle();
        annId = this.getIntent().getIntExtra("annId", 0);
        getData();
    }

    public void initTitle(){
        more_Tv.setVisibility(View.GONE);
        title_Ll.setVisibility(View.GONE);
        titleName_Tv.setText("公告详情");
    }

    @OnClick(R.id.back_iv)
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
}
