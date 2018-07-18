package com.cimcitech.cimcly.activity.home.work_weekly;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.bean.work_weekly.WorkWeeklyDetailVo;
import com.cimcitech.cimcly.bean.work_weekly.WorkWeeklyUpdateReq;
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

public class WorkWeeklyDetailActivity extends BaseActivity {

    @Bind(R.id.back_rl)
    RelativeLayout backRl;
    @Bind(R.id.start_time_tv)
    TextView startTimeTv;
    @Bind(R.id.end_time_tv)
    TextView endTimeTv;
    @Bind(R.id.performance_tv)
    EditText performanceTv;
    @Bind(R.id.nextworktask_tv)
    EditText nextworktaskTv;
    @Bind(R.id.add_bt)
    Button addBt;
    @Bind(R.id.user_name_tv)
    TextView userNameTv;
    @Bind(R.id.work_type_name_tv)
    TextView workTypeNameTv;
    @Bind(R.id.work_type_tv)
    TextView workTypeTv;
    @Bind(R.id.start_time_name_tv)
    TextView startTimeNameTv;
    @Bind(R.id.end_time_name_tv)
    TextView endTimeNameTv;
    @Bind(R.id.location_tv)
    TextView locationTv;
    @Bind(R.id.performance_name_tv)
    TextView performanceNameTv;
    @Bind(R.id.nextworktask_name_tv)
    TextView nextworktaskNameTv;

    private int repId;
    private WorkWeeklyDetailVo detailVo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_weekly_detail);
        ButterKnife.bind(this);
        repId = this.getIntent().getIntExtra("repId", repId);
        getData();
        showViewData();
    }

    public void showViewData() {
        if (Config.type == 1) {
            workTypeTv.setText("日报-早报");
        } else if (Config.type == 2) {
            workTypeTv.setText("日报-晚报");
        } else if (Config.type == 3) {
            workTypeTv.setText("日报-周报");
        }
    }

    @OnClick({R.id.add_bt, R.id.back_rl})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.back_rl:
                finish();
                break;
            case R.id.add_bt:
                if (detailVo != null)
                    mLoading.show();
                updateData();
                break;
        }
    }

    private void getData() {
        OkHttpUtils
                .post()
                .url(Config.workWeeklyInfo)
                .addHeader("checkTokenKey", Config.loginback.getToken())
                .addHeader("sessionKey", Config.loginback.getUserId() + "")
                .addParams("repId", repId + "")
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
                                detailVo = GjsonUtil.parseJsonWithGson(response, WorkWeeklyDetailVo.class);
                                if (detailVo != null)
                                    if (detailVo.isSuccess()) {
                                        userNameTv.setText(detailVo.getData().getUserName());
                                        startTimeTv.setText(DateTool.getDateStr(detailVo.getData().getBegintime()));
                                        endTimeTv.setText(DateTool.getDateStr(detailVo.getData().getEndtime()));
                                        performanceTv.setText(detailVo.getData().getPerformance());
                                        nextworktaskTv.setText(detailVo.getData().getNextworktask());
                                        locationTv.setText(detailVo.getData().getSignInAddress());
                                    }
                            }
                        }
                );
    }

    private void updateData() {

        String json = new Gson().toJson(new WorkWeeklyUpdateReq(
                detailVo.getData().getRepid(),
                nextworktaskTv.getText().toString().trim(),
                performanceTv.getText().toString().trim(),
                Config.loginback.getUserId()));

        OkHttpUtils
                .postString()
                .url(Config.updateWorkWeekly)
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
                                ToastUtil.showToast(response);
                                try {
                                    JSONObject json = new JSONObject(response);
                                    if (json.getBoolean("success")) {
                                        ToastUtil.showToast("保存成功");
                                        finish();
                                    } else
                                        ToastUtil.showToast("保存失败");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                mLoading.dismiss();
                            }
                        }
                );
    }
}
