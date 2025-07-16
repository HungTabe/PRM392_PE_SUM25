package com.example.studytracker.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.studytracker.model.StudySession;
import java.util.List;

@Dao
public interface StudySessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<StudySession> sessions);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(StudySession session);

    @Query("SELECT * FROM study_sessions")
    List<StudySession> getAllSessions();

    @Query("SELECT * FROM study_sessions " +
            "WHERE (:subject IS NULL OR subject_name = :subject) " +
            "AND level BETWEEN :minFocus AND :maxFocus " +
            "AND subject_date BETWEEN :fromDate AND :toDate " +
            "AND (:notes IS NULL OR notes LIKE '%' || :notes || '%')")
    List<StudySession> filterSessions(String subject, int minFocus, int maxFocus, String fromDate, String toDate, String notes);

    @Query("SELECT subject_name, SUM(duration) as total_duration " +
            "FROM study_sessions " +
            "WHERE subject_date >= :fromDate AND subject_date <= :toDate " +
            "GROUP BY subject_name")
    List<SubjectDuration> getStudyTimeBySubject(String fromDate, String toDate);

    @Query("SELECT * FROM study_sessions WHERE level <= 3")
    List<StudySession> getLowFocusSessions();

    // Lớp nội bộ để lưu kết quả tổng hợp
    class SubjectDuration {
        public String subject_name;
        public int total_duration;
    }

}