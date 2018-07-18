package com.cimcitech.cimcly.activity.home.my_client;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.adapter.PopupWindowAdapter;
import com.cimcitech.cimcly.bean.AreaVo;
import com.cimcitech.cimcly.bean.CustSelectVo;
import com.cimcitech.cimcly.bean.client.AddMyClientReq;
import com.cimcitech.cimcly.bean.client.Contact;
import com.cimcitech.cimcly.utils.AccountValidatorUtil;
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

public class MyClientAddActivity extends BaseActivity {
    @Bind(R.id.add_bt)
    Button addBt;
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
    @Bind(R.id.user_name_et)
    EditText userNameEt;
    @Bind(R.id.user_mobile_et)
    EditText userMobileEt;
    @Bind(R.id.user_phone_et)
    EditText userPhoneEt;
    @Bind(R.id.user_provinces_tv)
    TextView userProvincesTv;
    @Bind(R.id.user_address_et)
    EditText userAddressEt;
    @Bind(R.id.beizhu_et)
    EditText beizhuEt;

    @Bind(R.id.title_ll)
    LinearLayout title_Ll;
    @Bind(R.id.more_tv)
    TextView more_Tv;
    @Bind(R.id.titleName_tv)
    TextView titleName_Tv;

    //modidied by qianghe on 2017/10/11 begin
    @Bind(R.id.province_Linear)
    LinearLayout provinceLinear;
    @Bind(R.id.city_Linear)
    LinearLayout cityLinear;
    //modidied by qianghe on 2017/10/11 end

    private CustSelectVo custSelectVo;
    private PopupWindow pop, popupWindow;
    public int intValue = 0;
    private CustSelectVo.Data.CustType custType;  //类型
    private CustSelectVo.Data.Region region;//国家
    private CustSelectVo.Data.Region.CateList cateList; //省份
    private CustSelectVo.Data.Region.CateList.City city; //城市
    private CustSelectVo.Data.Web web; //税种

    private AreaVo.Province userProvince;
    private AreaVo.Province.City userCity;
    private AreaVo areaVo;
    private ListView listView2;

    private boolean isCustExist; //判断客户名称是否存在

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_my_client2);
        ButterKnife.bind(this);
        initTitle();
        systemDateTv.setText(DateTool.getSystemDate());
        getData();

        clientNameTv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b)
                    isCustExist(); //判断客户名称是否存在
            }
        });

        //modidied by qianghe on 2017/10/11 begin
        provinceLinear.setVisibility(View.VISIBLE);
        cityLinear.setVisibility(View.VISIBLE);
        //modidied by qianghe on 2017/10/11 end
    }

    public void initTitle(){
        more_Tv.setVisibility(View.GONE);
        titleName_Tv.setText("新建客户信息");
        title_Ll.setVisibility(View.GONE);
    }

    @OnClick({R.id.customer_category_tv, R.id.country_tv, R.id.province_tv, R.id.city_tv,
            R.id.taxes_tv, R.id.add_bt, R.id.back_iv, R.id.user_provinces_tv})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.customer_category_tv: //获取客户类别
                intValue = 0;
                if (custSelectVo.isSuccess()) {
                    List<String> list = new ArrayList<>();
                    for (int i = 0; i < custSelectVo.getData().getCustType().size(); i++) {
                        list.add(custSelectVo.getData().getCustType().get(i).getCodevalue());
                    }
                    showContactUsPopWin(MyClientAddActivity.this, "选择客户类别", list);
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
                    showContactUsPopWin(MyClientAddActivity.this, "选择国家", list);
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
                    showContactUsPopWin(MyClientAddActivity.this, "选择省份", list);
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
                        showContactUsPopWin(MyClientAddActivity.this, "选择城市", list);
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
                    showContactUsPopWin(MyClientAddActivity.this, "选择客户分区", list);
                    pop.showAtLocation(view, Gravity.CENTER, 0, 0);
                }
                break;
            case R.id.add_bt:
                if (!checkInput())
                    return;
                mLoading.show();
                addData();
                break;
            case R.id.back_iv:
                finish();
                break;
            case R.id.user_provinces_tv: //联系人信息 省、市
                //if (areaVo != null)
                  //  if (areaVo.isSuccess()) {
                if (Config.areaVo != null)
                    if (Config.areaVo.isSuccess()) {
                        List<String> list = new ArrayList<>();
                        for (int i = 0; i < Config.areaVo.getData().size(); i++) {
                            list.add(Config.areaVo.getData().get(i).getCategoryname());
                        }
                        String[] args = new String[list.size()];
                        list.toArray(args);
                        showAreaPopWin(list);
                        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                    }
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
                    pop.dismiss();
                }
                if (intValue == 3) {//城市
                    city = cateList.getCateList().get(i);
                    cityTv.setText(adapter.getAll().get(i));
                    cityTv.setTextColor(Color.parseColor("#666666"));
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
        if (custType == null) {
            ToastUtil.showToast("请选择客户类别");
            return false;
        }
        if (clientNameTv.getText().toString().trim().equals("")) {
            ToastUtil.showToast("请输入客户名称");
            return false;
        }
        //if (mobileEt.getText().toString().trim().equals("") || mobileEt.getText().toString()
                //.trim().length() < 11) {
        if (mobileEt.getText().toString().trim().equals("")) {
            ToastUtil.showToast("请输入正确电话号码");
            return false;
        }
        /*if (!AccountValidatorUtil.isMobile(mobileEt.getText().toString().trim())) {
            ToastUtil.showToast("请输入正确电话号码");
            return false;
        }*/
        if (region == null) {
            ToastUtil.showToast("请选择国家");
            return false;
        }
        if (countryTv.getText().equals("中国") && cateList == null) {
            ToastUtil.showToast("请选择省份");
            return false;
        }
        if (countryTv.getText().equals("中国") && city == null) {
            ToastUtil.showToast("请选择城市");
            return false;
        }
        if (addressEt.getText().toString().trim().equals("")) {
            ToastUtil.showToast("请输入详细地址");
            return false;
        }
        /*if (web == null) {
            ToastUtil.showToast("请选择税种");
            return false;
        }*/
        /*if (mobileEt.getText().toString().trim().length() != 11) {
            ToastUtil.showToast("请输入正确的手机号码");
            return false;
        }*/
        if (userNameEt.getText().toString().trim().equals("")) {
            ToastUtil.showToast("请输入联系人姓名");
            return false;
        }
        if (userPhoneEt.getText().toString().trim().equals("")) {
            ToastUtil.showToast("请输入联系人手机号码");
            return false;
        }
        /*if (userPhoneEt.getText().toString().trim().length() != 11) {
            ToastUtil.showToast("请输入正确的联系人手机号码");
            return false;
        }*/
        if (!zipCodeEt.getText().toString().trim().equals(""))
            if (!AccountValidatorUtil.isPostCode(zipCodeEt.getText().toString().trim())) {
                ToastUtil.showToast("请输入正确的邮政编码");
                return false;
            }
        if (isCustExist) {
            ToastUtil.showToast("客户名称已经存在");
            return false;
        }
        return true;
    }


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
                                ToastUtil.showNetError();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                try {
                                    custSelectVo = GjsonUtil.parseJsonWithGson(response, CustSelectVo.class);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
    }

    public void addData() {
        String userProvinceStr = "";
        String userCityStr = "";

        if (userProvince != null)
            userProvinceStr = userProvince.getCategoryno();
        if (userCity != null) {
            userCityStr = userCity.getCategoryno();
        }
        //客户分区默认为CNY
        if(web == null){
            web = custSelectVo.getData().getWeb().get(1);
        }

        String json = "";
        //中国
        if(region.getCategoryname().equals("中国")){
            String json_China = new Gson().toJson(new AddMyClientReq(
                    clientNameTv.getText().toString().trim(),
                    addressEt.getText().toString().trim(),
                    mobileEt.getText().toString().trim(),
                    custType.getCodeid(),
                    zipCodeEt.getText().toString().trim(),
                    web.getCodeid(),
                    Config.loginback.getUserId(),
                    Config.loginback.getUserId(),
                    abbreviationEt.getText().toString().trim(),
                    faxEt.getText().toString().trim(),
                    region.getCategoryno(),
                    cateList.getCategoryno(),
                    city.getCategoryno(),
                    invoiceTv.getText().toString().trim(),
                    beizhuEt.getText().toString().trim(),
                    new Contact(
                            userNameEt.getText().toString().trim(),
                            userMobileEt.getText().toString().trim(),
                            userPhoneEt.getText().toString().trim(),
                            userProvinceStr, userCityStr, userAddressEt.getText().toString().trim(),
                            beizhuEt.getText().toString().trim())));
            json = json_China;
        }else {
            //外国地区,隐藏省，市
            String json_Overseas = new Gson().toJson(new AddMyClientReq(
                    clientNameTv.getText().toString().trim(),
                    addressEt.getText().toString().trim(),
                    mobileEt.getText().toString().trim(),
                    custType.getCodeid(),
                    zipCodeEt.getText().toString().trim(),
                    web.getCodeid(),
                    Config.loginback.getUserId(),
                    Config.loginback.getUserId(),
                    abbreviationEt.getText().toString().trim(),
                    faxEt.getText().toString().trim(),
                    region.getCategoryno(),
                    null,
                    null,
                    invoiceTv.getText().toString().trim(),
                    beizhuEt.getText().toString().trim(),
                    new Contact(
                            userNameEt.getText().toString().trim(),
                            userMobileEt.getText().toString().trim(),
                            userPhoneEt.getText().toString().trim(),
                            userProvinceStr, userCityStr, userAddressEt.getText().toString().trim(),
                            beizhuEt.getText().toString().trim())));
            json = json_Overseas;
        }

        OkHttpUtils
                .postString()
                .url(Config.addCust)
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
                                    JSONObject json = new JSONObject(response);
                                    if (json.getBoolean("success")) {
                                        ToastUtil.showToast("添加成功");
                                        Config.isAddMyClient = true;
                                        finish();
                                    }else
                                        ToastUtil.showToast("添加失败");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                mLoading.dismiss();
                            }
                        }
                );
    }

    /**
     * 判断客户名称是否存在
     */
    public void isCustExist() {

        String clientName = clientNameTv.getText().toString().trim();

        OkHttpUtils
                .post()
                .url(Config.isCustExist)
                .addHeader("checkTokenKey", Config.loginback.getToken())
                .addHeader("sessionKey", Config.loginback.getUserId() + "")
                .addParams("custName", clientName)
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
                                    //{"data":true,"msg":"","success":true}
                                    JSONObject json = new JSONObject(response);
                                    if (json.getBoolean("success"))
                                        //true 有相同  false 没有相同
                                        if (json.getBoolean("data")) {
                                            isCustExist = true;//表示存在
                                        } else {
                                            isCustExist = false; //表示不存在
                                        }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
    }

    public void showAreaPopWin(List str) {
        LayoutInflater inflater = LayoutInflater.from(MyClientAddActivity.this);
        // 引入窗口配置文件
        View view = inflater.inflate(R.layout.dialog_area_selector_view, null);
        view.getBackground().setAlpha(100);
        // 创建PopupWindow对象
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, false);
        View pop_reward_view = view.findViewById(R.id.pop_reward_view);
        ListView listView = view.findViewById(R.id.type1_list);
        final PopupWindowAdapter adapter = new PopupWindowAdapter(MyClientAddActivity.this, str);
        listView.setAdapter(adapter);
        listView2 = view.findViewById(R.id.type2_list);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        // 需要设置一下此参数，点击外边可消失
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置点击窗口外边窗口消失
        popupWindow.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        popupWindow.setFocusable(true);
        pop_reward_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                userProvince = Config.areaVo.getData().get(i);
                List<String> list = new ArrayList<>();
                for (int j = 0; j < userProvince.getCateList().size(); j++) {
                    list.add(userProvince.getCateList().get(j).getCategoryname());
                }
                PopupWindowAdapter adapter = new PopupWindowAdapter(MyClientAddActivity.this, list);
                listView2.setAdapter(adapter);
            }
        });
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                userCity = userProvince.getCateList().get(i);
                userProvincesTv.setText(userProvince.getCategoryname() + " " + userCity.getCategoryname());
                userProvincesTv.setTextColor(Color.parseColor("#666666"));
                popupWindow.dismiss();
            }
        });
    }
}
