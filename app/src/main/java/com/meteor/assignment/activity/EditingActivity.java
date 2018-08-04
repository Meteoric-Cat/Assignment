package com.meteor.assignment.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.meteor.assignment.model.Note;

public class EditingActivity extends CreatingActivity {
    private static final String DIALOG_BUNDLE_KEY = "Meteor";

    private static final String ALERT_DIALOG_TAG="Alert dialog";

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
        deletionAlertDialog=DeletionAlertDialog.newInstance(
                getString(R.string.dag_title),
                getString(R.string.dag_message),
                getString(R.string.dag_posButtom),
                getString(R.string.dag_nevButton)
        );
    }

    private void initUIListeners() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.iv_bin:
                        deletionAlertDialog.show(getSupportFragmentManager(), ALERT_DIALOG_TAG);
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

    protected void handleDeletion() {
        setResult(MainActivity.EDITING_DELETE);
        finish();
    }

    protected void cancelDeletion() {

    }

    public static class DeletionAlertDialog extends DialogFragment {
        static DeletionAlertDialog newInstance(String title, String message, String posButtomName, String negButtomName) {
            String[] temp = new String[4];
            temp[0] = title;
            temp[1] = message;
            temp[2] = posButtomName;
            temp[3] = negButtomName;

            Bundle bundle = new Bundle();
            bundle.putStringArray(DIALOG_BUNDLE_KEY, temp);

            DeletionAlertDialog dialog = new DeletionAlertDialog();
            dialog.setArguments(bundle);
            return dialog;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            String[] temp = getArguments().getStringArray(DIALOG_BUNDLE_KEY);

            if (temp.length >= 4) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(temp[0])
                        .setMessage(temp[1])
                        .setPositiveButton(temp[2], new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                ((EditingActivity)getActivity()).handleDeletion();
                            }
                        })
                        .setNegativeButton(temp[3],new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ((EditingActivity)getActivity()).cancelDeletion();
                            }
                        });
                return builder.create();
            }
            return null;
        }
    }
}
