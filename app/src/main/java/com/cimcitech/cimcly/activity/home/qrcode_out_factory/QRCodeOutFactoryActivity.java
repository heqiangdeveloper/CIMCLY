package com.cimcitech.cimcly.activity.home.qrcode_out_factory;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.bean.depart_request.RequestFeedbackBean;
import com.cimcitech.cimcly.utils.Config;
import com.google.gson.Gson;
import com.google.zxing.client.android.CaptureActivity2;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;


import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class QRCodeOutFactoryActivity extends AppCompatActivity{

    @Bind(R.id.qrcode_tv)
    TextView qrcode_Tv;
    @Bind(R.id.result_tv)
    TextView result_Tv;
    @Bind(R.id.back_rl)
    RelativeLayout backRl;
    @Bind(R.id.warn_tv)
    TextView warn_Tv;
    @Bind(R.id.out_factory_bt)
    Button out_factory_Btn;

    private final int PERMISSION_CODE = 1;
    private final int CAMERA_CODE = 2;
    private final String StartStr = "http://service.lingyu.com?id=";
    private String vehicleno = "";
    private boolean isCommitSuccess = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_out_factory);
        ButterKnife.bind(this);

        warn_Tv.setVisibility(View.GONE);
        result_Tv.setVisibility(View.GONE);
        out_factory_Btn.setVisibility(View.GONE);
    }

    @OnClick({R.id.qrcode_tv,R.id.back_rl,R.id.out_factory_bt})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.back_rl:
                finish();
                break;
            case R.id.qrcode_tv:
                //申请相机权限
                List<String> permissionList = new ArrayList<>();
                if(ContextCompat.checkSelfPermission(QRCodeOutFactoryActivity.this, Manifest.permission
                        .CAMERA) != PackageManager.PERMISSION_GRANTED){
                    permissionList.add(Manifest.permission.CAMERA);
                }
                if(!permissionList.isEmpty()){
                    String [] permissions = permissionList.toArray(new String[permissionList.size()]);
                    ActivityCompat.requestPermissions(QRCodeOutFactoryActivity.this,permissions,PERMISSION_CODE);
                }else {
                    getQRCode();
                }
                break;
            case R.id.out_factory_bt:
                if(vehicleno.length() != 0){
                    submitCarInStorageData(vehicleno);
                }
                break;
        }
    }

    public void getQRCode(){
        Intent i = new Intent(QRCodeOutFactoryActivity.this, CaptureActivity2.class);
        startActivityForResult(i,CAMERA_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_CODE:
                if(grantResults.length > 0){
                    for(int result : grantResults){
                        if(result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(QRCodeOutFactoryActivity.this,"必须同意相机使用权限",Toast.LENGTH_SHORT).show();
                            //finish();
                            return;
                        }
                    }
                    getQRCode();
                }else {
                    Toast.makeText(QRCodeOutFactoryActivity.this,"未知错误",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CODE && resultCode == RESULT_OK) {
            //Bundle bundle = data.getExtras();
            //String scanResult = bundle.getString("result");
            String scanResult = data.getStringExtra("CaptureIsbn");
            Log.d("hqtest","s is: " + scanResult);
            //二维码结果
            if(scanResult.length() > 29 && scanResult.substring(0,29).equals(StartStr)){
                vehicleno = scanResult.substring(29);
                warn_Tv.setVisibility(View.VISIBLE);
                result_Tv.setVisibility(View.VISIBLE);
                out_factory_Btn.setVisibility(View.VISIBLE);
                result_Tv.setText(vehicleno);
            }else if(scanResult.length() != 0 && scanResult.contains("L-") && scanResult.indexOf
                    ("L") == scanResult.length() - 1 - 5){//条形码扫描的结果
                vehicleno = scanResult;
                warn_Tv.setVisibility(View.VISIBLE);
                result_Tv.setVisibility(View.VISIBLE);
                out_factory_Btn.setVisibility(View.VISIBLE);
                result_Tv.setText(vehicleno);
            }
            else {
                vehicleno = "";
                warn_Tv.setVisibility(View.GONE);
                result_Tv.setVisibility(View.GONE);
                out_factory_Btn.setVisibility(View.GONE);
                Toast.makeText(QRCodeOutFactoryActivity.this,"请确认是否是正确的二维码或条形码！",Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public void submitCarInStorageData(String vehiclenos){
        OkHttpUtils
                .post()
                .url(Config.carOutFactoryAction)
                .addParams("userId", Config.loginback.getUserId() + "")
                .addParams("vehicleNos", vehiclenos)
                .addHeader("checkTokenKey", Config.loginback.getToken())
                .addHeader("sessionKey", Config.loginback.getUserId() + "")
                //.content(json)
                //.mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(
                        new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                warn_Tv.setVisibility(View.VISIBLE);
                                result_Tv.setVisibility(View.VISIBLE);
                                out_factory_Btn.setVisibility(View.VISIBLE);
                                Toast.makeText(QRCodeOutFactoryActivity.this,"出厂失败，请检查网络",Toast
                                        .LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                RequestFeedbackBean RequestFeedbackStr = new Gson().fromJson(response, RequestFeedbackBean.class);
                                if(RequestFeedbackStr.isSuccess()){
                                    warn_Tv.setVisibility(View.GONE);
                                    result_Tv.setVisibility(View.GONE);
                                    out_factory_Btn.setVisibility(View.GONE);
                                    if(RequestFeedbackStr.getMsg().trim().length() == 0){
                                        Toast.makeText(QRCodeOutFactoryActivity.this,"出厂成功",Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(QRCodeOutFactoryActivity.this,RequestFeedbackStr
                                                .getMsg(),Toast.LENGTH_SHORT).show();
                                    }
                                    vehicleno = "";
                                }else {
                                    warn_Tv.setVisibility(View.VISIBLE);
                                    result_Tv.setVisibility(View.VISIBLE);
                                    out_factory_Btn.setVisibility(View.VISIBLE);
                                    Toast.makeText(QRCodeOutFactoryActivity.this,RequestFeedbackStr
                                            .getMsg(),Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                );
    }
}
