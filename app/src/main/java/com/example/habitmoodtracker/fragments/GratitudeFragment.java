package com.example.habitmoodtracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.habitmoodtracker.AuthManager;
import com.example.habitmoodtracker.R;
import com.example.habitmoodtracker.adapters.GratitudeAdapter;
import com.example.habitmoodtracker.models.GratitudeEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GratitudeFragment extends Fragment {

    private RecyclerView recyclerViewGratitude;
    private EditText editTextGratitude;
    private FloatingActionButton fabAdd;
    private GratitudeAdapter adapter;
    private List<GratitudeEntry> gratitudeList;
    private DatabaseReference databaseReference;
    private AuthManager authManager;

    public GratitudeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gratitude, container, false);

        authManager = AuthManager.getInstance(requireContext());
        initViews(view);
        setupFirebase();
        setupRecyclerView();
        setupListeners();
        loadGratitudeEntries();

        return view;
    }

    private void initViews(View view) {
        recyclerViewGratitude = view.findViewById(R.id.recyclerViewGratitude);
        editTextGratitude = view.findViewById(R.id.editTextGratitude);
        fabAdd = view.findViewById(R.id.fabAdd);
    }

    private void setupFirebase() {
        String userId = authManager.getCurrentUserId();
        if (userId != null) {
            databaseReference = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(userId)
                    .child("gratitude_entries");
        }
    }

    private void setupRecyclerView() {
        gratitudeList = new ArrayList<>();
        adapter = new GratitudeAdapter(gratitudeList, this::deleteEntry);
        recyclerViewGratitude.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewGratitude.setAdapter(adapter);
    }

    private void setupListeners() {
        fabAdd.setOnClickListener(v -> addGratitudeEntry());
    }

    private void addGratitudeEntry() {
        String text = editTextGratitude.getText().toString().trim();

        if (text.isEmpty()) {
            Toast.makeText(getContext(), "Please write something 💭", Toast.LENGTH_SHORT).show();
            return;
        }

        GratitudeEntry entry = new GratitudeEntry(text);

        if (databaseReference != null) {
            databaseReference.child(entry.getId()).setValue(entry)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "✨ Gratitude saved!", Toast.LENGTH_SHORT).show();
                        editTextGratitude.setText("");
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void loadGratitudeEntries() {
        if (databaseReference == null) {
            Toast.makeText(getContext(), "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gratitudeList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    GratitudeEntry entry = dataSnapshot.getValue(GratitudeEntry.class);
                    if (entry != null) {
                        gratitudeList.add(entry);
                    }
                }
                // Sort by timestamp descending (newest first)
                Collections.sort(gratitudeList, (a, b) ->
                        Long.compare(b.getTimestamp(), a.getTimestamp()));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error loading entries", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteEntry(GratitudeEntry entry) {
        if (databaseReference != null && entry.getId() != null) {
            databaseReference.child(entry.getId()).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Entry deleted", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to delete", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}