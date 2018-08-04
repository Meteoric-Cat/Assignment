package com.meteor.assignment.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.meteor.assignment.model.Note;

public class EditingActivity extends CreatingActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing);

        initUIViews();
    }

    @Override
    protected void initUIViews() {
        super.initUIViews();

        bottomNavigationView = findViewById(R.id.bnv_bottomMenu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.editingactivity_topmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        if (intent != null) {
            Note note = intent.getParcelableExtra(getString(R.string.note_key));
            if (note != null) {
                etTitle.setText(note.getTitle());
                etContent.setText(note.getContent());
            }
        }
    }
}
