package com.example.studytracker.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.studytracker.database.AppDatabase;
import com.example.studytracker.database.StudySessionDao;
import com.example.studytracker.model.StudySession;
import com.example.studytracker.network.ApiService;
import com.example.studytracker.network.GeminiRequest;
import com.example.studytracker.network.GeminiResponse;
import com.example.studytracker.network.RetrofitClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudySessionRepository {
    private StudySessionDao studySessionDao;
    private MutableLiveData<List<StudySession>> sessionsLiveData;
    private MutableLiveData<List<StudySessionDao.SubjectDuration>> chartDataLiveData;
    private MutableLiveData<List<String>> suggestionsLiveData;
    private ExecutorService executorService;

    public StudySessionRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        studySessionDao = db.studySessionDao();
        sessionsLiveData = new MutableLiveData<>();
        chartDataLiveData = new MutableLiveData<>();
        suggestionsLiveData = new MutableLiveData<>();
        executorService = Executors.newSingleThreadExecutor();
        loadData();
    }

    public LiveData<List<StudySession>> getSessions() {
        return sessionsLiveData;
    }

    public LiveData<List<StudySessionDao.SubjectDuration>> getChartData() {
        return chartDataLiveData;
    }

    public LiveData<List<String>> getSuggestions() {
        return suggestionsLiveData;
    }

    private void loadData() {
        executorService.execute(() -> {
            List<StudySession> localSessions = studySessionDao.getAllSessions();
            if (localSessions != null && !localSessions.isEmpty()) {
                sessionsLiveData.postValue(localSessions);
            } else {
                fetchSessionsFromApi();
            }
        });
    }

    private void fetchSessionsFromApi() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getStudySessions().enqueue(new Callback<List<StudySession>>() {
            @Override
            public void onResponse(Call<List<StudySession>> call, Response<List<StudySession>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<StudySession> sessions = response.body();
                    Map<String, StudySession> uniqueSessions = new HashMap<>();
                    for (StudySession session : sessions) {
                        if (session.getId() != null) {
                            uniqueSessions.put(session.getId(), session);
                        }
                    }
                    executorService.execute(() -> {
                        studySessionDao.insertAll(new ArrayList<>(uniqueSessions.values()));
                        sessionsLiveData.postValue(new ArrayList<>(uniqueSessions.values()));
                    });
                } else {
                    executorService.execute(() -> {
                        List<StudySession> sessions = studySessionDao.getAllSessions();
                        sessionsLiveData.postValue(sessions);
                    });
                }
            }

            @Override
            public void onFailure(Call<List<StudySession>> call, Throwable t) {
                executorService.execute(() -> {
                    List<StudySession> sessions = studySessionDao.getAllSessions();
                    sessionsLiveData.postValue(sessions);
                });
            }
        });
    }

    public void addSession(StudySession session) {
        executorService.execute(() -> {
            studySessionDao.insert(session);
            List<StudySession> updatedSessions = studySessionDao.getAllSessions();
            sessionsLiveData.postValue(updatedSessions);
        });
    }

    public void filterSessions(String subject, int minFocus, int maxFocus, String fromDate, String toDate, String notes) {
        executorService.execute(() -> {
            List<StudySession> filteredSessions = studySessionDao.filterSessions(
                    subject.isEmpty() ? null : subject,
                    minFocus == 0 ? 1 : minFocus,
                    maxFocus == 0 ? 5 : maxFocus,
                    fromDate == null ? "1970-01-01T00:00:00.000Z" : fromDate,
                    toDate == null ? "9999-12-31T23:59:59.999Z" : toDate,
                    notes.isEmpty() ? null : notes
            );
            sessionsLiveData.postValue(filteredSessions);
        });
    }

    public void clearFilters() {
        executorService.execute(() -> {
            List<StudySession> allSessions = studySessionDao.getAllSessions();
            sessionsLiveData.postValue(allSessions);
        });
    }

    public void loadChartData(String fromDate, String toDate) {
        executorService.execute(() -> {
            List<StudySessionDao.SubjectDuration> chartData = studySessionDao.getStudyTimeBySubject(fromDate, toDate);
            chartDataLiveData.postValue(chartData);
        });
    }

    public void getLearningSuggestions(String apiKey) {
        executorService.execute(() -> {
            List<StudySession> lowFocusSessions = studySessionDao.getLowFocusSessions();
            List<String> suggestions = new ArrayList<>();
            if (lowFocusSessions.isEmpty()) {
                suggestions.add("Không có phiên học nào cần cải thiện.");
                suggestionsLiveData.postValue(suggestions);
                return;
            }

            ApiService apiService = RetrofitClient.getGeminiApiService();
            for (StudySession session : lowFocusSessions) {
                if (session.getNotes() != null && !session.getNotes().isEmpty()) {
                    GeminiRequest request = new GeminiRequest(session.getNotes(), session.getSubject_name());
                    apiService.getGeminiSuggestions(apiKey, request).enqueue(new Callback<GeminiResponse>() {
                        @Override
                        public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                String suggestion = response.body().getSuggestion();
                                suggestions.add(session.getSubject_name() + ": " + suggestion);
                                suggestionsLiveData.postValue(new ArrayList<>(suggestions));
                            } else {
                                suggestions.add(session.getSubject_name() + ": Không thể lấy gợi ý từ API. Mã lỗi: " + response.code());
                                suggestionsLiveData.postValue(new ArrayList<>(suggestions));
                            }
                        }

                        @Override
                        public void onFailure(Call<GeminiResponse> call, Throwable t) {
                            suggestions.add(session.getSubject_name() + ": Lỗi mạng - " + t.getMessage());
                            suggestionsLiveData.postValue(new ArrayList<>(suggestions));
                        }
                    });
                } else {
                    suggestions.add(session.getSubject_name() + ": Không có ghi chú để phân tích.");
                    suggestionsLiveData.postValue(new ArrayList<>(suggestions));
                }
            }
        });
    }
}