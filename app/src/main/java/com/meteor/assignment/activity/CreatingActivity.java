package com.meteor.assignment.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.meteor.assignment.model.Note;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreatingActivity extends AppCompatActivity {
    protected static final String INVALID_INPUT = "";
    protected static final String DMY_FORMAT = "dd/MM/yyyy";
    protected static final String HM_FORMAT = "HH:mm";

    protected TextView tvTime, tvAlarm;
    protected EditText etTitle, etContent;

    protected String alarmTime, imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating);

        initUIViews();
        initLogicComponents();
    }

    protected void initUIViews() {
        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);

        tvTime = findViewById(R.id.tv_time);
        tvAlarm = findViewById(R.id.tv_alarm);
    }

    protected void initLogicComponents() {
        alarmTime = "NULL";
        imageUrl = "NULL";
    }

    @Override
    protected void onStart() {
        super.onStart();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DMY_FORMAT + " " + HM_FORMAT);
        tvTime.setText(simpleDateFormat.format(calendar.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.creatingactivity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_check: {
                String title=etTitle.getText().toString().trim();
                String content=etContent.getText().toString().trim();

                if (TextUtils.isEmpty(title) && TextUtils.isEmpty(content)) {
                    setResult(getResources().getInteger(R.integer.CREATING_FAIL));
                    finish();
                }

                Intent intent = new Intent();
                Note note = new Note(title, content,
                        tvTime.getText().toString(), imageUrl, alarmTime);

                if (etTitle.getText().toString().equals(INVALID_INPUT)) {
                    note.setTitle(note.getContent());
                }

                intent.putExtra(getString(R.string.note_key), note);
                setResult(getResources().getInteger(R.integer.CREATING_OK), intent);
                finish();
            }
        }
        return true;
    }
}
