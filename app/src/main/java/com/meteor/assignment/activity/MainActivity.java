package com.meteor.assignment.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.meteor.assignment.adapter.MainActivityRvAdapter;
import com.meteor.assignment.model.Note;

public class MainActivity extends AppCompatActivity {
    private static final int SPAN_COUNT=2;

    private static final int CREATING_REQUEST=1;
    private static final int CREATING_OK=1;
    private static final int CREATING_FAIL=2;

    private static final int EDITING_REQUEST=2;
    private static final int EDITING_OK=1;
    private static final int EDITING_FAIL=2;

    private RecyclerView rvNotes;
    private MainActivityRvAdapter rvAdapter;
    private RecyclerView.LayoutManager rvLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUIViews();
        initUIListeners();
    }

    private void initUIViews() {
        rvNotes = findViewById(R.id.rv_notes);
        rvAdapter=new MainActivityRvAdapter();
        rvLayoutManager=new GridLayoutManager(this, SPAN_COUNT);

        rvNotes.setLayoutManager(rvLayoutManager);
        rvNotes.setAdapter(rvAdapter);
    }

    private void initUIListeners() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainactivity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.mi_add) {
            Intent intent = new Intent(this, CreatingActivity.class);
            startActivityForResult(intent, getResources().getInteger(R.integer.CREATING_REQUEST));
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CREATING_REQUEST:{
                if (resultCode==getResources().getInteger(R.integer.CREATING_OK)){
                    Note note=data.getParcelableExtra(getString(R.string.note_key));
                    if (note!=null){
                        this.rvAdapter.addItem(note);
                    }
                }
                break;
            }
        }
    }
}
