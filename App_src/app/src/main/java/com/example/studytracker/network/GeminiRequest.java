package com.example.studytracker.network;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

public class GeminiRequest {
    @SerializedName("contents")
    private List<Content> contents;

    public GeminiRequest(String notes, String subjectName) {
        String prompt = "Bạn là một trợ lý học tập AI. Dựa trên ghi chú từ một phiên học, hãy phân tích nội dung và trích xuất các từ khóa chính liên quan đến chủ đề học tập (ví dụ: \"calculus\", \"World War II\"). Sau đó, cung cấp một gợi ý cụ thể để cải thiện hiệu quả học tập cho chủ đề đó, chẳng hạn như phương pháp học, tài nguyên đề xuất (bài viết, video), hoặc kỹ thuật cải thiện tập trung. Gợi ý phải ngắn gọn, thực tế, và phù hợp với sinh viên. Nếu ghi chú không đủ thông tin, trả về một gợi ý chung dựa trên môn học.\n\n" +
                "Ghi chú: " + notes + "\n" +
                "Môn học: " + subjectName + "\n\n" +
                "Ví dụ phản hồi:\n- Từ khóa: calculus\n- Gợi ý: Tập trung vào bài tập tích phân. Xem video trên Khan Academy về tích phân cơ bản.";
        this.contents = Collections.singletonList(new Content(Collections.singletonList(new Part(prompt))));
    }

    public List<Content> getContents() {
        return contents;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }

    public static class Content {
        @SerializedName("parts")
        private List<Part> parts;

        public Content(List<Part> parts) {
            this.parts = parts;
        }

        public List<Part> getParts() {
            return parts;
        }

        public void setParts(List<Part> parts) {
            this.parts = parts;
        }
    }

    public static class Part {
        @SerializedName("text")
        private String text;

        public Part(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}