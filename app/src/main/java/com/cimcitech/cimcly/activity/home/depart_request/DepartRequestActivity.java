
package com.cimcitech.cimcly.activity.home.depart_request;

import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cimcitech.cimcly.ApkApplication;
import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.adapter.car_in_storage.AlreadyInStorageAdapter;
import com.cimcitech.cimcly.adapter.car_in_storage.WaitInStorageAdapter;
import com.cimcitech.cimcly.adapter.depart_request.AlreadyInStorageRequsetAdapter;
import com.cimcitech.cimcly.adapter.depart_request.AlreadyRequsetAdapter;
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
public class DepartRequestActivity extends AppCompatActivity {
    @Bind(R.id.apply_bt)
    Button applyBt;
    @Bind(R.id.my_tv)
    TextView myTv;
    @Bind(R.id.xs_tv)
    TextView xsTv;
    @Bind(R.id.my_view)
    View myView;
    @Bind(R.id.xs_view)
    View xsView;
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
    @Bind(R.id.who_spinner)
    Spinner whoSpinner;
    @Bind(R.id.who_ll)
    LinearLayout who_Ll;

    private int pageNum = 1;
    private Result<ListPagers<WaitInStorageInfo>> status;
    private List<WaitInStorageInfo> data_AlreadyInStorageRequset = new ArrayList<>();
    private List<WaitInStorageInfo> data_AlreadyRequset = new ArrayList<>();
    private AlreadyInStorageRequsetAdapter adapter_AlreadyInStorageRequset;
    private AlreadyRequsetAdapter adapter_AlreadyRequset;
    private Handler handler = new Handler();
    private boolean isLoading;
    public static boolean isAlreadyInStorageRequset= true;
    private final int REFRESH_DATA = 1;
    private ArrayAdapter<String> arr_adapter;

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
        setContentView(R.layout.activity_depart_request2);
        ButterKnife.bind(this);
        initTitle();
        isAlreadyInStorageRequset = true;
        initViewData();
        getData();
        applyBt.setVisibility(View.VISIBLE);
        setSpinnerListener();
    }

    public void initTitle(){
        more_Tv.setVisibility(View.GONE);
        whoSpinner.setVisibility(View.VISIBLE);
        titleName_Tv.setText("发车申请");
        title_Ll.setVisibility(View.VISIBLE);
        status_Ll.setVisibility(View.GONE);
        who_Ll.setVisibility(View.GONE);

        //myTv.setText("已入库");
        //xsTv.setText("已申请");

        //数据
        List<String> data_list = new ArrayList<String>();
        data_list.add("已入库");
        data_list.add("已申请");

        //适配器
        arr_adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        whoSpinner.setAdapter(arr_adapter);
    }

    public void setSpinnerListener(){
        whoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //设置Spinner显示的字体颜色
                TextView tv = (TextView) view;
                tv.setTextColor(Color.WHITE);
                String whos = (String) whoSpinner.getAdapter().getItem(position);
                isAlreadyInStorageRequset = whos.equals("已入库") ? true:false;
                updateData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void initViewData() {
        adapter_AlreadyInStorageRequset = new AlreadyInStorageRequsetAdapter
                (DepartRequestActivity.this, data_AlreadyInStorageRequset);
        adapter_AlreadyRequset = new AlreadyRequsetAdapter(DepartRequestActivity.this, data_AlreadyRequset);
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
                        adapter_AlreadyInStorageRequset.notifyDataSetChanged();
                        data_AlreadyInStorageRequset.clear(); //清除数据
                        adapter_AlreadyRequset.notifyDataSetChanged();
                        data_AlreadyRequset.clear(); //清除数据
                        pageNum = 1;
                        isLoading = false;
                        if (isAlreadyInStorageRequset)
                            getData(); //获取数据
                        else
                            getSubData();
                    }
                }, 1000);
            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(DepartRequestActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        if(isAlreadyInStorageRequset){
            recyclerView.setAdapter(adapter_AlreadyInStorageRequset);
        }else {
            recyclerView.setAdapter(adapter_AlreadyRequset);
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
                                    if (isAlreadyInStorageRequset)
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
        if (isAlreadyInStorageRequset){
            adapter_AlreadyInStorageRequset = new AlreadyInStorageRequsetAdapter(DepartRequestActivity.this,
                    data_AlreadyInStorageRequset);
            recyclerView.setAdapter(adapter_AlreadyInStorageRequset);
            adapter_AlreadyInStorageRequset.notifyDataSetChanged();
            data_AlreadyInStorageRequset.clear();
            getData(); //获取数据
        }else {
            adapter_AlreadyRequset = new AlreadyRequsetAdapter(DepartRequestActivity.this, data_AlreadyRequset);
            recyclerView.setAdapter(adapter_AlreadyRequset);
            adapter_AlreadyRequset.notifyDataSetChanged();
            data_AlreadyRequset.clear();
            getSubData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Config.isAlreadyInStorage) {
            Config.isAlreadyInStorage = false;
            updateData();
        }
    }

    @OnClick({R.id.back_iv, R.id.my_view, R.id.xs_tv, R.id.apply_bt, R.id.search_bt})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.my_tv:
                isAlreadyInStorageRequset = true;
                applyBt.setVisibility(View.VISIBLE);
                myView.setVisibility(View.VISIBLE);
                xsView.setVisibility(View.INVISIBLE);
                updateData();
                break;
            case R.id.xs_tv:
                isAlreadyInStorageRequset = false;
                applyBt.setVisibility(View.GONE);
                myView.setVisibility(View.INVISIBLE);
                xsView.setVisibility(View.VISIBLE);
                updateData();
                break;
            case R.id.apply_bt:
                if(isAlreadyInStorageRequset){
                    submitRequest();//发车申请
                }else{
                    //submitCarOutStorage();
                }
                break;
            case R.id.search_bt:
                updateData();
                ApkApplication.imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//设置键盘
                break;
        }
    }

    public void submitRequest(){
        if(adapter_AlreadyInStorageRequset != null){
            Map<Integer, Boolean> map = adapter_AlreadyInStorageRequset.getMap();
            List<WaitInStorageInfo> selectData = new ArrayList<>();
            StringBuffer sb = new StringBuffer();
            for(int i = 0; i < map.size(); i++){
                if(map.get(i)){
                    WaitInStorageInfo info = data_AlreadyInStorageRequset.get(i);
                    selectData.add(info);
                }
            }
            if(selectData.size() == 0){
                Toast.makeText(DepartRequestActivity.this,"您还未选择待申请的车辆！",Toast.LENGTH_SHORT).show();
            }else if(selectData.size() == 1){
                sb.delete(0,sb.length());
                sb.append(selectData.get(0).getVehicleno());
                submitRequestData(selectData.get(0).getSorderno(),sb.toString());
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
                    submitRequestData(sorderno,sb.toString().substring(0,sb.toString().length()-1));
                }else {
                    Toast.makeText(DepartRequestActivity.this,"所选择的车辆订单号须一致！",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void submitRequestData(String orderno,String vehiclenos){
        OkHttpUtils
                .post()
                .url(Config.departRequestAction)
                .addParams("sorderNo", orderno)
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
                                Toast.makeText(DepartRequestActivity.this,"发车申请失败,请检查网络",Toast
                                        .LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.d("hqrequest","response is: " + response);
                                RequestFeedbackBean RequestFeedbackStr = new Gson().fromJson(response, RequestFeedbackBean.class);
                                if(RequestFeedbackStr.isSuccess()){
                                    Toast.makeText(DepartRequestActivity.this,"发车申请成功",Toast.LENGTH_SHORT).show();
                                    sendMsg(REFRESH_DATA);
                                }else {
                                    Toast.makeText(DepartRequestActivity.this,RequestFeedbackStr
                                            .getMsg(),Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(DepartRequestActivity.this,"退库失败",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Toast.makeText(DepartRequestActivity.this,"退库成功",Toast.LENGTH_SHORT).show();
                                sendMsg(REFRESH_DATA);
                            }
                        }
                );
    }

    //
    public void submitCarOutStorage(){
        if(adapter_AlreadyRequset != null){
            Map<Integer, Boolean> map = adapter_AlreadyRequset.getMap();
            List<WaitInStorageInfo> selectData = new ArrayList<>();
            StringBuffer sb = new StringBuffer();
            for(int i = 0; i < map.size(); i++){
                if(map.get(i)){
                    WaitInStorageInfo info = data_AlreadyRequset.get(i);
                    selectData.add(info);
                }
            }
            if(selectData.size() == 0){
                Toast.makeText(DepartRequestActivity.this,"您还未选择要退库的车辆！",Toast.LENGTH_SHORT).show();
            }else if(selectData.size() == 1){
                sb.delete(0,sb.length());
                sb.append(selectData.get(0).getVehicleno());
                submitCarOutStorageData(sb.toString());
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
                }else {
                    Toast.makeText(DepartRequestActivity.this,"所选择的车辆订单号须一致！",Toast.LENGTH_SHORT).show();
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
                                Log.d("hqlog","response is：" + response);
                                Type userlistType = new TypeToken<Result<ListPagers<WaitInStorageInfo>>>() {
                                }.getType();
                                status = new Gson().fromJson(response, userlistType);
                                if (status != null) {
                                    if (status.isSuccess()) {
                                        if (status.getData().getList() != null && status.getData().getList().size() > 0) {
                                            for (int i = 0; i < status.getData().getList().size(); i++) {
                                                data_AlreadyInStorageRequset.add(status.getData().getList().get(i));
                                            }
                                        }
                                        if (status.getData().isHasNextPage()) {
                                            adapter_AlreadyInStorageRequset.setNotMoreData(false);
                                        } else {
                                            adapter_AlreadyInStorageRequset.setNotMoreData(true);
                                        }

                                        adapter_AlreadyInStorageRequset.notifyDataSetChanged();
                                        swipeRefreshLayout.setRefreshing(false);
                                        adapter_AlreadyInStorageRequset.notifyItemRemoved(adapter_AlreadyInStorageRequset
                                                .getItemCount());
                                    }
                                } else {
                                    adapter_AlreadyInStorageRequset.notifyDataSetChanged();
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
                                Type userlistType = new TypeToken<Result<ListPagers<WaitInStorageInfo>>>() {
                                }.getType();
                                status = new Gson().fromJson(response, userlistType);
                                if (status != null) {
                                    if (status.isSuccess()) {
                                        if (status.getData().getList() != null && status.getData().getList().size() > 0) {
                                            for (int i = 0; i < status.getData().getList().size(); i++) {
                                                data_AlreadyRequset.add(status.getData().getList().get(i));
                                            }
                                        }
                                        if (status.getData().isHasNextPage()) {
                                            adapter_AlreadyRequset.setNotMoreData(false);
                                        } else {
                                            adapter_AlreadyRequset.setNotMoreData(true);
                                        }
                                        adapter_AlreadyRequset.notifyDataSetChanged();
                                        swipeRefreshLayout.setRefreshing(false);
                                        adapter_AlreadyRequset.notifyItemRemoved(adapter_AlreadyRequset.getItemCount());
                                    }
                                } else {
                                    adapter_AlreadyRequset.notifyDataSetChanged();
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            }
                        }
                );
    }
}
