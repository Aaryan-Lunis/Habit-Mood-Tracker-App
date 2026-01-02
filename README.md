# 📱 Habit & Mood Tracker App

An Android application designed to help users build better habits, track moods, and improve mental well-being through structured logging, insightful dashboards, and an AI-powered wellness chatbot.

---

## ✨ Features

- 📊 **Habit Tracking**
  - Log daily habits and activities
  - Track consistency and streaks over time

- 😊 **Mood Tracking**
  - Record daily mood states
  - Visualize emotional patterns and trends

- 🙏 **Gratitude Journal**
  - Write and store daily gratitude entries
  - Encourage positive reflection and mindfulness

- 🤖 **AI Wellness Chatbot**
  - Powered by **Google Gemini AI**
  - Provides motivational, productivity, and wellness guidance
  - Context-aware and friendly responses

- 🔐 **Authentication & Cloud Sync**
  - Firebase Authentication for secure login/signup
  - Cloud-based data storage with Firestore

- 🎨 **Clean & Modern UI**
  - Dashboard with statistics and streaks
  - Intuitive navigation using fragments

---

## 🛠 Tech Stack

- **Android** (Java)
- **Firebase**
  - Authentication
  - Firestore Database
  - Cloud Storage
- **Gemini AI API** (for chatbot)
- **Room Database** (local persistence)
- **RecyclerView & Fragments**
- **Gradle (Kotlin DSL)**

---

## 📂 Project Structure

app/
├── activities/ # Main activities (Login, Signup, Dashboard)
├── fragments/ # Dashboard, Chatbot, Habits, Mood, Settings
├── adapters/ # RecyclerView adapters
├── models/ # Data models
├── api/ # API & networking logic
└── res/ # UI layouts, drawables, themes
---
## 🔐 API Key & Environment Setup

This project uses external APIs that are **not included in the repository** for security reasons.

### Steps to run locally:

1. Clone the repository
   ```bash
   git clone https://github.com/Aaryan-Lunis/Habit-Mood-Tracker-App.git
Open the project in Android Studio

Create a local.properties file in the root directory

Add your Gemini API key:

properties
Copy code
GEMINI_API_KEY=your_api_key_here
Sync Gradle and run the app

⚠️ local.properties is intentionally ignored by Git to prevent secret exposure.

🔒 Security Notes
No private API keys are committed to the repository

Firebase access is controlled via authentication and security rules

Gemini API key is injected securely at build time

🚀 Future Enhancements
Advanced analytics for habit and mood correlations

Personalized AI recommendations based on user history

Push notifications for habit reminders

Dark mode customization and themes

Data export & insights dashboard

👤 Author
Aaryan Lunis
Computer Engineering Student | Android & AI Enthusiast

GitHub: https://github.com/Aaryan-Lunis

⭐ If you find this project interesting, feel free to star the repository!
