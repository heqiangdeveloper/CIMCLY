package com.cimcitech.cimcly.activity.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.bean.LoginVo;
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

public class LoginActivity extends BaseActivity {

    @Bind(R.id.user_name_tv)
    EditText userNameTv;
    @Bind(R.id.password_tv)
    EditText passwordTv;
    @Bind(R.id.login_bt)
    Button loginBt;

    private LoginVo loginVo;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        sp = this.getSharedPreferences(Config.KEY_LOGIN_AUTO, MODE_PRIVATE);
        getUserInfo();
    }

    @OnClick({R.id.clear_name_iv, R.id.clear_password_iv})
    public void onclick(View v) {
        switch (v.getId()) {
            case R.id.clear_name_iv:
                userNameTv.setText("");
                break;
            case R.id.clear_password_iv:
                passwordTv.setText("");
                break;
        }
    }

    private void getUserInfo() {
        if (sp.getString("user_name", "") != "") {
            String name = sp.getString("user_name", "");
            String pwd = sp.getString("password", "");
            System.out.println(name + pwd);
            userNameTv.setText(sp.getString("user_name", ""));
            passwordTv.setText(sp.getString("password", ""));
        }
    }

    public void getData() {
        OkHttpUtils
                .post()
                .url(Config.USER_LOGIN_URL)
                .addParams("userName", userNameTv.getText().toString().trim())
                .addParams("passwd", passwordTv.getText().toString().trim())
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
                                    loginVo = GjsonUtil.parseJsonWithGson(response, LoginVo.class);
                                    if (loginVo != null) {
                                        if (loginVo.isSuccess()) {
                                            Config.isLogin = true;
                                            Config.loginback = loginVo.getData();
                                            saveUserInfo();
                                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            finish();
                                        }
                                    } else {
                                        ToastUtil.showToast("登录失败");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                mLoading.dismiss();
                            }
                        }
                );
    }

    @OnClick(R.id.login_bt)
    public void onclick() {
        if (!checkInput()) return;
        mLoading.show();
        getData();
    }

    /***
     * 保存账户与密码
     */
    private void saveUserInfo() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("user_name", userNameTv.getText().toString().trim());
        editor.putString("password", passwordTv.getText().toString().trim());
        editor.commit();
    }

    public boolean checkInput() {
        String username = userNameTv.getText().toString().trim();
        String password = passwordTv.getText().toString().trim();
        if (password.equals("")) {
            Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (username.equals("")) {
            Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
