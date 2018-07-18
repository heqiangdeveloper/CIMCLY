package com.cimcitech.cimcly.activity.home.intention_track;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.adapter.PopupWindowAdapter;
import com.cimcitech.cimcly.bean.client.ClientNameVo;
import com.cimcitech.cimcly.bean.client.ClientVo;
import com.cimcitech.cimcly.bean.contact.ContactNameVo;
import com.cimcitech.cimcly.bean.contact.CustIdReq;
import com.cimcitech.cimcly.bean.customer_visit.CustomerVisitInfoVo;
import com.cimcitech.cimcly.bean.opport_unit.OpportSelectVo;
import com.cimcitech.cimcly.bean.opport_unit.OpportUnitAddReq;
import com.cimcitech.cimcly.bean.opport_unit.OpprtCustReq;
import com.cimcitech.cimcly.utils.Config;
import com.cimcitech.cimcly.utils.DateTool;
import com.cimcitech.cimcly.utils.GjsonUtil;
import com.cimcitech.cimcly.utils.ToastUtil;
import com.cimcitech.cimcly.widget.BaseActivity;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

public class IntentionTrackAddActivity extends BaseActivity {
    @Bind(R.id.add_bt)
    Button addBt;
    @Bind(R.id.client_no)
    TextView clientNo;
    @Bind(R.id.time_tv)
    TextView timeTv;
    @Bind(R.id.client_name_tv)
    TextView clientNameTv;
    @Bind(R.id.contact_person_tv)
    TextView contactPersonTv;
    @Bind(R.id.intentional_theme_et)
    EditText intentionalThemeEt;
    @Bind(R.id.product_category_tv)
    TextView productCategoryTv;
    @Bind(R.id.product_variety_tv)
    TextView productVarietyTv;
    @Bind(R.id.product_model_tv)
    TextView productModelTv;
    @Bind(R.id.product_number_et)
    EditText productNumberEt;
    @Bind(R.id.contract_amount_et)
    EditText contractAmountEt;
    @Bind(R.id.delivery_date_tv)
    TextView deliveryDateTv;
    @Bind(R.id.order_type_tv)
    TextView orderTypeTv;
    @Bind(R.id.possibility_tv)
    TextView possibilityTv;
    @Bind(R.id.payment_method_tv)
    TextView paymentMethodTv;
    @Bind(R.id.detailed_requirements_et)
    EditText detailedRequirementsEt;
    @Bind(R.id.remarks_information_et)
    EditText remarksInformationEt;
    @Bind(R.id.currency_tv)
    TextView currencyTv;
    @Bind(R.id.titleName_tv)
    TextView titleName_Tv;
    @Bind(R.id.more_tv)
    TextView more_Tv;
    @Bind(R.id.title_ll)
    LinearLayout title_Ll;
    @Bind(R.id.who_spinner)
    Spinner whoSpinner;

    public int intValue = 0;
    private PopupWindow pop;

    private CustomerVisitInfoVo info; //拜访详情过来的
    private ClientVo customer; //我的客户过来的

    private ClientNameVo clientVo; //客户
    private ClientNameVo.Data item; //客户item

    private ContactNameVo contactNameVo; //联系人
    private ContactNameVo.Data contactItem; //联系他item

    private OpportSelectVo opportSelectVo; //选择项
    private OpportSelectVo.DataBean.ProductBean productBean;//产品类别
    private OpportSelectVo.DataBean.ProductBean.CodeValueListBeanX codeValueListBeanX; //产品品种
    private OpportSelectVo.DataBean.ProductBean.CodeValueListBeanX.CodeValueListBean codeValueListBean;//产品型号

    private OpportSelectVo.DataBean.PayMethodBean payMethodBean; //付款方式
    private OpportSelectVo.DataBean.PossibiltyBean possibiltyBean;//可能性
    private OpportSelectVo.DataBean.OpportTypeBean opportTypeBean;//订单类型
    private OpportSelectVo.DataBean.CurrencyBean currencyBean;//币种

    private int mYear, mMonth, mDay;
    private final int DATE_DIALOG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_intention_track2);
        ButterKnife.bind(this);
        initTitle();

        getClientData();
        getOpportSelect();
        info = (CustomerVisitInfoVo) this.getIntent().getSerializableExtra("CustomerVisitInfo");
        customer = (ClientVo) this.getIntent().getSerializableExtra("customer");
        timeTv.setText(DateTool.getSystemDate());
        deliveryDateTv.setText(DateTool.getSystemDate());
        final Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
        setDataView();
    }

    public void initTitle(){
        more_Tv.setVisibility(View.GONE);
        title_Ll.setVisibility(View.GONE);
        whoSpinner.setVisibility(View.GONE);
        titleName_Tv.setText("新建意向订单");
    }

    public void setDataView() {
        //客户拜访详情跳转过来
        if (info != null) {
            //clientNameTv.setText(info.getData().getCustname());
            clientNameTv.setTextColor(Color.parseColor("#333333"));
            getContactPersonData(info.getData().getCustid());
            //我的客户过来的
        } else if (customer != null) {
            //clientNameTv.setText(customer.getData().getCustname());
            clientNameTv.setTextColor(Color.parseColor("#333333"));
            getContactPersonData(customer.getData().getCustid());
        }
    }

    @OnClick({R.id.back_iv, R.id.client_name_tv, R.id.contact_person_tv, R.id.product_category_tv,
            R.id.product_variety_tv, R.id.product_model_tv, R.id.delivery_date_tv, R.id.order_type_tv,
            R.id.possibility_tv, R.id.payment_method_tv, R.id.currency_tv, R.id.add_bt})
    public void onclick(View v) {
        List<String> list;
        switch (v.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.client_name_tv:
                intValue = 0;
                list = new ArrayList<>();
                if (clientVo != null) {
                    for (int i = 0; i < clientVo.getData().size(); i++) {
                        list.add(clientVo.getData().get(i).getCustname());
                    }
                }
                showContactUsPopWin(IntentionTrackAddActivity.this, "选择客户", list);
                pop.showAtLocation(v, Gravity.CENTER, 0, 0);
                break;
            case R.id.contact_person_tv:
                intValue = 1;
                if (contactNameVo != null) {
                    if (contactNameVo.isSuccess()) {
                        list = new ArrayList<>();
                        for (int i = 0; i < contactNameVo.getData().size(); i++)
                            list.add(contactNameVo.getData().get(i).getPersonname());
                        showContactUsPopWin(IntentionTrackAddActivity.this, "选择联系人", list);
                        pop.showAtLocation(v, Gravity.CENTER, 0, 0);
                    }
                } else
                    ToastUtil.showToast("请先选择客户");
                break;
            case R.id.product_category_tv:
                intValue = 2;
                if (opportSelectVo != null)
                    if (opportSelectVo.isSuccess()) {
                        list = new ArrayList<>();
                        for (int i = 0; i < opportSelectVo.getData().getProduct().size(); i++)
                            list.add(opportSelectVo.getData().getProduct().get(i).getCodevalue());
                        showContactUsPopWin(IntentionTrackAddActivity.this, "选择产品类别", list);
                        pop.showAtLocation(v, Gravity.CENTER, 0, 0);
                    }
                break;
            case R.id.product_variety_tv:
                intValue = 3;
                if (opportSelectVo != null)
                    if (productBean != null) {
                        list = new ArrayList<>();
                        for (int i = 0; i < productBean.getCodeValueList().size(); i++)
                            list.add(productBean.getCodeValueList().get(i).getCodevalue());
                        showContactUsPopWin(IntentionTrackAddActivity.this, "选择产品品种", list);
                        pop.showAtLocation(v, Gravity.CENTER, 0, 0);
                    } else
                        ToastUtil.showToast("请先选择产品类别");
                break;
            case R.id.product_model_tv:
                intValue = 4;
                if (opportSelectVo != null)
                    if (productBean != null)
                        if (codeValueListBeanX != null) {
                            list = new ArrayList<>();
                            for (int i = 0; i < codeValueListBeanX.getCodeValueList().size(); i++)
                                list.add(codeValueListBeanX.getCodeValueList().get(i).getCodetype() + "|"
                                        + codeValueListBeanX.getCodeValueList().get(i).getCodevalue());
                            showContactUsPopWin(IntentionTrackAddActivity.this, "选择产品型号", list);
                            pop.showAtLocation(v, Gravity.CENTER, 0, 0);
                        } else
                            ToastUtil.showToast("请先选择产品品种");
                    else
                        ToastUtil.showToast("请先选择产品类别");
                break;
            case R.id.delivery_date_tv:
                showDialog(DATE_DIALOG);//跳出时间选择控件
                break;
            case R.id.order_type_tv:
                intValue = 5;
                if (opportSelectVo != null) {
                    list = new ArrayList<>();
                    for (int i = 0; i < opportSelectVo.getData().getOpportType().size(); i++)
                        list.add(opportSelectVo.getData().getOpportType().get(i).getCodevalue());
                    showContactUsPopWin(IntentionTrackAddActivity.this, "选择订单类型", list);
                    pop.showAtLocation(v, Gravity.CENTER, 0, 0);
                }
                break;
            case R.id.possibility_tv:
                intValue = 6;
                if (opportSelectVo != null) {
                    list = new ArrayList<>();
                    for (int i = 0; i < opportSelectVo.getData().getPossibilty().size(); i++)
                        list.add(opportSelectVo.getData().getPossibilty().get(i).getCodevalue());
                    showContactUsPopWin(IntentionTrackAddActivity.this, "选择可能性", list);
                    pop.showAtLocation(v, Gravity.CENTER, 0, 0);
                }
                break;
            case R.id.payment_method_tv:
                intValue = 7;
                if (opportSelectVo != null) {
                    list = new ArrayList<>();
                    for (int i = 0; i < opportSelectVo.getData().getPayMethod().size(); i++)
                        list.add(opportSelectVo.getData().getPayMethod().get(i).getCodevalue());
                    showContactUsPopWin(IntentionTrackAddActivity.this, "选择付款方式", list);
                    pop.showAtLocation(v, Gravity.CENTER, 0, 0);
                }
                break;
            case R.id.currency_tv:
                intValue = 8;
                if (opportSelectVo != null) {
                    list = new ArrayList<>();
                    for (int i = 0; i < opportSelectVo.getData().getCurrency().size(); i++)
                        list.add(opportSelectVo.getData().getCurrency().get(i).getCodevalue());
                    showContactUsPopWin(IntentionTrackAddActivity.this, "选择币种", list);
                    pop.showAtLocation(v, Gravity.CENTER, 0, 0);
                }
                break;
            case R.id.add_bt:
                if (!checkInput()) return;
                mLoading.show();
                AddOpportUnitData();
                break;
        }
    }

    public boolean checkInput() {
        if (item == null && info == null && customer == null) {
            ToastUtil.showToast("请选择客户");
            return false;
        }
        if (contactItem == null) {
            ToastUtil.showToast("请选择联系人");
            return false;
        }
        if (intentionalThemeEt.getText().toString().trim().equals("")) {
            ToastUtil.showToast("请输入意向主题");
            return false;
        }
        if (productBean == null) {
            ToastUtil.showToast("请选择产品类别");
            return false;
        }
        if (codeValueListBeanX == null) {
            ToastUtil.showToast("请选择产品品种");
            return false;
        }
        if (codeValueListBean == null) {
            ToastUtil.showToast("请选择产品型号");
            return false;
        }
        if (productNumberEt.getText().toString().trim().equals("")) {
            ToastUtil.showToast("请输入产品数量");
            return false;
        }
        if (contractAmountEt.getText().toString().trim().equals("")) {
            ToastUtil.showToast("请输入预计合同金额");
            return false;
        }
        if (opportTypeBean == null) {
            ToastUtil.showToast("请选择订单类型");
            return false;
        }
        if (possibiltyBean == null) {
            ToastUtil.showToast("请选择可能性");
            return false;
        }
        if (payMethodBean == null) {
            ToastUtil.showToast("请选择付款方式");
            return false;
        }
        if (currencyBean == null) {
            ToastUtil.showToast("请选择币种");
            return false;
        }
        return true;
    }

    /*******时间控件start********/

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG:
                return new DatePickerDialog(this, mdateListener, mYear, mMonth, mDay);
        }
        return null;
    }

    /**
     * 设置日期 利用StringBuffer追加
     */
    public void display() {
        //dateDisplay.setText(new StringBuffer().append(mMonth + 1).append("-")
        //.append(mDay).append("-").append(mYear).append(" "));
        String string = (new StringBuffer()
                .append(mYear).append("-")
                .append(mMonth + 1).append("-")
                .append(mDay).append(""))
                .toString();
        string = DateTool.transferDateStr(string);//转化为标准日期
        try {
            SimpleDateFormat foramt = new SimpleDateFormat("yyyy-MM-dd");
            Date mDate1 = foramt.parse(timeTv.getText().toString().trim());
            Date mDate2 = foramt.parse(string);
            if (mDate1.compareTo(mDate2) > 0) {
                ToastUtil.showToast("起日期大于止日期");
            } else {
                deliveryDateTv.setText(string);
                deliveryDateTv.setTextColor(Color.parseColor("#333333"));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private DatePickerDialog.OnDateSetListener mdateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            display();
        }
    };

    /*******时间空间end********/

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
                if (intValue == 0) {//客户名称
                    item = clientVo.getData().get(i);
                    clientNameTv.setText(item.getCustname() != null && !item.getCustname().equals("") ? item.getCustname() : "");
                    clientNo.setText(item.getCustomerno() != null && !item.getCustomerno().equals("") ? item.getCustomerno() : "");
                    clientNameTv.setTextColor(Color.parseColor("#333333"));
                    getContactPersonData(item.getCustid());
                    pop.dismiss();
                }
                if (intValue == 1) {
                    contactItem = contactNameVo.getData().get(i);
                    contactPersonTv.setText(contactItem.getPersonname()
                            != null && !contactItem.getPersonname().equals("") ? contactItem.getPersonname() : "");
                    contactPersonTv.setTextColor(Color.parseColor("#333333"));
                    pop.dismiss();
                }
                if (intValue == 2) {
                    productBean = opportSelectVo.getData().getProduct().get(i);
                    productCategoryTv.setText(productBean.getCodevalue());
                    productCategoryTv.setTextColor(Color.parseColor("#333333"));
                    pop.dismiss();
                }
                if (intValue == 3) {
                    codeValueListBeanX = productBean.getCodeValueList().get(i);
                    productVarietyTv.setText(codeValueListBeanX.getCodevalue());
                    productVarietyTv.setTextColor(Color.parseColor("#333333"));
                    pop.dismiss();
                }
                if (intValue == 4) {
                    codeValueListBean = codeValueListBeanX.getCodeValueList().get(i);
                    productModelTv.setText(adapter.getAll().get(i));
                    productModelTv.setTextColor(Color.parseColor("#333333"));
                    pop.dismiss();
                }
                if (intValue == 5) {
                    opportTypeBean = opportSelectVo.getData().getOpportType().get(i);
                    orderTypeTv.setText(opportTypeBean.getCodevalue());
                    orderTypeTv.setTextColor(Color.parseColor("#333333"));
                    pop.dismiss();
                }
                if (intValue == 6) {
                    possibiltyBean = opportSelectVo.getData().getPossibilty().get(i);
                    possibilityTv.setText(possibiltyBean.getCodevalue());
                    possibilityTv.setTextColor(Color.parseColor("#333333"));
                    pop.dismiss();
                }
                if (intValue == 7) {
                    payMethodBean = opportSelectVo.getData().getPayMethod().get(i);
                    paymentMethodTv.setText(payMethodBean.getCodevalue());
                    paymentMethodTv.setTextColor(Color.parseColor("#333333"));
                    pop.dismiss();
                }
                if (intValue == 8) {
                    currencyBean = opportSelectVo.getData().getCurrency().get(i);
                    currencyTv.setText(currencyBean.getCodevalue());
                    currencyTv.setTextColor(Color.parseColor("#333333"));
                    pop.dismiss();
                }
            }
        });
    }

    /**
     * 获取客户名称列表
     */
    public void getClientData() {
        String json = new Gson().toJson(new OpprtCustReq(Config.loginback.getUserId() + ""));
        OkHttpUtils
                .postString()
                .url(Config.custList)
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
                                try {
                                    clientVo = GjsonUtil.parseJsonWithGson(response, ClientNameVo.class);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                );
    }

    /**
     * 获取
     */
    public void getContactPersonData(Long custid) {

        String json = new Gson().toJson(new CustIdReq(custid));

        OkHttpUtils
                .postString()
                .url(Config.contactListData)
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
                                try {
                                    contactNameVo = GjsonUtil.parseJsonWithGson(response, ContactNameVo.class);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                );
    }

    public void getOpportSelect() {

        OkHttpUtils
                .post()
                .url(Config.getOpportSelect)
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
                                try {
                                    opportSelectVo = GjsonUtil.parseJsonWithGson(response, OpportSelectVo.class);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                );
    }


    /**
     * 添加意向订单
     */
    public void AddOpportUnitData() {

        Long custid = 0L;
        if (item != null)
            custid = item.getCustid();    //客户id
        else if (info != null)
            custid = info.getData().getCustid();    //客户id
        else if (customer != null)
            custid = customer.getData().getCustid();    //客户id

        String opportsubject = intentionalThemeEt.getText().toString().trim();    //意向主题
        String productid = codeValueListBean.getCodetype();    //产品型号
        String plansigndate = deliveryDateTv.getText().toString().trim();    //需求交付日
        String planmoney = contractAmountEt.getText().toString().trim();    //预计合同金额
        String possibility = possibiltyBean.getCodeid();    //可能性
        String detail = detailedRequirementsEt.getText().toString().trim();  //详细需求
        String remark = remarksInformationEt.getText().toString().trim();    //备注
        int creator = Config.loginback.getUserId();    //创建者
        String currency = currencyBean.getCodeid();    //币种
        String opporttype = opportTypeBean.getCodeid();    //意向类型
        String paymentmethod = payMethodBean.getCodeid();    //付款方式
        String productcategory = productBean.getCodetype();//产品类别
        String productvariety = codeValueListBeanX.getCodetype();//品种
        int productcount = Integer.parseInt(productNumberEt.getText().toString().trim());

        String json = new Gson().toJson(new OpportUnitAddReq(custid, opportsubject, productid,
                plansigndate, planmoney, possibility, detail, remark, creator, currency, opporttype, paymentmethod,
                productcategory, productvariety, productcount));

        OkHttpUtils
                .postString()
                .url(Config.addOpportUnit)
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
                                mLoading.dismiss();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                //ToastUtil.showToast(response);
                                try {
                                    //contactNameVo = GjsonUtil.parseJsonWithGson(response, ContactNameVo.class);
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getBoolean("success")) {
                                        ToastUtil.showToast("添加成功");
                                        Config.isAddTrack = true;
                                        finish();
                                    } else
                                        ToastUtil.showToast("添加失败");
                                    mLoading.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                );
    }
}