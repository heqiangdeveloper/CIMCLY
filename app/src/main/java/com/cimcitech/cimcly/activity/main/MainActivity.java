package com.cimcitech.cimcly.activity.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.chaychan.library.BottomBarItem;
import com.chaychan.library.BottomBarLayout;
import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.activity.user.DataCleanManager;
import com.cimcitech.cimcly.bean.AreaVo;
import com.cimcitech.cimcly.utils.Config;
import com.cimcitech.cimcly.utils.GjsonUtil;
import com.cimcitech.cimcly.utils.ToastUtil;
import com.cimcitech.cimcly.widget.DataGenerator;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;


public class MainActivity extends AppCompatActivity {
    private RadioGroup mRadioGroup;
    private Fragment[] mFragments;
    private RadioButton mRadioButtonHome;

    private String appAuthString = "";
    private static boolean mBackKeyPressed = false;//记录是否有首次按键
    private long firstTime = 0;
    private BottomBarLayout mBottomBarLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mFragments = DataGenerator.getFragments();
        mBottomBarLayout = (BottomBarLayout) findViewById(R.id.bbl) ;

        initView2();
        getAreaData();

        //达到缓存的上限，就清理缓存
        Runtime rt = Runtime.getRuntime();
        long maxMemory = rt.maxMemory();//该手机分配给每个APP的最大内存
        Log.i("heqmom", Long.toString(maxMemory / (1024 * 1024)));
        DataCleanManager manager = new DataCleanManager();
        long maxCache = maxMemory / 8;//缓存的上限
        try {
            long currentCache = manager.getFolderSize(getApplication().getCacheDir());
            if (currentCache >= maxCache) {
                manager.clearAllCache(getApplication());
            }
        } catch (Exception e) {

        }
    }

    public void initView2(){
        mBottomBarLayout.setOnItemSelectedListener(new BottomBarLayout.OnItemSelectedListener() {
            Fragment mFragment = null;
            @Override
            public void onItemSelected(BottomBarItem bottomBarItem, int i, int position) {
                for(int n = 0; n < 4; n++){
                    mBottomBarLayout.getBottomItem(n).setStatus(false);
                }
                if(position == 0){//消息
                    mFragment = mFragments[0];
                }else if(position == 1){//客户拜访
                    mFragment = mFragments[1];
                }else if(position == 2){//意向跟踪
                    mFragment = mFragments[2];
                }else{//我的
                    mFragment = mFragments[3];
                }
                mBottomBarLayout.getBottomItem(position).setStatus(true);
                if (mFragments != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_container, mFragment).commit();
                }
            }
        });
        //mBottomBarLayout.getBottomItem(1).setStatus(true);
        mBottomBarLayout.getBottomItem(0).callOnClick();//默认选中首页
    }

    /**
     * 获取省市级联的省市信息
     */
    public void getAreaData() {
        /*if( Config.loginback == null){
            Config.loginback.setToken("2FD08ED0-E53B-48B1-B8E6-E6B4290A2770");
        }*/
        OkHttpUtils
                .post()
                .url(Config.getProviceAndCity)
                .addHeader("checkTokenKey", Config.loginback.getToken())
                .addHeader("sessionKey", Config.loginback.getUserId() + "")
                .build()
                .execute(
                        new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                ToastUtil.showNetError();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                // ToastUtil.showToast(response);
                                try {
                                    Config.areaVo = GjsonUtil.parseJsonWithGson(response, AreaVo.class);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);//防止快速切换Fragment导致的FC-->不保存Fragment的状态，让其与activity一同消亡
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
                System.exit(0);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
