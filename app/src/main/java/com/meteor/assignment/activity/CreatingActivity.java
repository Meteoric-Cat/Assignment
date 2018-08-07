package com.meteor.assignment.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.meteor.assignment.configuration.ActivityConfiguration;
import com.meteor.assignment.fragment.BackgroundColorDialog;
import com.meteor.assignment.fragment.CameraOptionDialog;
import com.meteor.assignment.model.Note;
import com.meteor.assignment.service.AlarmNotificationReceiver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;

public class CreatingActivity extends AppCompatActivity implements CameraOptionDialog.ClickHandler {
    protected static final String INVALID_INPUT = "";
    protected static final String DMY_FORMAT = "dd/MM/yyyy";
    protected static final String HM_FORMAT = "HH:mm";

    protected static final String CAMERA_OPTION_DIALOG = "Camera dialog";
    protected static final String BACKGROUND_COLOR_DIALOG = "Background dialog";

    protected static final int INITIAL_LOADING_TYPE_1 = 1;
    protected static final int INITIAL_LOADING_TYPE_2 = 2;
    protected static final int CAMERA_LOADING_TYPE = 3;
    protected static final int GALLERY_LOADING_TYPE = 4;

    protected static final String IMAGE_LOADING_EXCEPTION = "Can't load the image";                 //display to user
    //protected static final String IMAGE_TAKING_EXCEPTION = "Can't create image with camera";
    protected static final String IMAGE_LOADING_ANNOUNCEMENT = "Reopen to see the added image.";

    protected static final int INVALID_NOTE_ID = -1;

    protected static final int IMAGE_WIDTH = 200;
    protected static final int IMAGE_HEIGHT = 200;

    protected static final int TODAY_POSITION = 0;
    protected static final int TOMORROW_POSITION = 1;
    protected static final int NEXT_WEEK_POSITION = 2;
    protected static final int OTHER_POSITION = 3;

    protected TextView tvTime, tvAlarm;
    protected EditText etTitle, etContent;
    protected ImageView ivImage, ivSetterClose;
    protected CustomSpinner spDMYPicker, spHMPicker;

    protected CameraOptionDialog cameraOptionDialog;
    protected BackgroundColorDialog backgroundColorDialog;

    protected LinkedList<String> dateData, timeData;
    protected ArrayAdapter<String> dateAdapter, timeAdapter;
    protected DatePicker datePicker;
    protected DatePickerDialog datePickerDialog;
    protected TimePicker timePicker;
    protected TimePickerDialog timePickerDialog;

    protected Note note;
    protected int noteID;
    protected int maxNoteID;
    protected int startCount;
    //protected boolean childCheck=false;                                                           //use instead of instanceof function

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!(this instanceof EditingActivity)) {                                                   //or getClass to check
            setContentView(R.layout.activity_creating);
        } else {
            setContentView(R.layout.activity_editing);
        }

        Log.d("PARENT:", "START");
        initUIViews();
        initUIListeners();
        initLogicComponents();
    }

    protected void initUIViews() {
        Log.d("PARENT:", "GO HERE");
        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
        tvTime = findViewById(R.id.tv_time);
        tvAlarm = findViewById(R.id.tv_alarm);
        ivImage = findViewById(R.id.iv_photo);

        initAlarmGroup();

        cameraOptionDialog = new CameraOptionDialog();
        backgroundColorDialog = new BackgroundColorDialog();

        datePickerDialog = new DatePickerDialog(this);
        datePicker = datePickerDialog.getDatePicker();

        timePicker = new TimePicker(this);
        timePicker.setIs24HourView(true);
        timePickerDialog = new TimePickerDialog(this, null, 0, 0, true);
        timePickerDialog.setView(timePicker);
    }

    protected void initAlarmGroup() {
        String[] values = getResources().getStringArray(R.array.sp_dmyPicker);
        dateData = new LinkedList<>();
        for (String temp : values) {
            dateData.add(temp);
        }

        dateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dateData);
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDMYPicker = findViewById(R.id.sp_dmyPicker);
        spDMYPicker.setAdapter(dateAdapter);

        values = getResources().getStringArray(R.array.sp_hmPicker);
        timeData = new LinkedList<>();
        for (String temp : values) {
            timeData.add(temp);
        }

        timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, timeData);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spHMPicker = findViewById(R.id.sp_hmPicker);
        spHMPicker.setAdapter(timeAdapter);

        ivSetterClose = findViewById(R.id.iv_setterClose);
    }

    protected void initUIListeners() {
        initOnClickListeners();
        initDialogListeners();
        initOnItemSelectedListeners();
    }

    protected void initDialogListeners() {
        String btnPositive = "Ok";

        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, btnPositive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String date = String.format("%s/%s/%s",
                        ((datePicker.getDayOfMonth() < 10) ? "0" : "") + datePicker.getDayOfMonth(),
                        ((datePicker.getMonth() < 10) ? "0" : "") + datePicker.getMonth(),
                        ((datePicker.getYear()))
                );

                dateData.set(dateData.size() - 1, date);
                Log.d("Adapter:", "Hellero");
                dateAdapter.notifyDataSetChanged();
            }
        });

        timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, btnPositive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String time = ((timePicker.getHour() < 10) ? "0" : "") + timePicker.getHour() + ":" +
                        ((timePicker.getMinute() < 10) ? "0" : "") + timePicker.getMinute();

                timeData.set(timeData.size() - 1, time);
                timeAdapter.notifyDataSetChanged();
            }
        });
    }

    protected void initOnClickListeners() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.tv_alarm:
                    case R.id.iv_setterClose: {
                        int visibility = (spDMYPicker.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
                        changeAlarmGroupVisibility(visibility);

                        visibility = (tvAlarm.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
                        tvAlarm.setVisibility(visibility);
                        break;
                    }
                }
            }
        };

        ivSetterClose.setOnClickListener(onClickListener);
        tvAlarm.setOnClickListener(onClickListener);
    }

    protected void initOnItemSelectedListeners() {
        AdapterView.OnItemSelectedListener onItemSelectedListenerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String currentItem = null;
                //String lastItem = null;
                switch (parent.getId()) {
                    case R.id.sp_dmyPicker: {
                        //currentItem = dateData.get(position);
                        //lastItem = dateData.getLast();
                        //Log.d(currentItem, lastItem);
                        if (startCount < 6) {
                            startCount++;
                            return;
                        }
                        if (id == dateData.size() - 1) {
                            datePickerDialog.show();
                        }
                        break;
                    }
                    case R.id.sp_hmPicker: {
                        //currentItem = timeData.get(position);
                        //lastItem = timeData.getLast();
                        if (startCount < 6) {
                            startCount++;
                            return;
                        }
                        if (id == timeData.size() - 1) {
                            timePickerDialog.show();
                        }
                        break;
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        };

        spDMYPicker.setOnItemSelectedListener(onItemSelectedListenerListener);
        spHMPicker.setOnItemSelectedListener(onItemSelectedListenerListener);
    }

    protected void changeAlarmGroupVisibility(int visibility) {
        ivSetterClose.setVisibility(visibility);
        spHMPicker.setVisibility(visibility);
        spDMYPicker.setVisibility(visibility);
    }

    protected void initLogicComponents() {
        note = new Note();
        startCount = 6;
    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().setBackgroundDrawable(ActivityConfiguration.getInstance().windowBackground);

        if (!(this instanceof EditingActivity)) {
            updateTimeView();
        }

        Intent intent = getIntent();
        if (intent != null) {
            noteID = intent.getIntExtra(getString(R.string.note_id_key), INVALID_NOTE_ID);
        }
    }

    protected void updateTimeView() {
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
                String title = etTitle.getText().toString().trim();
                String content = etContent.getText().toString().trim();
                //no declare intent here

                if (TextUtils.isEmpty(title) && TextUtils.isEmpty(content)) {
                    setResult(getResources().getInteger(R.integer.CREATING_FAIL));
                    finish();
                }

                note.setTitle(title);
                note.setContent(content);
                if (etTitle.getText().toString().equals(INVALID_INPUT)) {
                    note.setTitle(note.getContent());
                }
                note.setBirthTime(tvTime.getText().toString());
                Calendar alarmTime = setAlarmTimeForNote();
                Log.d("Time", new SimpleDateFormat(DMY_FORMAT + " " + HM_FORMAT).format(alarmTime.getTime()));
                setAlarmTaskForSystem(alarmTime);

                Intent intent = new Intent();
                intent.putExtra(getString(R.string.note_key), note);
                intent.putExtra(getString(R.string.note_id_key), noteID);

                setResult(getResources().getInteger(R.integer.CREATING_OK), intent);
                finish();
                break;
            }
            case R.id.mi_color: {
                backgroundColorDialog.show(getSupportFragmentManager(), BACKGROUND_COLOR_DIALOG);
                break;
            }
            case R.id.mi_camera: {
                cameraOptionDialog.show(getSupportFragmentManager(), CAMERA_OPTION_DIALOG);
                break;
            }
            default:
                return false;
        }
        return true;
    }

    protected Calendar setAlarmTimeForNote() {
        if (tvAlarm.getVisibility() != View.VISIBLE) {
            String INVALID_TIME = "other";
            if (spHMPicker.getSelectedItem().toString().equalsIgnoreCase(INVALID_TIME)) {
                return null;
            }

            String alarmTime = null;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DMY_FORMAT);
            Calendar now = Calendar.getInstance();
            Calendar value = (Calendar) now.clone();
            String[] temp;

            switch (spDMYPicker.getSelectedItemPosition()) {
                case TODAY_POSITION: {
                    alarmTime = simpleDateFormat.format(value.getTime());
                    Log.d("hahaTime", new SimpleDateFormat(DMY_FORMAT + " " + HM_FORMAT).format(value.getTime()));
                    break;
                }
                case TOMORROW_POSITION: {
                    int oneDay = 1;
                    Log.d("haaaaaaaTime", new SimpleDateFormat(DMY_FORMAT + " " + HM_FORMAT).format(value.getTime()));
                    value.add(Calendar.DAY_OF_YEAR, oneDay);
                    alarmTime = simpleDateFormat.format(value);
                    break;
                }
                case NEXT_WEEK_POSITION: {
                    int oneWeek = 7;
                    value.add(Calendar.DAY_OF_YEAR, oneWeek);
                    alarmTime = simpleDateFormat.format(value);
                    break;
                }
                case OTHER_POSITION: {
                    alarmTime = spDMYPicker.getSelectedItem().toString();
                    Log.d("OTHER:", alarmTime);
                    temp = alarmTime.split("/");

                    value.set(Calendar.DAY_OF_MONTH, Integer.parseInt(temp[0]));
                    value.set(Calendar.MONTH, Integer.parseInt(temp[1]));
                    value.set(Calendar.YEAR, Integer.parseInt(temp[2]));
                    break;
                }
            }

            temp = spHMPicker.getSelectedItem().toString().split(":");
            Log.d("Time", new SimpleDateFormat(DMY_FORMAT + " " + HM_FORMAT).format(value.getTime()));
            value.set(Calendar.HOUR_OF_DAY, Integer.parseInt(temp[0]));
            value.set(Calendar.MINUTE, Integer.parseInt(temp[1]));

            alarmTime = alarmTime + " " + spHMPicker.getSelectedItem().toString();
            Log.d("ALARM TIME:", alarmTime);
            note.setAlarmTime(alarmTime);

            if (value.compareTo(now) >= 0) {
                Log.d("Time", new SimpleDateFormat(DMY_FORMAT + " " + HM_FORMAT).format(value.getTime()));
                return value;
            }
        } else {
            note.setAlarmTime("NULL");
        }
        return null;
    }

    protected void setAlarmTaskForSystem(Calendar alarmTime) {
        if (alarmTime == null) {
            return;
        }

        Intent intent = new Intent();
        Note newNote = new Note(note);
        int newNoteID = noteID;
        int max = maxNoteID;
        intent.setAction(AlarmNotificationReceiver.ACCEPTED_ACTION);
        intent.putExtra(getString(R.string.broadcast_note_key), newNote);
        intent.putExtra(getString(R.string.broadcast_note_id_key), newNoteID);
        intent.putExtra(getString(R.string.broadcast_max_note_id_key), max);
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(this, noteID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.d("Time", new SimpleDateFormat(DMY_FORMAT + " " + HM_FORMAT).format(alarmTime.getTime()));

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), broadcastIntent);
        //sendBroadcast(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("Activity:", "Successful");
                new ImageLoadingTask(requestCode).execute(data);
            } else Log.d("RESULT:", String.valueOf(resultCode));
        } else
            Toast.makeText(getApplicationContext(), "Error when read image", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void handleTakingPhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_LOADING_TYPE);
    }

    @Override
    public void handleChoosingPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        String intentType = "image/*";

        intent.setType(intentType);
        startActivityForResult(intent, GALLERY_LOADING_TYPE);
    }

    public class ImageLoadingTask extends AsyncTask<Intent, Void, Bitmap> {
        private boolean resultFlag;
        private int taskType;

        public ImageLoadingTask(int taskType) {
            super();
            this.taskType = taskType;
        }

        @Override
        protected Bitmap doInBackground(Intent... intents) {
            int targetID = 0;
            resultFlag = false;
            Bitmap result = null;

            switch (taskType) {
                case INITIAL_LOADING_TYPE_1:
                case INITIAL_LOADING_TYPE_2: {
                    result = handleInitialLoading(intents[targetID]);
                    break;
                }
                case CAMERA_LOADING_TYPE: {
                    result = handleCameraLoading(intents[targetID]);
                    break;
                }
                case GALLERY_LOADING_TYPE: {
                    result = handleGalleryLoading(intents[targetID]);
                    break;
                }
            }
            return result;
        }

        private Bitmap handleInitialLoading(Intent intent) {
            try {
                String imageUrl = intent.getStringExtra(getString(R.string.note_url_key));
                Bitmap bitmap = null;
                Log.d("IMAGE URL:", imageUrl);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                if (taskType == INITIAL_LOADING_TYPE_1) {
                    bitmap = BitmapFactory.decodeFile(imageUrl, options);
                } else {
                    Uri uri = Uri.parse(imageUrl);
                    bitmap = getFileBitmapFromUri(uri);
                }

                Bitmap result = Bitmap.createScaledBitmap(bitmap, IMAGE_WIDTH, IMAGE_HEIGHT, true);
                resultFlag = true;
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private Bitmap handleCameraLoading(Intent intent) {
            try {
                String bitmapKey = "data";
                Bitmap bitmap = (Bitmap) intent.getExtras().get(bitmapKey);

                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/images";
                File outFile = new File(filePath);
                if (!outFile.exists()) {
                    outFile.mkdirs();
                }

                String imageFileName = "image_" + noteID + ".jpg";
                File file = new File(filePath, imageFileName);
                FileOutputStream fileOutputStream = new FileOutputStream(file);

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();

                bitmap = (Bitmap) intent.getExtras().get(bitmapKey);
                Bitmap result = Bitmap.createScaledBitmap(bitmap, IMAGE_WIDTH, IMAGE_HEIGHT, true);

                note.setImageUrl(file.getAbsolutePath());
                //Log.d("Image url:",file.getAbsolutePath());

                resultFlag = true;
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private Bitmap handleGalleryLoading(Intent intent) {
            try {
                Uri imageUri = intent.getData();
                //getContentResolver.openInputStream does not work when open editing activity because of permission denial

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                String path = getRealPathFromUri(imageUri);

                note.setImageUrl(path);
                Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                Bitmap result = Bitmap.createScaledBitmap(bitmap, IMAGE_WIDTH, IMAGE_HEIGHT, true);

                resultFlag = true;
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private Bitmap getFileBitmapFromUri(Uri uri) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                return imageBitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private void saveData(Bitmap bitmap) {
            try {
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/images";
                File outFile = new File(filePath);
                if (!outFile.exists()) {
                    outFile.mkdirs();
                }

                String imageFileName = "image_" + noteID + ".jpg";
                File file = new File(filePath, imageFileName);
                FileOutputStream fileOutputStream = new FileOutputStream(file);

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                note.setImageUrl(file.getAbsolutePath());

                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private String getRealPathFromUri(Uri uri) {
            Cursor cursor = null;
            try {
                String[] projection = {MediaStore.Images.Media.DATA};
                cursor = getContentResolver().query(uri, projection, null, null, null);

                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                    cursor.close();
                    return path;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (resultFlag) {
                if (result != null) {
                    ivImage.setImageBitmap(result);                                                 //can not set in background thread
                    ivImage.setVisibility(View.VISIBLE);
                    //ivImage.invalidate();
                    if (taskType == GALLERY_LOADING_TYPE) {
                        Toast.makeText(getApplicationContext(), IMAGE_LOADING_ANNOUNCEMENT, Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), IMAGE_LOADING_EXCEPTION, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class CustomSpinner extends AppCompatSpinner {

        public CustomSpinner(Context context) {
            super(context);
        }

        public CustomSpinner(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public CustomSpinner(Context context, AttributeSet attributeSet, int defStyleAttr) {
            super(context, attributeSet, defStyleAttr);
        }

        @Override
        public void setSelection(int position, boolean animate) {
            super.setSelection(position, animate);
            if (position == getSelectedItemPosition()) {
                getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
            }
        }

        @Override
        public void setSelection(int position) {
            super.setSelection(position);
            if (position == getSelectedItemPosition()) {
                getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
            }
        }
    }
}
