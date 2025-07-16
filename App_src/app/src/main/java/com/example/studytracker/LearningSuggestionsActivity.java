package com.example.studytracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studytracker.databinding.ActivityLearningSuggestionsBinding;
import com.example.studytracker.viewmodel.MainViewModel;
import java.util.ArrayList;
import java.util.List;

public class LearningSuggestionsActivity extends AppCompatActivity {
    private ActivityLearningSuggestionsBinding binding;
    private MainViewModel viewModel;
    private SuggestionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLearningSuggestionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Thiết lập RecyclerView
        adapter = new SuggestionAdapter(new ArrayList<>());
        binding.suggestionsList.setLayoutManager(new LinearLayoutManager(this));
        binding.suggestionsList.setAdapter(adapter);

        // Lấy API key từ strings.xml
        String apiKey = getString(R.string.gemini_api_key);
        viewModel.getLearningSuggestions(apiKey);

        // Quan sát gợi ý
        viewModel.getSuggestions().observe(this, suggestions -> {
            if (suggestions == null || suggestions.isEmpty()) {
                binding.noSuggestionsText.setVisibility(View.VISIBLE);
                binding.suggestionsList.setVisibility(View.GONE);
                Toast.makeText(this, "Không có gợi ý hoặc lỗi API", Toast.LENGTH_SHORT).show();
            } else {
                binding.noSuggestionsText.setVisibility(View.GONE);
                binding.suggestionsList.setVisibility(View.VISIBLE);
                adapter.updateSuggestions(suggestions);
            }
        });
    }

    private static class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.ViewHolder> {
        private List<String> suggestions;

        public SuggestionAdapter(List<String> suggestions) {
            this.suggestions = suggestions;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggestion, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String suggestion = suggestions.get(position);
            String[] parts = suggestion.split(": ", 2);
            holder.subjectName.setText(parts[0]);
            holder.suggestionText.setText(parts.length > 1 ? parts[1] : suggestion);
        }

        @Override
        public int getItemCount() {
            return suggestions.size();
        }

        public void updateSuggestions(List<String> newSuggestions) {
            this.suggestions = newSuggestions;
            notifyDataSetChanged();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView subjectName, suggestionText;

            ViewHolder(View itemView) {
                super(itemView);
                subjectName = itemView.findViewById(R.id.subject_name);
                suggestionText = itemView.findViewById(R.id.suggestion_text);
            }
        }
    }
}