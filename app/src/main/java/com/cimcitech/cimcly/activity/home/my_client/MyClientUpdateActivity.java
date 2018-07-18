package com.cimcitech.cimcly.activity.home.my_client;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.activity.home.customer_visit.CustomerVisitAddActivity;
import com.cimcitech.cimcly.activity.home.contact_person.ContactPersonActivity;
import com.cimcitech.cimcly.activity.home.intention_track.IntentionTrackAddActivity;
import com.cimcitech.cimcly.adapter.PopupWindowAdapter;
import com.cimcitech.cimcly.bean.CustSelectVo;
import com.cimcitech.cimcly.bean.client.AddMyClientReq;
import com.cimcitech.cimcly.bean.client.ClientVo;
import com.cimcitech.cimcly.utils.AccountValidatorUtil;
import com.cimcitech.cimcly.utils.Config;
import com.cimcitech.cimcly.utils.CustSelectUtils;
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

public class MyClientUpdateActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.back_rl)
    RelativeLayout backRl;
    @Bind(R.id.contact_person_tv)
    TextView contactPersonTv;
    @Bind(R.id.visit_tv)
    TextView visitTv;
    @Bind(R.id.intention_tv)
    TextView intentionTv;
    @Bind(R.id.save_tv)
    TextView saveTv;
    @Bind(R.id.update_ll)
    LinearLayout updateLl;
    @Bind(R.id.system_date_tv)
    TextView systemDateTv;
    @Bind(R.id.customer_category_tv)
    TextView customerCategoryTv;
    @Bind(R.id.client_name_tv)
    EditText clientNameTv;
    @Bind(R.id.abbreviation_et)
    EditText abbreviationEt;
    @Bind(R.id.mobile_et)
    EditText mobileEt;
    @Bind(R.id.fax_et)
    EditText faxEt;
    @Bind(R.id.country_tv)
    TextView countryTv;
    @Bind(R.id.province_tv)
    TextView provinceTv;
    @Bind(R.id.city_tv)
    TextView cityTv;
    @Bind(R.id.address_et)
    EditText addressEt;
    @Bind(R.id.zip_code_et)
    EditText zipCodeEt;
    @Bind(R.id.taxes_tv)
    TextView taxesTv;
    @Bind(R.id.invoice_tv)
    EditText invoiceTv;
    @Bind(R.id.beizhu_et)
    EditText beizhuEt;

    //modidied by qianghe on 2017/10/11 begin
    @Bind(R.id.province_Linear)
    LinearLayout provinceLinear;
    @Bind(R.id.city_Linear)
    LinearLayout cityLinear;
    //modidied by qianghe on 2017/10/11 end

    private Long custid;
    private ClientVo customer;
    private CustSelectVo custSelectVo;
    private PopupWindow pop;
    public int intValue = 0;
    private boolean isUpdate = false; //是否修改了省份，如果修改要一同修改城市

    private CustSelectVo.Data.CustType custType;  //类型
    private CustSelectVo.Data.Region region;//国家
    private CustSelectVo.Data.Region.CateList cateList; //省份
    private CustSelectVo.Data.Region.CateList.City city; //城市
    private CustSelectVo.Data.Web web; //税种

    private ProgressDialog dialog = null;
    private  static final int SAVEDATA = 1001;
    private  static final int SAVEDATA_HIDE = 1002;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SAVEDATA:
                    dialog = new ProgressDialog(MyClientUpdateActivity.this);
                    dialog.setMessage("数据保存中…");
                    dialog.setCancelable(false);
                    dialog.show();
                    break;
                case SAVEDATA_HIDE:
                    if(dialog.isShowing())
                        dialog.dismiss();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_client_update);
        ButterKnife.bind(this);
        systemDateTv.setText(DateTool.getSystemDate());
        custid = this.getIntent().getLongExtra("custid", 0);
        mLoading.show();
        getData();
    }

    @OnClick({R.id.save_tv, R.id.contact_person_tv, R.id.visit_tv, R.id.intention_tv,
            R.id.customer_category_tv, R.id.country_tv, R.id.province_tv, R.id.city_tv,
            R.id.taxes_tv, R.id.back_rl})
    public void onclick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.save_tv:
                if (!checkInput())
                    return;
                if (customer != null) {
                    //mLoading.show();
                    sendMsg(SAVEDATA);
                    updateData(customer);
                }
                break;
            case R.id.customer_category_tv: //获取客户类别
                intValue = 0;
                if (custSelectVo.isSuccess()) {
                    List<String> list = new ArrayList<>();
                    for (int i = 0; i < custSelectVo.getData().getCustType().size(); i++) {
                        list.add(custSelectVo.getData().getCustType().get(i).getCodevalue());
                    }
                    showContactUsPopWin(MyClientUpdateActivity.this, "选择客户类别", list);
                    pop.showAtLocation(view, Gravity.CENTER, 0, 0);
                }
                break;
            case R.id.country_tv: //国家
                intValue = 1;
                if (custSelectVo.isSuccess()) {
                    List<String> list = new ArrayList<>();
                    for (int i = 0; i < custSelectVo.getData().getRegion().size(); i++) {
                        list.add(custSelectVo.getData().getRegion().get(i).getCategoryname());
                    }
                    showContactUsPopWin(MyClientUpdateActivity.this, "选择国家", list);
                    pop.showAtLocation(view, Gravity.CENTER, 0, 0);
                }
                break;
            case R.id.province_tv:
                intValue = 2;//省份
                if (custSelectVo.isSuccess()) {
                    List<String> list = new ArrayList<>();
                    for (int i = 0; i < custSelectVo.getData().getRegion().get(0).getCateList().size(); i++) {
                        list.add(custSelectVo.getData().getRegion().get(0).getCateList().get(i).getCategoryname());
                    }
                    showContactUsPopWin(MyClientUpdateActivity.this, "选择省份", list);
                    pop.showAtLocation(view, Gravity.CENTER, 0, 0);
                }
                break;
            case R.id.city_tv:
                intValue = 3;//城市
                if (custSelectVo.isSuccess()) {
                    if (cateList != null) {
                        List<String> list = new ArrayList<>();
                        for (int i = 0; i < cateList.getCateList().size(); i++) {
                            list.add(cateList.getCateList().get(i).getCategoryname());
                        }
                        showContactUsPopWin(MyClientUpdateActivity.this, "选择城市", list);
                        pop.showAtLocation(view, Gravity.CENTER, 0, 0);
                    } else {
                        ToastUtil.showToast("请先选择省份");
                    }
                }
                break;
            case R.id.taxes_tv: //税种
                intValue = 4;
                if (custSelectVo.isSuccess()) {
                    List<String> list = new ArrayList<>();
                    for (int i = 0; i < custSelectVo.getData().getWeb().size(); i++) {
                        list.add(custSelectVo.getData().getWeb().get(i).getCodevalue());
                    }
                    showContactUsPopWin(MyClientUpdateActivity.this, "选择客户分区", list);
                    pop.showAtLocation(view, Gravity.CENTER, 0, 0);
                }
                break;
            case R.id.back_rl:
                finish();
                break;
            case R.id.contact_person_tv:
                intent = new Intent(MyClientUpdateActivity.this, ContactPersonActivity.class);
                intent.putExtra("custid", custid);
                startActivity(intent);
                break;
            case R.id.visit_tv:
                intent = new Intent(MyClientUpdateActivity.this, CustomerVisitAddActivity.class);
                intent.putExtra("customer", customer);
                startActivity(intent);
                break;
            case R.id.intention_tv:
                intent = new Intent(MyClientUpdateActivity.this, IntentionTrackAddActivity.class);
                intent.putExtra("customer", customer);
                startActivity(intent);
                break;
        }
    }

    public void sendMsg(int flag){
        Message msg = new Message();
        msg.what = flag;
        handler.sendMessage(msg);
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
                if (intValue == 0) {//类型
                    custType = custSelectVo.getData().getCustType().get(i);
                    customerCategoryTv.setText(adapter.getAll().get(i));
                    customerCategoryTv.setTextColor(Color.parseColor("#666666"));
                    pop.dismiss();
                }
                if (intValue == 1) {//国家
                    region = custSelectVo.getData().getRegion().get(i);
                    countryTv.setText(adapter.getAll().get(i));
                    countryTv.setTextColor(Color.parseColor("#666666"));
                    pop.dismiss();
                    //modidied by qianghe on 2017/10/11 begin
                    if(countryTv.getText().equals("中国")){
                        provinceLinear.setVisibility(View.VISIBLE);
                        cityLinear.setVisibility(View.VISIBLE);
                    }else{
                        provinceLinear.setVisibility(View.GONE);
                        cityLinear.setVisibility(View.GONE);
                    }
                    //modidied by qianghe on 2017/10/11 end
                }
                if (intValue == 2) {//省份
                    provinceTv.setText(adapter.getAll().get(i));
                    provinceTv.setTextColor(Color.parseColor("#666666"));
                    cateList = custSelectVo.getData().getRegion().get(0).getCateList().get(i);
                    //如果用户修改了省份，那么同样要修改城市
                    isUpdate = true;
                    pop.dismiss();
                }
                if (intValue == 3) {//城市
                    city = cateList.getCateList().get(i);
                    cityTv.setText(adapter.getAll().get(i));
                    cityTv.setTextColor(Color.parseColor("#666666"));
                    //如果用户修改了省份，那么同样要修改城市
                    isUpdate = false;
                    pop.dismiss();
                }
                if (intValue == 4) {
                    web = custSelectVo.getData().getWeb().get(i);
                    taxesTv.setText(adapter.getAll().get(i));
                    taxesTv.setTextColor(Color.parseColor("#666666"));
                    pop.dismiss();
                }
            }
        });
    }

    public boolean checkInput() {
        if (customer != null) {
            if (customer.getData().getCusttype() == null || customer.getData().getCusttype().equals("")) {
                if (custType == null) {
                    ToastUtil.showToast("请选择客户类型");
                    return false;
                }
            }
            if (customer.getData().getCountry() == null || customer.getData().getCountry().equals("")) {
                if (region == null) {
                    ToastUtil.showToast("请选择国家");
                    return false;
                }
            }
            if (customer.getData().getProvince() == null || customer.getData().getProvince().equals("")) {
                if (countryTv.getText().equals("中国") && cateList == null) {
                    ToastUtil.showToast("请选择省份");
                    return false;
                }
            }

            if (isUpdate) {
                ToastUtil.showToast("请选择城市");
                return false;
            } else if (customer.getData().getCity() == null || customer.getData().getCity().equals("")) {
                if (countryTv.getText().equals("中国") && city == null) {
                    ToastUtil.showToast("请选择城市");
                    return false;
                }
            }

            if (customer.getData().getWeb() == null || customer.getData().getWeb().equals("")) {
                if (web == null) {
                    ToastUtil.showToast("请选择税种");
                    return false;
                }
            }
        }
        if (mobileEt.getText().toString().trim().equals("") || mobileEt.getText().toString().trim().length() < 11) {
            ToastUtil.showToast("请输入正确电话号码");
            return false;
        }
        if (!AccountValidatorUtil.isMobile(mobileEt.getText().toString().trim())) {
            ToastUtil.showToast("请输入正确电话号码");
            return false;
        }
        if (clientNameTv.getText().toString().trim().equals("")) {
            ToastUtil.showToast("请请输入客户名称");
            return false;
        }
        if (addressEt.getText().toString().trim().equals("")) {
            ToastUtil.showToast("请请输入详细地址");
            return false;
        }
        return true;
    }

    public void setEditTextVaule(String string, EditText textView) {
        if (string != null && !string.equals("")) {
            textView.setText(string);
            textView.setTextColor(Color.parseColor("#666666"));
        } else {
            textView.setText("");
        }
    }

    public void setTextViewVaule(String string, TextView textView) {
        if (string != null && !string.equals("")) {
            textView.setText(string);
            textView.setTextColor(Color.parseColor("#666666"));
        } else {
            textView.setText("");
        }
    }


    public void updateData(ClientVo customer) {

        String custtypeStr = ""; //客户类型
        String countryStr = "";//国家
        String provinceStr = "";//省份
        String cityStr = "";//城市
        String webStr = "";//税种

        if (customer != null) {
            if (custType == null)
                custtypeStr = customer.getData().getCusttype();
            else
                custtypeStr = custType.getCodeid();
            if (region == null)
                countryStr = customer.getData().getCountry();
            else
                countryStr = region.getCategoryno();
            if (cateList == null)
                provinceStr = customer.getData().getProvince();
            else
                provinceStr = cateList.getCategoryno();
            if (city == null)
                cityStr = customer.getData().getCity();
            else
                cityStr = city.getCategoryno();
            if (web == null)
                webStr = customer.getData().getWeb();
            else
                webStr = web.getCodeid();
        }
        String json = "";
        if(countryTv.getText().equals("中国")){
            String json_China = new Gson().toJson(new AddMyClientReq(
                    customer.getData().getCustid(),
                    clientNameTv.getText().toString().trim(),
                    addressEt.getText().toString().trim(),
                    mobileEt.getText().toString().trim(),
                    custtypeStr,
                    zipCodeEt.getText().toString().trim(),
                    webStr,
                    Config.loginback.getUserId(),
                    Config.loginback.getUserId(),
                    abbreviationEt.getText().toString().trim(),
                    faxEt.getText().toString().trim(),
                    countryStr,
                    provinceStr,
                    cityStr,
                    invoiceTv.getText().toString().trim(),
                    beizhuEt.getText().toString().trim()));
            json = json_China;
        }else {
            String json_Overseas = new Gson().toJson(new AddMyClientReq(
                    customer.getData().getCustid(),
                    clientNameTv.getText().toString().trim(),
                    addressEt.getText().toString().trim(),
                    mobileEt.getText().toString().trim(),
                    custtypeStr,
                    zipCodeEt.getText().toString().trim(),
                    webStr,
                    Config.loginback.getUserId(),
                    Config.loginback.getUserId(),
                    abbreviationEt.getText().toString().trim(),
                    faxEt.getText().toString().trim(),
                    countryStr,
                    null,
                    null,
                    invoiceTv.getText().toString().trim(),
                    beizhuEt.getText().toString().trim()));
            json = json_Overseas;
        }

        OkHttpUtils
                .postString()
                .url(Config.modifyCust)
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
                                //mLoading.dismiss();
                                sendMsg(SAVEDATA_HIDE);
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.d("testlog","response is: " + response);
                                sendMsg(SAVEDATA_HIDE);
                                try {
                                    JSONObject json = new JSONObject(response);
                                    if (json.getBoolean("success")) {
                                        ToastUtil.showToast("保存成功");
                                        finish();
                                    } else {
                                        ToastUtil.showToast("保存失败");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                //mLoading.dismiss();
                            }
                        }
                );
    }

    //查客户详细信息
    public void inquireData(Long custid) {
        OkHttpUtils
                .post()
                .url(Config.getCurrInfo)
                .addHeader("checkTokenKey", Config.loginback.getToken())
                .addHeader("sessionKey", Config.loginback.getUserId() + "")
                .addParams("custId", custid + "")
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
                                    customer = GjsonUtil.parseJsonWithGson(response, ClientVo.class);
                                    initViewData();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
    }

    public void initViewData() {

        setTextViewVaule(CustSelectUtils.getCustTypeName(custSelectVo, customer.getData().getCusttype()), customerCategoryTv);
        setTextViewVaule(customer.getData().getCustname(), clientNameTv);
        setTextViewVaule(customer.getData().getShortname(), abbreviationEt);
        setTextViewVaule(customer.getData().getCusttel(), mobileEt);
        setEditTextVaule(customer.getData().getFax(), faxEt);
        String countryNameStr = CustSelectUtils.getCountryName(custSelectVo, customer.getData()
                .getCountry());
        setTextViewVaule(countryNameStr, countryTv);
        if(countryNameStr.equals("中国")){
            provinceTv.setVisibility(View.VISIBLE);
            cityTv.setVisibility(View.VISIBLE);
            setTextViewVaule(CustSelectUtils.getProvinceName(custSelectVo, customer.getData().getCountry(),
                    customer.getData().getProvince()),
                    provinceTv);
            setTextViewVaule(CustSelectUtils.getCityName(custSelectVo, customer.getData().getCountry(),
                    customer.getData().getProvince(), customer.getData().getCity()), cityTv);
        }else{
            provinceTv.setVisibility(View.GONE);
            cityTv.setVisibility(View.GONE);
        }

        setEditTextVaule(customer.getData().getCustaddress(), addressEt);
        setEditTextVaule(customer.getData().getZipcode(), zipCodeEt);
        setTextViewVaule(CustSelectUtils.getWebName(custSelectVo, customer.getData().getWeb()), taxesTv);
        setEditTextVaule(customer.getData().getMakeup(), invoiceTv);
        //备注
        setEditTextVaule(customer.getData().getSummary(), beizhuEt);

        mLoading.dismiss();
    }

    // 查新增客户的缺省值 //查完后查客户的信息
    public void getData() {
        OkHttpUtils
                .post()
                .url(Config.getCustSelect)
                .addParams("userId", Config.loginback.getUserId() + "")
                .addHeader("checkTokenKey", Config.loginback.getToken())
                .addHeader("sessionKey", Config.loginback.getUserId() + "")
                .build()
                .execute(
                        new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                if(mLoading.isShowing()) mLoading.dismiss();
                                ToastUtil.showNetError();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                if(mLoading.isShowing()) mLoading.dismiss();
                                try {
                                    custSelectVo = GjsonUtil.parseJsonWithGson(response, CustSelectVo.class);
                                    inquireData(custid);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
    }
}
