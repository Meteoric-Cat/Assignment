package com.meteor.assignment.activity;

import android.app.Activity;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meteor.assignment.configuration.ActivityConfiguration;
import com.meteor.assignment.fragment.BackgroundColorDialog;
import com.meteor.assignment.fragment.CameraOptionDialog;
import com.meteor.assignment.model.Note;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreatingActivity extends AppCompatActivity implements CameraOptionDialog.ClickHandler {
    protected static final String INVALID_INPUT = "";
    protected static final String DMY_FORMAT = "dd/MM/yyyy";
    protected static final String HM_FORMAT = "HH:mm";

    protected static final String CAMERA_OPTION_DIALOG = "Camera dialog";
    protected static final String BACKGROUND_COLOR_DIALOG="Background dialog";

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

    protected TextView tvTime, tvAlarm;
    protected EditText etTitle, etContent;
    protected ImageView ivImage, ivSetterClose;
    protected LinearLayout llAlarmSetter;

    protected CameraOptionDialog cameraOptionDialog;
    protected BackgroundColorDialog backgroundColorDialog;

    protected Note note;
    protected int noteID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating);

        initUIViews();
        try {
            initUIListeners();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        initLogicComponents();
    }

    protected void initUIViews() {
        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
        tvTime = findViewById(R.id.tv_time);
        tvAlarm = findViewById(R.id.tv_alarm);
        ivImage = findViewById(R.id.iv_photo);

        llAlarmSetter=findViewById(R.id.ll_alarmSetter);
        ivSetterClose=findViewById(R.id.iv_setterClose);

        cameraOptionDialog = new CameraOptionDialog();
        backgroundColorDialog=new BackgroundColorDialog();
    }

    protected void initUIListeners() {
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.tv_alarm:
                    case R.id.iv_setterClose: {
                        int visibility=(llAlarmSetter.getVisibility()==View.VISIBLE)?View.GONE:View.VISIBLE;
                        llAlarmSetter.setVisibility(visibility);

                        visibility=(tvAlarm.getVisibility()==View.VISIBLE)?View.GONE:View.VISIBLE;
                        tvAlarm.setVisibility(visibility);
                        break;
                    }
                }
            }
        };

        ivSetterClose.setOnClickListener(onClickListener);
        tvAlarm.setOnClickListener(onClickListener);
    }

    protected void initLogicComponents() {
        note = new Note();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().setBackgroundDrawable(ActivityConfiguration.getInstance().windowBackground);

        updateTimeView();

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
                note.setBirthTime(tvTime.getText().toString());

                if (etTitle.getText().toString().equals(INVALID_INPUT)) {
                    note.setTitle(note.getContent());
                }

                Intent intent = new Intent();
                intent.putExtra(getString(R.string.note_key), note);
                intent.putExtra(getString(R.string.note_id_key),noteID);

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
                //Log.d("URI:", imageUri.toString());
//                note.setImageUrl(imageUri.toString());
//                //getContentResolver.openInputStream does not work when open editing activity because of permission denial
//                Bitmap bitmap = getFileBitmapFromUri(imageUri);

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
}
