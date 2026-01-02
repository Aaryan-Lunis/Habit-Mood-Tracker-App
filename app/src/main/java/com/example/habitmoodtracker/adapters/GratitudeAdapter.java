package com.example.habitmoodtracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.habitmoodtracker.R;
import com.example.habitmoodtracker.models.GratitudeEntry;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class GratitudeAdapter extends RecyclerView.Adapter<GratitudeAdapter.ViewHolder> {

    private List<GratitudeEntry> entries;
    private OnDeleteClickListener deleteListener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault());

    public interface OnDeleteClickListener {
        void onDelete(GratitudeEntry entry);
    }

    public GratitudeAdapter(List<GratitudeEntry> entries, OnDeleteClickListener deleteListener) {
        this.entries = entries;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gratitude, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GratitudeEntry entry = entries.get(position);
        holder.bind(entry);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewGratitude;
        TextView textViewTimestamp;
        ImageButton buttonDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewGratitude = itemView.findViewById(R.id.textViewGratitude);
            textViewTimestamp = itemView.findViewById(R.id.textViewTimestamp);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }

        public void bind(GratitudeEntry entry) {
            textViewGratitude.setText(entry.getText());
            textViewTimestamp.setText(dateFormat.format(entry.getDate()));

            buttonDelete.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDelete(entry);
                }
            });
        }
    }
}