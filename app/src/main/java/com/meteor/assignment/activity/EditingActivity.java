package com.meteor.assignment.activity;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.meteor.assignment.db.NoteTable;
import com.meteor.assignment.fragment.DeletionAlertDialog;

import java.io.File;

public class EditingActivity extends CreatingActivity implements DeletionAlertDialog.ClickHandler {
    private static final String ALERT_DIALOG_TAG = "Alert dialog";
    //private static final int CHANGE_TWEAK_TYPE = 1;
    //private static final int RECOVERY_TWEAK_TYPE = 2;

    private BottomNavigationView bottomNavigationView;
    private DeletionAlertDialog deletionAlertDialog;

    private int maxNoteID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing);

        initUIViews();
        initUIListeners2();
    }

    @Override
    protected void initUIViews() {
        super.initUIViews();

        bottomNavigationView = findViewById(R.id.bnv_bottomMenu);
        deletionAlertDialog = new DeletionAlertDialog();
    }

    protected void initUIListeners2() {

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.iv_left: {
                        new QueryTask().execute(new Integer(noteID + 2));
                        break;
                    }
                    case R.id.iv_share: {
                        String dataToSend = "Title: " + note.getTitle() + ";\n" +                   //can not call String.join
                                "Content: " + note.getContent() + ";\n" +                           //tested with gmail
                                "Birth time: " + note.getBirthTime() + ";\n" +
                                "Alarm time: " + note.getAlarmTime();
                        Uri uriToSend= Uri.fromFile(new File(note.getImageUrl()));
                        String chooserName="Share note";

                        Intent sharingIntent=new Intent(Intent.ACTION_SEND);
                        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        sharingIntent.putExtra(Intent.EXTRA_TEXT,dataToSend);
                        sharingIntent.setType("image/*");
                        sharingIntent.putExtra(Intent.EXTRA_STREAM,uriToSend);
                        startActivity(Intent.createChooser(sharingIntent, chooserName));
                        break;
                    }
                    case R.id.iv_bin: {
                        getSupportFragmentManager().beginTransaction();
                        deletionAlertDialog.show(getSupportFragmentManager(), ALERT_DIALOG_TAG);
                        break;
                    }
                    case R.id.iv_right: {
                        new QueryTask().execute(new Integer(noteID));                               //noteID-1+1
                        break;
                    }
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
            note = intent.getParcelableExtra(getString(R.string.note_key));
            maxNoteID = intent.getIntExtra(getString(R.string.max_id_key), INVALID_NOTE_ID);

            updateContentUIViews();
            tweakBottomNavigationView();
        }
    }

    private void updateContentUIViews() {
        if (note != null) {
            etTitle.setText(note.getTitle());
            etContent.setText(note.getContent());

            String imageUrl = note.getImageUrl();
            Intent data = new Intent();
            data.putExtra(getString(R.string.note_url_key), imageUrl);
            Log.d("image url:", imageUrl);

            if (!imageUrl.equals("NULL")) {
                if (!imageUrl.startsWith("content://")) {
                    new ImageLoadingTask(INITIAL_LOADING_TYPE_1).execute(data);
                }
                ;
            } else {
                ivImage.setVisibility(View.GONE);
            }
        }
    }

    private void tweakBottomNavigationView() {
        int backItemID = 0;
        int nextItemID = 3;
        int alphaChannel = (noteID == maxNoteID - 1) ? 100 : 255;
        boolean availability = (noteID == maxNoteID - 1) ? false : true;

        bottomNavigationView.getMenu().getItem(backItemID).getIcon().setAlpha(alphaChannel);
        bottomNavigationView.getMenu().getItem(backItemID).setEnabled(availability);

        alphaChannel = (noteID == 0) ? 100 : 255;
        availability = (noteID == 0) ? false : true;

        bottomNavigationView.getMenu().getItem(nextItemID).getIcon().setAlpha(alphaChannel);
        bottomNavigationView.getMenu().getItem(nextItemID).setEnabled(availability);
    }

    public void handleDeletion() {
        deletionAlertDialog.dismiss();

        Intent intent = new Intent();
        intent.putExtra(getString(R.string.note_id_key), noteID);

        setResult(MainActivity.EDITING_DELETE, intent);
        finish();
    }

    public class QueryTask extends AsyncTask<Integer, Void, Boolean> {                              //extends MainActivity.DatabaseTask...
        @Override
        protected Boolean doInBackground(Integer... integers) {
            int rowID = integers[0];

            try {
                Cursor cursor = getContentResolver().query(ContentUris.withAppendedId(NoteTable.getInstance().TABLE_URI, rowID),
                        null, null, null, null);
                if ((cursor != null) && (cursor.getCount() > 0)) {
                    cursor.moveToFirst();

                    note.setTitle(cursor.getString(cursor.getColumnIndex(NoteTable.getInstance().TITLE)));
                    note.setContent(cursor.getString(cursor.getColumnIndex(NoteTable.getInstance().CONTENT)));
                    //note.setBirthTime(cursor.getString(cursor.getColumnIndex(NoteTable.getInstance().BIRTH_TIME)));
                    note.setImageUrl(cursor.getString(cursor.getColumnIndex(NoteTable.getInstance().IMAGE_URL)));
                    note.setAlarmTime(cursor.getString(cursor.getColumnIndex(NoteTable.getInstance().ALARM_TIME)));
                    noteID = integers[0] - 1;

                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                updateTimeView();
                updateContentUIViews();
                tweakBottomNavigationView();
            }
        }
    }
}
