package com.cimcitech.cimcly.activity.home.work_weekly;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cimcitech.cimcly.ApkApplication;
import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.adapter.work_weekly.WorkWeeklyAdapter;
import com.cimcitech.cimcly.adapter.work_weekly.WorkWeeklyPopupWindowAdapter;
import com.cimcitech.cimcly.bean.work_weekly.WorkWeeklyReq;
import com.cimcitech.cimcly.bean.work_weekly.WorkWeeklyVo;
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

public class WorkWeeklyActivity extends AppCompatActivity {
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
    @Bind(R.id.more_tv)
    TextView more_Tv;
    @Bind(R.id.titleName_tv)
    TextView titleName_Tv;
    @Bind(R.id.title_ll)
    LinearLayout title_Ll;
    @Bind(R.id.search_ll)
    LinearLayout search_Ll;
    @Bind(R.id.add_ib)
    ImageButton add_Ib;
    @Bind(R.id.who_spinner)
    Spinner whoSpinner;
    @Bind(R.id.who_ll)
    LinearLayout who_Ll;

    @Bind(R.id.ww_top_area)
    LinearLayout ww_top_Area;
    @Bind(R.id.ww_top_category_name)
    TextView ww_top_category_Name;
    @Bind(R.id.ww_top_category_label)
    TextView ww_top_category_Label;

    private int pageNum = 1;
    private WorkWeeklyVo weeklyVo;
    private List<WorkWeeklyVo.DataBean.ListBean> data = new ArrayList<>();
    private WorkWeeklyAdapter adapter;
    private Handler uiHandler = null;
    private Handler handler = new Handler();
    private final int INIT_DATA = 1003;
    private boolean isLoading;
    public static boolean isMy = true;
    private PopupWindow pop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_weekly2);
        ButterKnife.bind(this);
        initTitle();
        isMy = true;
        initViewData();
        Config.type = 1;
        setSpinnerListener();
        //getData();
    }

    public void initTitle(){
        more_Tv.setVisibility(View.GONE);
        whoSpinner.setVisibility(View.VISIBLE);
        titleName_Tv.setText("工作汇报");
        title_Ll.setVisibility(View.GONE);
        search_Ll.setVisibility(View.GONE);
        who_Ll.setVisibility(View.GONE);
    }

    public void setSpinnerListener(){
        whoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String whos = (String) whoSpinner.getAdapter().getItem(position);
                isMy = whos.equals("我的") ? true:false;
                updateData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick({R.id.back_iv, R.id.my_tv, R.id.xs_tv, R.id.add_ib,R.id.ww_top_area})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.ww_top_area:
                ww_top_category_Label.setText(getResources().getString(R.string.
                        message_top_category_label_open));
                List<String> list = new ArrayList<String>();
                list.add("今天");
                list.add("本周");
                list.add("历史");
                showContactUsPopWin(WorkWeeklyActivity.this, "", list);
                pop.showAtLocation(view, Gravity.CENTER, 0, 0);
                break;
            case R.id.back_iv:
                finish();
                break;
            case R.id.my_tv:
                isMy = true;
                myView.setVisibility(View.VISIBLE);
                xsView.setVisibility(View.INVISIBLE);
                updateData();
                break;
            case R.id.xs_tv:
                isMy = false;
                myView.setVisibility(View.INVISIBLE);
                xsView.setVisibility(View.VISIBLE);
                updateData();
                break;
            case R.id.add_ib:
                startActivity(new Intent(WorkWeeklyActivity.this, WorkWeeklyAddActivity.class));
                break;
            case R.id.search_bt:
                updateData();
                ApkApplication.imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
        }
    }

    public void showContactUsPopWin(Context context, String title, final List<String> list) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_message, null);
        //view.getBackground().setAlpha(100);
        // 创建PopupWindow对象
        pop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, false);
        View pop_reward_view = view.findViewById(R.id.pop_reward_view);
        TextView name_tv = view.findViewById(R.id.pop_item_name_tv);
        TextView label_tv = view.findViewById(R.id.pop_item_label_tv);
        String content = ww_top_category_Name.getText().toString();
        final WorkWeeklyPopupWindowAdapter adapter = new WorkWeeklyPopupWindowAdapter(context, list, content);
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
                ww_top_category_Label.setText(getResources().getString(R.string.
                        message_top_category_label_close));
                pop.dismiss();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ww_top_category_Name.setText(list.get(i));
                ww_top_category_Label.setText(getResources().getString(R.string.
                        message_top_category_label_close));
                pop.dismiss();
                switch(i){
                    case 0:
                        Config.type = 1;
                        break;
                    case 1:
                        Config.type = 2;
                        break;
                    case 2:
                        Config.type = 3;
                        break;
                }
                updateData();
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
        if (isMy)
            getData(); //获取数据
        else
            getSubData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Config.isAddWork) {
            Config.isAddWork = false;
            updateData();
        }
    }

    public void initViewData() {
        adapter = new WorkWeeklyAdapter(WorkWeeklyActivity.this, data);
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
                        if (isMy)
                            getData(); //获取数据
                        else
                            getSubData();
                    }
                }, 1000);
            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(WorkWeeklyActivity.this);
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
                                if (weeklyVo.getData().isHasNextPage()) {
                                    pageNum++;
                                    if (isMy)
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
        adapter.setOnItemClickListener(new WorkWeeklyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(WorkWeeklyActivity.this, WorkWeeklyDetailActivity.class);
                WorkWeeklyVo.DataBean.ListBean listBean = (WorkWeeklyVo.DataBean.ListBean) adapter.getAll().get(position);
                intent.putExtra("repId", listBean.getRepid());
                startActivity(intent);
            }

            @Override
            public void onItemLongClickListener(View view, int position) {
            }
        });
    }

    private void getData() {
        String json = new Gson().toJson(new WorkWeeklyReq(pageNum, 10,
                new WorkWeeklyReq.WeeklyReport(Config.loginback.getUserId(), Config.type)));
        OkHttpUtils
                .postString()
                .url(Config.workWeekly)
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
                                weeklyVo = GjsonUtil.parseJsonWithGson(response, WorkWeeklyVo.class);
                                if (weeklyVo != null) {
                                    if (weeklyVo.isSuccess()) {
                                        if (weeklyVo.getData().getList() != null && weeklyVo.getData().getList().size() > 0) {
                                            for (int i = 0; i < weeklyVo.getData().getList().size(); i++) {
                                                data.add(weeklyVo.getData().getList().get(i));//.getData().getList().get(i)
                                            }
                                        }
                                        if (weeklyVo.getData().isHasNextPage()) {
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

    private void getSubData() {
        String json = new Gson().toJson(new WorkWeeklyReq(pageNum, 10,
                new WorkWeeklyReq.WeeklyReport(Config.loginback.getUserId(), Config.type)));
        OkHttpUtils
                .postString()
                .url(Config.workWeeklySub)
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
                                weeklyVo = GjsonUtil.parseJsonWithGson(response, WorkWeeklyVo.class);
                                if(weeklyVo!=null) {
                                    if (weeklyVo.isSuccess()) {
                                        if (weeklyVo.getData().getList() != null && weeklyVo.getData().getList().size() > 0) {
                                            for (int i = 0; i < weeklyVo.getData().getList().size(); i++) {
                                                data.add(weeklyVo.getData().getList().get(i));//.getData().getList().get(i)
                                            }
                                        }
                                        if (weeklyVo.getData().isHasNextPage()) {
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
