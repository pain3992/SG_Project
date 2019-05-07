package com.graduate.seoil.sg_projdct;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class SimplePieActivity extends AppCompatActivity {
    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_pie);

        pieChart = findViewById(R.id.pieChart);

        pieChart.setUsePercentValues(true);  // true : 퍼센테이지로 보임
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);  // 간격인듯? 일단 모름

        pieChart.setDragDecelerationFrictionCoef(0.99f);

        pieChart.setDrawHoleEnabled(false); // true : 파이차트 가운데 홀 만듬.
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f); // 가운데 투명 원

        ArrayList<PieEntry> yValues = new ArrayList<>();

        yValues.add(new PieEntry(34, "Korea"));
        yValues.add(new PieEntry(23, "USA"));
        yValues.add(new PieEntry(14, "UK"));
        yValues.add(new PieEntry(35, "India"));
        yValues.add(new PieEntry(40, "Russa"));
        yValues.add(new PieEntry(23, "Japan"));

//        Description description = new Description();
//        description.setText("This is KgpTalkie\n Subscribe this channel for more videos");
//        description.setTextSize(5);
//        pieChart.setDescription(description);

        pieChart.animateY(1000, Easing.EaseInOutCubic);

        PieDataSet dataSet = new PieDataSet(yValues, "Countries");
        dataSet.setSliceSpace(3f);  // 슬라이스 간격
        dataSet.setSelectionShift(5f); // 커질수록 원 크기가 작아짐.
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(10f); // 데이터 글씨 크기
        data.setValueTextColor(Color.YELLOW); // 데이터 색상.

        pieChart.setData(data);

    }
}
