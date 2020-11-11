package com.example.firstapp.podarportal;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class Attendance extends AppCompatActivity {

    private PieChart mChart;
    ArrayList<String> attendance = new ArrayList<>();
    ArrayList<String> attendance_key = new ArrayList<>();
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        mChart = findViewById(R.id.pie_chart);
        mTextView = findViewById(R.id.noDataEntry);
        Description des = mChart.getDescription();
        des.setEnabled(false);
        Legend leg = mChart.getLegend();
        leg.setEnabled(false);
        try {
            if (getIntent().getStringArrayListExtra("attendance").size() == 0) {
                mTextView.setVisibility(View.VISIBLE);
                mChart = findViewById(View.INVISIBLE);
            } else {
                attendance = getIntent().getStringArrayListExtra("attendance");
                attendance_key=getIntent().getStringArrayListExtra("attendance_key");
                int abs = 0;
                for (int i = 0; i < attendance.size(); i++) {
                    if (attendance.get(i).equals("absent"))
                        abs++;
                    displayNote(i);
                }

                float abp = (float) abs / attendance.size() * 100;//abp stands for absentee percentage

                ArrayList<PieEntry> Attendance = new ArrayList<>();
                Attendance.add(new PieEntry(abp, 0));
                Attendance.add(new PieEntry(100 - abp, 1));
                mChart.setUsePercentValues(true);
                PieDataSet dataSet = new PieDataSet(Attendance, "Attendance Chart");
                PieData data = new PieData(dataSet);
                data.setValueTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                data.setValueTextSize(24f);
                mChart.setData(data);
                mChart.setNoDataText("");
                dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                mChart.animateXY(1000, 1000);
            }
        } catch (NullPointerException e) {
            Log.d("NullPointer", e.toString());
        }
    }

    @SuppressLint("SetTextI18n")
    private void displayNote(int id){
        LinearLayout linearLayout = findViewById(R.id.log);
        LinearLayout horizontalLayout= new LinearLayout (this);
        horizontalLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        ImageView imageView= new ImageView(this);
        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setGravity(Gravity.CENTER);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setTextColor(Color.parseColor("#834C9B"));
        textView.setText(attendance_key.get(id)+" - "+attendance.get(id));
        textView.setTextSize(16);
        linearLayout.addView(textView);
    }
}