
package com.cimcitech.cimcly.activity.home.car_in_storage;

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
import com.cimcitech.cimcly.adapter.car_in_storage.AlreadyInStorageAdapter;
import com.cimcitech.cimcly.adapter.car_in_storage.WaitInStorageAdapter;
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
public class CarInStorageActivity extends AppCompatActivity {

    @Bind(R.id.back_rl)
    RelativeLayout backRl;
    @Bind(R.id.add_bt)
    Button addBt;
    @Bind(R.id.prepare_in_tv)
    TextView prepare_in_Tv;
    @Bind(R.id.in_tv)
    TextView in_Tv;
    @Bind(R.id.prepare_in_view)
    View prepare_in_View;
    @Bind(R.id.in_view)
    View in_View;
    @Bind(R.id.title_ll)
    LinearLayout titleLl;
    @Bind(R.id.search_et)
    EditText searchEt;
    @Bind(R.id.search_bt)
    Button searchBt;
    @Bind(R.id.search_bar)
    LinearLayout searchBar;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recycler_view_layout)
    CoordinatorLayout recyclerViewLayout;

    private int pageNum = 1;
    private Result<ListPagers<WaitInStorageInfo>> status;
    private List<WaitInStorageInfo> data_WaitInStorage = new ArrayList<>();
    private List<WaitInStorageInfo> data_AlreadyInStorage = new ArrayList<>();
    private WaitInStorageAdapter adapter_WaitInStorage;
    private AlreadyInStorageAdapter adapter_AlreadyInStorage;
    private Handler handler = new Handler();
    private boolean isLoading;
    public static boolean isWaitInStorage = true;
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
        setContentView(R.layout.activity_car_in_storage);
        ButterKnife.bind(this);
        isWaitInStorage = true;
        initViewData();
        getData();
    }

    public void initViewData() {
        adapter_WaitInStorage = new WaitInStorageAdapter(CarInStorageActivity.this, data_WaitInStorage);
        adapter_AlreadyInStorage = new AlreadyInStorageAdapter(CarInStorageActivity.this, data_AlreadyInStorage);
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
                        adapter_WaitInStorage.notifyDataSetChanged();
                        data_WaitInStorage.clear(); //清除数据
                        adapter_AlreadyInStorage.notifyDataSetChanged();
                        data_AlreadyInStorage.clear(); //清除数据
                        pageNum = 1;
                        isLoading = false;
                        if (isWaitInStorage)
                            getData(); //获取数据
                        else
                            getSubData();
                    }
                }, 1000);
            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(CarInStorageActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        if(isWaitInStorage){
            recyclerView.setAdapter(adapter_WaitInStorage);
        }else {
            recyclerView.setAdapter(adapter_AlreadyInStorage);
        }
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
                                    if (isWaitInStorage)
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
        //给List添加点击事件
        /*adapter_InStorage.setOnItemClickListener(new CarInStorageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClickListener(View view, int position) {
            }
        });*/
    }

    //刷新数据
    private void updateData() {
        //adapter = new CarInStorageAdapter(CarInStorageActivity.this, data);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        //清除数据
        //adapter.notifyDataSetChanged();
        //this.data.clear();
        pageNum = 1;
        if (isWaitInStorage){
            adapter_WaitInStorage = new WaitInStorageAdapter(CarInStorageActivity.this, data_WaitInStorage);
            recyclerView.setAdapter(adapter_WaitInStorage);
            adapter_WaitInStorage.notifyDataSetChanged();
            data_WaitInStorage.clear();
            getData(); //获取数据
        }else {
            adapter_AlreadyInStorage = new AlreadyInStorageAdapter(CarInStorageActivity.this, data_AlreadyInStorage);
            recyclerView.setAdapter(adapter_AlreadyInStorage);
            adapter_AlreadyInStorage.notifyDataSetChanged();
            data_AlreadyInStorage.clear();
            getSubData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Config.isCarInStorage) {
            Config.isCarInStorage = false;
            updateData();
        }
    }

    @OnClick({R.id.back_rl, R.id.prepare_in_tv, R.id.in_tv, R.id.add_bt, R.id.search_bt})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.back_rl:
                finish();
                break;
            case R.id.prepare_in_tv:
                isWaitInStorage = true;
                addBt.setText(isWaitInStorage? "入库":"退库");
                prepare_in_View.setVisibility(View.VISIBLE);
                in_View.setVisibility(View.INVISIBLE);
                updateData();
                break;
            case R.id.in_tv:
                isWaitInStorage = false;
                addBt.setText(isWaitInStorage? "入库":"退库");
                prepare_in_View.setVisibility(View.INVISIBLE);
                in_View.setVisibility(View.VISIBLE);
                updateData();
                break;
            case R.id.add_bt:
                //startActivity(new Intent(MyClientActivity.this, MyClientAddActivity.class));
                if(isWaitInStorage){
                    submitCarInStorage();//入库
                }else{
                    submitCarOutStorage();//退库
                }
                break;
            case R.id.search_bt:
                updateData();
                ApkApplication.imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
        }
    }

    public void submitCarInStorage(){
        if(adapter_WaitInStorage != null){
            Map<Integer, Boolean> map = adapter_WaitInStorage.getMap();
            List<WaitInStorageInfo> selectData = new ArrayList<>();
            StringBuffer sb = new StringBuffer();
            for(int i = 0; i < map.size(); i++){
                if(map.get(i)){
                    WaitInStorageInfo info = data_WaitInStorage.get(i);
                    selectData.add(info);
                }
            }
            if(selectData.size() == 0){
                Toast.makeText(CarInStorageActivity.this,"您还未选择待入库的车辆！",Toast.LENGTH_SHORT).show();
            }else if(selectData.size() == 1){
                sb.delete(0,sb.length());
                sb.append(selectData.get(0).getVehicleno());
                submitCarInStorageData(sb.toString());
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
                    submitCarInStorageData(sb.toString().substring(0,sb.toString().length()-1));
                }else {
                    Toast.makeText(CarInStorageActivity.this,"所选择的车辆订单号须一致！",Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    public void submitCarInStorageData(String vehiclenos){
        OkHttpUtils
                .post()
                .url(Config.waitInStorageAction)
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
                                Toast.makeText(CarInStorageActivity.this,"入库失败,请检查网络",Toast
                                        .LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                RequestFeedbackBean requestFeedbackStr = new Gson().fromJson(response, RequestFeedbackBean.class);
                                if(requestFeedbackStr.isSuccess()){
                                    Toast.makeText(CarInStorageActivity.this,"入库成功",Toast.LENGTH_SHORT).show();
                                    sendMsg(REFRESH_DATA);
                                }else{
                                    Toast.makeText(CarInStorageActivity.this,requestFeedbackStr.getMsg(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
    }

    public void submitCarOutStorageData(String vehiclenos){
        OkHttpUtils
                .post()
                .url(Config.outStorageAction)
                //.addParams("userId", Config.loginback.getUserId() + "")
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
                                Toast.makeText(CarInStorageActivity.this,"退库失败，请检查网络",Toast
                                        .LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                RequestFeedbackBean RequestFeedbackStr = new Gson().fromJson(response, RequestFeedbackBean.class);
                                if(RequestFeedbackStr.isSuccess()){
                                    Toast.makeText(CarInStorageActivity.this,"退库成功",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(CarInStorageActivity.this,RequestFeedbackStr.getMsg(),
                                            Toast.LENGTH_SHORT).show();
                                    sendMsg(REFRESH_DATA);
                                }
                            }
                        }
                );
    }

    //
    public void submitCarOutStorage(){
        if(adapter_AlreadyInStorage != null){
            Map<Integer, Boolean> map = adapter_AlreadyInStorage.getMap();
            List<WaitInStorageInfo> selectData = new ArrayList<>();
            StringBuffer sb = new StringBuffer();
            for(int i = 0; i < map.size(); i++){
                if(map.get(i)){
                    WaitInStorageInfo info = data_AlreadyInStorage.get(i);
                    selectData.add(info);
                }
            }
            if(selectData.size() == 0){
                Toast.makeText(CarInStorageActivity.this,"您还未选择要退库的车辆！",Toast.LENGTH_SHORT).show();
            }else if(selectData.size() == 1){
                sb.delete(0,sb.length());
                sb.append(selectData.get(0).getVehicleno());
                submitCarOutStorageData(sb.toString());
                /*if(isSubmitCarOutFactory && data != null && data.size() != 0){
                    Log.d("inlog","refresh data!");
                    isWaitInStorage = false;
                    data.clear();
                    updateData();
                }*/
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
                    submitCarOutStorageData(sb.toString().substring(0,sb.toString().length()-1));
                    /*if(isSubmitCarOutFactory && data != null && data.size() != 0){
                        data.clear();
                        updateData();
                    }*/
                }else {
                    Toast.makeText(CarInStorageActivity.this,"所选择的车辆订单号须一致！",Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    public void getData() {
        //recyclerView.setAdapter(adapter_InStorage);//绑定数据源
        String json = new Gson().toJson(new WaitInStorageReq(pageNum, 10, "",
                new WaitInStorageReq.WaitInStorageReqBean(Config.loginback.getUserId() + "",
                        searchEt.getText().toString().trim())));
        OkHttpUtils
                .postString()
                .url(Config.waitInStorageList)
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
                                Log.d("hqlog","response is：" + response);
                                Type userlistType = new TypeToken<Result<ListPagers<WaitInStorageInfo>>>() {
                                }.getType();
                                status = new Gson().fromJson(response, userlistType);
                                if (status != null) {
                                    if (status.isSuccess()) {
                                        if (status.getData().getList() != null && status.getData().getList().size() > 0) {
                                            for (int i = 0; i < status.getData().getList().size(); i++) {
                                                data_WaitInStorage.add(status.getData().getList().get(i));
                                            }
                                        }
                                        if (status.getData().isHasNextPage()) {
                                            adapter_WaitInStorage.setNotMoreData(false);
                                        } else {
                                            adapter_WaitInStorage.setNotMoreData(true);
                                        }

                                        adapter_WaitInStorage.notifyDataSetChanged();
                                        swipeRefreshLayout.setRefreshing(false);
                                        adapter_WaitInStorage.notifyItemRemoved(adapter_WaitInStorage.getItemCount());
                                    }
                                } else {
                                    adapter_WaitInStorage.notifyDataSetChanged();
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            }
                        }
                );
    }

    /**
     * 获取已入库列表
     */
    public void getSubData() {
        //recyclerView.setAdapter(adapter_OutFactory);//绑定数据源
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
                                                data_AlreadyInStorage.add(status.getData().getList().get(i));
                                            }
                                        }
                                        if (status.getData().isHasNextPage()) {
                                            adapter_AlreadyInStorage.setNotMoreData(false);
                                        } else {
                                            adapter_AlreadyInStorage.setNotMoreData(true);
                                        }
                                        adapter_AlreadyInStorage.notifyDataSetChanged();
                                        swipeRefreshLayout.setRefreshing(false);
                                        adapter_AlreadyInStorage.notifyItemRemoved(adapter_AlreadyInStorage.getItemCount());
                                    }
                                } else {
                                    adapter_AlreadyInStorage.notifyDataSetChanged();
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            }
                        }
                );
    }
}
