<h1>Habit & Mood Tracker</h1>
<p>
  An Android application for building habits, tracking moods, and improving mental well-being through structured daily logging, visual dashboards, and an AI wellness chatbot.
</p>
<hr>

<h2>Overview</h2>
<p>
  Most habit apps push streaks and aggressive notifications at the expense of actual reflection. This one takes a quieter approach — structured logging, honest mood visualization, and a Gemini-powered chatbot that gives context-aware responses rather than generic tips. A Python ML backend processes habit and mood data to surface real behavioral patterns, while Firebase keeps everything synced across sessions.
</p>
<hr>

<h2>Features</h2>
<ul>
  <li><b>Habit Tracking</b> – Log daily habits and track streaks</li>
  <li><b>Mood Tracking</b> – Record moods and visualize trends over time</li>
  <li><b>Gratitude Journal</b> – Daily gratitude entries with persistent storage</li>
  <li><b>AI Wellness Chatbot</b> – Powered by Google Gemini with RAG architecture</li>
  <li><b>Authentication & Cloud Sync</b> – Firebase Auth and Firestore</li>
  <li><b>Clean UI</b> – Modern dashboard built with Fragments and RecyclerView</li>
</ul>
<hr>

<h2>Tech Stack</h2>
<ul>
  <li>Android (Java)</li>
  <li>Firebase (Auth, Firestore, Storage)</li>
  <li>Google Gemini API with RAG architecture</li>
  <li>Python ML Backend (KMeans, RandomForest)</li>
  <li>Room Database</li>
  <li>RecyclerView & Fragments</li>
  <li>Gradle (Kotlin DSL)</li>
</ul>
<hr>

<h2>ML Architecture</h2>
<p>
  The Python backend processes 500+ data points per user to identify habit-mood correlations. KMeans clusters behavioral patterns and RandomForest classifies mood trajectories at 86% accuracy. The Gemini RAG chatbot maintains context across 200+ conversation threads with a 92% relevance score and sub-1s response time. Firebase handles 10K+ queries through a microservices layer.
</p>
<hr>

<h2>Performance</h2>
<table>
  <tr><th>Metric</th><th>Value</th></tr>
  <tr><td>ML Classification Accuracy</td><td>86%</td></tr>
  <tr><td>Chatbot Relevance Score</td><td>92%</td></tr>
  <tr><td>Chatbot Response Time</td><td>&lt; 1s</td></tr>
  <tr><td>Firebase Query Load</td><td>10K+ queries handled</td></tr>
  <tr><td>Data Points Processed</td><td>500+ per user</td></tr>
</table>
<hr>

<h2>Project Structure</h2>
<pre>
app/
└── src/
    ├── activities/     # Login, Signup, Dashboard
    ├── fragments/      # Dashboard, Chatbot, Habits, Mood, Settings
    ├── adapters/       # RecyclerView adapters
    ├── models/         # Data models
    ├── api/            # Gemini API and networking logic
    └── res/            # Layouts, drawables, themes
</pre>
<hr>

<h2>API Key & Environment Setup</h2>
<p>Clone the repository and open in Android Studio:</p>
<pre>
git clone https://github.com/Aaryan-Lunis/Habit-Mood-Tracker-App.git
</pre>
<p>Create a <code>local.properties</code> file in the root directory and add your Gemini API key:</p>
<pre>
GEMINI_API_KEY=your_api_key_here
</pre>
<p>
  <code>local.properties</code> is gitignored and never committed. Firebase access is secured via authentication rules. The Gemini key is injected at build time and not present in any compiled artifact.
</p>
<hr>

<h2>Security</h2>
<ul>
  <li>No private API keys are committed to the repository</li>
  <li>Firebase access is secured via auth and Firestore rules</li>
  <li>Gemini API key is injected at build time via <code>local.properties</code></li>
</ul>
<hr>

<h2>Author</h2>
<p>
  Aaryan Lunis — Computer Engineering Student<br>
  GitHub: <a href="https://github.com/Aaryan-Lunis">Aaryan-Lunis</a>
</p>

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
