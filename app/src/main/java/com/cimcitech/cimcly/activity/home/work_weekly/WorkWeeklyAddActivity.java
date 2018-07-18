package com.cimcitech.cimcly.activity.home.work_weekly;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.Address;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.cimcitech.cimcly.ApkApplication;
import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.adapter.PopupWindowAdapter;
import com.cimcitech.cimcly.baidu.LocationService;
import com.cimcitech.cimcly.bean.work_weekly.ReportTypeVo;
import com.cimcitech.cimcly.bean.work_weekly.WorkWeeklyAddReq;
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

import static android.R.id.list;

public class WorkWeeklyAddActivity extends BaseActivity {

    @Bind(R.id.back_rl)
    RelativeLayout backRl;
    @Bind(R.id.start_time_tv)
    TextView startTimeTv;
    @Bind(R.id.end_time_tv)
    TextView endTimeTv;
    @Bind(R.id.performance_tv)
    EditText performanceTv;
    @Bind(R.id.nextworktask_tv)
    EditText nextworktaskTv;
    @Bind(R.id.add_bt)
    Button addBt;
    @Bind(R.id.work_type_tv)
    TextView workTypeTv;
    @Bind(R.id.location_tv)
    TextView locationTv;
    @Bind(R.id.work_type_name_tv)
    TextView workTypeNameTv;
    @Bind(R.id.start_time_name_tv)
    TextView startTimeNameTv;
    @Bind(R.id.end_time_name_tv)
    TextView endTimeNameTv;
    @Bind(R.id.performance_name_tv)
    TextView performanceNameTv;
    @Bind(R.id.nextworktask_name_tv)
    TextView nextworktaskNameTv;

    private ReportTypeVo reportTypeVo;
    private ReportTypeVo.DataBean itemType;
    private int mYear, mMonth, mDay;
    private final int DATE_DIALOG = 1;
    private PopupWindow pop;
    private double latitude, longitude;
    private ArrayList<Poi> pois = new ArrayList<>(); //获取到的定位位置的对象
    private LocationService locationService;
    private int intValue = 0;
    private StringBuffer locSb ;
    private StringBuffer sb ;
    private List<String> list;

    public int INFO = 1;
    private ProgressDialog dialog = null;
    private static final int STARTTOLOCATING = 1;
    private static final int ENDTOLOCATING = 2;
    private static final int LOCATESUCCESS = 3;
    private static final int LOCATEFAIL = 4;

    private static final int requestLocTime = 7000;
    private boolean isFinishlocating = false;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case STARTTOLOCATING:
                    isFinishlocating = false;
                    dialog = new ProgressDialog(WorkWeeklyAddActivity.this);
                    dialog.setMessage("定位中…");
                    dialog.setCancelable(true);
                    dialog.show();
                    break;
                case LOCATESUCCESS:
                    isFinishlocating = true;
                    if(dialog.isShowing())
                        dialog.dismiss();
                    if(locSb != null && locSb.length() != 0 && !locSb.toString().contains("null")){
                        locationTv.setText(locSb.toString());
                    }
                    break;
                case LOCATEFAIL:
                    isFinishlocating = true;
                    if(dialog.isShowing())
                        dialog.dismiss();
                    Toast.makeText(WorkWeeklyAddActivity.this,"定位失败！请检查网络或GPS",Toast
                            .LENGTH_SHORT).show();
                    break;
            }
            //Toast.makeText(WorkWeeklyAddActivity.this,sb.toString(),Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_weekly_add);
        ButterKnife.bind(this);
        startTimeTv.setText(DateTool.getSystemDate());
        endTimeTv.setText(DateTool.getSystemDate());
        final Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
        getReportType();

        locationService = ((ApkApplication) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);

        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(WorkWeeklyAddActivity.this,Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(WorkWeeklyAddActivity.this,Manifest.permission
                .READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(WorkWeeklyAddActivity.this,Manifest.permission
                .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()){
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(WorkWeeklyAddActivity.this,permissions,1);
        }else {
            getLocation();
        }
    }

    @OnClick({R.id.back_rl, R.id.add_bt, R.id.start_time_tv, R.id.end_time_tv, R.id.work_type_tv, R.id.location_tv})
    public void onclick(View view) {

        switch (view.getId()) {
            case R.id.back_rl:
                finish();
                break;
            case R.id.add_bt:
                if (!checkInput()) return;
                mLoading.show();
                addData();
                break;
            case R.id.start_time_tv:
                //showDialog(DATE_DIALOG);//跳出时间选择控件
                break;
            case R.id.end_time_tv:
                showDialog(DATE_DIALOG);//跳出时间选择控件
                break;
            case R.id.work_type_tv:
                intValue = 0;
                if (null != reportTypeVo && reportTypeVo.isSuccess()) {
                    list = new ArrayList<>();
                    for (int i = 0; i < reportTypeVo.getData().size(); i++)
                        list.add(reportTypeVo.getData().get(i).getCodevalue());
                    showContactUsPopWin(WorkWeeklyAddActivity.this, "工作汇报类型", list);
                    pop.showAtLocation(view, Gravity.CENTER, 0, 0);
                }
                break;
            case R.id.location_tv:

                intValue = 1;

                //if (locSb != null && locSb.length() != 0) {
                if(locationTv.getText().toString().trim().equals("")){
                    getLocation();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults.length > 0){
                    for(int result : grantResults){
                        if(result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(WorkWeeklyAddActivity.this,"必须同意所有的权限才能使用定位",Toast.LENGTH_SHORT
                            ).show();
                            finish();
                            return;
                        }
                    }
                    getLocation();
                }else {
                    Toast.makeText(WorkWeeklyAddActivity.this,"发送未知错误",Toast.LENGTH_SHORT
                    ).show();
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
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (intValue == 0) {
                    itemType = reportTypeVo.getData().get(position);
                    workTypeTv.setText(itemType.getCodevalue());
                }
                if (intValue == 1) {
                    //Poi poi = pois.get(position);
                    //locationTv.setText(poi.getName());
                    /*if(locList != null && locList.size() != 0){
                        locationTv.setText(locList.get(0));
                    }*/
                }
                pop.dismiss();
            }
        });
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

    public boolean checkInput() {
        if (itemType == null) {
            ToastUtil.showToast("请选择工作汇报类型");
            return false;
        }
        if (locationTv.getText().toString().trim().equals("")) {
            ToastUtil.showToast("请选择汇报地址");
            return false;
        }
        if (performanceTv.getText().toString().trim().equals("")) {
            ToastUtil.showToast("请输入工作完成情况");
            return false;
        }
        return true;
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
        try {
            SimpleDateFormat foramt = new SimpleDateFormat("yyyy-MM-dd");
            Date mDate1 = foramt.parse(startTimeTv.getText().toString().trim());
            Date mDate2 = foramt.parse(string);
            if (mDate1.compareTo(mDate2) > 0) {
                ToastUtil.showToast("起日期大于止日期");
            } else {
                /*
                * 防止当选择了结束时间后，结束时间文本显示格式变成了2018-3-14，导致服务器无法识别，提交失败问题
                * Modified by he.qiang@cimc.com  on 2018-03-14 begin
                * */
                //endTimeTv.setText(string);
                endTimeTv.setText(foramt.format(mDate2));
                //Modified by he.qiang@cimc.com  on 2018-03-14 end
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

    private void addData() {

        String begintime = startTimeTv.getText().toString().trim();
        int creator = Config.loginback.getUserId();
        String endtime = endTimeTv.getText().toString().trim();
        String nextworktask = nextworktaskTv.getText().toString().trim();
        String performance = performanceTv.getText().toString().trim();
        int userid = Config.loginback.getUserId();
        String reportType = itemType.getCodeid();
        double siginInLat = latitude;
        double siginInLon = longitude;
        String signInAddress = locationTv.getText().toString().trim();

        String json = new Gson().toJson(new WorkWeeklyAddReq(begintime, creator, endtime, nextworktask, performance
                , userid, reportType, siginInLat, siginInLon, signInAddress));
        OkHttpUtils
                .postString()
                .url(Config.addWorkWeekly)
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
                                Log.d("hqtest","error is: " + e);
                                mLoading.dismiss();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                ToastUtil.showToast(response);
                                try {
                                    JSONObject json = new JSONObject(response);
                                    if (json.getBoolean("success")) {
                                        ToastUtil.showToast("添加成功");
                                        Config.isAddWork = true;
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

    private void getReportType() {
        OkHttpUtils
                .post()
                .url(Config.getReportType)
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
                                // ToastUtil.showToast(response);
                                try {
                                    reportTypeVo = GjsonUtil.parseJsonWithGson(response, ReportTypeVo.class);
                                    /*if (pois.size() > 0)    //设置默认显示的签到地址，set定位中的第一条
                                        locationTv.setText(pois.get(0).getName());*/
                                    /*if(locList != null && locList.size() != 0){
                                        locationTv.setText(locList.get(0));
                                    }*/
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
        // -----------location config ------------
        /*locationService = ((ApkApplication) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听
        int type = getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }
        locationService.start();// 定位SDK
        */
    }

    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            Log.d("loclog","local type is: " + location.getLocType());//值为161，代表定位成功
            /*Toast.makeText(WorkWeeklyAddActivity.this,"type is: " + location.getLocType(),Toast
                    .LENGTH_SHORT).show();*/
            if (null != location && location.getLocType() == BDLocation.TypeNetWorkLocation) {
                locationService.unregisterListener(mListener);
                locationService.stop();
                sb = new StringBuffer(256);
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                /*Address addr = location.getAddress();
                locStr = new StringBuffer();
                locStr.append(addr.city);
                locStr.append(addr.district);
                if(!addr.street.equals("")){
                    locStr.append(addr.street);
                }
                if(!addr.streetNumber.equals("") ){
                    locStr.append(addr.streetNumber);
                }*/
                //locStr = new StringBuffer();
                Log.d("loclog","location.getCity() is: " + location.getCity());
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
                //sendMsg(INFO);
            }else{
                sendMsg(LOCATEFAIL);
            }
        }

        public void onConnectHotSpotMessage(String s, int i) {
        }
    };

    /***********地图定位相关end************/

    public void sendMsg(int flag){
        Message msg = new Message();
        msg.what = flag;
        handler.sendMessage(msg);
    }
}
