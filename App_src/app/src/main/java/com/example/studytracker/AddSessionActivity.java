package com.example.studytracker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studytracker.database.AppDatabase;
import com.example.studytracker.databinding.ActivityAddSessionBinding;
import com.example.studytracker.model.StudySession;
import com.example.studytracker.network.ApiService;
import com.example.studytracker.network.RetrofitClient;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddSessionActivity extends AppCompatActivity {
    private ActivityAddSessionBinding binding;
    private List<String> subjects = Arrays.asList("Mathematics", "History", "Physics", "English Literature", "Computer Science", "Biology", "Chemistry", "Economics", "Art", "Geography");
    private String selectedDate;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddSessionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo ExecutorService
        executorService = Executors.newSingleThreadExecutor();

        // Thiết lập Spinner cho môn học
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.subjectSpinner.setAdapter(adapter);

        // Thiết lập DatePicker
        binding.datePicker.setOnClickListener(v -> showDatePickerDialog());

        // Xử lý nút Save
        binding.saveButton.setOnClickListener(v -> saveSession());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Lưu ngày được chọn dưới dạng ISO 8601
                    ZonedDateTime dateTime = ZonedDateTime.of(selectedYear, selectedMonth + 1, selectedDay, 0, 0, 0, 0, ZonedDateTime.now().getZone());
                    selectedDate = dateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
                    binding.datePicker.setText(String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear));
                }, year, month, day);
        datePickerDialog.show();
    }

    private void saveSession() {
        String subject = binding.subjectSpinner.getSelectedItem() != null ? binding.subjectSpinner.getSelectedItem().toString() : "";
        String durationStr = binding.durationInput.getText().toString();
        float focusLevel = binding.focusLevel.getRating();
        String notes = binding.notesInput.getText().toString();

        // Xác thực (bonus)
        if (subject.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn môn học", Toast.LENGTH_SHORT).show();
            return;
        }
        if (durationStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập thời lượng", Toast.LENGTH_SHORT).show();
            return;
        }
        int duration;
        try {
            duration = Integer.parseInt(durationStr);
            if (duration <= 0) {
                Toast.makeText(this, "Thời lượng phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Thời lượng phải là số hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedDate == null) {
            Toast.makeText(this, "Vui lòng chọn ngày", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo StudySession mới
        StudySession session = new StudySession(
                UUID.randomUUID().toString(), // Tạo ID duy nhất
                selectedDate,
                subject,
                duration,
                (int) focusLevel,
                notes.isEmpty() ? "" : notes
        );

        // Gửi yêu cầu POST đến API
        ApiService apiService = RetrofitClient.getApiService();
        apiService.createStudySession(session).enqueue(new Callback<StudySession>() {
            @Override
            public void onResponse(Call<StudySession> call, Response<StudySession> response) {
                if (response.isSuccessful()) {
                    // Hiển thị Toast khi gửi yêu cầu thành công
                    Toast.makeText(AddSessionActivity.this, "Yêu cầu đã được gửi thành công", Toast.LENGTH_SHORT).show();

                    // Lưu vào cơ sở dữ liệu trên luồng nền
                    executorService.execute(() -> {
                        AppDatabase.getDatabase(AddSessionActivity.this).studySessionDao().insert(session);
                        runOnUiThread(() -> {
                            Toast.makeText(AddSessionActivity.this, "Phiên học đã được lưu", Toast.LENGTH_SHORT).show();
                            finish(); // Quay lại MainActivity
                        });
                    });
                } else {
                    Toast.makeText(AddSessionActivity.this, "Không thể gửi yêu cầu đến API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StudySession> call, Throwable t) {
                Toast.makeText(AddSessionActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Đóng ExecutorService để tránh rò rỉ tài nguyên
        executorService.shutdown();
    }
}