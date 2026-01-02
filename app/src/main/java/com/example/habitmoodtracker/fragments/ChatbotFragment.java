package com.example.habitmoodtracker.fragments;

import com.example.habitmoodtracker.BuildConfig;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.habitmoodtracker.R;
import com.example.habitmoodtracker.adapters.ChatAdapter;
import com.example.habitmoodtracker.models.ChatMessage;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatbotFragment extends Fragment {

    private static final String TAG = "ChatbotFragment";
    private RecyclerView recyclerViewChat;
    private EditText editTextMessage;
    private ImageButton buttonSend;
    private ProgressBar progressBar;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private GenerativeModelFutures model;
    private Executor executor;




    public ChatbotFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatbot, container, false);

        initViews(view);
        setupRecyclerView();
        setupGeminiAI();
        setupListeners();

        // Welcome message
        addBotMessage("Hi! 👋 I'm your wellness assistant. Ask me anything about health, habits, motivation, or productivity!");

        return view;
    }

    private void initViews(View view) {
        recyclerViewChat = view.findViewById(R.id.recyclerViewChat);
        editTextMessage = view.findViewById(R.id.editTextMessage);
        buttonSend = view.findViewById(R.id.buttonSend);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void setupRecyclerView() {
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewChat.setAdapter(chatAdapter);
    }

    private void setupGeminiAI() {
        try {
            executor = Executors.newSingleThreadExecutor();


            GenerativeModel gm = new GenerativeModel(
                    "gemini-2.5-flash",
                    BuildConfig.GEMINI_API_KEY
            );

            model = GenerativeModelFutures.from(gm);
            Log.d(TAG, "✅ Gemini AI initialized successfully with gemini-2.5-flash model");
        } catch (Exception e) {
            Log.e(TAG, "❌ Error initializing Gemini: " + e.getMessage());
            addBotMessage("⚠️ Error initializing AI: " + e.getMessage());
            buttonSend.setEnabled(false);
        }
    }

    private void setupListeners() {
        buttonSend.setOnClickListener(v -> sendMessage());

        editTextMessage.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void sendMessage() {
        String message = editTextMessage.getText().toString().trim();

        if (message.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        addUserMessage(message);
        editTextMessage.setText("");
        setLoadingState(true);

        try {
            String prompt = "You are a helpful wellness and mental health assistant. " +
                    "Provide supportive, motivational, and helpful advice about: " +
                    "health, wellness, habits, motivation, productivity, exercise, meditation, sleep, and mental wellbeing. " +
                    "Keep responses concise (2-3 sentences) and friendly. " +
                    "User question: " + message;

            Content content = new Content.Builder()
                    .addText(prompt)
                    .build();

            ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    if (getActivity() == null) return;

                    requireActivity().runOnUiThread(() -> {
                        setLoadingState(false);
                        try {
                            String botResponse = result.getText();
                            if (botResponse != null && !botResponse.isEmpty()) {
                                addBotMessage(botResponse);
                                Log.d(TAG, "✅ Response received: " + botResponse);
                            } else {
                                addBotMessage("I received an empty response. Try again! 🤔");
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing response: " + e.getMessage());
                            addBotMessage("Sorry, I had trouble understanding. Try again!");
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    if (getActivity() == null) return;

                    requireActivity().runOnUiThread(() -> {
                        setLoadingState(false);
                        Log.e(TAG, "❌ API Error: " + t.getMessage(), t);

                        String errorMsg = "Sorry, I couldn't process that. ";

                        if (t.getMessage() != null) {
                            if (t.getMessage().contains("API has not been used")) {
                                errorMsg = "⚠️ Please enable the Generative AI API in Google Cloud Console and wait 5 minutes.";
                            } else if (t.getMessage().contains("API key")) {
                                errorMsg = "⚠️ Invalid API key. Please check your Gemini API key.";
                            } else if (t.getMessage().contains("network") || t.getMessage().contains("internet")) {
                                errorMsg = "📡 Please check your internet connection.";
                            } else if (t.getMessage().contains("quota")) {
                                errorMsg = "⚠️ API quota exceeded. Please try again later.";
                            } else if (t.getMessage().contains("NOT_FOUND") || t.getMessage().contains("not found")) {
                                errorMsg = "⚠️ Model not available. Please update to Gemini 2.5 Flash.";
                            } else {
                                errorMsg += "Please try again! 🤔";
                            }
                        }

                        addBotMessage(errorMsg);
                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }, executor);

        } catch (Exception e) {
            setLoadingState(false);
            Log.e(TAG, "Exception sending message: " + e.getMessage(), e);
            addBotMessage("Sorry, something went wrong. Try again! 🤔");
        }
    }

    private void addUserMessage(String message) {
        chatMessages.add(new ChatMessage(message, true));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        recyclerViewChat.smoothScrollToPosition(chatMessages.size() - 1);
    }

    private void addBotMessage(String message) {
        chatMessages.add(new ChatMessage(message, false));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        recyclerViewChat.smoothScrollToPosition(chatMessages.size() - 1);
    }

    private void setLoadingState(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        buttonSend.setEnabled(!isLoading);
        editTextMessage.setEnabled(!isLoading);
    }
}