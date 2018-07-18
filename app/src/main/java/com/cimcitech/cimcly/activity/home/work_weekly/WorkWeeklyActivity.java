package com.cimcitech.cimcly.activity.home.work_weekly;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cimcitech.cimcly.ApkApplication;
import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.adapter.work_weekly.WorkWeeklyAdapter;
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

    @Bind(R.id.back_rl)
    RelativeLayout backRl;
    @Bind(R.id.add_bt)
    Button addBt;
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
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recycler_view_layout)
    CoordinatorLayout recyclerViewLayout;
    @Bind(R.id.textView1)
    Button textView1;
    @Bind(R.id.textView2)
    Button textView2;
    @Bind(R.id.textView3)
    Button textView3;
    @Bind(R.id.imageView1)
    ImageView imageView1;
    @Bind(R.id.imageView2)
    ImageView imageView2;
    @Bind(R.id.imageView3)
    ImageView imageView3;

    private int pageNum = 1;
    private WorkWeeklyVo weeklyVo;
    private List<WorkWeeklyVo.DataBean.ListBean> data = new ArrayList<>();
    private WorkWeeklyAdapter adapter;
    private Handler uiHandler = null;
    private Handler handler = new Handler();
    private final int INIT_DATA = 1003;
    private boolean isLoading;
    public static boolean isMy = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_weekly);
        ButterKnife.bind(this);
        isMy = true;
        initViewData();
        getData();
    }

    @OnClick({R.id.back_rl, R.id.my_tv, R.id.xs_tv, R.id.add_bt, R.id.textView1, R.id.textView2, R.id.textView3})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.back_rl:
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
            case R.id.add_bt:
                startActivity(new Intent(WorkWeeklyActivity.this, WorkWeeklyAddActivity.class));
                break;
            case R.id.search_bt:
                updateData();
                ApkApplication.imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            case R.id.textView1:
                setImageViewShow(imageView1);
                Config.type = 1;
                updateData();
                break;
            case R.id.textView2:
                setImageViewShow(imageView2);
                Config.type = 2;
                updateData();
                break;
            case R.id.textView3:
                setImageViewShow(imageView3);
                Config.type = 3;
                updateData();
                break;
        }
    }

    public void setImageViewShow(ImageView imageView) {
        imageView1.setVisibility(View.INVISIBLE);
        imageView2.setVisibility(View.INVISIBLE);
        imageView3.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.VISIBLE);
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
                                Log.d("mytest","workweekly response is: " + response);
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
