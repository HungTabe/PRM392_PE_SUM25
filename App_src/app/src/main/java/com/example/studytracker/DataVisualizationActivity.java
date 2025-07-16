package com.example.studytracker;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.studytracker.database.StudySessionDao;
import com.example.studytracker.databinding.ActivityDataVisualizationBinding;
import com.example.studytracker.viewmodel.MainViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DataVisualizationActivity extends AppCompatActivity {
    private ActivityDataVisualizationBinding binding;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDataVisualizationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Tính toán khoảng thời gian 7 ngày qua
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime weekAgo = now.minusDays(7);
        String toDate = now.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
        String fromDate = weekAgo.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);

        // Tải dữ liệu biểu đồ
        viewModel.loadChartData(fromDate, toDate);

        // Quan sát dữ liệu biểu đồ
        viewModel.getChartData().observe(this, chartData -> {
            if (chartData == null || chartData.isEmpty()) {
                binding.pieChart.setNoDataText("Không có dữ liệu để hiển thị");
                return;
            }

            List<PieEntry> entries = new ArrayList<>();
            int totalDuration = 0;
            for (StudySessionDao.SubjectDuration data : chartData) {
                if (data.total_duration > 0) {
                    entries.add(new PieEntry(data.total_duration, data.subject_name));
                    totalDuration += data.total_duration;
                }
            }

            if (entries.isEmpty()) {
                binding.pieChart.setNoDataText("Không có dữ liệu trong 7 ngày qua");
                return;
            }

            PieDataSet dataSet = new PieDataSet(entries, "Thời gian học theo môn");
            dataSet.setColors(new int[]{
                    Color.rgb(255, 102, 0), // Cam
                    Color.rgb(0, 153, 255), // Xanh dương
                    Color.rgb(255, 51, 153), // Hồng
                    Color.rgb(0, 204, 102), // Xanh lá
                    Color.rgb(153, 51, 255), // Tím
                    Color.rgb(255, 204, 0), // Vàng
                    Color.rgb(102, 102, 102), // Xám
                    Color.rgb(255, 0, 0), // Đỏ
                    Color.rgb(0, 255, 255), // Xanh lam
                    Color.rgb(204, 0, 204) // Tím đậm
            });
            dataSet.setValueTextSize(12f);
            dataSet.setValueTextColor(Color.WHITE);
            dataSet.setValueFormatter(new PercentFormatter(binding.pieChart));

            PieData pieData = new PieData(dataSet);
            binding.pieChart.setData(pieData);
            binding.pieChart.getDescription().setEnabled(false);
            binding.pieChart.setUsePercentValues(true);
            binding.pieChart.setEntryLabelTextSize(12f);
            binding.pieChart.setEntryLabelColor(Color.BLACK);
            binding.pieChart.setCenterText("Thời gian học\n7 ngày qua");
            binding.pieChart.setCenterTextSize(14f);
            binding.pieChart.animateY(1000);
            binding.pieChart.invalidate();
        });
    }
}