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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.bean.ListReportPagers;
import com.cimcitech.cimcly.bean.Result;
import com.cimcitech.cimcly.bean.report.Cell;
import com.cimcitech.cimcly.bean.report.ContractReportData;
import com.cimcitech.cimcly.bean.report.ContractReportVo;
import com.cimcitech.cimcly.bean.report.ReportData;
import com.cimcitech.cimcly.bean.report.ReportVo;
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

public class CustomerTop10ReportActivity extends AppCompatActivity{
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
    @Bind(R.id.linear_total)
    LinearLayout linearTotal;
    @Bind(R.id.year_spinner)
    Spinner yearSpinner;
    @Bind(R.id.title_ll)
    LinearLayout title_Ll;
    @Bind(R.id.titleName_tv)
    TextView titleName_Tv;

    private Result<ListReportPagers<ReportData>> status;
    private List<ReportData> data = new ArrayList<>();
    private int totalCarCount = 0 ;
    private int totalTotalprice = 0 ;
    private int totalContCount = 0 ;
    private ArrayList<ArrayMap<String,Integer>>  alist = null;

    private AlertDialog dialog = null;
    private XAxis xAxis;
    private ArrayList<ContractReportData> list;
    private String selectYear = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_customer_top10_2);
        ButterKnife.bind(this);
        initTitle();
        getYear();
    }

    public void initTitle(){
        titleName_Tv.setText("合同总额前10的客户年度合同数统计");
        title_Ll.setVisibility(View.GONE);
    }

    public void getYear(){
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectYear = (String) yearSpinner.getAdapter().getItem(position);
                getData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick({R.id.back_iv})
    public void onclick(View view) {
        finish();
    }

    public void getData() {
        OkHttpUtils
                .post()
                .url(Config.getCustomerTop10)
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

                                ContractReportVo contractReportVo = new Gson().fromJson(response,ContractReportVo.class);
                                list = new  ArrayList<ContractReportData>();
                                if (contractReportVo != null && contractReportVo.isSuccess()) {
                                    if (contractReportVo.getData() != null && contractReportVo.getData().size() > 0) {
                                        for (int i = 0; i < contractReportVo.getData().size(); i++) {
                                            ContractReportData rd = contractReportVo.getData().get(i);
                                            list.add(rd);
                                        }
                                    }
                                }
                                showTables();
                                showGrid();
                            }
                        }
                );
    }

    private void showTables(){
        //barChart= (BarChart) findViewById(R.id.barChart);
        //1、基本设置
        xAxis=barChart.getXAxis();
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisLineColor(Color.WHITE);
        //xAxis.setGridColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.WHITE);
        barChart.setDrawGridBackground(false); // 是否显示表格颜色
        barChart.getAxisLeft().setDrawAxisLine(false);
        barChart.setTouchEnabled(true); // 设置是否可以触摸
        barChart.setDragEnabled(true);// 是否可以拖拽
        barChart.setScaleEnabled(false);// 是否可以缩放
        barChart.setExtraBottomOffset(5);//设置Table表格距离整个图表区域下边框的距离
        //2、y轴和比例尺

        barChart.setDescriptionPosition(barChart.getWidth(),barChart.getHeight() - 260);
        barChart.setDescription("   客户");// 数据描述

        barChart.setDescriptionColor(Color.WHITE);
        barChart.setBackgroundColor(Color.rgb(0, 150, 136));//设置背景色

        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);

        Legend legend = barChart.getLegend();//隐藏比例尺
        legend.setEnabled(false);

        //3、x轴数据,和显示位置
        ArrayList<String> xValues = new ArrayList<String>();
        if(list != null && list.size() != 0){
            for(int i = 0; i < list.size(); i++){
                xValues.add(list.get(i).getCustName());
            }
        }

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//数据位于底部
        xAxis.setLabelRotationAngle(80);//x轴标签旋转方向

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setEnabled(true);
        //leftAxis.setSpaceBottom(60);
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
        if(list != null && list.size() != 0){
            for(int i = 0; i < list.size(); i++){
                yValues.add(new BarEntry(list.get(i).getContCount(), i));
            }
        }

        //5、设置显示的数字为整形
        BarDataSet barDataSet=new BarDataSet(yValues,"");
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
        barDataSet.setBarSpacePercent(50f);//值越大，柱状图就越宽度越小；
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
                    Intent i = new Intent(CustomerTop10ReportActivity.this,
                            CustomerTop10ReportDetailActivity.class);
                    i.putExtra("custId",list.get(e.getXIndex()).getCustId());
                    i.putExtra("year",selectYear);
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
                linear3,linear4,linear5,linear6,linear7,linear8,linear9,linear10,linear11};
        totalCarCount = 0;
        totalTotalprice = 0;
        totalContCount = 0;
        if(list != null && list.size() != 0){
            for(int i = 0; i < list.size(); i++){
                linearLayout[i].setVisibility(View.VISIBLE);
                ((TextView)linearLayout[i].getChildAt(0)).setText(list.get(i).getCustName());
                ((TextView)linearLayout[i].getChildAt(1)).setText(list.get(i).getContCount() + "");
                ((TextView)linearLayout[i].getChildAt(2)).setText(list.get(i).getCarCount() + "");
                ((TextView)linearLayout[i].getChildAt(3)).setText(list.get(i).getTotalprice() + "");

                totalCarCount += list.get(i).getCarCount();
                totalTotalprice += list.get(i).getTotalprice();
                totalContCount += list.get(i).getContCount();
            }

            ((TextView)linearTotal.getChildAt(0)).setText("合计");
            ((TextView)linearTotal.getChildAt(1)).setText(totalContCount + "");
            ((TextView)linearTotal.getChildAt(2)).setText(totalCarCount + "");
            ((TextView)linearTotal.getChildAt(3)).setText(totalTotalprice + "");
        }
    }

}
