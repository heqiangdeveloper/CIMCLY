package com.cimcitech.cimcly.activity.home.customer_visit;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cimcitech.cimcly.ApkApplication;
import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.adapter.customer_visit.CustomerVisitFragmentAdapter;
import com.cimcitech.cimcly.bean.CustomerVisit;
import com.cimcitech.cimcly.bean.ListPagers;
import com.cimcitech.cimcly.bean.RequestBean.RuquMyVisit;
import com.cimcitech.cimcly.bean.Result;
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
 * Created by cimcitech on 2017/7/31.
 */

public class CustomerVisitFragment extends Fragment {

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
    @Bind(R.id.search_et)
    EditText searchEt;
    @Bind(R.id.search_bt)
    Button searchBt;
    @Bind(R.id.search_bar)
    LinearLayout searchBar;
    @Bind(R.id.title_ll)
    LinearLayout titleLl;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recycler_view_layout)
    CoordinatorLayout recyclerViewLayout;
    @Bind(R.id.back_img)
    ImageView back_Img;

    private int pageNum = 1;
    private CustomerVisitFragmentAdapter adapter;
    private boolean isLoading;
    private List<CustomerVisit> data = new ArrayList<>();
    private Handler uiHandler = null;
    private Handler handler = new Handler();
    private final int INIT_DATA = 1003;
    private Result<ListPagers<CustomerVisit>> status;
    public static boolean myData = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_customer_visit, container, false);
        ButterKnife.bind(this, view);
        initHandler();
        myData = true;
        back_Img.setVisibility(View.INVISIBLE);
        initViewData();
        getData();
        Log.d("hqf","customervisitfrag oncreate...");
        return view;

    }

    //刷新数据
    private void updateData() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(null != swipeRefreshLayout)
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

    @Override
    public void onResume() {
        super.onResume();
        Log.d("hqf","customervisitfrag  onresume...");
        if (Config.isAddVisit) {
            Config.isAddVisit = false;
            updateData();
        }
    }

    @OnClick({R.id.back_rl, R.id.my_tv, R.id.xs_tv, R.id.add_bt, R.id.search_bt})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.back_rl:
                //getActivity().finish();
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
            case R.id.add_bt:
                startActivity(new Intent(getActivity(), CustomerVisitAddActivity.class));
                break;
            case R.id.search_bt:
                updateData();
                ApkApplication.imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
        }
    }

    public void initViewData() {
        //防止切换底部的Fragment时，出现recyclerView内容的重复加载
        if(null != data){
            Log.d("hqf","customervisitfrag initViewData data is not null...");
            data.clear();
        }
        adapter = new CustomerVisitFragmentAdapter(getActivity(), data);
        swipeRefreshLayout.setColorSchemeResources(R.color.blueStatus);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(null != swipeRefreshLayout)
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
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
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
                    if(null != swipeRefreshLayout)
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
        adapter.setOnItemClickListener(new CustomerVisitFragmentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                CustomerVisit customerVisit = (CustomerVisit) adapter.getAll().get(position);
                Intent intent = new Intent(getActivity(), CustomerVisitDetailActivity.class);
                intent.putExtra("cvid", customerVisit.getCvid());
                startActivity(intent);
            }

            @Override
            public void onItemLongClickListener(View view, int position) {
                //长按
            }
        });
    }

    private void initHandler() {
        uiHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case INIT_DATA:
                        adapter.notifyDataSetChanged();
                        if(null != swipeRefreshLayout)
                            swipeRefreshLayout.setRefreshing(false);
                        adapter.notifyItemRemoved(adapter.getItemCount());
                        break;
                }
            }
        };
    }

    public void getData() {
        if(null != searchEt){//防止快速切换Fragment导致的FC
            String json = new Gson().toJson(new RuquMyVisit(pageNum, 10, "",
                    new RuquMyVisit.CustomerVisitBean(Config.loginback.getUserId(),
                            searchEt.getText().toString().trim())));

            Log.e("CustomerVisitActivity", json);
            OkHttpUtils
                    .postString()
                    .url(Config.custVisit)
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
                                    Type userlistType = new TypeToken<Result<ListPagers<CustomerVisit>>>() {
                                    }.getType();
                                    status = new Gson().fromJson(response, userlistType);
                                    if (status != null) {
                                        if (status.isSuccess()) {
                                            if (status.getData().getList() != null && status.getData().getList().size() > 0) {
                                                for (int i = 0; i < status.getData().getList().size(); i++) {
                                                    data.add(status.getData().getList().get(i));
                                                }
                                            }
                                            if (status.getData().isHasNextPage()) {
                                                adapter.setNotMoreData(false);
                                            } else {
                                                adapter.setNotMoreData(true);
                                            }
                                            adapter.notifyDataSetChanged();
                                            if(null != swipeRefreshLayout)
                                                swipeRefreshLayout.setRefreshing(false);
                                            adapter.notifyItemRemoved(adapter.getItemCount());
                                        }
                                    } else {
                                        adapter.notifyDataSetChanged();
                                        if(null != swipeRefreshLayout)
                                            swipeRefreshLayout.setRefreshing(false);
                                    }
                                }
                            }
                    );
        }
    }

    public void getSubData() {
        String json = new Gson().toJson(new RuquMyVisit(pageNum, 10, "",
                new RuquMyVisit.CustomerVisitBean(Config.loginback.getUserId())));
        Log.e("CustomerVisitActivity", json);
        OkHttpUtils
                .postString()
                .url(Config.custSubVisit)
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
                                Type userlistType = new TypeToken<Result<ListPagers<CustomerVisit>>>() {
                                }.getType();
                                status = new Gson().fromJson(response, userlistType);
                                if (status != null) {
                                    if (status.isSuccess()) {
                                        if (status.getData().getList() != null && status.getData().getList().size() > 0) {
                                            for (int i = 0; i < status.getData().getList().size(); i++) {
                                                data.add(status.getData().getList().get(i));
                                            }
                                        }
                                        if (status.getData().isHasNextPage()) {
                                            adapter.setNotMoreData(false);
                                        } else {
                                            adapter.setNotMoreData(true);
                                        }
                                        adapter.notifyDataSetChanged();
                                        if(null != swipeRefreshLayout)
                                             swipeRefreshLayout.setRefreshing(false);
                                        adapter.notifyItemRemoved(adapter.getItemCount());
                                    }
                                } else {
                                    adapter.notifyDataSetChanged();
                                    if(null != swipeRefreshLayout)
                                         swipeRefreshLayout.setRefreshing(false);
                                }
                            }
                        }
                );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        Log.d("hqf","customervisitfrag ondestroy...");
    }
}
