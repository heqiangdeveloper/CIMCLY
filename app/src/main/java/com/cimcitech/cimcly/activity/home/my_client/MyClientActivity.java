package com.cimcitech.cimcly.activity.home.my_client;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cimcitech.cimcly.ApkApplication;
import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.adapter.my_client.MyClientAdapter;
import com.cimcitech.cimcly.bean.ListPagers;
import com.cimcitech.cimcly.bean.client.MyClientReq;
import com.cimcitech.cimcly.bean.Result;
import com.cimcitech.cimcly.bean.client.Customer;
import com.cimcitech.cimcly.utils.Config;
import com.cimcitech.cimcly.utils.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

/**
 * 我的客户
 */
public class MyClientActivity extends AppCompatActivity {
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
    @Bind(R.id.add_ib)
    ImageButton add_Ib;
    @Bind(R.id.who_spinner)
    Spinner whoSpinner;
    @Bind(R.id.who_ll)
    LinearLayout who_Ll;

    private int pageNum = 1;
    private Result<ListPagers<Customer>> status;
    private List<Customer> data = new ArrayList<>();
    private MyClientAdapter adapter;
    private Handler uiHandler = null;
    private Handler handler = new Handler();
    private final int INIT_DATA = 1003;
    private boolean isLoading;
    private boolean myData = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_client2);
        ButterKnife.bind(this);
        initTitle();
        initViewData();
        getData();
        setSpinnerListener();
    }

    public void initTitle(){
        more_Tv.setVisibility(View.GONE);
        whoSpinner.setVisibility(View.VISIBLE);
        titleName_Tv.setText("我的客户");
        title_Ll.setVisibility(View.VISIBLE);
        who_Ll.setVisibility(View.GONE);
        status_Ll.setVisibility(View.GONE);
    }

    public void setSpinnerListener(){
        whoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String whos = (String) whoSpinner.getAdapter().getItem(position);
                myData = whos.equals("我的") ? true:false;
                updateData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //刷新数据
    private void updateData() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        //清除数据
        adapter.notifyDataSetChanged();
        this.data.clear();
        pageNum = 1;
        if (myData)
            getData(); //获取数据
        else
            getSubordinateData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Config.isAddMyClient) {
            Config.isAddMyClient = false;
            updateData();
        }
    }

    @OnClick({R.id.back_iv, R.id.my_tv, R.id.xs_tv, R.id.add_ib, R.id.search_bt})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.my_tv:
                myData = true;
                myView.setVisibility(View.VISIBLE);
                xsView.setVisibility(View.INVISIBLE);
                updateData();
                break;
            case R.id.xs_tv:
                myData = false;
                myView.setVisibility(View.INVISIBLE);
                xsView.setVisibility(View.VISIBLE);
                updateData();
                break;
            case R.id.add_ib:
                startActivity(new Intent(MyClientActivity.this, MyClientAddActivity.class));
                break;
            case R.id.search_bt:
                updateData();
                ApkApplication.imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
        }
    }

    public void initViewData() {
        adapter = new MyClientAdapter(MyClientActivity.this, data);
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
                        if (myData)
                            getData(); //获取数据
                        else
                            getSubordinateData();
                    }
                }, 1000);
            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(MyClientActivity.this);
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
                                if (null != status.getData() && status.getData().isHasNextPage()) {
                                    pageNum++;
                                    if (myData)
                                        getData();//添加数据
                                    else
                                        getSubordinateData();
                                }
                                swipeRefreshLayout.setRefreshing(false);
                                isLoading = false;
                            }
                        }, 1000);
                    }
                }
            }
        });
        //给List添加点击事件
        adapter.setOnItemClickListener(new MyClientAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MyClientActivity.this, MyClientUpdateActivity.class);
                Customer customer = (Customer) adapter.getAll().get(position);
                intent.putExtra("custid", customer.getCustid());
                startActivity(intent);
            }

            @Override
            public void onItemLongClickListener(View view, int position) {
            }
        });
    }

    public void getData() {
        String json = new Gson().toJson(new MyClientReq(pageNum, 10, "",
                new MyClientReq.CustomerVisitBean(Config.loginback.getUserId() + "",
                        searchEt.getText().toString().trim())));
        OkHttpUtils
                .postString()
                .url(Config.URL_CLIENT_LIST)
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
                                Type userlistType = new TypeToken<Result<ListPagers<Customer>>>() {
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

    /**
     * 获取下属客户列表
     */
    public void getSubordinateData() {
        String json = new Gson().toJson(new MyClientReq(pageNum, 10, "",
                new MyClientReq.CustomerVisitBean(Config.loginback.getUserId() + "",
                        searchEt.getText().toString().trim())));
        OkHttpUtils
                .postString()
                .url(Config.subordinate)
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
                                Type userlistType = new TypeToken<Result<ListPagers<Customer>>>() {
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
