package com.cimcitech.cimcly.activity.home.contact_person;

import android.content.Context;
import android.content.Intent;
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
import com.cimcitech.cimcly.bean.contact.ContactInfoVo;
import com.cimcitech.cimcly.bean.opport_unit.OpprtCustReq;
import com.cimcitech.cimcly.bean.person.UpdatePersonReq;
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

public class ContactPersonUpdateActivity extends BaseActivity {

    @Bind(R.id.back_rl)
    RelativeLayout backRl;
    @Bind(R.id.client_no)
    TextView clientNo;
    @Bind(R.id.time_tv)
    TextView timeTv;
    @Bind(R.id.client_tv)
    TextView clientTv;
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

    private AreaVo areaVo;
    private ContactInfoVo contactInfoVo;
    private PopupWindow pop, popupWindow;
    private AreaVo.Province userProvince;
    private AreaVo.Province.City userCity;
    private ListView listView2;
    private ClientNameVo clientVo;
    private ClientNameVo.Data item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_person_update);
        ButterKnife.bind(this);
        contactInfoVo = (ContactInfoVo) this.getIntent().getSerializableExtra("contactInfoVo");
        getClientData();
        initViewData();
    }

    private void initViewData() {
        if (contactInfoVo != null) {
            clientNo.setText(contactInfoVo.getData().getCustid());
            clientTv.setText(contactInfoVo.getData().getCustname());
            nameEt.setText(contactInfoVo.getData().getPersonname());
            mobileEt.setText(contactInfoVo.getData().getConttel() != null ?
                    contactInfoVo.getData().getConttel() + "" : "");
            phoneEt.setText(contactInfoVo.getData().getContmobile());
            String prov = contactInfoVo.getData().getFamilyarea();
            String city = contactInfoVo.getData().getFamilycity();
            if(prov != null && city != null){
                areaTv.setText(prov + " " + city);
            }else{
                areaTv.setText("");
            }
            addressTv.setText(contactInfoVo.getData().getFamilyaddress());
            timeTv.setText(DateTool.getDateStr(contactInfoVo.getData().getCreatedate()));
        }
    }

    @OnClick({R.id.back_rl, R.id.client_tv, R.id.add_bt, R.id.area_tv})
    public void onclick(View v) {
        switch (v.getId()) {
            case R.id.back_rl:
                finish();
                break;
            case R.id.area_tv:
                if (Config.areaVo != null)
                    if (Config.areaVo.isSuccess()) {
                        List<String> list = new ArrayList<>();
                        for (int i = 0; i < Config.areaVo.getData().size(); i++) {
                            list.add(Config.areaVo.getData().get(i).getCategoryname());//省份名称
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
                //保存
                mLoading.show();
                updateData();
                break;
            case R.id.client_tv:
                List<String> list = new ArrayList<>();
                if (clientVo != null) {
                    for (int i = 0; i < clientVo.getData().size(); i++) {
                        list.add(clientVo.getData().get(i).getCustname());
                    }
                }
                showPopWin(ContactPersonUpdateActivity.this, "选择客户", list);
                pop.showAtLocation(v, Gravity.CENTER, 0, 0);
                break;
        }
    }

    public boolean checkInput() {
        if (clientTv.getText().toString().trim().equals("")) {
            ToastUtil.showToast("请选择所属客户");
            return false;
        }
        if (nameEt.getText().toString().trim().equals("")) {
            ToastUtil.showToast("请输入姓名");
            return false;
        }
        if (mobileEt.getText().toString().trim().equals("")) {
            ToastUtil.showToast("请输入联系电话");
            return false;
        }
        if (phoneEt.getText().toString().trim().equals("") || phoneEt.getText().toString().trim().length() < 11) {
            ToastUtil.showToast("请输入正确手机号码");
            return false;
        }
        if (!AccountValidatorUtil.isMobile(phoneEt.getText().toString().trim())) {
            ToastUtil.showToast("请输入正确手机号码");
            return false;
        }
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

    public void showAreaPopWin(List str) {
        LayoutInflater inflater = LayoutInflater.from(ContactPersonUpdateActivity.this);
        // 引入窗口配置文件
        View view = inflater.inflate(R.layout.dialog_area_selector_view, null);
        view.getBackground().setAlpha(100);
        // 创建PopupWindow对象
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, false);
        View pop_reward_view = view.findViewById(R.id.pop_reward_view);
        ListView listView = view.findViewById(R.id.type1_list);
        final PopupWindowAdapter adapter = new PopupWindowAdapter(ContactPersonUpdateActivity.this, str);
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
                PopupWindowAdapter adapter = new PopupWindowAdapter(ContactPersonUpdateActivity.this, list);
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
                                    //initViewData();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                );
    }

    public void updateData() {
        if (userProvince == null && userCity == null
                && null != contactInfoVo.getData().getFamilyarea()
                && null != contactInfoVo.getData().getFamilycity()) {
            if (Config.areaVo != null)
                a:for (int i = 0; i < Config.areaVo.getData().size(); i++) {
                    if (contactInfoVo.getData().getFamilyarea().equals(Config.areaVo.getData().get(i).getCategoryname())) {
                        userProvince = Config.areaVo.getData().get(i);
                        for (int j = 0; j < userProvince.getCateList().size(); j++)
                            if (contactInfoVo.getData().getFamilycity().equals(userProvince.getCateList().get(j).getCategoryname())) {
                                userCity = userProvince.getCateList().get(j);
                                break a;
                            }
                    }
                }
        }
        String familyarea = "";
        String familycity = "";
        if(null == userProvince || null == userCity){
            if(mLoading.isShowing()) mLoading.dismiss();
            Toast.makeText(ContactPersonUpdateActivity.this,"请填写省市信息！",Toast.LENGTH_SHORT).show();
            return;
        }else{
            familyarea = userProvince.getCategoryno();//防止FC
            familycity = userCity.getCategoryno();
        }
        Long getCustid = item == null ? Long.parseLong(contactInfoVo.getData().getCustid())
                : item.getCustid();
        int contpersonid = contactInfoVo.getData().getContpersonid();
        String personname = nameEt.getText().toString().trim();
        String conttel = mobileEt.getText().toString().trim();
        String contmobile = phoneEt.getText().toString().trim();

        String familyaddress = addressTv.getText().toString().trim();
        int modify = Config.loginback.getUserId();

        String json = new Gson().toJson(new UpdatePersonReq(contpersonid, getCustid, personname,
                conttel, contmobile, familyarea, familycity, familyaddress, modify));
        OkHttpUtils
                .postString()
                .url(Config.modifyCont)
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
                                        ToastUtil.showToast("保存成功");
                                        Config.isAddPerson = true;
                                        Intent intent = new Intent();
                                        Bundle b = new Bundle();
                                        b.putString("contPersonId", contactInfoVo.getData().getContpersonid() + "");
                                        intent.putExtras(b);
                                        ContactPersonUpdateActivity.this.setResult(RESULT_OK, intent);
                                        finish();
                                    } else
                                        ToastUtil.showToast("保存失败");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                mLoading.dismiss();
                            }
                        }
                );
    }
}
