package com.cimcitech.cimcly.activity.home.customer_visit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.activity.home.intention_track.IntentionTrackAddActivity;
import com.cimcitech.cimcly.bean.customer_visit.CustomerVisitInfoVo;
import com.cimcitech.cimcly.bean.customer_visit.UpdateCustomerVisitInfoReq;
import com.cimcitech.cimcly.utils.Config;
import com.cimcitech.cimcly.utils.DateTool;
import com.cimcitech.cimcly.utils.GjsonUtil;
import com.cimcitech.cimcly.utils.ToastUtil;
import com.cimcitech.cimcly.widget.BaseActivity;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

public class CustomerVisitDetailActivity extends BaseActivity {

    @Bind(R.id.back_rl)
    RelativeLayout backRl;
    @Bind(R.id.client_no_tv)
    TextView clientNoTv;
    @Bind(R.id.create_time_tv)
    TextView createTimeTv;
    @Bind(R.id.client_name_tv)
    TextView clientNameTv;
    @Bind(R.id.contact_person_tv)
    TextView contactPersonTv;
    @Bind(R.id.mobile_tv)
    TextView mobileTv;
    @Bind(R.id.visit_time_tv)
    TextView visitTimeTv;
    @Bind(R.id.visit_time_2_tv)
    TextView visitTime2Tv;
    @Bind(R.id.client_address_tv)
    TextView clientAddressTv;
    @Bind(R.id.check_in_tv)
    TextView checkInTv;
    @Bind(R.id.visit_summary_tv)
    EditText visitSummaryTv;
    @Bind(R.id.save_tv)
    Button saveTv;
    @Bind(R.id.create_tv)
    Button createTv;

    private int cvid;
    private CustomerVisitInfoVo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_visit_detail);
        ButterKnife.bind(this);
        cvid = this.getIntent().getIntExtra("cvid", 0);
        getData();
    }

    @OnClick({R.id.save_tv, R.id.create_tv, R.id.back_rl})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.create_tv:
                Intent intent = new Intent(CustomerVisitDetailActivity.this, IntentionTrackAddActivity.class);
                intent.putExtra("CustomerVisitInfo", info);
                startActivity(intent);
                break;
            case R.id.save_tv:
                if (visitSummaryTv.getText().toString().trim().equals("")) {
                    ToastUtil.showToast("请输入拜访总结");
                } else {
                    mLoading.show();
                    updateData();
                }
                break;
            case R.id.back_rl:
                finish();
                break;
        }
    }

    public void initViewData() {
        clientNoTv.setText(info.getData().getContpersonid() != null ? info.getData().getContpersonid() + "" : "");
        createTimeTv.setText(DateTool.getDateStr(info.getData().getCreatedate()));
        clientNameTv.setText(info.getData().getCustname());
        contactPersonTv.setText(info.getData().getContpersonname());
        mobileTv.setText(info.getData().getCustphone());
        visitTimeTv.setText(info.getData().getVisitbegintime());
        visitTime2Tv.setText(info.getData().getVisitendtime());
        clientAddressTv.setText(info.getData().getCustaddress());
        checkInTv.setText(info.getData().getSigninaddress());
        visitSummaryTv.setText(info.getData().getVisitsummary());
        visitSummaryTv.setSelection(visitSummaryTv.getText().length());
    }

    public void getData() {
        OkHttpUtils
                .post()
                .url(Config.getCustVisitInfo)
                .addHeader("checkTokenKey", Config.loginback.getToken())
                .addHeader("sessionKey", Config.loginback.getUserId() + "")
                .addParams("cvId", cvid + "")
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
                                try {
                                    info = GjsonUtil.parseJsonWithGson(response, CustomerVisitInfoVo.class);
                                    if(info!=null)
                                    if (info.isSuccess()) {
                                        initViewData();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
    }

    public void updateData() {
        int cvid = info.getData().getCvid();
        int userid = info.getData().getUserid();
        Long custid = info.getData().getCustid();
        String customerno = info.getData().getCustomerno();
        String custname = info.getData().getCustname();
        String custaddress = info.getData().getCustaddress();
        int custmanagerid = info.getData().getCustmanagerid();
        String custmanagername = info.getData().getCustmanagername();
        Long contpersonid = info.getData().getContpersonid();
        String signinaddress = info.getData().getSigninaddress();
        double sigininlat = info.getData().getSigininlat();
        double sigininlon = info.getData().getSigininlon();
        String visitsummary = visitSummaryTv.getText().toString().trim();
        String custphone = info.getData().getCustphone();
        int modifiedby = info.getData().getModifiedby();
        int creator = info.getData().getCreator();
        String visitbegintime = info.getData().getVisitbegintime();
        String visitendtime = info.getData().getVisitendtime();

        String json = new Gson().toJson(new UpdateCustomerVisitInfoReq(cvid,
                userid, custid, customerno, custname,
                custaddress, custmanagerid, custmanagername, contpersonid, signinaddress, sigininlat
                , sigininlon, visitsummary, custphone, modifiedby, creator, visitbegintime, visitendtime));
        OkHttpUtils
                .postString()
                .url(Config.modifyCustVisit)
                .addHeader("checkTokenKey", Config.loginback.getToken())
                .addHeader("sessionKey", Config.loginback.getUserId() + "")
                .content(json)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
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
                                //ToastUtil.showToast(response);
                                try {
                                    JSONObject json = new JSONObject(response);
                                    if (json.getBoolean("success")) {
                                        ToastUtil.showToast("保存成功");
                                        finish();
                                    } else {
                                        ToastUtil.showToast("保存失败");
                                    }
                                    mLoading.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
    }
}
