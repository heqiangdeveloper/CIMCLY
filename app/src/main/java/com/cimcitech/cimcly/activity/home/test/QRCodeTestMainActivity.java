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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class QRCodeTestMainActivity extends AppCompatActivity{

    @Bind(R.id.qrcode_tv)
    TextView qrcode_Tv;
    @Bind(R.id.back_rl)
    RelativeLayout backRl;
    @Bind(R.id.write_tv)
    TextView write_Tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.qrcode_tv,R.id.back_rl,R.id.write_tv})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.back_rl:
                finish();
                break;
            case R.id.qrcode_tv://扫码获取车工号
                Intent i1 = new Intent(QRCodeTestMainActivity.this,QRCodeTestActivity.class);
                startActivity(i1);
                break;
            case R.id.write_tv://手工填写车工号
                Intent i2 = new Intent(QRCodeTestMainActivity.this,QRCodeWriteTestActivity.class);
                startActivity(i2);
                break;
        }
    }
}
