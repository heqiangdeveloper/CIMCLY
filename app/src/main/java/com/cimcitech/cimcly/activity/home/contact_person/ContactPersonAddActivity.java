package com.cimcitech.cimcly.activity.home.contact_person;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.cimcitech.cimcly.bean.client.ClientNameVo;
import com.cimcitech.cimcly.bean.client.ClientVo;
import com.cimcitech.cimcly.bean.opport_unit.OpprtCustReq;
import com.cimcitech.cimcly.bean.person.AddPersonReq;
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

public class ContactPersonAddActivity extends BaseActivity {
    @Bind(R.id.name_et)
    EditText nameEt;
    @Bind(R.id.mobile_et)
    EditText mobileEt;
    @Bind(R.id.phone_et)
    EditText phoneEt;
    @Bind(R.id.area_tv)
    TextView areaTv;
    @Bind(R.id.address_tv)
    EditText addressTv;
    @Bind(R.id.add_bt)
    Button addBt;
    @Bind(R.id.client_tv)
    TextView clientTv;
    @Bind(R.id.client_no)
    TextView clientNo;
    @Bind(R.id.time_tv)
    TextView timeTv;
    @Bind(R.id.title_ll)
    LinearLayout title_Ll;
    @Bind(R.id.more_tv)
    TextView more_Tv;
    @Bind(R.id.titleName_tv)
    TextView titleName_Tv;

    private PopupWindow pop, popupWindow;
    private AreaVo.Province userProvince;
    private AreaVo.Province.City userCity;
    private AreaVo areaVo;
    private ListView listView2;
    private ClientVo customer;
    private ClientNameVo clientVo;
    private ClientNameVo.Data item;
    private Long custid; //如果它大于0，代表是客户详情过来的  客户详情 -- 联系人列表 -- 新增联系人

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_person_add2);
        ButterKnife.bind(this);
        initTitle();
        custid = this.getIntent().getLongExtra("custid", 0);
        timeTv.setText(DateTool.getSystemDate());
        if (custid > 0)
            inquireData(custid);
        getClientData();
    }

    public void initTitle(){
        more_Tv.setVisibility(View.GONE);
        titleName_Tv.setText("新建联系人信息");
        title_Ll.setVisibility(View.GONE);
    }

    @OnClick({R.id.back_iv, R.id.client_tv, R.id.add_bt, R.id.area_tv})
    public void onclick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.area_tv:
                if (Config.areaVo != null)
                    if (Config.areaVo.isSuccess()) {
                        List<String> list = new ArrayList<>();
                        for (int i = 0; i < Config.areaVo.getData().size(); i++) {
                            list.add(Config.areaVo.getData().get(i).getCategoryname());
                        }
                        String[] args = new String[list.size()];
                        list.toArray(args);
                        showAreaPopWin(list);
                        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                    }
                break;
            case R.id.add_bt:
                if (!checkInput())
                    return;
                mLoading.show();
                getAddData();
                break;
            case R.id.client_tv:
                List<String> list = new ArrayList<>();
                if (clientVo != null && null != clientVo.getData()) {
                    for (int i = 0; i < clientVo.getData().size(); i++) {
                        list.add(clientVo.getData().get(i).getCustname());
                    }
                }
                showPopWin(ContactPersonAddActivity.this, "选择客户", list);
                pop.showAtLocation(v, Gravity.CENTER, 0, 0);
                break;
        }
    }

    public void showAreaPopWin(List str) {
        LayoutInflater inflater = LayoutInflater.from(ContactPersonAddActivity.this);
        // 引入窗口配置文件
        View view = inflater.inflate(R.layout.dialog_area_selector_view, null);
        view.getBackground().setAlpha(100);
        // 创建PopupWindow对象
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, false);
        View pop_reward_view = view.findViewById(R.id.pop_reward_view);
        ListView listView = view.findViewById(R.id.type1_list);
        final PopupWindowAdapter adapter = new PopupWindowAdapter(ContactPersonAddActivity.this, str);
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
                PopupWindowAdapter adapter = new PopupWindowAdapter(ContactPersonAddActivity.this, list);
                listView2.setAdapter(adapter);
            }
        });
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                userCity = userProvince.getCateList().get(i);
                areaTv.setText(userProvince.getCategoryname() + " " + userCity.getCategoryname());
                popupWindow.dismiss();
            }
        });
    }

    public boolean checkInput() {
        /*if (clientTv.getText().toString().trim().equals("")) {
            ToastUtil.showToast("请选择所属客户");
            return false;
        }
        if (nameEt.getText().toString().trim().equals("")) {
            ToastUtil.showToast("请输入姓名");
            return false;
        }*/
        String phoneStr = phoneEt.getText().toString().trim();//手机
        String mobileStr = mobileEt.getText().toString().trim();//联系电话

        if (!isValidatePhone(phoneStr)) {
            ToastUtil.showToast("请输入正确手机号码");
            return false;
        }
        if (!isValidatePhone(mobileStr)) {
            ToastUtil.showToast("请输入正确联系电话");
            return false;
        }
        //if (phoneEt.getText().toString().trim().equals("") || phoneEt.getText().toString().trim
                //().length() < 11) {
        /*if (phoneEt.getText().toString().trim().equals("")){
            ToastUtil.showToast("请输入正确手机号码");
            return false;
        }*/
        /*if (!AccountValidatorUtil.isMobile(phoneEt.getText().toString().trim())) {
            ToastUtil.showToast("请输入正确手机号码");
            return false;
        }*/
        if (areaTv.getText().toString().trim().equals("")) {
            ToastUtil.showToast("请选择省、市");
            return false;
        }
        if (addressTv.getText().toString().trim().equals("")) {
            ToastUtil.showToast("请输入详细地址");
            return false;
        }
        return true;
    }

    //判断是否是正确的手机或固话格式
    public boolean isValidatePhone(String str){
        if(str.length() == 0){
            return false;
        }else if(!str.contains("-")){
            if(!(AccountValidatorUtil.isCellPhone(str) || AccountValidatorUtil.isPhone(str))){
                return false;
            }else{
                return true;
            }
        }else if(str.contains("-")){
            int num = 0;
            for(int i = 0; i < str.length(); i++){
                if(str.charAt(i) == '-'){
                    num++;
                }
            }
            if(num > 1){
                return false;
            }
            String str1 = str.substring(0,str.indexOf("-"));//区号，3位或4位
            String str2 = str.substring(str.indexOf("-")+1);
            if((str1.trim().length() == 3 || str1.trim().length() == 4) &&  str2.trim().length() > 1){
                return true;
            }else {
                return false;
            }
        }
        return true;
    }

    //获取我的客户名称列表
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
                                    if(customer!=null)
                                    if (customer.isSuccess()) {
                                        clientTv.setText(customer.getData().getCustname());
                                        clientNo.setText(customer.getData().getCustomerno());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
    }

    public void showPopWin(Context context, String title, List<String> list) {
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
                item = clientVo.getData().get(i);
                clientTv.setText(item.getCustname());
                clientNo.setText(item.getCustomerno());
                pop.dismiss();
            }
        });
    }

    public void getAddData() {

        Long getCustid = item == null ? custid : item.getCustid();

        String json = new Gson().toJson(new AddPersonReq.ContactReqBean(
                nameEt.getText().toString().trim(),
                getCustid,
                mobileEt.getText().toString().trim(),
                phoneEt.getText().toString().trim(),
                userProvince.getCategoryno(),
                userCity.getCategoryno(),
                addressTv.getText().toString().trim(),
                "", Config.loginback.getUserId()
        ));
        OkHttpUtils
                .postString()
                .url(Config.addCont)
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
                                    //clientVo = GjsonUtil.parseJsonWithGson(response, ClientNameVo.class);
                                    JSONObject json = new JSONObject(response);
                                    if (json.getBoolean("success")) {
                                        ToastUtil.showToast("添加成功");
                                        Config.isAddPerson = true;
                                        finish();
                                    } else
                                        ToastUtil.showToast("添加失败");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                mLoading.dismiss();
                            }
                        }
                );
    }
}
