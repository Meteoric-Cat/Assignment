package com.meteor.assignment.activity;

import android.app.Activity;
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
import android.util.Log;
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

    protected static final int INITIAL_LOADING_TYPE_1 = 1;
    protected static final int INITIAL_LOADING_TYPE_2 = 2;
    protected static final int CAMERA_LOADING_TYPE = 3;
    protected static final int GALLERY_LOADING_TYPE = 4;

    protected static final String IMAGE_LOADING_EXCEPTION = "Can't load the image";                 //display to user
    protected static final String IMAGE_TAKING_EXCEPTION = "Can't create image with camera";

    protected static final int INVALID_NOTE_ID = -1;

    protected TextView tvTime, tvAlarm;
    protected EditText etTitle, etContent;
    protected ImageView ivImage;

    protected CameraOptionDialog cameraOptionDialog;

    protected Note note;
    protected int noteID;

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

        Intent intent = getIntent();
        if (intent != null) {
            noteID = intent.getIntExtra(getString(R.string.note_id_key), INVALID_NOTE_ID);
        }
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
                setResult(getResources().getInteger(R.integer.CREATING_OK), intent);
                finish();
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

    public class ImageLoadingTask extends AsyncTask<Intent, Void, Void> {
        private boolean resultFlag;
        private int taskType;

        public ImageLoadingTask(int taskType) {
            super();
            this.taskType = taskType;
        }

        @Override
        protected Void doInBackground(Intent... intents) {
            int targetID = 0;
            resultFlag = false;

            switch (taskType) {
                case INITIAL_LOADING_TYPE_1:
                case INITIAL_LOADING_TYPE_2: {
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
                String imageUrl = intent.getStringExtra(getString(R.string.note_url_key));
                Bitmap bitmap=null;
                Log.d("IMAGE URL:",imageUrl);
                BitmapFactory.Options options=new BitmapFactory.Options();
                options.inPreferredConfig=Bitmap.Config.ARGB_8888;

                if (taskType == INITIAL_LOADING_TYPE_1) {
                    bitmap=BitmapFactory.decodeFile(imageUrl, options);
                } else {
                    Uri uri=Uri.parse(imageUrl);
                    bitmap=BitmapFactory.decodeFile(uri.getPath(), options);
                }

                ivImage.setImageBitmap(bitmap);
                resultFlag=true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void handleCameraLoading(Intent intent) {
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

                note.setImageUrl(file.getAbsolutePath());
                Log.d("Image url:",file.getAbsolutePath());
                ivImage.setImageBitmap(bitmap);
                resultFlag = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void handleGalleryLoading(Intent intent) {                                            //gallery case
            try {
                Uri imageUri = intent.getData();
                //Log.d("URI:", imageUri.toString());
                note.setImageUrl(imageUri.toString());

                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();

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
