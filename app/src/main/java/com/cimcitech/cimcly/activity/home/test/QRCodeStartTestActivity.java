package com.cimcitech.cimcly.activity.home.test;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.bean.test.VehicleInfoVo;
import com.cimcitech.cimcly.utils.Config;
import com.cimcitech.cimcly.utils.ToastUtil;
import com.cimcitech.cimcly.widget.BaseActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class QRCodeStartTestActivity extends BaseActivity {

    @Bind(R.id.orderNo_tv)
    TextView orderNo_Tv;
    @Bind(R.id.proOrderNo_tv)
    TextView proOrderNo_Tv;
    @Bind(R.id.cust_name_tv)
    TextView cust_name_Tv;
    @Bind(R.id.vehicleNo_tv)
    TextView vehicleNo_Tv;
    @Bind(R.id.productionName_tv)
    TextView productionName_Tv;
    @Bind(R.id.isStandard_sp)
    Spinner isStandard_Sp;
    @Bind(R.id.remark_et)
    EditText remark_Et;
    @Bind(R.id.back_rl)
    RelativeLayout backRl;
    @Bind(R.id.submit_bt)
    Button submit_Bt;
    @Bind(R.id.cancel_bt)
    Button cancel_Bt;
    private String vehicleno = "";
    private VehicleInfoVo vehicleInfo = null;
    private final static String resultOK = "是";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_test_view);
        ButterKnife.bind(this);

        vehicleno = getIntent().getStringExtra("vehicleno");
        vehicleInfo = (VehicleInfoVo)getIntent().getSerializableExtra("vehicleInfo");
        initView();

        isStandard_Sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String data = (String)isStandard_Sp.getItemAtPosition(position);//从spinner中获取被选择的数据
                remark_Et.setText("");
                if(data.equals(resultOK)){
                    remark_Et.setHint("");
                }else {
                    remark_Et.setHint("请在此填写不合格的原因");
                    remark_Et.setHintTextColor(Color.RED);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void initView(){
        orderNo_Tv.setText(vehicleInfo.getData().getSorderNo());
        proOrderNo_Tv.setText(vehicleInfo.getData().getProdOrderNo());
        cust_name_Tv.setText(vehicleInfo.getData().getCustName());
        vehicleNo_Tv.setText(vehicleno);
        productionName_Tv.setText(vehicleInfo.getData().getProductionName());
    }

    @OnClick({R.id.submit_bt,R.id.back_rl,R.id.cancel_bt})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.back_rl:
                finish();
                break;
            case R.id.submit_bt:
                if(!checkInput()){//格式检查
                    return;
                }
                int scheduleResultId = vehicleInfo.getData().getScheduleResultId();
                String isStandard = ((String) isStandard_Sp.getSelectedItem()).equals(resultOK)
                        ?"Y":"N";
                String remark = remark_Et.getText().toString().trim();
                mLoading.show();
                submitTestData(scheduleResultId,isStandard,remark);
                break;
            case R.id.cancel_bt:
                finish();
                break;
        }
    }

    public boolean checkInput(){
        boolean isAllInput = false;
        String isStandardStr = (String) isStandard_Sp.getSelectedItem();
        if(isStandardStr.equals(resultOK)){//合格
            isAllInput =true;
        }else{//不合格
            if(remark_Et.getText().toString().trim().equals("") || remark_Et.getText().length() == 0){
                isAllInput = false;
            }else {
                isAllInput = true;
            }
        }
        return isAllInput;
    }

    /*
    *int scheduleResultId 当前步骤号
    * String isStandard 是否合格
    * String remark 备注
    * */
    public void submitTestData(int scheduleResultId,String isStandard, String remark){
        OkHttpUtils
                .get()
                .url(Config.submitTestResult)
                .addParams("scheduleResultId", scheduleResultId + "")
                .addParams("isStandard", isStandard)
                .addParams("remark", remark)
                .addParams("creator", Config.loginback.getUserId() + "")
                .addHeader("checkTokenKey", Config.loginback.getToken())
                .addHeader("sessionKey", Config.loginback.getUserId() + "")
                //.content(json)
                //.mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(
                        new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                ToastUtil.showNetError();
                                mLoading.dismiss();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                mLoading.dismiss();
                                try {
                                    JSONObject json = new JSONObject(response);
                                    if (json.getBoolean("success")) {
                                        Toast.makeText(QRCodeStartTestActivity.this,"提交成功",Toast
                                                .LENGTH_SHORT).show();
                                        Intent i = new Intent(QRCodeStartTestActivity.this,
                                                QRCodeTestActivity.class);
                                        startActivity(i);
                                        finish();
                                    } else{
                                        Toast.makeText(QRCodeStartTestActivity.this,json.getString("msg"),Toast
                                                .LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
    }
}
