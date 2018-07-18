package com.cimcitech.cimcly.activity.home.feed_back;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.adapter.PopupWindowAdapter;
import com.cimcitech.cimcly.bean.feed_back.FeedBackReq;
import com.cimcitech.cimcly.bean.feed_back.QuestionTypeVo;
import com.cimcitech.cimcly.bean.order_contract.OrderContractSaveVo;
import com.cimcitech.cimcly.utils.Config;
import com.cimcitech.cimcly.utils.GjsonUtil;
import com.cimcitech.cimcly.utils.ToastUtil;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

public class FeedBackActivity extends AppCompatActivity {

    @Bind(R.id.back_rl)
    RelativeLayout backRl;
    @Bind(R.id.questiontheme_tv)
    EditText questionthemeTv;
    @Bind(R.id.questiontype_tv)
    TextView questiontypeTv;
    @Bind(R.id.questiondetail_tv)
    EditText questiondetailTv;
    @Bind(R.id.login_bt)
    Button loginBt;

    private PopupWindow pop;
    private QuestionTypeVo typeVo;
    private QuestionTypeVo.DataBean typeItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        ButterKnife.bind(this);
        getData();
    }

    @OnClick({R.id.back_rl, R.id.login_bt, R.id.questiontype_tv})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.back_rl:
                finish();
                break;
            case R.id.login_bt:
                if (questionthemeTv.getText().toString().trim().equals("")) {
                    ToastUtil.showToast("请输入反馈主题");
                    return;
                }
                if (typeItem == null) {
                    ToastUtil.showToast("请选择反馈类型");
                    return;
                }
                if (questiondetailTv.getText().toString().trim().equals("")) {
                    ToastUtil.showToast("请输入您的宝贵意见");
                    return;
                }
                submitData();
                break;
            case R.id.questiontype_tv:
                if (typeVo.isSuccess()) {
                    List<String> list = new ArrayList<>();
                    for (int i = 0; i < typeVo.getData().size(); i++)
                        list.add(typeVo.getData().get(i).getCodevalue());
                    showContactUsPopWin(FeedBackActivity.this, "选择反馈类型", list);
                    pop.showAtLocation(view, Gravity.CENTER, 0, 0);
                }
                break;
        }
    }

    public void showContactUsPopWin(Context context, String title, List<String> list) {
        LayoutInflater inflater = LayoutInflater.from(context);
        // 引入窗口配置文件
        View view = inflater.inflate(R.layout.dialog_add_client_view, null);
        view.getBackground().setAlpha(100);
        // 创建PopupWindow对象
        pop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, false);
        View pop_reward_view = view.findViewById(R.id.pop_reward_view);
        TextView title_tv = view.findViewById(R.id.title_tv);
        title_tv.setText(title);
        final PopupWindowAdapter adapter = new PopupWindowAdapter(context, list);
        ListView listView = view.findViewById(R.id.listContent);
        listView.setAdapter(adapter);
        // 需要设置一下此参数，点击外边可消失
        pop.setBackgroundDrawable(new BitmapDrawable());
        // 设置点击窗口外边窗口消失
        pop.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        pop.setFocusable(true);
        pop_reward_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop.dismiss();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                typeItem = typeVo.getData().get(position);
                questiontypeTv.setText(typeItem.getCodevalue());
                pop.dismiss();
            }
        });
    }

    //修改
    public void submitData() {

        String questiontheme = questionthemeTv.getText().toString().trim();
        String questiontype = typeItem.getCodeid();
        int userid = Config.loginback.getUserId();
        String questiondetail = questiondetailTv.getText().toString().trim();

        String json = new Gson().toJson(new FeedBackReq(questiontheme, questiontype, userid, questiondetail));

        OkHttpUtils
                .postString()
                .url(Config.addFeedback)
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
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                try {
                                    JSONObject json = new JSONObject(response);
                                    if (json.getBoolean("success")) {
                                        ToastUtil.showToast("提交成功");
                                        finish();
                                    } else {
                                        ToastUtil.showToast("提交失败");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
    }


    public void getData() {
        OkHttpUtils
                .post()
                .url(Config.getQuestionType)
                .addHeader("checkTokenKey", Config.loginback.getToken())
                .addHeader("sessionKey", Config.loginback.getUserId() + "")
                .build()
                .execute(
                        new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Toast.makeText(Config.context, e.toString(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                //ToastUtil.showToast(response);
                                typeVo = GjsonUtil.parseJsonWithGson(response, QuestionTypeVo.class);
                            }
                        }
                );
    }
}
