package com.meteor.assignment.db;

import android.net.Uri;

public class NoteTable {
    private static NoteTable instance=new NoteTable();

    public String TABLE_URL = "content://" + CustomContentProvider.PROVIDER_NAME + "/notes";
    public Uri TABLE_URI = Uri.parse(TABLE_URL);
    public String TABLE_NAME = "notes";

    public String PRIMARY_KEY = "_id";
    public String TITLE = "title";
    public String CONTENT = "content";
    public String BIRTH_TIME = "birth_time";
    public String IMAGE_URL = "image_url";
    public String ALARM_TIME = "alarm_time";

    public String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    PRIMARY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TITLE + " TEXT NOT NULL, " +
                    CONTENT + " TEXT NOT NULL, " +
                    BIRTH_TIME + " TEXT NOT NULL, " +
                    IMAGE_URL + " TEXT, " +
                    ALARM_TIME + " TEXT" + ");";

    private NoteTable() {
    }

    public static NoteTable getInstance() {
        return instance;
    }

}
