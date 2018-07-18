package com.cimcitech.cimcly.activity.user;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.utils.ApkUpdateUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutActivity extends AppCompatActivity {
    @Bind(R.id.check_version_tv)
    TextView checkVersionTv;

    @Bind(R.id.more_tv)
    TextView more_Tv;
    @Bind(R.id.title_ll)
    LinearLayout title_Ll;
    @Bind(R.id.titleName_tv)
    TextView titleName_Tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about2);
        ButterKnife.bind(this);
        initTitle();

        checkVersionTv.setText("V" + ApkUpdateUtil.getVersionName(AboutActivity.this));
    }

    public void initTitle(){
        more_Tv.setVisibility(View.GONE);
        title_Ll.setVisibility(View.GONE);
        titleName_Tv.setText("关于");
    }

    @OnClick({R.id.back_iv})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_iv:
                finish();
                break;
        }
    }

}
