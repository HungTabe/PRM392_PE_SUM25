package com.example.studytracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studytracker.R;
import com.example.studytracker.model.StudySession;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class StudySessionAdapter extends RecyclerView.Adapter<StudySessionAdapter.ViewHolder> {
    private List<StudySession> sessions;
    private Context context;

    public StudySessionAdapter(Context context, List<StudySession> sessions) {
        this.context = context;
        this.sessions = sessions;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_study_session, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StudySession session = sessions.get(position);
        holder.subjectName.setText(session.subject_name);
        holder.duration.setText(session.duration + " minutes");
        holder.focusLevel.setRating(session.level);

        try {
            // Phân tích subject_date theo định dạng ISO 8601
            SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            sdfInput.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date sessionDate = sdfInput.parse(session.subject_date);

            // Định dạng lại thành dd/MM/yyyy
            SimpleDateFormat sdfOutput = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            holder.studyDate.setText(sdfOutput.format(sessionDate));
        } catch (ParseException e) {
            // Xử lý lỗi phân tích ngày
            holder.studyDate.setText("Invalid date");
            e.printStackTrace();
        }

        // Gán biểu tượng môn học dựa trên subject_name
        int iconResId = getSubjectIcon(session.subject_name);
        holder.subjectIcon.setImageResource(iconResId);
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    private int getSubjectIcon(String subjectName) {
        switch (subjectName) {
            case "Mathematics":
                return R.drawable.ic_math;
            case "History":
                return R.drawable.ic_history;
            case "Physics":
                return R.drawable.ic_physics;
            // Thêm các môn học khác
            default:
                return R.drawable.ic_default;
        }
    }

    public void updateSessions(List<StudySession> newSessions) {
        this.sessions = newSessions;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView subjectIcon;
        TextView subjectName, studyDate, duration;
        RatingBar focusLevel;

        ViewHolder(View itemView) {
            super(itemView);
            subjectIcon = itemView.findViewById(R.id.subject_icon);
            subjectName = itemView.findViewById(R.id.subject_name);
            studyDate = itemView.findViewById(R.id.study_date);
            duration = itemView.findViewById(R.id.duration);
            focusLevel = itemView.findViewById(R.id.focus_level);
        }
    }
}