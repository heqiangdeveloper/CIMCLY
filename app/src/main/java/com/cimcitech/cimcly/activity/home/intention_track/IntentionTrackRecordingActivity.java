package com.cimcitech.cimcly.activity.home.intention_track;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.adapter.intention_track.IntentionTrackRecordingAdapter;
import com.cimcitech.cimcly.bean.opport_unit.IntentionTrackRecordingVo;
import com.cimcitech.cimcly.utils.Config;
import com.cimcitech.cimcly.utils.GjsonUtil;
import com.cimcitech.cimcly.utils.ToastUtil;
import com.cimcitech.cimcly.widget.BaseActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 跟进记录
 */
public class IntentionTrackRecordingActivity extends BaseActivity {
    @Bind(R.id.listContent)
    ListView listContent;
    @Bind(R.id.more_tv)
    TextView more_Tv;
    @Bind(R.id.title_ll)
    LinearLayout title_Ll;
    @Bind(R.id.titleName_tv)
    TextView titleName_Tv;

    private int opportId;
    private IntentionTrackRecordingVo intentionTrackRecordingVo;
    private IntentionTrackRecordingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intention_track_recording2);
        ButterKnife.bind(this);
        initTitle();
        opportId = this.getIntent().getIntExtra("opportId", 0);
        mLoading.show();
        getData();
    }

    public void initTitle(){
        more_Tv.setVisibility(View.GONE);
        title_Ll.setVisibility(View.GONE);
        titleName_Tv.setText("意向跟进");
    }

    @OnClick(R.id.back_iv)
    public void onclick() {
        finish();
    }

    public void getData() {
        OkHttpUtils
                .post()
                .url(Config.getListByOppoertId)
                .addHeader("checkTokenKey", Config.loginback.getToken())
                .addHeader("sessionKey", Config.loginback.getUserId() + "")
                .addParams("opportId", opportId + "")
                .build()
                .execute(
                        new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                if(mLoading.isShowing()){
                                    mLoading.dismiss();
                                }
                                ToastUtil.showNetError();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                //ToastUtil.showToast(response);
                                try {
                                    intentionTrackRecordingVo = GjsonUtil.parseJsonWithGson(response, IntentionTrackRecordingVo.class);
                                    if(intentionTrackRecordingVo!=null)
                                    if (intentionTrackRecordingVo.isSuccess()) {
                                        adapter = new IntentionTrackRecordingAdapter(IntentionTrackRecordingActivity.this,
                                                intentionTrackRecordingVo.getData());
                                        listContent.setAdapter(adapter);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if(mLoading.isShowing()){
                                    mLoading.dismiss();
                                }
                            }
                        }
                );
    }
}
