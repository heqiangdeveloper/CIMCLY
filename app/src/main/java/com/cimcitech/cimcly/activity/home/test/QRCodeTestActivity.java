package com.cimcitech.cimcly.activity.home.test;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.bean.test.VehicleInfoVo;
import com.cimcitech.cimcly.utils.Config;
import com.google.gson.Gson;
import com.xys.libzxing.zxing.activity.CaptureActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class QRCodeTestActivity extends AppCompatActivity{

    @Bind(R.id.qrcode_tv)
    TextView qrcode_Tv;
    @Bind(R.id.result_tv)
    TextView result_Tv;
    @Bind(R.id.back_rl)
    RelativeLayout backRl;
    @Bind(R.id.start_test_bt)
    Button start_test_Bt;
    @Bind(R.id.warn_tv)
    TextView warn_Tv;

    private final int PERMISSION_CODE = 1;
    private final int CAMERA_CODE = 2;
    private final String StartStr = "http://service.lingyu.com?id=";
    private String vehicleno = "";
    private VehicleInfoVo vehicleInfoVo =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);

        warn_Tv.setVisibility(View.GONE);
        result_Tv.setVisibility(View.GONE);
        start_test_Bt.setVisibility(View.GONE);
    }

    @OnClick({R.id.qrcode_tv,R.id.back_rl,R.id.start_test_bt})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.back_rl:
                finish();
                break;
            case R.id.qrcode_tv:
                //申请相机权限
                List<String> permissionList = new ArrayList<>();
                if(ContextCompat.checkSelfPermission(QRCodeTestActivity.this, Manifest.permission
                        .CAMERA) != PackageManager.PERMISSION_GRANTED){
                    permissionList.add(Manifest.permission.CAMERA);
                }
                if(!permissionList.isEmpty()){
                    String [] permissions = permissionList.toArray(new String[permissionList.size()]);
                    ActivityCompat.requestPermissions(QRCodeTestActivity.this,permissions,PERMISSION_CODE);
                }else {
                    getQRCode();
                }
                break;
            case R.id.start_test_bt:
                if(vehicleno.length() != 0){
                    isVehicleAlreadyTest(vehicleno);
                }
                break;
        }
    }

    public void getQRCode(){
        Intent i = new Intent(QRCodeTestActivity.this, CaptureActivity.class);
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
                            Toast.makeText(QRCodeTestActivity.this,"必须同意相机使用权限",Toast.LENGTH_SHORT).show();
                            //finish();
                            return;
                        }
                    }
                    getQRCode();
                }else {
                    Toast.makeText(QRCodeTestActivity.this,"未知错误",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            Log.d("hqtest","s is: " + scanResult);
            if(scanResult.length() > 29 && scanResult.substring(0,29).equals(StartStr)){
                vehicleno = scanResult.substring(29);
                warn_Tv.setVisibility(View.VISIBLE);
                result_Tv.setVisibility(View.VISIBLE);
                start_test_Bt.setVisibility(View.VISIBLE);
                result_Tv.setText(vehicleno);
            }else if(scanResult.length() != 0 && scanResult.contains("L-") && scanResult.indexOf
                    ("L") == scanResult.length() - 1 - 5){//条形码扫描的结果
                vehicleno = scanResult;
                warn_Tv.setVisibility(View.VISIBLE);
                result_Tv.setVisibility(View.VISIBLE);
                start_test_Bt.setVisibility(View.VISIBLE);
                result_Tv.setText(vehicleno);
            }
            else {
                vehicleno = "";
                warn_Tv.setVisibility(View.GONE);
                result_Tv.setVisibility(View.GONE);
                start_test_Bt.setVisibility(View.GONE);
                Toast.makeText(QRCodeTestActivity.this,"请确认是否是正确的二维码或条形码！",Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public void isVehicleAlreadyTest(final String vehicleno){
        OkHttpUtils
                .get()
                .url(Config.getVehicleInfo)
                //.addParams("userId", Config.loginback.getUserId() + "")
                .addParams("vehicleNo", vehicleno)
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
                                start_test_Bt.setVisibility(View.VISIBLE);
                                Toast.makeText(QRCodeTestActivity.this,"查看检验状态失败，请检测网络！",Toast
                                        .LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.d("qrlog","response is: " + response);
                                vehicleInfoVo = new Gson().fromJson(response, VehicleInfoVo.class);
                                if(vehicleInfoVo.isSuccess()){
                                    String productionName = vehicleInfoVo.getData().getProductionName();
                                    warn_Tv.setVisibility(View.GONE);
                                    result_Tv.setVisibility(View.GONE);
                                    start_test_Bt.setVisibility(View.GONE);
                                    if(productionName.equals("")){//已检测
                                        Toast.makeText(QRCodeTestActivity.this,"该车辆已经检验过，请确认！",
                                                Toast.LENGTH_SHORT).show();
                                        //vehicleno = "";
                                    }else{//未检验
                                        Intent i = new Intent(QRCodeTestActivity.this,QRCodeStartTestActivity.class);
                                        i.putExtra("vehicleInfo",vehicleInfoVo);
                                        i.putExtra("vehicleno",vehicleno);
                                        startActivity(i);
                                        //finish();
                                    }
                                }else{
                                    warn_Tv.setVisibility(View.VISIBLE);
                                    result_Tv.setVisibility(View.VISIBLE);
                                    start_test_Bt.setVisibility(View.VISIBLE);
                                    Toast.makeText(QRCodeTestActivity.this,vehicleInfoVo.getMsg(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
    }
}
