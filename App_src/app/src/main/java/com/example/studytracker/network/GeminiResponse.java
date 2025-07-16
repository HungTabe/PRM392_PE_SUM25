package com.example.studytracker.network;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GeminiResponse {
    @SerializedName("candidates")
    private List<Candidate> candidates;

    public String getSuggestion() {
        if (candidates != null && !candidates.isEmpty()) {
            Candidate candidate = candidates.get(0);
            if (candidate.content != null && !candidate.content.parts.isEmpty()) {
                return candidate.content.parts.get(0).text;
            }
        }
        return "Không có gợi ý.";
    }

    public static class Candidate {
        @SerializedName("content")
        private Content content;

        public static class Content {
            @SerializedName("parts")
            private List<Part> parts;

            public static class Part {
                @SerializedName("text")
                private String text;
            }
        }
    }
}