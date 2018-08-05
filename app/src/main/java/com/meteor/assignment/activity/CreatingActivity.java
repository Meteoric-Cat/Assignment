package com.meteor.assignment.activity;

import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.meteor.assignment.fragment.CameraOptionDialog;
import com.meteor.assignment.model.Note;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreatingActivity extends AppCompatActivity implements CameraOptionDialog.ClickHandler {
    protected static final String INVALID_INPUT = "";
    protected static final String DMY_FORMAT = "dd/MM/yyyy";
    protected static final String HM_FORMAT = "HH:mm";

    protected static final String CAMERA_OPTION_DIALOG = "Camera dialog";

    protected static final int INITIAL_LOADING_TYPE = 0;
    protected static final int CAMERA_LOADING_TYPE = 1;
    protected static final int GALLERY_LOADING_TYPE = 2;

    protected static final String IMAGE_LOADING_EXCEPTION = "Can't find the image";     //display to user

    protected TextView tvTime, tvAlarm;
    protected EditText etTitle, etContent;
    protected ImageView ivImage;

    protected Note note;

    protected CameraOptionDialog cameraOptionDialog;

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

        ivImage = findViewById(R.id.iv_photo);

        cameraOptionDialog = new CameraOptionDialog();
    }

    protected void initLogicComponents() {
        note = new Note();
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

                Intent intent = new Intent();
                intent.putExtra(getString(R.string.note_key), note);
                setResult(getResources().getInteger(R.integer.CREATING_OK), intent);
                finish();
            }
            case R.id.mi_camera: {
                cameraOptionDialog.show(getSupportFragmentManager(), CAMERA_OPTION_DIALOG);
                break;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            new ImageLoadingTask(requestCode, null).execute(data);
        }
    }

    @Override
    public void handleTakingPhoto() {
//        String path= Environment.getExternalStorageState()+"/DCIM/Camera/"+
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

    public class ImageLoadingTask extends AsyncTask<Intent, Void, Void> {
        private boolean resultFlag;
        private int taskType;
        private Uri imageUri;

        public ImageLoadingTask(int taskType, Uri imageUri) {
            super();
            this.taskType = taskType;
            this.imageUri = imageUri;
        }

        @Override
        protected Void doInBackground(Intent... intents) {
            int targetID = 0;
            resultFlag = false;

            switch (taskType) {
                case INITIAL_LOADING_TYPE: {
                    handleInitialLoading(intents[targetID]);
                    break;
                }

                case CAMERA_LOADING_TYPE: {
                    handleCameraLoading(intents[targetID]);
                    break;
                }
                case GALLERY_LOADING_TYPE: {
                    handleGalleryLoading(intents[targetID]);
                    break;
                }
            }
            return null;
        }

        private void handleInitialLoading(Intent intent) {
            try {
                if (!note.getImageUrl().equals("NULL")) {
                    InputStream inputStream = getContentResolver().openInputStream(Uri.parse(note.getImageUrl()));
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    ivImage.setImageBitmap(bitmap);
                    resultFlag = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void handleCameraLoading(Intent intent) {
        }

        private void handleGalleryLoading(Intent intent) {
            try {
                imageUri = intent.getData();
                note.setImageUrl(imageUri.toString());

                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                ivImage.setImageBitmap(bitmap);
                resultFlag = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (resultFlag) {
                ivImage.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getApplicationContext(), IMAGE_LOADING_EXCEPTION, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
