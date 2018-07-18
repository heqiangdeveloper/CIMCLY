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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.bean.test.VehicleInfoVo;
import com.cimcitech.cimcly.utils.Config;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class QRCodeWriteTestActivity extends AppCompatActivity{
    @Bind(R.id.year_spinner)
    Spinner year_Spinner;
    @Bind(R.id.partb_et)
    EditText partb_Et;
    @Bind(R.id.partc_tv)
    TextView partc_Tv;
    @Bind(R.id.partd_et)
    EditText partd_Et;

    @Bind(R.id.more_tv)
    TextView more_Tv;
    @Bind(R.id.title_ll)
    LinearLayout title_Ll;
    @Bind(R.id.titleName_tv)
    TextView titleName_Tv;

    private final int PERMISSION_CODE = 1;
    private final int CAMERA_CODE = 2;
    private final String StartStr = "http://service.lingyu.com?id=";
    private String vehicleno = "";
    private VehicleInfoVo vehicleInfoVo =null;
    private final int PARTAINDEX = 3;//默认是2018年
    private String selectYear = "";//A段
    private String PARTBSTR = "";//B段
    private final String PARTCSTR = "L-";//C段
    private String PARTDSTR = "";//D段
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_test2);
        ButterKnife.bind(this);
        initTitle();
        initView();
        getYear();
        partb_Et.setTransformationMethod(new AutoCaseTransformationMethod());
    }

    public void initTitle(){
        more_Tv.setVisibility(View.GONE);
        title_Ll.setVisibility(View.GONE);
        titleName_Tv.setText("手工填写车工号");
    }

    //初始化界面
    public void initView(){
        partc_Tv.setText(PARTCSTR);
        //year_Spinner.setSelection(PARTAINDEX);
    }

    public void getYear(){
        year_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectYear = (String) year_Spinner.getAdapter().getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick({R.id.back_iv,R.id.start_test_bt})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.start_test_bt:
                if(!checkInput()){
                    return;
                }
                Log.d("qrlog","vehicleno is： " + vehicleno);
                isVehicleAlreadyTest(vehicleno);
                break;
        }
    }

    public boolean checkInput(){
        /*  车工号规则：
        *  1、	A段，前两位年份后两位是枚举值，要求用户下拉选择，默认是今年；
        *  2、	B段，第3位到固定字符“L”中间是用户手工输入的物料号，有字母和数据组成，强制转化为大写；
        *  3、	C段，固定字符“L-”；
        *  4、	D段，纯数字流水号，用户手工输入，要求位数限制为4位，只能输入数字格式，不足四位强制在前面补0；
        *   18      J020       L-      0023
        *  A段     B段        C段      D段
         */
        PARTBSTR = partb_Et.getText().toString().trim();
        PARTDSTR = partd_Et.getText().toString().trim();
        if(PARTBSTR.length() == 0 || PARTDSTR.length() == 0){
            Toast.makeText(QRCodeWriteTestActivity.this,"请填写完整车工号！",Toast.LENGTH_SHORT).show();
            return false;
        }else if(PARTBSTR.length() < 4){//B段最少是4位
            Toast.makeText(QRCodeWriteTestActivity.this,"B段最少是4位！",Toast.LENGTH_SHORT).show();
            return false;
        }else{
            //D段：要求位数限制为4位，只能输入数字格式，不足四位强制在前面补0；
            switch (PARTDSTR.length()){
                case 1:
                    PARTDSTR = "000" + PARTDSTR;
                    break;
                case 2:
                    PARTDSTR = "00" + PARTDSTR;
                    break;
                case 3:
                    PARTDSTR = "0" + PARTDSTR;
                    break;
                default:
                    break;
            }
            //B段，有字母和数据组成，强制转化为大写
            PARTBSTR = PARTBSTR.toUpperCase();
            Log.d("qrlog","PARTBSTR is: " + PARTBSTR);
            vehicleno = selectYear + PARTBSTR + PARTCSTR + PARTDSTR;//车工号
            return true;
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
                                Toast.makeText(QRCodeWriteTestActivity.this,"查看检验状态失败，请检测网络！",Toast
                                        .LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.d("qrlog","response is: " + response);
                                vehicleInfoVo = new Gson().fromJson(response, VehicleInfoVo.class);
                                if(vehicleInfoVo.isSuccess()){
                                    String productionName = vehicleInfoVo.getData().getProductionName();
                                    if(null == productionName){//车工号不正确
                                        Toast.makeText(QRCodeWriteTestActivity.this,
                                                "未查到该车工号信息，请确认输入的车工号是否正确！",Toast.LENGTH_SHORT).show();
                                    }else if(productionName != null && productionName.equals("")) {//已检测
                                        Toast.makeText(QRCodeWriteTestActivity.this,"该车辆已经检验过，请确认！",
                                                Toast.LENGTH_SHORT).show();
                                        //vehicleno = "";
                                    }else{//未检验
                                        Intent i = new Intent(QRCodeWriteTestActivity.this,QRCodeStartTestActivity.class);
                                        i.putExtra("vehicleInfo",vehicleInfoVo);
                                        i.putExtra("vehicleno",vehicleno);
                                        startActivity(i);
                                        //finish();
                                    }
                                }else{
                                    Toast.makeText(QRCodeWriteTestActivity.this,"查看检验状态失败，请检测网络！",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
    }
}
