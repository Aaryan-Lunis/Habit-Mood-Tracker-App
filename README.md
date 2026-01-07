<h1>📱 Habit & Mood Tracker App</h1>

<p>
An Android application designed to help users build better habits, track moods, and improve mental well-being through structured logging, insightful dashboards, and an AI-powered wellness chatbot.
</p>

<hr>

<h2>✨ Features</h2>

<ul>
  <li><b>📊 Habit Tracking</b> – Log daily habits and track streaks</li>
  <li><b>😊 Mood Tracking</b> – Record moods and visualize trends</li>
  <li><b>🙏 Gratitude Journal</b> – Daily gratitude entries</li>
  <li><b>🤖 AI Wellness Chatbot</b> – Powered by Google Gemini AI</li>
  <li><b>🔐 Authentication & Cloud Sync</b> – Firebase Auth & Firestore</li>
  <li><b>🎨 Clean UI</b> – Modern dashboard with fragments</li>
</ul>

<hr>

<h2>🛠 Tech Stack</h2>

<ul>
  <li>Android (Java)</li>
  <li>Firebase (Auth, Firestore, Storage)</li>
  <li>Google Gemini AI API</li>
  <li>Room Database</li>
  <li>RecyclerView & Fragments</li>
  <li>Gradle (Kotlin DSL)</li>
</ul>

<hr>

<h2>📂 Project Structure</h2>

<pre>
app/
 ├── activities/        # Login, Signup, Dashboard
 ├── fragments/         # Dashboard, Chatbot, Habits, Mood, Settings
 ├── adapters/          # RecyclerView adapters
 ├── models/            # Data models
 ├── api/               # API & networking logic
 └── res/               # Layouts, drawables, themes
</pre>

<hr>

<h2>🔐 API Key & Environment Setup</h2>

<ol>
  <li>Clone the repository</li>
</ol>

<pre>
git clone https://github.com/Aaryan-Lunis/Habit-Mood-Tracker-App.git
</pre>

<ol start="2">
  <li>Open the project in Android Studio</li>
  <li>Create a <code>local.properties</code> file</li>
  <li>Add your Gemini API key</li>
</ol>

<pre>
GEMINI_API_KEY=your_api_key_here
</pre>

<p>
<b>Note:</b> <code>local.properties</code> is ignored by Git to prevent secret exposure.
</p>

<hr>

<h2>🔒 Security Notes</h2>

<ul>
  <li>No private API keys are committed</li>
  <li>Firebase access is secured via auth & rules</li>
  <li>Gemini API key is injected at build time</li>
</ul>

<hr>

<h2>🚀 Future Enhancements</h2>

<ul>
  <li>Advanced habit–mood analytics</li>
  <li>Personalized AI recommendations</li>
  <li>Push notifications</li>
  <li>Dark mode & themes</li>
  <li>Data export & insights</li>
</ul>

<hr>

<h2>👤 Author</h2>

<p>
<b>Aaryan Lunis</b><br>
Computer Engineering Student | Android & AI Enthusiast<br>
GitHub: <a href="https://github.com/Aaryan-Lunis">https://github.com/Aaryan-Lunis</a>
</p>

<p>⭐ If you find this project interesting, feel free to star the repository!</p>
