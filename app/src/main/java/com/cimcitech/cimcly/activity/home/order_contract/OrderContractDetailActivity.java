package com.cimcitech.cimcly.activity.home.order_contract;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.adapter.order_contract.OrderContractDetailAdapter;
import com.cimcitech.cimcly.bean.order_contract.OrderContractDetailVo;
import com.cimcitech.cimcly.bean.order_contract.OrderContractSaveVo;
import com.cimcitech.cimcly.bean.order_contract.OrderStandardDetailVo;
import com.cimcitech.cimcly.utils.Config;
import com.cimcitech.cimcly.utils.DateTool;
import com.cimcitech.cimcly.utils.GjsonUtil;
import com.cimcitech.cimcly.utils.ToastUtil;
import com.cimcitech.cimcly.widget.BaseActivity;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

public class OrderContractDetailActivity extends BaseActivity {


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
    @Bind(R.id.contractno_tv)
    TextView contractnoTv;
    @Bind(R.id.owner_tv)
    TextView ownerTv;
    @Bind(R.id.orderno_tv)
    TextView ordernoTv;
    @Bind(R.id.fstate_tv)
    TextView fstateTv;
    @Bind(R.id.custName_tv)
    TextView custNameTv;
    @Bind(R.id.signdate_tv)
    TextView signdateTv;
    @Bind(R.id.custtype_tv)
    TextView custtypeTv;
    @Bind(R.id.totalprice_tv)
    TextView totalpriceTv;
    @Bind(R.id.residual_tv)
    TextView residualTv;
    @Bind(R.id.count_tv)
    EditText countTv;
    @Bind(R.id.isbringchassis_tv)
    TextView isbringchassisTv;
    @Bind(R.id.chassiscount_tv)
    TextView chassiscountTv;
    @Bind(R.id.isSpecial_tv)
    TextView isSpecialTv;
    @Bind(R.id.systemprice_tv)
    TextView systempriceTv;
    @Bind(R.id.protocolPrice_tv)
    TextView protocolPriceTv;
    @Bind(R.id.productCategory_tv)
    TextView productCategoryTv;
    @Bind(R.id.prodtypecode_tv)
    TextView prodtypecodeTv;
    @Bind(R.id.paymentMethodDesc_tv)
    TextView paymentMethodDescTv;
    @Bind(R.id.soldToParty_tv)
    TextView soldToPartyTv;
    @Bind(R.id.contractTypeDesc_tv)
    TextView contractTypeDescTv;
    @Bind(R.id.sitecode_tv)
    TextView sitecodeTv;
    @Bind(R.id.isbringmaterial_tv)
    TextView isbringmaterialTv;
    @Bind(R.id.arrivalDate_tv)
    TextView arrivalDateTv;
    @Bind(R.id.signplace_tv)
    TextView signplaceTv;
    @Bind(R.id.ordertype_tv)
    TextView ordertypeTv;
    @Bind(R.id.deposit_tv)
    TextView depositTv;
    @Bind(R.id.singlePrice_tv)
    TextView singlePriceTv;
    @Bind(R.id.chassismodel_tv)
    TextView chassismodelTv;
    @Bind(R.id.dpPrice_tv)
    TextView dpPriceTv;
    @Bind(R.id.currencyDesc_tv)
    TextView currencyDescTv;
    @Bind(R.id.financialprice_tv)
    TextView financialpriceTv;
    @Bind(R.id.productVariety_tv)
    TextView productVarietyTv;
    @Bind(R.id.paymethodDesc_tv)
    TextView paymethodDescTv;
    @Bind(R.id.shipPartyName_td)
    TextView shipPartyNameTd;
    @Bind(R.id.siteMethodDesc_tv)
    TextView siteMethodDescTv;
    @Bind(R.id.estimatedeliverydate_tv)
    TextView estimatedeliverydateTv;
    @Bind(R.id.ownMaterial_tv)
    TextView ownMaterialTv;
    @Bind(R.id.textView1)
    Button textView1;
    @Bind(R.id.textView2)
    Button textView2;
    @Bind(R.id.textView3)
    Button textView3;
    @Bind(R.id.textView4)
    Button textView4;
    @Bind(R.id.imageView1)
    ImageView imageView1;
    @Bind(R.id.imageView2)
    ImageView imageView2;
    @Bind(R.id.imageView3)
    ImageView imageView3;
    @Bind(R.id.imageView4)
    ImageView imageView4;
    @Bind(R.id.listContent1)
    ListView listContent1;
    @Bind(R.id.listContent2)
    ListView listContent2;
    @Bind(R.id.listContent3)
    ListView listContent3;
    @Bind(R.id.listContent4)
    ListView listContent4;
    @Bind(R.id.save_bt)
    Button saveBt;
    @Bind(R.id.cancel_bt)
    Button cancelBt;
    @Bind(R.id.submit_bt)
    Button submitBt;
    @Bind(R.id.bottom_layout)
    LinearLayout bottomLayout;

    private View order_contract, specifications_confirmation_view;
    private OrderStandardDetailVo orderStandardDetailVo;
    private int sOrderId;
    private OrderContractDetailVo detailVo;

    //底盘特性 list
    private List<OrderStandardDetailVo.DataBean.ModelFeatureDetailListBean> chassisModels = new ArrayList<>();
    //图案要求 list
    private List<OrderStandardDetailVo.DataBean.ModelFeatureDetailListBean> patternModels = new ArrayList<>();
    //选装配置 list
    private List<OrderStandardDetailVo.DataBean.ModelFeatureDetailListBean> optionalModels = new ArrayList<>();
    //特殊要求 list
    private List<OrderStandardDetailVo.DataBean.ModelFeatureDetailListBean> specialModels = new ArrayList<>();

    // adapter
    private OrderContractDetailAdapter optionalAdapter;
    private OrderContractDetailAdapter chassisAdapter;
    private OrderContractDetailAdapter patternAdapter;
    private OrderContractDetailAdapter specialAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_contract_detail);
        ButterKnife.bind(this);
        sOrderId = this.getIntent().getIntExtra("sOrderId", 0);
        order_contract = this.findViewById(R.id.order_contract_detail_view);
        specifications_confirmation_view = this.findViewById(R.id.specifications_confirmation_view);
        mLoading.show();
        getData();
    }

    public void setListViewShow(ListView listView) {
        listContent1.setVisibility(View.GONE);
        listContent2.setVisibility(View.GONE);
        listContent3.setVisibility(View.GONE);
        listContent4.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
    }

    public void setImageViewShow(ImageView imageView) {
        imageView1.setVisibility(View.INVISIBLE);
        imageView2.setVisibility(View.INVISIBLE);
        imageView3.setVisibility(View.INVISIBLE);
        imageView4.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.back_rl, R.id.my_tv, R.id.xs_tv, R.id.textView1,
            R.id.textView2, R.id.textView3, R.id.textView4, R.id.save_bt, R.id.cancel_bt, R.id.submit_bt})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.back_rl:
                finish();
                break;
            case R.id.my_tv:
                myView.setVisibility(View.VISIBLE);
                xsView.setVisibility(View.INVISIBLE);
                order_contract.setVisibility(View.VISIBLE);
                specifications_confirmation_view.setVisibility(View.GONE);
                break;
            case R.id.xs_tv:
                myView.setVisibility(View.INVISIBLE);
                xsView.setVisibility(View.VISIBLE);
                order_contract.setVisibility(View.GONE);
                specifications_confirmation_view.setVisibility(View.VISIBLE);
                break;
            case R.id.textView1://底盘特性
                setImageViewShow(imageView1);
                setListViewShow(listContent1);
                break;
            case R.id.textView2://图案要求
                setImageViewShow(imageView2);
                setListViewShow(listContent2);
                break;
            case R.id.textView3://选装配置
                setImageViewShow(imageView3);
                setListViewShow(listContent3);
                break;
            case R.id.textView4://特殊要求
                setImageViewShow(imageView4);
                setListViewShow(listContent4);
                break;
            case R.id.save_bt:
                if (countTv.getText().toString().trim().equals("")
                        || Integer.parseInt(countTv.getText().toString().trim()) <= 0) {
                    ToastUtil.showToast("上装数量必须大于0");
                } else {
                    sContOrderSave();
                }
                break;
            case R.id.cancel_bt:
                if (countTv.getText().toString().trim().equals("")
                        || Integer.parseInt(countTv.getText().toString().trim()) <= 0) {
                    ToastUtil.showToast("上装数量必须大于0");
                } else {
                    sContOrderCancel();
                }
                break;
            case R.id.submit_bt:
                if (countTv.getText().toString().trim().equals("")
                        || Integer.parseInt(countTv.getText().toString().trim()) <= 0) {
                    ToastUtil.showToast("上装数量必须大于0");
                } else {
                    mLoading.show();
                    sContOrderSubmit();
                }
                break;
        }
    }

    public void getData() {
        OkHttpUtils
                .post()
                .url(Config.orderContractInfo)
                .addHeader("checkTokenKey", Config.loginback.getToken())
                .addHeader("sessionKey", Config.loginback.getUserId() + "")
                .addParams("sOrderId", sOrderId + "")
                .build()
                .execute(
                        new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                ToastUtil.showNetError();
                                if(mLoading.isShowing()) mLoading.dismiss();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                //ToastUtil.showToast(response);
                                detailVo = GjsonUtil.parseJsonWithGson(response, OrderContractDetailVo.class);
                                if (detailVo != null)
                                    if (detailVo.isSuccess())
                                        initViewData();
                                getOrderStandard();
                                    if(mLoading.isShowing()) mLoading.dismiss();
                            }
                        }
                );
    }

    public void getOrderStandard() {
        OkHttpUtils
                .post()
                .url(Config.getOrderStandard)
                .addHeader("checkTokenKey", Config.loginback.getToken())
                .addHeader("sessionKey", Config.loginback.getUserId() + "")
                .addParams("sOrderId", sOrderId + "")
                .build()
                .execute(
                        new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                ToastUtil.showNetError();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.d("testlog","response is: " + response);
                                orderStandardDetailVo = GjsonUtil.parseJsonWithGson(response, OrderStandardDetailVo.class);
                                if (orderStandardDetailVo != null)
                                    if (orderStandardDetailVo.isSuccess()) {
                                        //底盘属性list
                                        if (orderStandardDetailVo.getData().size() > 0)
                                            for (int i = 0; i < orderStandardDetailVo.getData().size(); i++)
                                                if (orderStandardDetailVo.getData().get(i).getViewLabel().equals("QP09"))
                                                    chassisModels = orderStandardDetailVo.getData().get(i)
                                                            .getModelFeatureDetailList();

                                        //图案要求list
                                        if (orderStandardDetailVo.getData().size() > 0)
                                            for (int i = 0; i < orderStandardDetailVo.getData().size(); i++)
                                                if (orderStandardDetailVo.getData().get(i).getViewLabel().equals("QP08"))
                                                    patternModels = orderStandardDetailVo.getData().get(i)
                                                            .getModelFeatureDetailList();

                                        //选装配置list
                                        if (orderStandardDetailVo.getData().size() > 0)
                                            for (int i = 0; i < orderStandardDetailVo.getData().size(); i++)
                                                if (orderStandardDetailVo.getData().get(i).getViewLabel().equals("QP05"))
                                                    optionalModels = orderStandardDetailVo.getData().get(i)
                                                            .getModelFeatureDetailList();

                                        //特殊要求list
                                        if (orderStandardDetailVo.getData().size() > 0)
                                            for (int i = 0; i < orderStandardDetailVo.getData().size(); i++)
                                                if (orderStandardDetailVo.getData().get(i).getViewLabel().equals("QP06"))
                                                    specialModels = orderStandardDetailVo.getData().get(i)
                                                            .getModelFeatureDetailList();

                                        optionalAdapter = new OrderContractDetailAdapter(OrderContractDetailActivity.this,
                                                optionalModels);
                                        chassisAdapter = new OrderContractDetailAdapter(OrderContractDetailActivity.this,
                                                chassisModels);
                                        patternAdapter = new OrderContractDetailAdapter(OrderContractDetailActivity.this,
                                                patternModels);
                                        specialAdapter = new OrderContractDetailAdapter(OrderContractDetailActivity.this,
                                                specialModels);

                                        listContent1.setAdapter(chassisAdapter);
                                        listContent2.setAdapter(patternAdapter);
                                        listContent3.setAdapter(optionalAdapter);
                                        listContent4.setAdapter(specialAdapter);

                                        listContent1.setVisibility(View.VISIBLE);
                                    }
                            }
                        }
                );
    }

    private void initViewData() {
        final double boudle;
        final double deposit;
        OrderContractDetailVo.DataBean data = detailVo.getData();
        if (data.getFstate().equals("FS01") && data.getIscommit().equals("N")) {
            bottomLayout.setVisibility(View.VISIBLE);
        } else
            bottomLayout.setVisibility(View.GONE);
        contractnoTv.setText(data.getContractno());
        ownerTv.setText(data.getOwnerName());
        ordernoTv.setText(data.getOrderno());
        fstateTv.setText(data.getfStateName());

        if (data.getCount() == 0) {
            boudle = data.getTotalprice() / 1;//合同金额/上装数量
            deposit = data.getDeposit() / 1;//定金金额/上装数量
        } else {
            boudle = data.getTotalprice() / data.getCount();//合同金额/上装数量
            deposit = data.getDeposit() / data.getCount();//定金金额/上装数量
        }
        custNameTv.setText(data.getCustName() != null ? "客户名称：" + data.getCustName() : "客户名称：");
        signdateTv.setText(data.getSigndate() > 0 ? "签订日期：" + DateTool.getDateStr(data.getSigndate()) : "签订日期：");
        custtypeTv.setText(data.getCustTypeDesc() != null ? "客户类型：" + data.getCustTypeDesc() : "客户类型：");
        totalpriceTv.setText(data.getTotalprice() > 0 ? data.getTotalprice() + "" : "0");//合同金额
        residualTv.setText(data.getResidual() > 0 ? "" + data.getResidual() : "0");//剩余金额
        depositTv.setText(data.getDeposit() > 0 ? "" + data.getDeposit() : "0");//定金金额
        countTv.setText(data.getCount() > 0 ? data.getCount() + "" : "0");//上装数量
        isbringchassisTv.setText(data.getIsbringchassis().equals("Y") ? "底盘来源：客户自带" : "底盘来源：公司采购");
        chassiscountTv.setText(data.getChassiscount() > 0 ? "底盘数量：" + data.getChassiscount() : "底盘数量：");
        isSpecialTv.setText(data.getIsspecial().equals("T") ? "是否特批：是" : "是否特批：否");
        systempriceTv.setText(data.getSystemprice() > 0 ? "计算价格：" + data.getSystemprice() : "计算价格：");
        protocolPriceTv.setText(data.getProtocolPrice() > 0 ? "建议价格：" + data.getProtocolPrice() : "建议价格：");
        productCategoryTv.setText(data.getProductCategoryDesc() != null ? "产品类型：" + data.getProductCategoryDesc() : "产品类型：");
        prodtypecodeTv.setText(data.getProdtypecode() != null ? "产品型号：" + data.getProdtypecode() : "产品型号：");
        paymentMethodDescTv.setText(data.getPaymentMethodDesc() != null ? "付款方式：" + data.getPaymentMethodDesc() : "付款方式：");
        soldToPartyTv.setText(data.getSoldtoparty() != null ? "售达方：" + data.getSoldtoparty() : "售达方：");
        contractTypeDescTv.setText(data.getContractTypeDesc() != null ? "合同类型：" + data.getContractTypeDesc() : "合同类型：");
        sitecodeTv.setText(data.getSitecode() != null ? "交货地点：" + data.getSitecode() : "交货地点：");
        isbringmaterialTv.setText(data.getIsbringmaterial().equals("Y") ? "自带物资：自带物资" : "自带物资：不带物资");
        arrivalDateTv.setText(data.getArrivaldate() > 0 ? "到场日期：" + DateTool.getDateStr(data
                .getArrivaldate()) : "到场日期：");
        signplaceTv.setText(data.getSigndate() > 0 ? "签订地点：" + DateTool.getDateStr(data.getSigndate()) : "签订地点：");
        ordertypeTv.setText(data.getOrdertype() != null ? "订单类型：" + data.getOrderTypeDesc() : "订单类型：");
        singlePriceTv.setText(data.getSinglePrice() > 0 ? "上装单价：" + data.getSinglePrice() : "装单价：");
        chassismodelTv.setText(data.getChassismodel() != null ? "底盘型号：" + data.getChassismodel() : "底盘型号：");
        dpPriceTv.setText(data.getDpprice() > 0 ? "底盘价格：" + data.getDpprice() : "底盘价格：");
        currencyDescTv.setText(data.getCustTypeDesc() != null ? "币种：" + data.getCustTypeDesc() : "币种：");
        financialpriceTv.setText(data.getFinancialprice() > 0 ? "审批价格：" + data.getFinancialprice() : "审批价格：");
        productVarietyTv.setText(data.getProductVarietyDesc() != null ? "产品品种：" + data.getProductVarietyDesc() : "产品品种：");
        paymethodDescTv.setText(data.getPaymentMethodDesc() != null ? "支付方式：" + data.getPaymentMethodDesc() : "支付方式：");
        shipPartyNameTd.setText(data.getShipPartyName() != null ? "运达方：" + data.getShipPartyName() : "运达方：");
        siteMethodDescTv.setText(data.getSiteMethodDesc() != null ? "运输方式：" + data.getSiteMethodDesc() : "运输方式：");
        estimatedeliverydateTv.setText(data.getEstimatedeliverydate() > 0 ? "交货时间：" + DateTool.getDateStr(data.getEstimatedeliverydate()) : "交货时间：");
        ownMaterialTv.setText(data.getOwnmaterial() != null ? "物资名称：" + data.getOwnmaterial() : "物资名称：");
        countTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!countTv.getText().toString().trim().equals("")) {
                    double count = Double.parseDouble(countTv.getText().toString().trim());
                    double newBoudle = count * boudle;
                    double newDeposit = count * deposit;//定金金额/上装数量
                    double newResidual = newBoudle - newDeposit;
                    totalpriceTv.setText(newBoudle + "");
                    residualTv.setText(newResidual + "");
                    depositTv.setText(newDeposit + "");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    //终止
    public void sContOrderCancel() {
        OkHttpUtils
                .post()
                .url(Config.sContOrderCancel)
                .addHeader("checkTokenKey", Config.loginback.getToken())
                .addHeader("sessionKey", Config.loginback.getUserId() + "")
                .addParams("sOrderId", detailVo.getData().getSorderid() + "")
                .addParams("userId", Config.loginback.getUserId() + "")
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
                                try {
                                    JSONObject json = new JSONObject(response);
                                    if (json.getBoolean("success")) {
                                        ToastUtil.showToast("终止成功");
                                        finish();
                                    } else {
                                        ToastUtil.showToast("终止失败");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
    }

    //提交
    public void sContOrderSubmit() {
        OkHttpUtils
                .post()
                .url(Config.sContOrderSubmit)
                .addHeader("checkTokenKey", Config.loginback.getToken())
                .addHeader("sessionKey", Config.loginback.getUserId() + "")
                .addParams("sOrderId", detailVo.getData().getSorderid() + "")
                .addParams("userId", Config.loginback.getUserId() + "")
                .build()
                .execute(
                        new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                ToastUtil.showNetError();
                                mLoading.dismiss();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                try {
                                    JSONObject json = new JSONObject(response);
                                    if (json.getBoolean("success")) {
                                        ToastUtil.showToast("提交成功");
                                        finish();
                                    } else {
                                        ToastUtil.showToast("提交失败");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                mLoading.dismiss();
                            }
                        }
                );
    }

    //修改
    public void sContOrderSave() {

        int sorderid = detailVo.getData().getSorderid();
        String count = countTv.getText().toString().trim();
        double deposit = Double.parseDouble(depositTv.getText().toString().trim());
        double totalprice = Double.parseDouble(totalpriceTv.getText().toString().trim());

        String json = new Gson().toJson(new OrderContractSaveVo(sorderid, count, deposit, totalprice));

        OkHttpUtils
                .postString()
                .url(Config.sContOrderSave)
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
                                try {
                                    JSONObject json = new JSONObject(response);
                                    if (json.getBoolean("success")) {
                                        ToastUtil.showToast("修改成功");
                                        finish();
                                    } else {
                                        ToastUtil.showToast("修改失败");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
    }
}
