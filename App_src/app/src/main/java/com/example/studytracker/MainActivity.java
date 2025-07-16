package com.example.studytracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.studytracker.adapter.StudySessionAdapter;
import com.example.studytracker.databinding.ActivityMainBinding;
import com.example.studytracker.viewmodel.MainViewModel;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MainViewModel viewModel;
    private StudySessionAdapter adapter;
    private ActivityMainBinding binding;
    private String fromDate;
    private String toDate;
    private List<String> subjects = Arrays.asList("", "Mathematics", "History", "Physics", "English Literature", "Computer Science", "Biology", "Chemistry", "Economics", "Art", "Geography");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Thiết lập RecyclerView
        adapter = new StudySessionAdapter(this, new ArrayList<>());
        binding.sessionList.setLayoutManager(new LinearLayoutManager(this));
        binding.sessionList.setAdapter(adapter);

        // Quan sát dữ liệu
        viewModel.getSessions().observe(this, sessions -> {
            adapter.updateSessions(sessions);
        });

        viewModel.getTotalStudyTime().observe(this, time -> {
            binding.totalTime.setText("Total Study Time: " + time + " minutes");
        });

        viewModel.getMostStudiedSubject().observe(this, subject -> {
            binding.mostStudiedSubject.setText("Most Studied: " + subject);
        });

        viewModel.getAverageFocusScore().observe(this, score -> {
            binding.averageFocus.setText(String.format("Average Focus: %.1f", score));
        });

        // Thiết lập Spinner cho môn học
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjects);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.subjectFilter.setAdapter(subjectAdapter);

        // Thiết lập DatePicker cho khoảng ngày
        binding.dateFrom.setOnClickListener(v -> showDatePickerDialog(true));
        binding.dateTo.setOnClickListener(v -> showDatePickerDialog(false));

        binding.analyzeAiButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LearningSuggestionsActivity.class);
            startActivity(intent);
        });

        // Xử lý nút Filter
        binding.filterButton.setOnClickListener(v -> {
            String subject = binding.subjectFilter.getSelectedItem() != null ? binding.subjectFilter.getSelectedItem().toString() : "";
            String minFocusStr = binding.focusMin.getText().toString();
            String maxFocusStr = binding.focusMax.getText().toString();
            String notes = binding.notesSearch.getText().toString();

            int minFocus = minFocusStr.isEmpty() ? 0 : Integer.parseInt(minFocusStr);
            int maxFocus = maxFocusStr.isEmpty() ? 0 : Integer.parseInt(maxFocusStr);

            if (minFocus < 0 || minFocus > 5 || maxFocus < 0 || maxFocus > 5 || (minFocus > maxFocus && maxFocus != 0)) {
                Toast.makeText(this, "Mức độ tập trung phải nằm trong khoảng 1-5 và min <= max", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.filterSessions(subject, minFocus, maxFocus, fromDate, toDate, notes);
        });

        // Xử lý nút Clear Filters
        binding.clearFiltersButton.setOnClickListener(v -> {
            binding.subjectFilter.setSelection(0);
            binding.dateFrom.setText("");
            binding.dateTo.setText("");
            binding.focusMin.setText("");
            binding.focusMax.setText("");
            binding.notesSearch.setText("");
            fromDate = null;
            toDate = null;
            viewModel.clearFilters();
            Toast.makeText(this, "Đã đặt lại bộ lọc", Toast.LENGTH_SHORT).show();
        });

        // Xử lý nút Add
        binding.fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddSessionActivity.class);
            startActivity(intent);
        });

        // Xử lý nút Chart
        binding.chartButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DataVisualizationActivity.class);
            startActivity(intent);
        });
    }

    private void showDatePickerDialog(boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    ZonedDateTime dateTime = ZonedDateTime.of(selectedYear, selectedMonth + 1, selectedDay, 0, 0, 0, 0, ZonedDateTime.now().getZone());
                    String formattedDate = dateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
                    String displayDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                    if (isFromDate) {
                        fromDate = formattedDate;
                        binding.dateFrom.setText(displayDate);
                    } else {
                        toDate = formattedDate;
                        binding.dateTo.setText(displayDate);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }
}