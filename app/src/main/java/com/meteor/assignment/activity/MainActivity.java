package com.meteor.assignment.activity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.meteor.assignment.adapter.MainActivityRvAdapter;
import com.meteor.assignment.configuration.ActivityConfiguration;
import com.meteor.assignment.db.NoteTable;
import com.meteor.assignment.model.Note;

public class MainActivity extends AppCompatActivity {
    public static final int CREATING_REQUEST = 1;
    public static final int CREATING_OK = 1;
    public static final int CREATING_FAIL = 2;

    public static final int EDITING_REQUEST = 2;
    public static final int EDITING_OK = 1;
    public static final int EDITING_FAIL = 2;
    public static final int EDITING_NEW_NOTE = 3;
    public static final int EDITING_DELETE = 4;

    private static final int SPAN_COUNT = 2;

    private static final int QUERY_TYPE = 0;
    private static final int INSERT_TYPE = 1;
    private static final int DELETE_TYPE = 2;
    private static final int UPDATE_TYPE = 3;

    private static final int MIN_ID = 0;

    private static final String QUERY_ERROR_DISPLAY = "No note";
    private static final String INSERT_ERROR_DISPLAY = "You can't create a note";                     //display to user

    private RecyclerView rvNotes;
    private MainActivityRvAdapter rvAdapter;
    private RecyclerView.LayoutManager rvLayoutManager;

    private int clickedNoteID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUIViews();
        initUIListeners();
    }

    private void initUIViews() {
        rvNotes = findViewById(R.id.rv_notes);
        rvAdapter = new MainActivityRvAdapter();
        rvLayoutManager = new GridLayoutManager(this, SPAN_COUNT);

        rvNotes.setLayoutManager(rvLayoutManager);
        rvNotes.setAdapter(rvAdapter);

        DatabaseTask initialTask = new DatabaseTask(QUERY_TYPE, MIN_ID, null,
                false, NoteTable.getInstance().TABLE_URI);
        initialTask.execute();
    }

    private void initUIListeners() {
        rvNotes.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                if (child != null) {
                    MainActivityRvAdapter.CustomViewHolder customViewHolder =
                            (MainActivityRvAdapter.CustomViewHolder) recyclerView.getChildViewHolder(child);
                    clickedNoteID = customViewHolder.getDataID();

                    Intent intent = new Intent(MainActivity.this, EditingActivity.class);
                    intent.putExtra(getString(R.string.note_key), rvAdapter.getItem(clickedNoteID));
                    intent.putExtra(getString(R.string.note_id_key), clickedNoteID);
                    intent.putExtra(getString(R.string.max_id_key), rvAdapter.getItemCount());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    startActivityForResult(intent, EDITING_REQUEST);
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainactivity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().setBackgroundDrawable(ActivityConfiguration.getInstance().windowBackground);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.mi_add) {
            Intent intent = new Intent(this, CreatingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(getString(R.string.note_id_key), rvAdapter.getItemCount());
            startActivityForResult(intent, getResources().getInteger(R.integer.CREATING_REQUEST));
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CREATING_REQUEST: {
                if (resultCode == getResources().getInteger(R.integer.CREATING_OK)) {
                    Note note = data.getParcelableExtra(getString(R.string.note_key));
                    if (note != null) {
                        DatabaseTask databaseTask = new DatabaseTask(INSERT_TYPE, MIN_ID, note,
                                false, NoteTable.getInstance().TABLE_URI);
                        databaseTask.execute();
                    }
                    //else...
                }
                break;
            }
            case EDITING_REQUEST: {
                if (data != null) {
                    clickedNoteID = data.getIntExtra(getString(R.string.note_id_key), clickedNoteID);
                }
                switch (resultCode) {
                    case CREATING_OK: {
                        if (data != null) {
                            Note note = data.getParcelableExtra(getString(R.string.note_key));

                            if (note != null) {
                                DatabaseTask databaseTask = new DatabaseTask(UPDATE_TYPE, clickedNoteID + 1,
                                        note, false, NoteTable.getInstance().TABLE_URI);
                                databaseTask.execute();
                            }
                        }
                        break;
                    }
                    case EDITING_DELETE: {
                        DatabaseTask databaseTask = new DatabaseTask(DELETE_TYPE, clickedNoteID + 1,
                                null, false, NoteTable.getInstance().TABLE_URI);
                        databaseTask.execute();
                        //Log.d("TASK:","executed");
                        break;
                    }
                    case EDITING_NEW_NOTE: {
                        Intent intent = new Intent(this, CreatingActivity.class);
                        intent.putExtra(getString(R.string.note_id_key), rvAdapter.getItemCount());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, CREATING_REQUEST);
                        break;
                    }
                }
                break;
            }
        }
    }

    public class DatabaseTask extends AsyncTask<Void, Void, Cursor> {
        private int taskType;
        private int rowID;
        private Note note;
        private boolean resultFlag;
        private Uri databaseUri;
        //projection,selection, selectionArg,...

        public DatabaseTask(int taskType, int rowID, Note note, boolean resultFlag, Uri databaseUri) {
            super();

            this.taskType = taskType;
            this.rowID = rowID;
            this.note = note;
            this.resultFlag = resultFlag;
            this.databaseUri = databaseUri;
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            Cursor result = null;
            resultFlag = false;
            if (this.rowID > MIN_ID) {
                this.databaseUri = ContentUris.withAppendedId(this.databaseUri, rowID);
                //Log.d("URI:",this.databaseUri.toString());
            }

            switch (this.taskType) {
                case QUERY_TYPE: {
                    result = handleQuery();
                    break;
                }
                case INSERT_TYPE: {
                    handleInsertion();
                    break;
                }
                case DELETE_TYPE: {
                    handleDeletion();
                    break;
                }
                case UPDATE_TYPE: {
                    handleUpdate();
                    break;
                }
            }
            return result;
        }

        private Cursor handleQuery() {
            Cursor result = null;
            try {
                result = getContentResolver().query(this.databaseUri, null, null, null, null);
                resultFlag = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        private void handleInsertion() {
            try {
                getContentResolver().insert(this.databaseUri, this.getContentValuesFromNote(note));
                resultFlag = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void handleDeletion() {
            int amount = 0;
            try {
                amount = getContentResolver().delete(this.databaseUri, null, null);
                resultFlag = true;
                //Log.d("Amount:",String.valueOf(amount));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (amount == 0) {
                resultFlag = false;
            }
        }

        private void handleUpdate() {
            int amount = 0;
            try {
                amount = getContentResolver().update(this.databaseUri, this.getContentValuesFromNote(note), null, null);
                resultFlag = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (amount == 0) {
                resultFlag = false;
            }
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            switch (this.taskType) {
                case QUERY_TYPE: {
                    displayQueryResult(cursor);
                    break;
                }
                case INSERT_TYPE: {
                    displayInsertionResult();
                    break;
                }
                case DELETE_TYPE: {
                    displayDeletionResult();
                    break;
                }
                case UPDATE_TYPE: {
                    displayUpdateResult();
                    break;
                }

            }
        }

        private void displayQueryResult(Cursor cursor) {
            if (resultFlag) {
                //check uri if many tables is existing
                if (rowID == MIN_ID) {
                    if ((cursor != null) && (cursor.getCount() > 0)) {
                        while (cursor.moveToNext()) {
                            rvAdapter.addItem(new Note(
                                    cursor.getString(cursor.getColumnIndex(NoteTable.getInstance().TITLE)),
                                    cursor.getString(cursor.getColumnIndex(NoteTable.getInstance().CONTENT)),
                                    cursor.getString(cursor.getColumnIndex(NoteTable.getInstance().BIRTH_TIME)),
                                    cursor.getString(cursor.getColumnIndex(NoteTable.getInstance().IMAGE_URL)),
                                    cursor.getString(cursor.getColumnIndex(NoteTable.getInstance().ALARM_TIME))
                            ));
                        }
                    }
                }
            }
        }

        private void displayInsertionResult() {
            if (resultFlag) {
                //check uri
                if (rowID == MIN_ID) {
                    rvAdapter.addItem(this.note);
                } else {
                    Toast.makeText(getApplicationContext(), INSERT_ERROR_DISPLAY, Toast.LENGTH_SHORT).show();
                }
            }
        }

        private void displayDeletionResult() {
            if (resultFlag) {
                //check uri
                if (rowID > MIN_ID) {
                    rvAdapter.removeItem(rowID - 1);
                    Log.d("RESULT:", "remove successfully");
                }
                //else ... (remove all items but that event won't happen
            }
        }

        private void displayUpdateResult() {
            if (resultFlag) {
                //check uri
                if (rowID > MIN_ID) {
                    rvAdapter.updateItem(rowID - 1, note);
                }
            }
        }

        private ContentValues getContentValuesFromNote(Note note) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(NoteTable.getInstance().TITLE, this.note.getTitle());
            contentValues.put(NoteTable.getInstance().CONTENT, this.note.getContent());
            contentValues.put(NoteTable.getInstance().BIRTH_TIME, this.note.getBirthTime());
            contentValues.put(NoteTable.getInstance().IMAGE_URL, this.note.getImageUrl());
            contentValues.put(NoteTable.getInstance().ALARM_TIME, this.note.getAlarmTime());
            return contentValues;
        }
    }

}
