package com.cimcitech.cimcly.activity.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.activity.home.customer_visit.CustomerVisitActivity;
import com.cimcitech.cimcly.activity.home.annount.AnnounceDetailActivity;
import com.cimcitech.cimcly.activity.home.annount.AnnounceListActivity;
import com.cimcitech.cimcly.activity.home.car_in_storage.CarInStorageActivity;
import com.cimcitech.cimcly.activity.home.car_out_factory.CarOutFactoryActivity;
import com.cimcitech.cimcly.activity.home.contact_person.ContactPersonActivity;
import com.cimcitech.cimcly.activity.home.depart_request.DepartRequestActivity;
import com.cimcitech.cimcly.activity.home.feed_back.FeedBackActivity;
import com.cimcitech.cimcly.activity.home.my_client.MyClientActivity;
import com.cimcitech.cimcly.activity.home.order_contract.OrderContractActivity;
import com.cimcitech.cimcly.activity.home.payment.PaymentActivity;
import com.cimcitech.cimcly.activity.home.production.ProductionActivity;
import com.cimcitech.cimcly.activity.home.qrcode_in_storage.QRCodeInStorageActivity;
import com.cimcitech.cimcly.activity.home.qrcode_out_factory.QRCodeOutFactoryActivity;
import com.cimcitech.cimcly.activity.home.quoted_price.QuotedPriceActivity;
import com.cimcitech.cimcly.activity.home.report.ReportMainActivity;
import com.cimcitech.cimcly.activity.home.test.QRCodeTestActivity;
import com.cimcitech.cimcly.activity.home.test.QRCodeTestMainActivity;
import com.cimcitech.cimcly.activity.home.work_weekly.WorkWeeklyActivity;
import com.cimcitech.cimcly.activity.home.intention_track.IntentionTrackActivity;
import com.cimcitech.cimcly.adapter.HomeGridAdapter;
import com.cimcitech.cimcly.adapter.announce.AnnounceHomeAdapter;
import com.cimcitech.cimcly.bean.announce.AnnounceReq;
import com.cimcitech.cimcly.bean.announce.AnnounceVo;
import com.cimcitech.cimcly.utils.Config;
import com.cimcitech.cimcly.utils.GjsonUtil;
import com.cimcitech.cimcly.utils.ToastUtil;
import com.cimcitech.cimcly.utils.Utility;
import com.cimcitech.cimcly.widget.MyGridView;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

public class HomeFragment extends Fragment {

    @Bind(R.id.homeGrid)
    MyGridView homeGrid;
    @Bind(R.id.listContent)
    ListView listContent;
    @Bind(R.id.news_mote_tv)
    TextView newsMoteTv;

    private HomeGridAdapter gridAdapter;
    private AnnounceVo announceVo;
    private AnnounceHomeAdapter adapter;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home2, container, false);
        ButterKnife.bind(this, view);
        initViewData();
        getData();
        return view;
    }

    @OnClick(R.id.news_mote_tv)
    public void newsMore() {
        if(null != mContext) startActivity(new Intent(mContext, AnnounceListActivity.class));
    }

    class listContentItemListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            AnnounceVo.DataBean.ListBean listBean = adapter.getAll().get(i);
            if(null != mContext){
                Intent intent = new Intent(mContext, AnnounceDetailActivity.class);
                intent.putExtra("annId", listBean.getAnnid());
                startActivity(intent);
            }
        }
    }

    public void initViewData() {
        if(null != mContext){
            listContent.setOnItemClickListener(new listContentItemListener());
            String appAuthStr = Config.AppAuthStr;
            gridAdapter = new HomeGridAdapter(mContext,appAuthStr);
            homeGrid.setAdapter(gridAdapter);
            homeGrid.setSelector(R.drawable.hide_listview_yellow_selector);
            homeGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView textView = (TextView) view.findViewById(R.id.tv_logo);
                    String itemName = textView.getText().toString();
                    switch(itemName){
                        case "客户拜访":
                            startActivity(new Intent(mContext, CustomerVisitActivity.class));
                            break;
                        case "意向跟踪":
                            startActivity(new Intent(mContext, IntentionTrackActivity.class));
                            break;
                        case "报价单":
                            startActivity(new Intent(mContext, QuotedPriceActivity.class));
                            break;
                        case "订单合同":
                            startActivity(new Intent(mContext, OrderContractActivity.class));
                            break;
                        case "工作汇报":
                            startActivity(new Intent(mContext, WorkWeeklyActivity.class));
                            break;
                        case "我的客户":
                            startActivity(new Intent(mContext, MyClientActivity.class));
                            break;
                        case "联系人":
                            startActivity(new Intent(mContext, ContactPersonActivity.class));
                            break;
                        case "问题反馈":
                            startActivity(new Intent(mContext, FeedBackActivity.class));
                            break;
                        case "回款跟踪":
                            startActivity(new Intent(mContext, PaymentActivity.class));
                            break;
                        case "生产进度":
                            startActivity(new Intent(mContext, ProductionActivity.class));
                            break;
                        case "车辆入库":
                            startActivity(new Intent(mContext, CarInStorageActivity.class));
                            break;
                        case "发车申请":
                            startActivity(new Intent(mContext, DepartRequestActivity.class));
                            break;
                        case "车辆出厂":
                            startActivity(new Intent(mContext, CarOutFactoryActivity.class));
                            break;
                        case "扫码入库":
                            startActivity(new Intent(mContext, QRCodeInStorageActivity.class));
                            break;
                        case "扫码出厂":
                            startActivity(new Intent(mContext, QRCodeOutFactoryActivity.class));
                            break;
                        case "报表":
                            startActivity(new Intent(mContext, ReportMainActivity.class));
                            break;
                        case "检验":
                            startActivity(new Intent(mContext, QRCodeTestMainActivity.class));
                            break;
                    }
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    public void getData() {
        String json = new Gson().toJson(new AnnounceReq(1, 10, ""));
        OkHttpUtils
                .postString()
                .url(Config.announceList)
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
                                announceVo = GjsonUtil.parseJsonWithGson(response, AnnounceVo.class);
                                if (announceVo != null)
                                    if (announceVo.isSuccess())
                                        if (announceVo.getData().getList().size() > 0) {
                                            if(mContext != null){
                                                adapter = new AnnounceHomeAdapter(mContext, announceVo.getData().getList());
                                                if(null != listContent){
                                                    listContent.setAdapter(adapter);
                                                    //以下代码不可少，否则页面不能够上下滑动
                                                    new Utility().setListViewHeightBasedOnChildren(listContent);
                                                }
                                            }
                                        }
                            }
                        }
                );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
