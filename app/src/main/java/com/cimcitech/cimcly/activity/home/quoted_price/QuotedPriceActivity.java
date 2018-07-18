package com.cimcitech.cimcly.activity.home.quoted_price;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cimcitech.cimcly.ApkApplication;
import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.adapter.PopupWindowAdapter;
import com.cimcitech.cimcly.adapter.quoted_price.QuotedPriceAdapter;
import com.cimcitech.cimcly.bean.quoted_price.GetQuoteStatus;
import com.cimcitech.cimcly.bean.quoted_price.QuotedPriceReq;
import com.cimcitech.cimcly.bean.quoted_price.QuotedPriceVo;
import com.cimcitech.cimcly.utils.Config;
import com.cimcitech.cimcly.utils.GjsonUtil;
import com.cimcitech.cimcly.utils.ToastUtil;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

public class QuotedPriceActivity extends AppCompatActivity {
    @Bind(R.id.my_tv)
    TextView myTv;
    @Bind(R.id.xs_tv)
    TextView xsTv;
    @Bind(R.id.my_view)
    View myView;
    @Bind(R.id.xs_view)
    View xsView;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recycler_view_layout)
    CoordinatorLayout recyclerViewLayout;
    @Bind(R.id.status_bt)
    Button statusBt;
    @Bind(R.id.search_et)
    EditText searchEt;
    @Bind(R.id.search_bt)
    Button searchBt;
    @Bind(R.id.status_bt_sanjiao)
    Button searchBtSanjiao;
    @Bind(R.id.title_ll)
    LinearLayout titleLl;
    @Bind(R.id.more_tv)
    TextView more_Tv;
    @Bind(R.id.titleName_tv)
    TextView titleName_Tv;
    @Bind(R.id.who_spinner)
    Spinner whoSpinner;
    @Bind(R.id.who_ll)
    LinearLayout who_Ll;

    private int pageNum = 1;
    private QuotedPriceVo status;
    private List<QuotedPriceVo.DataBean.PageInfoBean.ListBean> data = new ArrayList<>();
    private QuotedPriceAdapter adapter;
    private Handler uiHandler = null;
    private Handler handler = new Handler();
    private final int INIT_DATA = 1003;
    private boolean isLoading;
    private boolean myData = true;
    private PopupWindow pop;
    private GetQuoteStatus getQuoteStatus;
    private String quotestatus; //记录订单状态值去查询

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quoted_price2);
        ButterKnife.bind(this);
        initTitle();
        initViewData();
        getQuoteStatus();
        setSpinnerListener();
    }

    public void initTitle(){
        more_Tv.setVisibility(View.GONE);
        whoSpinner.setVisibility(View.VISIBLE);
        titleName_Tv.setText("报价单");
        titleLl.setVisibility(View.VISIBLE);
        who_Ll.setVisibility(View.GONE);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (Config.isQuotedPrice) {
            Config.isQuotedPrice = false;
            updateData();
        }
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
            getSubData();
    }

    @OnClick({R.id.back_iv, R.id.my_tv, R.id.xs_tv, R.id.search_bt, R.id.status_bt,R.id
            .status_bt_sanjiao})
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
            case R.id.status_bt:
                List<String> list = new ArrayList<>();
                if (getQuoteStatus != null) {
                    for (int i = 0; i < getQuoteStatus.getData().size(); i++) {
                        list.add(getQuoteStatus.getData().get(i).getCodevalue());
                    }
                }
                showContactUsPopWin(QuotedPriceActivity.this, "选择订单状态查询", list);
                pop.showAtLocation(view, Gravity.CENTER, 0, 0);
                break;
            case R.id.search_bt:
                updateData();
                ApkApplication.imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            case R.id.status_bt_sanjiao:
                List<String> list2 = new ArrayList<>();
                if (getQuoteStatus != null) {
                    for (int i = 0; i < getQuoteStatus.getData().size(); i++) {
                        list2.add(getQuoteStatus.getData().get(i).getCodevalue());
                    }
                }
                showContactUsPopWin(QuotedPriceActivity.this, "选择订单状态查询", list2);
                pop.showAtLocation(view, Gravity.CENTER, 0, 0);
                break;
        }
    }

    public void showContactUsPopWin(Context context, String title, List<String> list) {
        LayoutInflater inflater = LayoutInflater.from(context);
        // 引入窗口配置文件
        View view = inflater.inflate(R.layout.dialog_add_client_view, null);
        view.getBackground().setAlpha(100);
        // 创建PopupWindow对象
        pop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, false);
        View pop_reward_view = view.findViewById(R.id.pop_reward_view);
        TextView title_tv = view.findViewById(R.id.title_tv);
        title_tv.setText(title);
        final PopupWindowAdapter adapter = new PopupWindowAdapter(context, list);
        ListView listView = view.findViewById(R.id.listContent);
        listView.setAdapter(adapter);
        // 需要设置一下此参数，点击外边可消失
        pop.setBackgroundDrawable(new BitmapDrawable());
        // 设置点击窗口外边窗口消失
        pop.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        pop.setFocusable(true);
        pop_reward_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop.dismiss();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GetQuoteStatus.DataBean item = getQuoteStatus.getData().get(i);
                quotestatus = item.getCodeid();
                statusBt.setText(item.getCodevalue());
                updateData();
                pop.dismiss();
            }
        });
    }

    public void initViewData() {
        adapter = new QuotedPriceAdapter(QuotedPriceActivity.this, data);
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
                            getSubData();
                    }
                }, 1000);
            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(QuotedPriceActivity.this);
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
                                if (status.getData().getPageInfo().isHasNextPage()) {
                                    pageNum++;
                                    if (myData)
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
        adapter.setOnItemClickListener(new QuotedPriceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(QuotedPriceActivity.this, QuotedPriceDetailActivity.class);
                QuotedPriceVo.DataBean.PageInfoBean.ListBean listBean =
                        (QuotedPriceVo.DataBean.PageInfoBean.ListBean) adapter.getAll().get(position);
                intent.putExtra("quoteid", listBean.getQuoteid());
                startActivity(intent);
            }

            @Override
            public void onItemLongClickListener(View view, int position) {
            }
        });
    }

    public void getData() {
        String json = new Gson().toJson(new QuotedPriceReq(pageNum, 10, "",
                new QuotedPriceReq.QuotedPriceBean(Config.loginback.getUserId(),
                        searchEt.getText().toString().trim(), quotestatus)));
        OkHttpUtils
                .postString()
                .url(Config.quoteBaseList)
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
                                Log.d("heqquo","repose is: " + response);
                                status = GjsonUtil.parseJsonWithGson(response, QuotedPriceVo.class);
                                if (status != null) {
                                    if (status.isSuccess()) {
                                        if (status.getData().getPageInfo().getList() != null &&
                                                status.getData().getPageInfo().getList().size() > 0) {
                                            for (int i = 0; i < status.getData().getPageInfo().getList().size(); i++) {
                                                data.add(status.getData().getPageInfo().getList().get(i));
                                            }
                                        }
                                        if (status.getData().getPageInfo().isHasNextPage()) {
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

    public void getSubData() {
        String json = new Gson().toJson(new QuotedPriceReq(pageNum, 10, "",
                new QuotedPriceReq.QuotedPriceBean(Config.loginback.getUserId(),
                        searchEt.getText().toString().trim(), quotestatus)));
        OkHttpUtils
                .postString()
                .url(Config.opportUnitSubPageList)
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
                                //ToastUtil.showToast(response);
                                status = GjsonUtil.parseJsonWithGson(response, QuotedPriceVo.class);
                                if (status != null) {
                                    if (status.isSuccess()) {
                                        if (status.getData().getPageInfo().getList() != null &&
                                                status.getData().getPageInfo().getList().size() > 0) {
                                            for (int i = 0; i < status.getData().getPageInfo().getList().size(); i++) {
                                                data.add(status.getData().getPageInfo().getList().get(i));
                                            }
                                        }
                                        if (status.getData().getPageInfo().isHasNextPage()) {
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
     * 订单状态
     */
    public void getQuoteStatus() {
        OkHttpUtils
                .post()
                .url(Config.getQuoteStatus)
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
                                //ToastUtil.showToast(response);
                                getQuoteStatus = GjsonUtil.parseJsonWithGson(response, GetQuoteStatus.class);
                                if (getQuoteStatus != null) {
                                    if (getQuoteStatus.isSuccess())
                                        if (getQuoteStatus.getData().size() > 0) {
                                            quotestatus = getQuoteStatus.getData().get(0).getCodeid();
                                            statusBt.setText(getQuoteStatus.getData().get(0).getCodevalue());
                                            getData();
                                        }

                                }
                            }
                        }
                );
    }
}
