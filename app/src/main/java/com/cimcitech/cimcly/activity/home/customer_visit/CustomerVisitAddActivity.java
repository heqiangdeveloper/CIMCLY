package com.cimcitech.cimcly.activity.home.customer_visit;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.cimcitech.cimcly.ApkApplication;
import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.adapter.PopupWindowAdapter;
import com.cimcitech.cimcly.baidu.LocationService;
import com.cimcitech.cimcly.bean.client.ClientNameVo;
import com.cimcitech.cimcly.bean.client.ClientVo;
import com.cimcitech.cimcly.bean.contact.AddContactReq;
import com.cimcitech.cimcly.bean.contact.ContactInfoVo;
import com.cimcitech.cimcly.bean.contact.ContactNameVo;
import com.cimcitech.cimcly.bean.contact.CustIdReq;
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

public class CustomerVisitAddActivity extends BaseActivity {

    @Bind(R.id.client_no_tv)
    TextView clientNoTv;
    @Bind(R.id.create_time_tv)
    TextView createTimeTv;
    @Bind(R.id.client_name_tv)
    TextView clientNameTv;
    @Bind(R.id.client_manager_tv)
    TextView clientManagerTv;
    @Bind(R.id.contact_person_tv)
    TextView contactPersonTv;
    @Bind(R.id.mobile_tv)
    TextView mobileTv;
    @Bind(R.id.visit_time_tv)
    TextView visitTimeTv;
    @Bind(R.id.visit_time_2_tv)
    TextView visitTime2Tv;
    @Bind(R.id.address_tv)
    TextView addressTv;
    @Bind(R.id.location_tv)
    TextView locationTv;
    @Bind(R.id.visit_summary_tv)
    EditText visitSummaryTv;
    @Bind(R.id.titleName_tv)
    TextView titleName_Tv;

    @Bind(R.id.more_tv)
    TextView more_Tv;
    @Bind(R.id.title_ll)
    LinearLayout title_Ll;
    @Bind(R.id.status_ll)
    LinearLayout status_Ll;
    @Bind(R.id.who_spinner)
    Spinner whoSpinner;

    private LocationService locationService;
    private ArrayList<Poi> pois = new ArrayList<>(); //获取到的定位位置的对象
    public int intValue = 0;
    private PopupWindow pop;
    private int mYear, mMonth, mDay;
    private final int DATE_DIALOG = 1;
    private int visitDate = 0;

    private ContactInfoVo contactInfoVo; //联系人详情传过来的

    private ClientNameVo clientVo; //客户
    private ClientNameVo.Data item; //客户item

    private ContactNameVo contactNameVo; //联系人
    private ContactNameVo.Data contactItem; //联系人item

    private double latitude, longitude;

    private ClientVo customer;//我的客户过来的

    private ProgressDialog dialog = null;
    private static final int STARTTOLOCATING = 1;
    private static final int ENDTOLOCATING = 2;
    private static final int LOCATESUCCESS = 3;
    private static final int LOCATEFAIL = 4;

    private StringBuffer locSb ;
    private static final int requestLocTime = 7000;
    private boolean isFinishlocating = false;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case STARTTOLOCATING:
                    isFinishlocating = false;
                    dialog = new ProgressDialog(CustomerVisitAddActivity.this);
                    dialog.setMessage("定位中…");
                    dialog.setCancelable(true);
                    dialog.show();
                    break;
                case LOCATESUCCESS:
                    isFinishlocating = true;
                    if(dialog.isShowing())
                        dialog.dismiss();
                    if(locSb != null && locSb.toString().length() != 0 && !locSb.toString().contains("null")){
                        locationTv.setText(locSb.toString());
                    }
                    break;
                case LOCATEFAIL:
                    isFinishlocating = true;
                    locationTv.setText("");
                    if(dialog.isShowing())
                        dialog.dismiss();
                    Toast.makeText(CustomerVisitAddActivity.this,"定位失败！请检查网络或GPS",Toast
                            .LENGTH_SHORT).show();
                    break;
            }
            //Toast.makeText(WorkWeeklyAddActivity.this,sb.toString(),Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer_visit2);
        ButterKnife.bind(this);
        initTitle();

        createTimeTv.setText(DateTool.getSystemDate());
        visitTimeTv.setText(DateTool.getSystemDate());
        visitTime2Tv.setText(DateTool.getSystemDate());
        final Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
        customer = (ClientVo) this.getIntent().getSerializableExtra("customer");
        contactInfoVo = (ContactInfoVo) this.getIntent().getSerializableExtra("contactInfoVo");
        getClientData(); //请求数据
        setViewDate();

        locationService = ((ApkApplication) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);

        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(CustomerVisitAddActivity.this, Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(CustomerVisitAddActivity.this,Manifest.permission
                .READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(CustomerVisitAddActivity.this,Manifest.permission
                .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()){
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(CustomerVisitAddActivity.this,permissions,1);
        }else {
            getLocation();
        }
    }

    public void initTitle(){
        more_Tv.setVisibility(View.GONE);
        title_Ll.setVisibility(View.GONE);
        whoSpinner.setVisibility(View.GONE);
        titleName_Tv.setText("新建拜访记录");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults.length > 0){
                    for(int result : grantResults){
                        if(result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(CustomerVisitAddActivity.this,"必须同意所有的权限才能使用定位",Toast.LENGTH_SHORT
                            ).show();
                            finish();
                            return;
                        }
                    }
                    getLocation();
                }else {
                    Toast.makeText(CustomerVisitAddActivity.this,"发送未知错误",Toast.LENGTH_SHORT
                    ).show();
                }
                break;
        }
    }

    public void getLocation(){
        //added by heqiang
        //locationService = ((ApkApplication) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        //locationService.registerListener(mListener);
        //注册监听
        int type = getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }
        locationService.start();// 定位SDK
        isFinishlocating = false;
        sendMsg(STARTTOLOCATING);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(requestLocTime);
                    if(!isFinishlocating)
                        sendMsg(LOCATEFAIL);
                }catch (Exception e){

                }
            }
        }).start();

    }

    public void sendMsg(int flag){
        Message msg = new Message();
        msg.what = flag;
        handler.sendMessage(msg);
    }

    //我的客户跳转过来
    public void setViewDate() {
        if (customer != null) {
            clientNameTv.setText(customer.getData().getCustname());
            clientNoTv.setText(customer.getData().getCustomerno());
            clientNameTv.setTextColor(Color.parseColor("#333333"));
            clientNoTv.setTextColor(Color.parseColor("#333333"));
            addressTv.setText(customer.getData().getCustaddress());
            getContactPersonData(customer.getData().getCustid());
        } else if (contactInfoVo != null) {//我的联系人详情
            clientNameTv.setText(contactInfoVo.getData().getCustname());
            clientNoTv.setText(contactInfoVo.getData().getCustid());
            clientNameTv.setTextColor(Color.parseColor("#333333"));
            clientNoTv.setTextColor(Color.parseColor("#333333"));
            addressTv.setText(contactInfoVo.getData().getFamilyaddress());
            getContactPersonData(Long.parseLong(contactInfoVo.getData().getCustid()));
        }
    }

    @OnClick({R.id.back_iv, R.id.location_tv, R.id.client_name_tv, R.id.client_manager_tv
            , R.id.visit_time_tv, R.id.visit_time_2_tv, R.id.add_bt})
    public void onclick(View view) {
        List<String> list;
        switch (view.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.location_tv:
                intValue = 0;
                if(locationTv.getText().toString().trim().equals("")){
                    getLocation();
                }
                break;
            case R.id.client_name_tv:
                intValue = 1;
                list = new ArrayList<>();
                if (clientVo != null) {
                    for (int i = 0; i < clientVo.getData().size(); i++) {
                        list.add(clientVo.getData().get(i).getCustname());
                    }
                }
                showContactUsPopWin(CustomerVisitAddActivity.this, "选择客户", list);
                pop.showAtLocation(view, Gravity.CENTER, 0, 0);
                break;
            case R.id.client_manager_tv:
                intValue = 2;
                if (contactNameVo != null) {
                    if (contactNameVo.isSuccess()) {
                        list = new ArrayList<>();
                        for (int i = 0; i < contactNameVo.getData().size(); i++)
                            list.add(contactNameVo.getData().get(i).getPersonname());
                        showContactUsPopWin(CustomerVisitAddActivity.this, "选择客户经理", list);
                        pop.showAtLocation(view, Gravity.CENTER, 0, 0);
                    }
                } else
                    ToastUtil.showToast("请先选择客户");
                break;
            case R.id.visit_time_tv:
                visitDate = 0;
                showDialog(DATE_DIALOG); //跳出时间选择控件
                break;
            case R.id.visit_time_2_tv:
                visitDate = 1;
                showDialog(DATE_DIALOG);//跳出时间选择控件
                break;
            case R.id.add_bt:
                if (visitSummaryTv.getText().toString().trim().equals("")) {
                    ToastUtil.showToast("请输入拜访总结");
                    return;
                } else if (customer != null || item != null || contactInfoVo != null) { //两个其中一个不能为空
                    mLoading.show();
                    addData();
                } else
                    ToastUtil.showToast("请选择客户名称");
                break;
        }
    }

    /*******时间空间start********/

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
        //下面将“2018-8-8”格式时间，转化为标准日期“2018-08-06”
        SimpleDateFormat foramt = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try{
            date = foramt.parse(string);
        }catch (Exception e){

        }
        string = foramt.format(date);

        if (visitDate == 0)
            visitTimeTv.setText(string);
        else {
            try {
                Date mDate1 = foramt.parse(visitTimeTv.getText().toString().trim());
                Date mDate2 = foramt.parse(string);
                if (mDate1.compareTo(mDate2) > 0) {
                    ToastUtil.showToast("起日期大于止日期");
                } else {
                    visitTime2Tv.setText(string);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
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
                /*if (intValue == 0) {//签到地址
                    Poi poi = pois.get(i);
                    locationTv.setText(poi.getName());
                    pop.dismiss();
                }*/
                if (intValue == 1) {//客户名称
                    item = clientVo.getData().get(i);
                    clientNameTv.setText(item.getCustname() != null && !item.getCustname().equals("") ? item.getCustname() : "");
                    clientNoTv.setText(item.getCustomerno() != null && !item.getCustomerno().equals("") ? item.getCustomerno() : "");
                    addressTv.setText(item.getCustaddress() != null && !item.getCustaddress().equals("") ? item.getCustaddress() : "");
                    clientNameTv.setTextColor(Color.parseColor("#333333"));
                    getContactPersonData(item.getCustid());
                    pop.dismiss();
                }
                if (intValue == 2) {
                    contactItem = contactNameVo.getData().get(i);
                    clientManagerTv.setText(contactItem.getPersonname() != null &&
                            !contactItem.getPersonname().equals("") ? contactItem.getPersonname() : "");
                    contactPersonTv.setText(contactItem.getPersonname() != null &&
                            !contactItem.getPersonname().equals("") ? contactItem.getPersonname() : "");
                    clientManagerTv.setTextColor(Color.parseColor("#333333"));
                    contactPersonTv.setTextColor(Color.parseColor("#333333"));
                    mobileTv.setText(contactItem.getConttel());
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
     * 添加拜访记录
     */
    public void addData() {

        Long custid = 0L;
        String customerno = "";
        String custname = "";
        String custaddress = "";
        String custmanagerid = contactItem != null ? contactItem.getContpersonid() + "" : "";//	客户经理id
        String custmanagername = contactItem != null ? contactItem.getPersonname() : "";//拜访人
        String contpersonid = contactItem != null ? contactItem.getContpersonid() + "" : "";//客户联系人ID
        if (item != null) {
            custid = item.getCustid();
            customerno = item.getCustomerno();
            custname = item.getCustname();
            custaddress = item.getCustaddress();
        } else if (customer != null) {
            custid = customer.getData().getCustid();
            customerno = customer.getData().getCustomerno();
            custname = customer.getData().getCustname();
            custaddress = customer.getData().getCustaddress();
        } else if (contactInfoVo != null) {
            custid = Long.parseLong(contactInfoVo.getData().getCustid());
            customerno = contactInfoVo.getData().getCustomerNo() + "";
            custname = contactInfoVo.getData().getCustname();
            custaddress = contactInfoVo.getData().getFamilyaddress();
        }
        int userid = Config.loginback.getUserId();//	用户ID 当前用户ID
        //private String createdate;//	创建日期
        //private int creator;//创建人
        String signinaddress = locationTv.getText().toString().trim();//	签到地址
        double sigininlat = latitude;//	签到地点纬度
        double sigininlon = longitude;//签到地点经度
        String visitsummary = visitSummaryTv.getText().toString().trim();//	拜访总结
        //private String custphone =;//客户电话
        //String createdate = "";//创建时间
        //int modifiedby;//	修改人
        String visitbegintime = visitTimeTv.getText().toString().trim();//拜访开始时间
        String visitendtime = visitTime2Tv.getText().toString().trim();//拜访结束时间
        String custphone = mobileTv.getText().toString().trim();//	客户电话
        int creator = Config.loginback.getUserId();//	创建人

        String json = new Gson().toJson(new AddContactReq(userid, custid, customerno, custname, custaddress, custmanagerid
                , custmanagername, contpersonid, signinaddress, sigininlat, sigininlon
                , visitsummary, visitbegintime, visitendtime, custphone, creator));

        OkHttpUtils
                .postString()
                .url(Config.addCustVisit)
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
                                        ToastUtil.showToast("提交成功");
                                        Config.isAddVisit = true;
                                        finish();
                                    } else {
                                        ToastUtil.showToast("提交失败");
                                    }
                                    mLoading.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                );
    }

    /**
     * 获取联系人  通过custid获取联系人
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

    /***********地图定位相关start************/

    /***
     * Stop location service
     */
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //location.getLocType() = 161 表示定位成功
            Log.d("loclog","loc status is: " + location.getLocType());
            if (null != location && location.getLocType() == BDLocation.TypeNetWorkLocation) {
                locationService.unregisterListener(mListener);
                locationService.stop();
                StringBuffer sb = new StringBuffer(256);
                sb.append("\nlatitude : ");// 纬度
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");// 经度
                sb.append(location.getLongitude());
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                /*if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    pois.clear();
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        pois.add(poi);
                        sb.append(poi.getName() + ";");
                    }
                }*/
                if(location.getAddress() != null && location.getCity() != null){
                    locSb = new StringBuffer();
                    locSb.append(location.getCity());
                    locSb.append(location.getDistrict());
                    locSb.append(location.getStreet());
                    locSb.append(location.getStreetNumber());
                    sendMsg(LOCATESUCCESS);
                }else{
                    sendMsg(LOCATEFAIL);
                }

                /*if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    if (location.hasAltitude()) {// *****如果有海拔高度*****
                        sb.append("\nheight : ");
                        sb.append(location.getAltitude());// 单位：米
                    }
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }*/
            }else{
                sendMsg(LOCATEFAIL);
            }
        }

        public void onConnectHotSpotMessage(String s, int i) {
        }
    };

    /***********地图定位相关end************/
}
