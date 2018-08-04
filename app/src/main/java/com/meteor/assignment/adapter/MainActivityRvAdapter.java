package com.meteor.assignment.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meteor.assignment.activity.R;
import com.meteor.assignment.model.Note;

import java.util.LinkedList;

public class MainActivityRvAdapter extends RecyclerView.Adapter<MainActivityRvAdapter.CustomViewHolder> {
    private static final String INVALID_ALARM = "NULL";

    private LinkedList<Note> noteList;
    private int noteListSize;

    public MainActivityRvAdapter() {
        super();
        noteList = new LinkedList<Note>();
        noteListSize = 0;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RelativeLayout rlItem = (RelativeLayout) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.note_list_item, viewGroup, false);
        return new CustomViewHolder(rlItem);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder customViewHolder, int position) {
        customViewHolder.setData(this.noteList.get(this.noteListSize - position - 1));
    }

    @Override
    public int getItemCount() {
        return this.noteListSize;
    }

    public void addItem(Note note) {
        this.noteList.add(note);
        this.notifyItemInserted(0);
        this.noteListSize++;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rlItem;
        TextView tvNoteTitle, tvNoteContent, tvNoteTime;
        ImageView ivClockIcon;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            rlItem = (RelativeLayout) itemView;

            tvNoteTitle = itemView.findViewById(R.id.tv_noteTitle);
            tvNoteContent = itemView.findViewById(R.id.tv_noteContent);
            tvNoteTime = itemView.findViewById(R.id.tv_noteTime);

            ivClockIcon = itemView.findViewById(R.id.iv_clockIcon);
        }

        public void setData(Note note) {
            tvNoteTitle.setText(note.getTitle());
            tvNoteContent.setText(note.getContent());
            tvNoteTime.setText(note.getBirthTime());

            if (note.getAlarmTime().equals(INVALID_ALARM))
                ivClockIcon.setVisibility(View.GONE);
            else
                ivClockIcon.setVisibility(View.VISIBLE);
        }
    }
}
