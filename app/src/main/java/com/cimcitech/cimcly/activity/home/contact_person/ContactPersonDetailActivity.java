package com.cimcitech.cimcly.activity.home.contact_person;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.activity.home.customer_visit.CustomerVisitAddActivity;
import com.cimcitech.cimcly.bean.contact.ContactInfoVo;
import com.cimcitech.cimcly.utils.Config;
import com.cimcitech.cimcly.utils.DateTool;
import com.cimcitech.cimcly.utils.GjsonUtil;
import com.cimcitech.cimcly.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class ContactPersonDetailActivity extends AppCompatActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.back_rl)
    RelativeLayout backRl;
    @Bind(R.id.add_bt)
    Button addBt;
    @Bind(R.id.user_name_tv)
    TextView userNameTv;
    @Bind(R.id.mobile_tv)
    TextView mobileTv;
    @Bind(R.id.phone_tv)
    TextView phoneTv;
    @Bind(R.id.area_tv)
    TextView areaTv;
    @Bind(R.id.address_tv)
    TextView addressTv;
    @Bind(R.id.time_tv)
    TextView timeTv;
    @Bind(R.id.customer_no_tv)
    TextView customerNoTv;
    @Bind(R.id.update_bt)
    Button updateBt;

    private long contPersonId;
    private ContactInfoVo contactInfoVo;
    private final int AREA_CITY_RESULT = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_person_detail);
        ButterKnife.bind(this);
        contPersonId = this.getIntent().getLongExtra("contPersonId", 0);
        getData();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AREA_CITY_RESULT && resultCode == RESULT_OK) {
            contPersonId = Long.parseLong(data.getExtras().getString("contPersonId"));
            getData();
        }
    }

    @OnClick({R.id.back_rl, R.id.add_bt, R.id.update_bt})
    public void onclick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.back_rl:
                finish();
                break;
            case R.id.update_bt:
                intent = new Intent(ContactPersonDetailActivity.this, ContactPersonUpdateActivity.class);
                intent.putExtra("contactInfoVo", contactInfoVo);
                startActivityForResult(intent, AREA_CITY_RESULT);
                break;
            case R.id.add_bt:
                intent = new Intent(ContactPersonDetailActivity.this, CustomerVisitAddActivity.class);
                intent.putExtra("contactInfoVo",contactInfoVo);
                startActivity(intent);
                break;
        }
    }

    public void getData() {
        OkHttpUtils
                .post()
                .url(Config.getContactInfo)
                .addHeader("checkTokenKey", Config.loginback.getToken())
                .addHeader("sessionKey", Config.loginback.getUserId() + "")
                .addParams("contPersonId", contPersonId + "")
                .build()
                .execute(
                        new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                ToastUtil.showNetError();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.d("timelog","response is: " + response);
                                try {
                                    contactInfoVo = GjsonUtil.parseJsonWithGson(response, ContactInfoVo.class);
                                    if(contactInfoVo!=null)
                                    if (contactInfoVo.isSuccess()) {
                                        initViewData();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
    }

    private void initViewData() {
        String province = "";
        String city = "";
        userNameTv.setText(contactInfoVo.getData().getPersonname() != null ?
                contactInfoVo.getData().getPersonname() : "");
        phoneTv.setText(contactInfoVo.getData().getContmobile() != null ?
                contactInfoVo.getData().getContmobile() + "" : "");
        mobileTv.setText(contactInfoVo.getData().getConttel() != null ?
                contactInfoVo.getData().getConttel() + "" : "");
        if (contactInfoVo.getData().getFamilyarea() != null && !contactInfoVo.getData().getFamilyarea().equals(""))
            province = contactInfoVo.getData().getFamilyarea() + "";
        if (contactInfoVo.getData().getFamilycity() != null && !contactInfoVo.getData().getFamilycity().equals(""))
            city = contactInfoVo.getData().getFamilycity() + "";
        areaTv.setText(province + " " + city);
        addressTv.setText(contactInfoVo.getData().getFamilyaddress() != null ?
                contactInfoVo.getData().getFamilyaddress() + "" : "");
        if (contactInfoVo.getData().getCreatedate() > 0)
            timeTv.setText(DateTool.getDateStr(contactInfoVo.getData().getCreatedate()));
        if (contactInfoVo.getData().getCustomerNo() != null && !contactInfoVo.getData().getCustomerNo().equals(""))
            customerNoTv.setText(contactInfoVo.getData().getCustomerNo() + "");

    }
}
