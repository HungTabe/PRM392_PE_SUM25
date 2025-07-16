package com.example.studytracker.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.studytracker.database.StudySessionDao;
import com.example.studytracker.model.StudySession;
import com.example.studytracker.repository.StudySessionRepository;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainViewModel extends AndroidViewModel {
    private StudySessionRepository repository;
    private LiveData<List<StudySession>> sessions;
    private LiveData<List<StudySessionDao.SubjectDuration>> chartData;
    private LiveData<List<String>> suggestions;
    private MutableLiveData<Integer> totalStudyTime = new MutableLiveData<>(0);
    private MutableLiveData<String> mostStudiedSubject = new MutableLiveData<>("");
    private MutableLiveData<Float> averageFocusScore = new MutableLiveData<>(0f);

    public MainViewModel(Application application) {
        super(application);
        repository = new StudySessionRepository(application);
        sessions = repository.getSessions();
        chartData = repository.getChartData();
        suggestions = repository.getSuggestions();
        calculateSummary();
    }

    public LiveData<List<StudySession>> getSessions() {
        return sessions;
    }

    public LiveData<Integer> getTotalStudyTime() {
        return totalStudyTime;
    }

    public LiveData<String> getMostStudiedSubject() {
        return mostStudiedSubject;
    }

    public LiveData<Float> getAverageFocusScore() {
        return averageFocusScore;
    }

    public LiveData<List<StudySessionDao.SubjectDuration>> getChartData() {
        return chartData;
    }

    public LiveData<List<String>> getSuggestions() {
        return suggestions;
    }

    public void addSession(StudySession session) {
        repository.addSession(session);
    }

    public void filterSessions(String subject, int minFocus, int maxFocus, String fromDate, String toDate, String notes) {
        repository.filterSessions(subject, minFocus, maxFocus, fromDate, toDate, notes);
    }

    public void clearFilters() {
        repository.clearFilters();
    }

    public void loadChartData(String fromDate, String toDate) {
        repository.loadChartData(fromDate, toDate);
    }

    public void getLearningSuggestions(String apiKey) {
        repository.getLearningSuggestions(apiKey);
    }

    private void calculateSummary() {
        sessions.observeForever(sessions -> {
            if (sessions == null || sessions.isEmpty()) return;

            int totalTime = 0;
            float totalFocus = 0;
            int sessionCount = 0;
            Map<String, Integer> subjectTime = new HashMap<>();
            LocalDate now = LocalDate.now();
            LocalDate weekStart = now.minusDays(now.getDayOfWeek().getValue() - 1);

            for (StudySession session : sessions) {
                try {
                    ZonedDateTime zonedDateTime = ZonedDateTime.parse(session.subject_date, DateTimeFormatter.ISO_ZONED_DATE_TIME);
                    LocalDate localSessionDate = zonedDateTime.toLocalDate();

                    if (!localSessionDate.isBefore(weekStart) && !localSessionDate.isAfter(now)) {
                        totalTime += session.duration;
                        totalFocus += session.level;
                        sessionCount++;
                        subjectTime.put(session.subject_name, subjectTime.getOrDefault(session.subject_name, 0) + session.duration);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            totalStudyTime.setValue(totalTime);
            averageFocusScore.setValue(sessionCount > 0 ? totalFocus / sessionCount : 0);

            String mostStudied = "";
            int maxTime = 0;
            for (Map.Entry<String, Integer> entry : subjectTime.entrySet()) {
                if (entry.getValue() > maxTime) {
                    maxTime = entry.getValue();
                    mostStudied = entry.getKey();
                }
            }
            mostStudiedSubject.setValue(mostStudied);
        });
    }
}