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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.adapter.PaymentAdapter;
import com.cimcitech.cimcly.adapter.PaymentDetailAdapter;
import com.cimcitech.cimcly.bean.ListPagers;
import com.cimcitech.cimcly.bean.ListPaymentDetailPagers;
import com.cimcitech.cimcly.bean.Result;
import com.cimcitech.cimcly.bean.payment.PaymentCustomer;
import com.cimcitech.cimcly.bean.payment.PaymentCustomerDetail;
import com.cimcitech.cimcly.bean.payment.PaymentVo;
import com.cimcitech.cimcly.utils.Config;
import com.cimcitech.cimcly.utils.DateTool;
import com.cimcitech.cimcly.utils.GjsonUtil;
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

public class PaymentDetailActivity extends AppCompatActivity {


    @Bind(R.id.back_rl)
    RelativeLayout backRl;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recycler_view_layout)
    CoordinatorLayout recyclerViewLayout;

    private long sorderId;
    private int pageNum = 1;
    private PaymentCustomerDetail status;
    private List<PaymentCustomerDetail.DetailData> data = new ArrayList<>();
    private PaymentDetailAdapter adapter;
    private Handler uiHandler = null;
    private Handler handler = new Handler();
    private final int INIT_DATA = 1003;
    private boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_detail);
        ButterKnife.bind(this);
        sorderId = this.getIntent().getLongExtra("sorderid", 0);
        Log.d("heqpd","sortid is：" + sorderId);
        initViewData();
        getData();
    }

    @OnClick({R.id.back_rl})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.back_rl:
                finish();
                break;
        }
    }

    public void initViewData() {
        adapter = new PaymentDetailAdapter(PaymentDetailActivity.this, data);
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
                        /*data.clear(); //清除数据
                        pageNum = 1;
                        isLoading = false;
                        getData(); //获取数据*/
                    }
                }, 1000);
            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(PaymentDetailActivity.this);
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
                                /*if (status.getData().isHasNextPage()) {
                                    pageNum++;
                                    getData();//添加数据
                                }*/
                                isLoading = false;
                            }
                        }, 1000);
                    }
                }
            }
        });
        //给List添加点击事件
        adapter.setOnItemClickListener(new PaymentDetailAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                /*Intent intent = new Intent(PaymentActivity.this, PaymentDetailActivity.class);
                PaymentCustomer paymentCustomer = (PaymentCustomer) adapter.getAll().get(position);
                intent.putExtra("sorderid", paymentCustomer.getSorderid());
                startActivity(intent);*/
            }

            @Override
            public void onItemLongClickListener(View view, int position) {
            }
        });
    }

    public void getData() {

        OkHttpUtils
                .post()
                .url(Config.PaymentDetailUrl)
                .addHeader("checkTokenKey", Config.loginback.getToken())
                .addHeader("sessionKey", Config.loginback.getUserId() + "")
                .addParams("sOrderId", sorderId + "")
                .build()
                .execute(
                        new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Log.e("heqpmd", "请求失败");
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.i("heqpayment", "payment detail response is: " + response);
                                /*Type userlistType = new
                                        TypeToken<Result<ListPaymentDetailPagers<PaymentCustomerDetail>>>() {
                                }.getType();*/
                                status = new Gson().fromJson(response, PaymentCustomerDetail.class);
                                /*try{
                                    status = new Gson().fromJson(response, userlistType);
                                } catch (Exception e){
                                    Toast.makeText(PaymentDetailActivity.this,
                                            "返回的json数据transactiondate不能为负数",Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(PaymentDetailActivity.this,
                                            PaymentActivity.class));
                                    finish();
                                    return;
                                }*/

                                if (status.isSuccess()) {
                                    /*if (status.getData().getList() != null && status.getData()
                                            .getList().size() > 0) {
                                        for (int i = 0; i < status.getData().getList().size(); i++) {
                                            data.add(status.getData().getList().get(i));//.getData().getList().get(i)
                                        }
                                    }*/
                                    if (status.getData() != null && status.getData().size() != 0){
                                        for (int i = 0; i < status.getData().size(); i++) {
                                            data.add(status.getData().get(i));
                                        }
                                    }

                                    /*if (status.getData().isHasNextPage()) {
                                        adapter.setNotMoreData(false);
                                    } else {
                                        adapter.setNotMoreData(true);
                                    }*/
                                    adapter.setNotMoreData(true);
                                    adapter.notifyDataSetChanged();
                                    swipeRefreshLayout.setRefreshing(false);
                                    adapter.notifyItemRemoved(adapter.getItemCount());
                                }
                            }
                        });

    }


}
