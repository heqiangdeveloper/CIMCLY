package com.cimcitech.cimcly.activity.home.report;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.bean.ListReportPagers;
import com.cimcitech.cimcly.bean.Result;
import com.cimcitech.cimcly.bean.report.Cell;
import com.cimcitech.cimcly.bean.report.ProductReportData;
import com.cimcitech.cimcly.bean.report.ProductReportVo;
import com.cimcitech.cimcly.bean.report.ReportData;
import com.cimcitech.cimcly.utils.Config;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class ProductReportActivity extends AppCompatActivity{

    @Bind(R.id.back_rl)
    RelativeLayout backRl;
    @Bind(R.id.barChart)
    BarChart barChart;

    @Bind(R.id.linear1)
    LinearLayout linear1;
    @Bind(R.id.linear2)
    LinearLayout linear2;
    @Bind(R.id.linear3)
    LinearLayout linear3;
    @Bind(R.id.linear4)
    LinearLayout linear4;
    @Bind(R.id.linear5)
    LinearLayout linear5;
    @Bind(R.id.linear6)
    LinearLayout linear6;
    @Bind(R.id.linear7)
    LinearLayout linear7;
    @Bind(R.id.linear8)
    LinearLayout linear8;
    @Bind(R.id.linear9)
    LinearLayout linear9;
    @Bind(R.id.linear10)
    LinearLayout linear10;
    @Bind(R.id.linear11)
    LinearLayout linear11;
    @Bind(R.id.linear12)
    LinearLayout linear12;
    @Bind(R.id.linear_total)
    LinearLayout linearTotal;

    @Bind(R.id.area1_tv)
    TextView area1_Tv;
    @Bind(R.id.area1_high_tv)
    TextView area1_high_tv;
    @Bind(R.id.area1_low_tv)
    TextView area1_low_Tv;
    @Bind(R.id.area1_mid_tv)
    TextView area1_mid_Tv;
    @Bind(R.id.area1_sure_tv)
    TextView area1_sure_Tv;

    @Bind(R.id.area2_tv)
    TextView area2_Tv;
    @Bind(R.id.area2_high_tv)
    TextView area2_high_tv;
    @Bind(R.id.area2_low_tv)
    TextView area2_low_Tv;
    @Bind(R.id.area2_mid_tv)
    TextView area2_mid_Tv;
    @Bind(R.id.area2_sure_tv)
    TextView area2_sure_Tv;

    @Bind(R.id.area3_tv)
    TextView area3_Tv;
    @Bind(R.id.area3_high_tv)
    TextView area3_high_tv;
    @Bind(R.id.area3_low_tv)
    TextView area3_low_Tv;
    @Bind(R.id.area3_mid_tv)
    TextView area3_mid_Tv;
    @Bind(R.id.area3_sure_tv)
    TextView area3_sure_Tv;

    @Bind(R.id.area4_tv)
    TextView area4_Tv;
    @Bind(R.id.area4_high_tv)
    TextView area4_high_tv;
    @Bind(R.id.area4_low_tv)
    TextView area4_low_Tv;
    @Bind(R.id.area4_mid_tv)
    TextView area4_mid_Tv;
    @Bind(R.id.area4_sure_tv)
    TextView area4_sure_Tv;

    @Bind(R.id.area5_tv)
    TextView area5_Tv;
    @Bind(R.id.area5_high_tv)
    TextView area5_high_tv;
    @Bind(R.id.area5_low_tv)
    TextView area5_low_Tv;
    @Bind(R.id.area5_mid_tv)
    TextView area5_mid_Tv;
    @Bind(R.id.area5_sure_tv)
    TextView area5_sure_Tv;

    @Bind(R.id.area6_tv)
    TextView area6_Tv;
    @Bind(R.id.area6_high_tv)
    TextView area6_high_tv;
    @Bind(R.id.area6_low_tv)
    TextView area6_low_Tv;
    @Bind(R.id.area6_mid_tv)
    TextView area6_mid_Tv;
    @Bind(R.id.area6_sure_tv)
    TextView area6_sure_Tv;

    @Bind(R.id.area7_tv)
    TextView area7_Tv;
    @Bind(R.id.area7_high_tv)
    TextView area7_high_tv;
    @Bind(R.id.area7_low_tv)
    TextView area7_low_Tv;
    @Bind(R.id.area7_mid_tv)
    TextView area7_mid_Tv;
    @Bind(R.id.area7_sure_tv)
    TextView area7_sure_Tv;

    @Bind(R.id.area8_tv)
    TextView area8_Tv;
    @Bind(R.id.area8_high_tv)
    TextView area8_high_tv;
    @Bind(R.id.area8_low_tv)
    TextView area8_low_Tv;
    @Bind(R.id.area8_mid_tv)
    TextView area8_mid_Tv;
    @Bind(R.id.area8_sure_tv)
    TextView area8_sure_Tv;

    @Bind(R.id.area9_tv)
    TextView area9_Tv;
    @Bind(R.id.area9_high_tv)
    TextView area9_high_tv;
    @Bind(R.id.area9_low_tv)
    TextView area9_low_Tv;
    @Bind(R.id.area9_mid_tv)
    TextView area9_mid_Tv;
    @Bind(R.id.area9_sure_tv)
    TextView area9_sure_Tv;

    @Bind(R.id.area10_tv)
    TextView area10_Tv;
    @Bind(R.id.area10_high_tv)
    TextView area10_high_tv;
    @Bind(R.id.area10_low_tv)
    TextView area10_low_Tv;
    @Bind(R.id.area10_mid_tv)
    TextView area10_mid_Tv;
    @Bind(R.id.area10_sure_tv)
    TextView area10_sure_Tv;





    private Result<ListReportPagers<ReportData>> status;
    private List<ReportData> data = new ArrayList<>();

    private final String REGION = "区域代码";
    private final String HIGH = "高";
    private final String MIDDDLE = "中";
    private final String LOW = "低";
    private final String SURE = "已报价";

    private int totalHigh = 0 ;
    private int totalMiddle = 0 ;
    private int totalLow = 0 ;
    private int totalSure = 0 ;

    private int totalAreaNum = 0;
    private ArrayList<ArrayMap<String,Integer>>  alist = null;

    private ArrayMap<String,Integer> totalMap = new ArrayMap<>();
    private ArrayMap<String,Integer> hzMap = new ArrayMap<>();
    private ArrayMap<String,Integer> hbMap = new ArrayMap<>();
    private ArrayMap<String,Integer> xnMap = new ArrayMap<>();
    private ArrayMap<String,Integer> hdMap = new ArrayMap<>();
    private ArrayMap<String,Integer> xbMap = new ArrayMap<>();
    private ArrayMap<String,Integer> zyMap = new ArrayMap<>();
    private ArrayMap<String,Integer> wgMap = new ArrayMap<>();

    private final String ygdc = "液灌单车";
    private final String ygbg = "液灌半挂";
    private final String szdc = "散装单车";
    private final String szbg = "散装半挂";
    private final String jbc = "搅拌车";
    private final String tzkc = "特种客车";
    private final String hhdlc = "混合动力车";
    private final String lcc = "冷藏车";
    private final String hwc = "环卫车";
    private final String hwz = "环卫站";
    private final String gjc = "公交车";
    private final String glc = "公路车";

    private final  int  STARTLOADING = 1;
    private final  int  ENDLOADING = 2;

    private AlertDialog dialog = null;
    private XAxis xAxis;
    ArrayList<ProductReportData> list;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case STARTLOADING:
                    getData();
                    break;
                case ENDLOADING:
                    dialog.dismiss();
                    showTables();
                    break;
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_product);
        ButterKnife.bind(this);

        //PopupLoadingWindow();


        getData();


    }

    @OnClick({R.id.back_rl})
    public void onclick(View view) {
        switch (view.getId()){
            case R.id.back_rl:
                this.finish();
                break;
        }
    }

    private void PopupLoadingWindow(){

        /*chart.post(new Runnable() {
            @Override
            public void run() {
                sendMsg(STARTLOADING);
                dialog = new AlertDialog.Builder(AreaReportActivity.this)
                        .setTitle("提示")
                        .setMessage("正在加载数据中")
                        .setCancelable(false)
                        .create();
                dialog.show();
            }
        });*/
    }

    public void sendMsg(int flag){
        Message msg = new Message();
        msg.what = flag;
        mHandler.sendMessage(msg);
    }

    public void getData() {
        OkHttpUtils
                .post()
                .url(Config.getDiffProductOrder)
                .addHeader("checkTokenKey", Config.loginback.getToken())
                .addHeader("sessionKey", Config.loginback.getUserId() + "")
                .build()
                .execute(
                        new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                //ToastUtil.showNetError();
                                Log.d("heqpm","report error..");
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.d("heqpm","report response is：" + response);

                                ProductReportVo reportVo = new Gson().fromJson(response,ProductReportVo.class);
                                totalAreaNum = reportVo.getData().size();
                                list = new  ArrayList<ProductReportData>();
                                if (reportVo.isSuccess()) {
                                    if (reportVo.getData() != null && reportVo.getData().size() > 0) {
                                        for (int i = 0; i < reportVo.getData().size(); i++) {
                                            ProductReportData rd = reportVo.getData().get(i);

                                            list.add(rd);
                                            //putData(rd);
                                        }
                                    }
                                }
                                //putDataToTotalMap();
                                //initMaps();

                                //int  n = list.size();
                                showTables();
                                showGrid();
                            }
                        }
                );
    }

    public void putData(ReportData rd){
        /*switch(rd.getRegionDesc()){
            case hz:
                putDataToMap(rd,hzMap);
                break;
            case hb:
                putDataToMap(rd,hbMap);
                break;
            case xn:
                putDataToMap(rd,xnMap);
                break;
            case hd:
                putDataToMap(rd,hdMap);
                break;
            case xb:
                putDataToMap(rd,xbMap);
                break;
            case zy:
                putDataToMap(rd,zyMap);
                break;
            case wg:
                putDataToMap(rd,wgMap);
                break;
        }*/

    }

    public void putDataToMap(ReportData rd , ArrayMap<String,Integer> map){
        map.put(REGION,rd.getRegion());
        if(rd.getOpportUnityVos().size() > 0){
            for(int j = 0; j < rd.getOpportUnityVos().size(); j++){
                Cell cell = rd.getOpportUnityVos().get(j);
                if(cell != null){
                    if(cell.getPossibility() != null && cell.getPossibility().trim() != ""){
                        switch(cell.getPossibility()){
                            case "高":
                                map.put(HIGH,cell.getProductCount());
                                totalHigh += cell.getProductCount();
                                break;
                            case "中":
                                map.put(MIDDDLE,cell.getProductCount());
                                totalMiddle += cell.getProductCount();
                                break;
                            case "低":
                                map.put(LOW,cell.getProductCount());
                                totalLow += cell.getProductCount();
                                break;
                            case "已报价":
                                map.put(SURE,cell.getProductCount());
                                totalSure += cell.getProductCount();
                                break;
                        }
                    }

                }
            }
        }

    }

    public void putDataToTotalMap(){
        totalMap.put(REGION,0);
        totalMap.put(HIGH,totalHigh);
        totalMap.put(MIDDDLE,totalMiddle);
        totalMap.put(LOW,totalLow);
        totalMap.put(SURE,totalSure);
    }

    public void initMaps(){
        alist = new ArrayList<ArrayMap<String,Integer>>();
        if(totalMap.size() > 1){
            alist.add(totalMap);
        }
        if(hzMap.size() > 1){
            alist.add(hzMap);
        }
        if(hbMap.size() > 1){
            alist.add(hbMap);
        }
        if(hdMap.size() > 1){
            alist.add(hdMap);
        }
        if(xbMap.size() > 1){
            alist.add(xbMap);
        }
        if(zyMap.size() > 1){
            alist.add(zyMap);
        }
        if(wgMap.size() > 1){
            alist.add(wgMap);
        }
    }

    private void showTables(){
        //barChart= (BarChart) findViewById(R.id.barChart);
        //1、基本设置
        xAxis=barChart.getXAxis();
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//数据位于底部
        xAxis.setLabelRotationAngle(80);//x轴标签旋转方向
        barChart.setDrawGridBackground(false); // 是否显示表格颜色
        barChart.getAxisLeft().setDrawAxisLine(false);
        barChart.setTouchEnabled(true); // 设置是否可以触摸
        barChart.setDragEnabled(true);// 是否可以拖拽
        barChart.setScaleEnabled(false);// 是否可以缩放
        //2、y轴和比例尺

        barChart.setDescriptionPosition(barChart.getWidth(),barChart.getHeight() - 110);
        barChart.setDescription("   产品");// 数据描述
        barChart.setDescriptionColor(Color.WHITE);
        //barChart.setDescriptionTextSize(24);
        barChart.setBackgroundColor(Color.rgb(0, 150, 136));//设置背景色

        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);

        Legend legend = barChart.getLegend();//隐藏比例尺
        legend.setEnabled(false);

        //3、x轴数据,和显示位置
        ArrayList<String> xValues = new ArrayList<String>();
        /*xValues.add("一季度");
        xValues.add("二季度");
        xValues.add("三季度");
        xValues.add("四季度");*/
        if(list != null && list.size() != 0){
            for(int i = 0; i < list.size(); i++){
                xValues.add(list.get(i).getVarietyDesc());
            }
        }

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.setDrawLabels(true);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisLineColor(Color.WHITE);
        leftAxis.setGridColor(Color.WHITE);

        //YAxis rightAxis = barChart.getAxisRight();
        leftAxis.setStartAtZero(true);
        leftAxis.resetAxisMinValue();
        leftAxis.resetAxisMaxValue();
        leftAxis.setDrawAxisLine(true);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);


        //4、y轴数据
        ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();
        //new BarEntry(20, 0)前面代表数据，后面代码柱状图的位置；
        /*yValues.add(new BarEntry(20, 0));
        yValues.add(new BarEntry(18, 1));
        yValues.add(new BarEntry(4, 2));
        yValues.add(new BarEntry(45, 3));*/
        if(list != null && list.size() != 0){
            for(int i = 0; i < list.size(); i++){
                ArrayList<Cell> cellList = list.get(i).getOpportUnityVos();
                if(cellList == null && cellList.size() ==0 ){
                    //ReportData.count = 0;
                    list.get(i).setCount(0);
                }else{
                    int count = 0;
                    for(int j = 0; j < cellList.size(); j++){
                        if(cellList.get(j).getPossibility() != null){
                            //ReportData.count += cellList.get(i).getProductCount();
                            count += cellList.get(j).getProductCount();
                        }
                    }
                    list.get(i).setCount(count);
                }
            }
            for(int i = 0; i < list.size(); i++){
                yValues.add(new BarEntry(list.get(i).getCount(), i));
            }
        }

        //5、设置显示的数字为整形
        BarDataSet barDataSet=new BarDataSet(yValues,"");
        //设置顶点值的显示格式
        barDataSet.setValueFormatter(new ValueFormatter() {
             @Override
             public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                 int n = (int) value;
                 return n + "";
             }
         });
        //6、设置柱状图的颜色
        barDataSet.setColors(new int[]{Color.rgb(239, 83, 80), Color.rgb(236, 34, 122),
                Color.rgb(171, 71, 188), Color.rgb(255, 193, 7), Color.rgb(0, 255, 0),Color
                .rgb(255, 255, 0), Color.rgb(255, 0, 0), Color.rgb(0, 0, 255), Color.rgb(63, 81,
                181)});
        barDataSet.setBarSpacePercent(55f);//值越大，柱状图就越宽度越小；
        //7、显示，柱状图的宽度和动画效果
        BarData barData = new BarData(xValues, barDataSet);
        //设置顶点值的字体大小和颜色
        barData.setValueTextColor(Color.WHITE);
        barData.setValueTextSize(18);

        barChart.animateY(1000);
        barChart.setData(barData); //

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                if(list != null && list.size() != 0){
                    Intent i = new Intent(ProductReportActivity.this,ProductReportDetailActivity.class);
                    i.putExtra("productvariety",list.get(e.getXIndex()).getVariety());
                    startActivity(i);
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });


    }

    private void showGrid(){
        LinearLayout[] linearLayout = new LinearLayout[]{linear1,linear2,
                linear3,linear4,linear5,linear6,linear7,linear8,linear9,linear10,linear11, linear12};
        if(list != null && list.size() != 0){
            for(int i = 0; i < list.size(); i++){
                linearLayout[i].setVisibility(View.VISIBLE);

                ((TextView)linearLayout[i].getChildAt(0)).setText(list.get(i).getVarietyDesc());

                ArrayList<Cell> cellList = list.get(i).getOpportUnityVos();
                if(cellList == null && cellList.size() ==0 ){
                    list.get(i).setCount(0);
                    list.get(i).setHighCount(0);
                    list.get(i).setMidCount(0);
                    list.get(i).setLowCount(0);
                    list.get(i).setSureCount(0);
                }else{
                    for(int j = 0; j < cellList.size(); j++){
                        if(cellList.get(j).getPossibility() != null){
                            switch (cellList.get(j).getPossibility()){
                                case "高":
                                    list.get(i).setHighCount(cellList.get(j).getProductCount());
                                    break;
                                case "中":
                                    list.get(i).setMidCount(cellList.get(j).getProductCount());
                                    break;
                                case "低":
                                    list.get(i).setLowCount(cellList.get(j).getProductCount());
                                    break;
                                case "已报价":
                                    list.get(i).setSureCount(cellList.get(j).getProductCount());
                                    break;
                            }
                        }
                    }
                }

                totalHigh += list.get(i).getHighCount();
                totalMiddle += list.get(i).getMidCount();
                totalLow += list.get(i).getLowCount();
                totalSure += list.get(i).getSureCount();

                ((TextView)linearLayout[i].getChildAt(1)).setText(list.get(i).getLowCount() + "");
                ((TextView)linearLayout[i].getChildAt(2)).setText(list.get(i).getMidCount() + "");
                ((TextView)linearLayout[i].getChildAt(3)).setText(list.get(i).getHighCount() + "");
                ((TextView)linearLayout[i].getChildAt(4)).setText(list.get(i).getSureCount() + "");
                ((TextView)linearLayout[i].getChildAt(5)).setText(list.get(i).getCount()+ "");
            }

            ((TextView)linearTotal.getChildAt(0)).setText("合计");
            ((TextView)linearTotal.getChildAt(1)).setText(totalLow + "");
            ((TextView)linearTotal.getChildAt(2)).setText(totalMiddle + "");
            ((TextView)linearTotal.getChildAt(3)).setText(totalHigh + "");
            ((TextView)linearTotal.getChildAt(4)).setText(totalSure+ "");
            ((TextView)linearTotal.getChildAt(5)).setText(totalLow + totalMiddle + totalHigh +
                    totalSure + "");
        }
    }

}
