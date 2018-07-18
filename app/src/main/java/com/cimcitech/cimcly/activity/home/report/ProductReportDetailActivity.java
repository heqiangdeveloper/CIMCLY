package com.cimcitech.cimcly.activity.home.report;

import android.content.Context;
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
import android.widget.TextView;

import com.cimcitech.cimcly.ApkApplication;
import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.adapter.intention_track.IntentionTrackAdapter;
import com.cimcitech.cimcly.adapter.PopupWindowAdapter;
import com.cimcitech.cimcly.bean.GetCurrStageSelect;
import com.cimcitech.cimcly.bean.opport_unit.OpportUnitReq;
import com.cimcitech.cimcly.bean.opport_unit.OpportUnitVo;
import com.cimcitech.cimcly.bean.report.ProductOpportUnitReq;
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

public class ProductReportDetailActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.status_bt)
    Button statusBt;
    @Bind(R.id.status_bt_sanjiao)
    Button statusBtSanjiao;
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
    @Bind(R.id.who_ll)
    LinearLayout who_Ll;

    private PopupWindow pop;
    private int pageNum = 1;
    private IntentionTrackAdapter adapter;
    private boolean isLoading;
    private List<OpportUnitVo.DataBean.PageInfoBean.ListBean> data = new ArrayList<>();
    private Handler uiHandler = null;
    private Handler handler = new Handler();
    private final int INIT_DATA = 1003;
    private OpportUnitVo opportUnitVo;
    private boolean myDate = true;
    private GetCurrStageSelect getCurrStageSelect;
    private String quotestatus;

    private String PRODUCTVARIETY = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_report_detail2);
        ButterKnife.bind(this);
        initTitle();

        PRODUCTVARIETY = getIntent().getStringExtra("productvariety");
        initViewData();
        getCurrStageSelect();
    }

    public void initTitle(){
        more_Tv.setVisibility(View.GONE);
        titleName_Tv.setText("不同产品的意向订单统计明细");
        title_Ll.setVisibility(View.VISIBLE);
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
        //清除数据
        adapter.notifyDataSetChanged();
        this.data.clear();
        pageNum = 1;
        if (myDate)
            getData(); //获取数据
        else
            getSubData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Config.isAddTrack) {
            Config.isAddTrack = false;
            updateData();
        }
    }

    @OnClick({R.id.back_iv,R.id.status_bt_sanjiao,R.id.status_bt,R.id.search_bt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.status_bt_sanjiao:
            case R.id.status_bt:
                List<String> list = new ArrayList<>();
                if (getCurrStageSelect != null) {
                    for (int i = 0; i < getCurrStageSelect.getData().size(); i++) {
                        list.add(getCurrStageSelect.getData().get(i).getCodevalue());
                    }
                }
                showContactUsPopWin(ProductReportDetailActivity.this, "选择订单状态查询", list);
                pop.showAtLocation(view, Gravity.CENTER, 0, 0);
                break;
            case R.id.search_bt:
                updateData();
                ApkApplication.imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
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
                GetCurrStageSelect.DataBean item = getCurrStageSelect.getData().get(i);
                quotestatus = item.getCodeid();
                statusBt.setText(item.getCodevalue());
                updateData();
                pop.dismiss();
            }
        });
    }

    public void initViewData() {
        adapter = new IntentionTrackAdapter(ProductReportDetailActivity.this, data);
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
                        if (myDate)
                            getData(); //获取数据
                        else
                            getSubData();
                    }
                }, 1000);
            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(ProductReportDetailActivity.this);
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
                                if (opportUnitVo.getData().getPageInfo().isHasNextPage()) {
                                    pageNum++;
                                    if (myDate)
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
        adapter.setOnItemClickListener(new IntentionTrackAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClickListener(View view, int position) {
            }
        });
    }

    public void getData() {
        String json = new Gson().toJson(new ProductOpportUnitReq(pageNum, 10, "",
                new ProductOpportUnitReq.ProductOpportUnityBean(PRODUCTVARIETY,
                        searchEt.getText().toString().trim()
                        , quotestatus)));
        Log.d("heqint","request json is: " + json);
        OkHttpUtils
                .postString()
                .url(Config.rptOpportUnitList)
                .addHeader("checkTokenKey", Config.loginback.getToken())
                .addHeader("sessionKey", Config.loginback.getUserId() + "")
                .content(json)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(
                        new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                swipeRefreshLayout.setRefreshing(false);
                                ToastUtil.showNetError();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                //ToastUtil.showToast(response);
                                Log.d("heqint","response is: " + response);
                                opportUnitVo = GjsonUtil.parseJsonWithGson(response, OpportUnitVo.class);
                                if (opportUnitVo != null) {
                                    if (opportUnitVo.isSuccess()) {
                                        if (opportUnitVo.getData().getPageInfo().getList() != null
                                                && opportUnitVo.getData().getPageInfo().getList().size() > 0) {
                                            for (int i = 0; i < opportUnitVo.getData().getPageInfo().getList().size(); i++) {
                                                data.add(opportUnitVo.getData().getPageInfo().getList().get(i));
                                            }
                                        }
                                        if (opportUnitVo.getData().getPageInfo().isHasNextPage()) {
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
        String json = new Gson().toJson(new OpportUnitReq(pageNum, 10, "",
                new OpportUnitReq.OpportUnityBean(Config.loginback.getUserId(),
                        searchEt.getText().toString().trim()
                        , quotestatus)));
        OkHttpUtils
                .postString()
                .url(Config.opportUnitSubList)
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
                                Log.d("heqsubint","response is: " + response);
                                opportUnitVo = GjsonUtil.parseJsonWithGson(response, OpportUnitVo.class);
                                if (opportUnitVo != null) {
                                    if (opportUnitVo.isSuccess()) {
                                        /******
                                         orderAmountTotalTv.setText(opportUnitVo.getData().getOpportAmountTotal() + "");
                                         orderCountTv.setText("/" + opportUnitVo.getData().getOpportCount() + "个");
                                         opportAmountTotalCountTv.setText("转订单合同(元)/数量\n" +
                                         opportUnitVo.getData().getOrderAmountTotal()
                                         + "/" + opportUnitVo.getData().getOrderCount() + "个");
                                         ******/
                                        if (opportUnitVo.getData().getPageInfo().getList() != null
                                                && opportUnitVo.getData().getPageInfo().getList().size() > 0) {
                                            for (int i = 0; i < opportUnitVo.getData().getPageInfo().getList().size(); i++) {
                                                data.add(opportUnitVo.getData().getPageInfo().getList().get(i));
                                            }
                                        }
                                        if (opportUnitVo.getData().getPageInfo().isHasNextPage()) {
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
     * 获取状态 筛选值
     */
    public void getCurrStageSelect() {
        OkHttpUtils
                .post()
                .url(Config.getCurrStageSelect)
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
                                getCurrStageSelect = GjsonUtil.parseJsonWithGson(response, GetCurrStageSelect.class);
                                if (getCurrStageSelect != null) {
                                    if (getCurrStageSelect.isSuccess())
                                        if (getCurrStageSelect.getData().size() > 0) {
                                            quotestatus = getCurrStageSelect.getData().get(0).getCodeid();
                                            statusBt.setText(getCurrStageSelect.getData().get(0).getCodevalue());
                                            getData();
                                        }
                                }
                            }
                        }
                );
    }
}
