package com.example.studytracker.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.studytracker.model.StudySession;

@Database(entities = {StudySession.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract StudySessionDao studySessionDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "study_tracker_db")
                            .fallbackToDestructiveMigration() // Xóa và tạo lại DB nếu version thay đổi
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}