package com.meteor.assignment.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.meteor.assignment.fragment.DeletionAlertDialog;
import com.meteor.assignment.model.Note;

public class EditingActivity extends CreatingActivity implements DeletionAlertDialog.ClickHandler {
    private static final String ALERT_DIALOG_TAG = "Alert dialog";

    private BottomNavigationView bottomNavigationView;
    private DeletionAlertDialog deletionAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing);

        initUIViews();
        initUIListeners();
    }

    @Override
    protected void initUIViews() {
        super.initUIViews();

        bottomNavigationView = findViewById(R.id.bnv_bottomMenu);
        deletionAlertDialog = new DeletionAlertDialog();
    }

    private void initUIListeners() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.iv_bin:
                        getSupportFragmentManager().beginTransaction();
                        deletionAlertDialog.show(getSupportFragmentManager(), ALERT_DIALOG_TAG);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.editingactivity_topmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        super.onOptionsItemSelected(menuItem);

        if (menuItem.getItemId() == R.id.mi_newNote) {
            setResult(MainActivity.EDITING_NEW_NOTE);
            finish();
        }
        return true;
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

                String imageUrl = note.getImageUrl();
                Intent data = new Intent();
                data.putExtra(getString(R.string.note_url_key), imageUrl);

                if (!note.equals("NULL")) {
                    if (!imageUrl.startsWith("content://")) {
                        new ImageLoadingTask(INITIAL_LOADING_TYPE_1).execute(data);
                    } else {
                        new ImageLoadingTask(INITIAL_LOADING_TYPE_2).execute(data);
                    }
                }
            }
        }
    }

    public void handleDeletion() {
        deletionAlertDialog.dismiss();
        setResult(MainActivity.EDITING_DELETE);
        finish();
    }

}
