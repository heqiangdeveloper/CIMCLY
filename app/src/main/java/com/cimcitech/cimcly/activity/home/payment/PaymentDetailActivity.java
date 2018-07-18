package com.cimcitech.cimcly.activity.home.payment;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.adapter.payment.PaymentDetailAdapter;
import com.cimcitech.cimcly.bean.payment.PaymentCustomerDetail;
import com.cimcitech.cimcly.utils.Config;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class PaymentDetailActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_payment_detail2);
        ButterKnife.bind(this);
        initTitle();
        sorderId = this.getIntent().getLongExtra("sorderid", 0);
        Log.d("heqpd","sortid is：" + sorderId);
        initViewData();
        getData();
    }

    public void initTitle(){
        more_Tv.setVisibility(View.GONE);
        titleName_Tv.setText("回款跟踪详情");
        title_Ll.setVisibility(View.GONE);
    }

    @OnClick({R.id.back_iv})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
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
                        swipeRefreshLayout.setRefreshing(false);
                        isLoading = false;
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
