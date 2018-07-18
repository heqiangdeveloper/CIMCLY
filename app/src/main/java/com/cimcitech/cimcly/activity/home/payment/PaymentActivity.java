package com.cimcitech.cimcly.activity.home.payment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.adapter.payment.PaymentAdapter;
import com.cimcitech.cimcly.bean.ListPagers;
import com.cimcitech.cimcly.bean.Result;
import com.cimcitech.cimcly.bean.payment.PaymentCustomer;
import com.cimcitech.cimcly.bean.payment.PaymentReq;
import com.cimcitech.cimcly.utils.Config;
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
public class PaymentActivity extends AppCompatActivity {

    @Bind(R.id.back_rl)
    RelativeLayout backRl;

    @Bind(R.id.my_tv)
    TextView myTv;
    @Bind(R.id.xs_tv)
    TextView xsTv;
    @Bind(R.id.my_view)
    View myView;
    @Bind(R.id.xs_view)
    View xsView;
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
    private Result<ListPagers<PaymentCustomer>> status;
    private List<PaymentCustomer> data = new ArrayList<>();
    private PaymentAdapter adapter;
    private Handler uiHandler = null;
    private Handler handler = new Handler();
    private final int INIT_DATA = 1003;
    private boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);
        initViewData();
        getData();
    }

    //刷新数据
    private void updateData(int num) {
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
        if (num == 1)
            getData(); //获取数据
        else
            getSubordinateData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Config.isAddMyClient) {
            Config.isAddMyClient = false;
            updateData(1);
        }
    }

    @OnClick({R.id.back_rl, R.id.my_tv, R.id.xs_tv, R.id.search_bt})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.back_rl:
                finish();
                break;
            case R.id.my_tv:
                myView.setVisibility(View.VISIBLE);
                xsView.setVisibility(View.INVISIBLE);
                updateData(1);
                break;
            case R.id.xs_tv:
                myView.setVisibility(View.INVISIBLE);
                xsView.setVisibility(View.VISIBLE);
                updateData(2);
                break;
            case R.id.search_bt:
                if(myView.getVisibility() == View.VISIBLE){
                    updateData(1);
                }else {
                    updateData(2);
                }
                break;
            }
    }

    public void initViewData() {
        adapter = new PaymentAdapter(PaymentActivity.this, data);
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
                        getData(); //获取数据
                    }
                }, 1000);
            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(PaymentActivity.this);
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
                                    getData();//添加数据
                                }
                                isLoading = false;
                            }
                        }, 1000);
                    }
                }
            }
        });
        //给List添加点击事件
        adapter.setOnItemClickListener(new PaymentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(PaymentActivity.this, PaymentDetailActivity.class);
                PaymentCustomer paymentCustomer = (PaymentCustomer) adapter.getAll().get(position);
                intent.putExtra("sorderid", paymentCustomer.getSorderid());
                startActivity(intent);
            }

            @Override
            public void onItemLongClickListener(View view, int position) {
            }
        });
    }

    public void getData() {
        String json = new Gson().toJson(new PaymentReq(pageNum, 10, "",
                new PaymentReq.PaymentBean(Config.loginback.getUserId() + "",
                        searchEt.getText().toString().trim())));
        Log.d("heqpm","payment request is：" + json);
        Log.d("heqpm","token is：" + Config.loginback.getToken());
        OkHttpUtils
                .postString()
                .url(Config.PaymentUrl)
                .addHeader("checkTokenKey", Config.loginback.getToken())
                .addHeader("sessionKey", Config.loginback.getUserId() + "")
                .content(json)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(
                        new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Log.e("MyClientActivity", "请求失败");
                                swipeRefreshLayout.setRefreshing(false);
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.d("heqpm","payment response is：" + response);
                                Type userlistType = new TypeToken<Result<ListPagers<PaymentCustomer>>>() {
                                }.getType();
                                status = new Gson().fromJson(response, userlistType);
                                if (null != status && status.isSuccess()) {
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
                            }
                        }
                );
    }

    /**
     * 获取下属客户列表
     */
    public void getSubordinateData() {
        String json = new Gson().toJson(new PaymentReq(pageNum, 10, "",
                new PaymentReq.PaymentBean(Config.loginback.getUserId() + "",
                        searchEt.getText().toString().trim())));
        Log.d("heqpm","subpayment request is：" + json);
        OkHttpUtils
                .postString()
                .url(Config.subPaymentUrl)
                .addHeader("checkTokenKey", Config.loginback.getToken())
                .addHeader("sessionKey", Config.loginback.getUserId() + "")
                .content(json)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(
                        new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Log.e("MyClientActivity", "请求失败");
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.d("heqpm","subpayment response is：" + response);
                                Type userlistType = new TypeToken<Result<ListPagers<PaymentCustomer>>>() {
                                }.getType();
                                status = new Gson().fromJson(response, userlistType);
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
                            }
                        }
                );
    }
}
