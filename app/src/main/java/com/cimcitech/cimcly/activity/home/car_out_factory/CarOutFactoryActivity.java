package com.cimcitech.cimcly.activity.home.car_out_factory;

import android.net.LinkAddress;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cimcitech.cimcly.ApkApplication;
import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.activity.home.depart_request.DepartRequestActivity;
import com.cimcitech.cimcly.adapter.car_out_factory.CarOutFactoryAdapter;
import com.cimcitech.cimcly.adapter.depart_request.AlreadyInStorageRequsetAdapter;
import com.cimcitech.cimcly.bean.ListPagers;
import com.cimcitech.cimcly.bean.car_in_storage.WaitInStorageInfo;
import com.cimcitech.cimcly.bean.car_in_storage.WaitInStorageReq;
import com.cimcitech.cimcly.bean.Result;
import com.cimcitech.cimcly.bean.depart_request.RequestFeedbackBean;
import com.cimcitech.cimcly.utils.Config;
import com.cimcitech.cimcly.utils.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

/**
 * 我的客户
 */
public class CarOutFactoryActivity extends AppCompatActivity {
    @Bind(R.id.apply_bt)
    Button applyBt;
    @Bind(R.id.search_et)
    EditText searchEt;
    @Bind(R.id.search_bt)
    Button searchBt;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recycler_view_layout)
    CoordinatorLayout recyclerViewLayout;
    @Bind(R.id.title_ll)
    LinearLayout title_Ll;
    @Bind(R.id.more_tv)
    TextView more_Tv;
    @Bind(R.id.titleName_tv)
    TextView titleName_Tv;
    @Bind(R.id.status_ll)
    LinearLayout status_Ll;
    @Bind(R.id.who_ll)
    LinearLayout who_Ll;

    private int pageNum = 1;
    private Result<ListPagers<WaitInStorageInfo>> status;
    private List<WaitInStorageInfo> data = new ArrayList<>();
    private CarOutFactoryAdapter adapter;
    private Handler handler = new Handler();
    private boolean isLoading;
    private boolean isAlreadyRequest = true;
    private final int REFRESH_DATA = 1;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case REFRESH_DATA:
                    updateData();
                    break;
            }
        }
    };

    private void sendMsg(int flag){
        Message msg = new Message();
        msg.what = flag;
        mHandler.sendMessage(msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_out_factory2);
        ButterKnife.bind(this);
        initTitle();
        initViewData();
        getData();
    }

    public void initTitle(){
        more_Tv.setVisibility(View.GONE);
        titleName_Tv.setText("车辆出厂");
        title_Ll.setVisibility(View.VISIBLE);
        status_Ll.setVisibility(View.GONE);
        who_Ll.setVisibility(View.GONE);
    }

    //刷新数据
    private void updateData() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        adapter = new CarOutFactoryAdapter(CarOutFactoryActivity.this, data);
        recyclerView.setAdapter(adapter);
        //清除数据
        adapter.notifyDataSetChanged();
        this.data.clear();
        pageNum = 1;
        if (isAlreadyRequest)
            getData(); //获取数据
        else
            getSubData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Config.isDepartRequest) {
            Config.isDepartRequest = false;
            updateData();
        }
    }

    @OnClick({R.id.back_iv, R.id.apply_bt, R.id.search_bt})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.apply_bt:
                submitApply();
                break;
            case R.id.search_bt:
                updateData();
                ApkApplication.imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
        }
    }

    public void submitApply(){
        if(adapter != null){
            Map<Integer, Boolean> map = adapter.getMap();
            List<WaitInStorageInfo> selectData = new ArrayList<>();
            StringBuffer sb = new StringBuffer();
            for(int i = 0; i < map.size(); i++){
                if(map.get(i)){
                    WaitInStorageInfo info = data.get(i);
                    selectData.add(info);
                }
            }
            if(selectData.size() == 0){
                Toast.makeText(CarOutFactoryActivity.this,"您还未选择待出厂的车辆！",Toast.LENGTH_SHORT).show();
            }else if(selectData.size() == 1){
                sb.delete(0,sb.length());
                sb.append(selectData.get(0).getVehicleno());
                submitRequestData(sb.toString());
            }else if(selectData.size() > 1){
                sb.delete(0,sb.length());
                String sorderno = selectData.get(0).getSorderno();
                sb.append(selectData.get(0).getVehicleno() + ",");
                int i;
                for(i = 1; i < selectData.size(); i++){
                    Log.d("hqtest","car sorderno is: " +  selectData.get(i).getSorderno());
                    if(!selectData.get(i).getSorderno().equals(sorderno)){
                        break;
                    }
                    sb.append(selectData.get(i).getVehicleno() + ",");
                }
                if(i >= selectData.size()){
                    submitRequestData(sb.toString().substring(0,sb.toString().length()-1));
                }else {
                    Toast.makeText(CarOutFactoryActivity.this,"所选择的车辆订单号须一致！",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void submitRequestData(String vehiclenos){
        OkHttpUtils
                .post()
                .url(Config.carOutFactoryAction)
                .addParams("userId", Config.loginback.getUserId() + "")
                .addParams("vehicleNos", vehiclenos)
                .addHeader("checkTokenKey", Config.loginback.getToken())
                .addHeader("sessionKey", Config.loginback.getUserId() + "")
                //.content(json)
                //.mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(
                        new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Toast.makeText(CarOutFactoryActivity.this,"请检测网络",Toast
                                        .LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.d("hqrequest","response is: " + response);
                                RequestFeedbackBean RequestFeedbackStr = new Gson().fromJson(response, RequestFeedbackBean.class);
                                if(RequestFeedbackStr.isSuccess()){
                                    Toast.makeText(CarOutFactoryActivity.this,"车辆出厂成功",Toast
                                            .LENGTH_SHORT).show();
                                    sendMsg(REFRESH_DATA);
                                }else {
                                    Toast.makeText(CarOutFactoryActivity.this,RequestFeedbackStr
                                            .getMsg(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
    }

    public void submitCarOutStorage(){

    }

    public void initViewData() {
        adapter = new CarOutFactoryAdapter(CarOutFactoryActivity.this, data);
        swipeRefreshLayout.setColorSchemeResources(R.color.blueStatus);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //下拉刷新
                        adapter.notifyDataSetChanged();
                        data.clear(); //清除数据
                        pageNum = 1;
                        isLoading = false;
                        if (isAlreadyRequest)
                            getData(); //获取数据
                        else
                            getSubData();
                    }
                }, 1000);
            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(CarOutFactoryActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                /*int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == adapter.getItemCount()) {
                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                    if (isRefreshing) {
                        adapter.notifyItemRemoved(adapter.getItemCount());
                        return;
                    }*/
                int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0)
                        ? 0 : recyclerView.getChildAt(0).getTop();
                if (topRowVerticalPosition > 0) {
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                    if (isRefreshing) {
                        return;
                    }

                    if (!isLoading) {
                        isLoading = true;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //上拉加载
                                if (status.getData().isHasNextPage()) {
                                    pageNum++;
                                    if (isAlreadyRequest)
                                        getData();//添加数据
                                    else
                                        getSubData();
                                }
                                isLoading = false;
                            }
                        }, 1000);
                    }
                }
            }
        });
    }

    public void getData() {
        String json = new Gson().toJson(new WaitInStorageReq(pageNum, 10, "",
                new WaitInStorageReq.WaitInStorageReqBean(Config.loginback.getUserId() + "",
                        searchEt.getText().toString().trim())));
        OkHttpUtils
                .postString()
                .url(Config.alreadyRequestList)
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
                                Log.d("hetest","response is：" + response);
                                Type userlistType = new TypeToken<Result<ListPagers<WaitInStorageInfo>>>() {
                                }.getType();
                                status = new Gson().fromJson(response, userlistType);
                                if (status != null) {
                                    if (status.isSuccess()) {
                                        if (status.getData().getList() != null && status.getData().getList().size() > 0) {
                                            for (int i = 0; i < status.getData().getList().size(); i++) {
                                                data.add(status.getData().getList().get(i));//.getData().getList().get(i)
                                            }
                                        }
                                        if (status.getData().isHasNextPage()) {
                                            adapter.setNotMoreData(false);
                                        } else {
                                            adapter.setNotMoreData(true);
                                        }
                                        adapter.notifyDataSetChanged();
                                        swipeRefreshLayout.setRefreshing(false);
                                        adapter.notifyItemRemoved(adapter.getItemCount());
                                    }
                                } else {
                                    adapter.notifyDataSetChanged();
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            }
                        }
                );
    }


    //已申请发车的清单
    public void getSubData() {
        //adapter = new CarInStorageAdapter(CarInStorageActivity.this, data);
        String json = new Gson().toJson(new WaitInStorageReq(pageNum, 10, "",
                new WaitInStorageReq.WaitInStorageReqBean(Config.loginback.getUserId() + "",
                        searchEt.getText().toString().trim())));
        OkHttpUtils
                .postString()
                .url(Config.alreadyInStorageList)
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
                                Type userlistType = new TypeToken<Result<ListPagers<WaitInStorageInfo>>>() {
                                }.getType();
                                status = new Gson().fromJson(response, userlistType);
                                if (status != null) {
                                    if (status.isSuccess()) {
                                        if (status.getData().getList() != null && status.getData().getList().size() > 0) {
                                            for (int i = 0; i < status.getData().getList().size(); i++) {
                                                data.add(status.getData().getList().get(i));//.getData().getList().get(i)
                                            }
                                        }
                                        if (status.getData().isHasNextPage()) {
                                            adapter.setNotMoreData(false);
                                        } else {
                                            adapter.setNotMoreData(true);
                                        }
                                        adapter.notifyDataSetChanged();
                                        swipeRefreshLayout.setRefreshing(false);
                                        adapter.notifyItemRemoved(adapter.getItemCount());
                                    }
                                } else {
                                    adapter.notifyDataSetChanged();
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            }
                        }
                );
    }
}
