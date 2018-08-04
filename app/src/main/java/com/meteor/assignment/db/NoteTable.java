package com.meteor.assignment.db;

import android.content.UriMatcher;
import android.net.Uri;

public class NoteTable {
    public final String TABLE_URL = "content://" + CustomContentProvider.PROVIDER_NAME + "/notes";
    public final Uri TABLE_URI = Uri.parse(TABLE_URL);

    public final String TABLE_NAME="Notes";

    public final String PRIMARY_KEY = "_id";
    public final String TITLE = "title";
    public final String CONTENT = "content";
    public final String BIRTH_TIME = "birth_time";
    public final String IMAGE_URL = "image_url";
    public final String ALARM_TIME = "alarm_time";

    public final String CREATE_TABLE =
            "CREATE TABLE " + NoteTable.getInstance().TABLE_NAME + "(" +
                    NoteTable.getInstance().PRIMARY_KEY + " INTEGER PRIMARY KEY AUTO INCREMENT, " +
                    NoteTable.getInstance().TITLE + " TEXT NOT NULL, " +
                    NoteTable.getInstance().CONTENT + " TEXT NOT NULL, " +
                    NoteTable.getInstance().BIRTH_TIME + " TEXT NOT NULL, " +
                    NoteTable.getInstance().IMAGE_URL + " TEXT, " +
                    NoteTable.getInstance().ALARM_TIME + " TEXT" + ");";

    private static NoteTable instance=new NoteTable();

    private NoteTable() {
    }

    public static NoteTable getInstance(){
        return instance;
    }

}
