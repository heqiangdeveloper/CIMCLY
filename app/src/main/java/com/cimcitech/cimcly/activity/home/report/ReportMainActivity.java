package com.cimcitech.cimcly.activity.home.report;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cimcitech.cimcly.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReportMainActivity extends AppCompatActivity {

    @Bind(R.id.back_rl)
    RelativeLayout back_RL;
    @Bind(R.id.area_tv)
    TextView area_Tv;
    @Bind(R.id.product_tv)
    TextView product_Tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back_rl,R.id.area_tv,R.id.product_tv,R.id.area_contract_amount_tv,
            R.id.product_contract_amount_tv,R.id.province_top10_tv,R.id.customer_top10_tv})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.back_rl:
                finish();
                break;
            case R.id.area_tv://不同区域的意向订单统计
                startActivity(new Intent(ReportMainActivity.this,AreaReportActivity.class));
                break;
            case R.id.product_tv://不同产品的意向订单统计
                startActivity(new Intent(ReportMainActivity.this,ProductReportActivity.class));
                break;
            case R.id.area_contract_amount_tv://不同区域的合同数量统计
                startActivity(new Intent(ReportMainActivity.this,AreaContractAmountReportActivity.class));
                break;
            case R.id.product_contract_amount_tv://不同产品年度合同数统计
                startActivity(new Intent(ReportMainActivity.this,ProductContractAmountReportActivity.class));
                break;
            case R.id.province_top10_tv://合同总额前10的省份合同数量统计
                startActivity(new Intent(ReportMainActivity.this,ProvinceTop10ReportActivity.class));
                break;
            case R.id.customer_top10_tv://合同总额前10的客户合同数量统计
                startActivity(new Intent(ReportMainActivity.this,CustomerTop10ReportActivity.class));
                break;
        }
    }
}
