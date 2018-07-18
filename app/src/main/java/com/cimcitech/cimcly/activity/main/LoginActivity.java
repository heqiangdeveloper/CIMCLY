package com.cimcitech.cimcly.activity.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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

        userNameTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(userNameTv.getText().toString().trim().length() != 0 &&
                        passwordTv.getText().toString().trim().length() != 0){
                    loginBtnOn();
                }else{
                    loginBtnOff();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passwordTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(userNameTv.getText().toString().trim().length() != 0 &&
                        passwordTv.getText().toString().trim().length() != 0){
                    loginBtnOn();
                }else{
                    loginBtnOff();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sp = this.getSharedPreferences(Config.KEY_LOGIN_AUTO, MODE_PRIVATE);//如果存在则打开它，否则创建新的Preferences
        getUserInfo();
    }

    public void loginBtnOn(){
        loginBt.setBackgroundResource(R.drawable.shape_login_button_on);
        loginBt.setClickable(true);
    }

    public void loginBtnOff(){
        loginBt.setBackgroundResource(R.drawable.shape_login_button_off);
        loginBt.setClickable(false);
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
            //System.out.println(name + pwd);
            userNameTv.setText(sp.getString("user_name", ""));
            passwordTv.setText(sp.getString("password", ""));
            userNameTv.setSelection(userNameTv.getText().toString().length());
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
                                    Log.d("heqlogin",response);
                                    if (loginVo != null) {
                                        if (loginVo.isSuccess()) {
                                            Config.isLogin = true;
                                            Config.loginback = loginVo.getData();
                                            saveUserInfo();
                                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                            Config.AppAuthStr = loginVo.getData().getAppAuth();
                                            Config.userName = loginVo.getData().getUserName();
                                            //Config.isLeader = true;
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            //String s = loginVo.getData().getAppAuth();
                                            //intent.putExtra("AppAuth", s);
                                            startActivity(intent);
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
