
---

# Study Tracker

**Study Tracker** is an Android application designed to help students log, track, and analyze their study sessions. Built with Android Native (Java, Groovy DSL, min SDK 24), it provides a user-friendly interface to manage study activities, filter sessions, visualize study time distribution, and generate AI-powered learning suggestions using the Gemini API. The app follows the MVVM architecture, leveraging Room for local storage, Retrofit for API integration, and MPAndroidChart for data visualization.

## Demo
Watch the app in action: [Study Tracker Demo](https://youtu.be/EKcoCYHUSvI)

Q1 DEMO
<img width="378" height="803" alt="image" src="https://github.com/user-attachments/assets/804b99b3-8371-4371-b564-bd76e2935fe7" />

Q2 DEMO
<img width="379" height="796" alt="image" src="https://github.com/user-attachments/assets/74039f55-a080-4e0e-8659-b6884c7c3a98" />

Q3 DEMO
<img width="376" height="803" alt="image" src="https://github.com/user-attachments/assets/87c0bc5c-d9a3-46eb-b39b-7438db2be41f" />

Q4 DEMO
<img width="373" height="800" alt="image" src="https://github.com/user-attachments/assets/a2f069d3-e7c3-429d-af21-6deb6d8ea3ec" />

Q5 DEMO
<img width="381" height="798" alt="image" src="https://github.com/user-attachments/assets/9c3ebd12-ae0c-40ca-aacf-13fbcac50b20" />

## Features

### 1. Home Screen: Study Summary & Session List
- Displays a summary of total study time, most studied subject, and average focus score for the current week.
- Lists study sessions in a `RecyclerView`, showing subject name, icon, date, duration, and focus level (1-5 stars).
- Data is fetched from a mock API (`https://687319aac75558e273535336.mockapi.io/api/subjects`) and stored in SQLite using Room.
- Uses MVVM with `LiveData` for real-time updates.

### 2. Add New Study Session
- Allows users to add a new study session via `AddSessionActivity`.
- Input fields: subject (Spinner), date (DatePickerDialog), duration (minutes), focus level (RatingBar), and optional notes.
- Validates subject (non-empty) and duration (> 0).
- Sends data to the API via POST and saves to SQLite.

### 3. Multi-Criteria Filtering
- Provides a filter bar in `MainActivity` to filter sessions by:
  - Subject (Spinner).
  - Focus level range (min-max).
  - Date range (from-to).
  - Notes search (text).
- Includes a "Clear Filters" button to reset filters.
- Filtering is handled via custom SQLite queries in `StudySessionDao`.

### 4. Weekly Study Chart
- Displays a pie chart in `DataVisualizationActivity` showing study time distribution by subject for the past 7 days.
- Uses MPAndroidChart to render the chart, with each slice labeled with subject name and percentage.
- Data is aggregated from SQLite using `StudySessionDao`.

### 5. AI-Powered Learning Suggestions
- Analyzes notes of low-focus sessions (level ≤ 3) using the Gemini API (`https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent`).
- Displays suggestions in `LearningSuggestionsActivity` via a `RecyclerView`, showing subject and AI-generated advice (e.g., "Mathematics: Focus on calculus exercises").
- Prompt sent to Gemini API extracts keywords and provides actionable learning tips.

## Installation

1. **Clone the repository**:
   ```bash
   git clone <repository_url>
   ```

2. **Set up Android Studio**:
   - Open the project in Android Studio (version Arctic Fox or later).
   - Ensure the Android SDK (API 24 or higher) is installed.

3. **Add Gemini API Key**:
   - Store your Gemini API key in `res/values/strings.xml`:
     ```xml
     <string name="gemini_api_key">YOUR_API_KEY_HERE</string>
     ```
   - **Note**: For production, use `local.properties` or a secure backend to store the API key.

4. **Sync and Build**:
   - Sync the project with Gradle (`File > Sync Project with Gradle Files`).
   - Build the project (`Build > Rebuild Project`).

5. **Run the App**:
   - Connect an Android device or emulator (API 24 or higher).
   - Run the app (`Run > Run 'app'`).

## Dependencies

- **AndroidX**: AppCompat, ConstraintLayout, RecyclerView, Lifecycle, Room
- **Retrofit**: For API calls
- **MPAndroidChart**: For pie chart visualization
- **Material Design**: For UI components
- **Gemini API**: For AI-powered learning suggestions

## Usage

1. **Home Screen**:
   - View study summary and session list.
   - Use the filter bar to refine sessions by subject, focus level, date, or notes.
   - Press "Clear Filters" to reset.
   - Click "View Study Chart" to see a pie chart of study time distribution.
   - Click "Analyze with AI" to view AI-generated learning suggestions.
   - Click the Floating Action Button to add a new session.

2. **Add Session**:
   - Input study details and save to add a session to the database and API.

3. **Learning Suggestions**:
   - Displays AI-generated suggestions for low-focus sessions in a `RecyclerView`.

## Project Structure

- **Model**: `StudySession` (Room entity for study sessions).
- **Database**: `AppDatabase`, `StudySessionDao` (Room for SQLite operations).
- **Network**: `ApiService`, `RetrofitClient` (API integration with mock API and Gemini API).
- **ViewModel**: `MainViewModel` (manages data and business logic).
- **Adapter**: `StudySessionAdapter`, `SuggestionAdapter` (for RecyclerView).
- **Activities**: `MainActivity`, `AddSessionActivity`, `DataVisualizationActivity`, `LearningSuggestionsActivity`.

## Screenshots

- **MainActivity**: Filter bar, study summary, session list, and buttons for chart and AI analysis.
- **AddSessionActivity**: Form with subject, date, duration, focus level, and notes inputs.
- **DataVisualizationActivity**: Pie chart showing study time distribution by subject.
- **LearningSuggestionsActivity**: List of AI-generated suggestions for low-focus sessions.

## Video Demo
[Watch the Study Tracker Demo](https://youtu.be/EKcoCYHUSvI)

## Notes

- The Gemini API key is stored in `strings.xml` for development purposes. For production, use `local.properties` or a secure backend to avoid exposing the key.
- Ensure the mock API (`https://687319aac75558e273535336.mockapi.io/api/subjects`) is accessible and contains sessions with low focus levels (≤ 3) for AI analysis.
- The app supports both light and dark themes using Material Design 3.

## License
This project is licensed under the MIT License.

---
