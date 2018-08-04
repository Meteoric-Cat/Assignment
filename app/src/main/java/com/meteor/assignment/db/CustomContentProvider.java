package com.meteor.assignment.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class CustomContentProvider extends ContentProvider {
    public static final String PROVIDER_NAME = "com.meteor.assignment.db.CustomContentProvider";

    private static final String DATABASE_NAME = "NOTE_DATABASE";
    private static int DATABASE_VERSION = 1;

    private static final String INSERT_EXCEPTION_MESSAGE = "Could not insert into ";                //display to developer
    private static final String URI_EXCEPTION_MESSAGE_1 = " is unknown";
    private static final String URI_EXCPETION_MESSAGE_2 = " is not supported";

    private static final int NOTES_CODE = 1;
    private static final String NOTES_TYPE = "vnd.android.cursor.dir/vnd.meteor.notes";
    private static final int NOTE_CODE = 2;
    private static final String NOTE_TYPE = "vnd.android.cursor.item/vnc.meteor.notes";

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "notes", NOTES_CODE);
        uriMatcher.addURI(PROVIDER_NAME, "notes/#", NOTE_CODE);
    }

    private static final int ID_POSITION = 1;

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    private static final String UPDATE_SEQ_0 = "UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME=";
    private static final String UPDATE_SEQ_1 = "UPDATE SQLITE_SEQUENCE SET SEQ=(SEQ-1) WHERE NAME=";

    private SQLiteDatabase sqLiteDatabase;

    public class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory cursorFactory, int version) {
            super(context, name, cursorFactory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(NoteTable.getInstance().CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            sqLiteDatabase.execSQL(DROP_TABLE + NoteTable.getInstance().TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }

    @Override
    public boolean onCreate() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        return (sqLiteDatabase == null) ? false : true;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        if ((uriMatcher.match(uri) == NOTES_CODE) || (uriMatcher.match(uri) == NOTE_CODE)) {
            long rowId = this.sqLiteDatabase.insert(NoteTable.getInstance().TABLE_NAME, null, contentValues);
            if (rowId > 0) {
                Uri result = ContentUris.withAppendedId(uri, rowId);
                getContext().getContentResolver().notifyChange(result, null);
                return result;
            }
            throw new SQLException(INSERT_EXCEPTION_MESSAGE + NoteTable.getInstance().TABLE_NAME);
        } else throw new IllegalArgumentException(uri.toString() + URI_EXCEPTION_MESSAGE_1);
        //return null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] columns, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor result = null;
        switch (uriMatcher.match(uri)) {
            case NOTES_CODE: {
                result = this.sqLiteDatabase.query(NoteTable.getInstance().TABLE_NAME,
                        columns, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case NOTE_CODE: {
                String id = uri.getPathSegments().get(ID_POSITION);
                selection = selection + ((id == null) ? "" : (" AND (" + NoteTable.getInstance().PRIMARY_KEY + " = " + id + ")"));
                result = this.sqLiteDatabase.query(NoteTable.getInstance().TABLE_NAME,
                        columns, selection, selectionArgs, null, null, sortOrder);
                break;
            }
        }
        if (result != null)
            result.setNotificationUri(getContext().getContentResolver(), uri);
        return result;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        int result = 0;
        switch (uriMatcher.match(uri)) {
            case NOTES_CODE: {
                result = this.sqLiteDatabase.update(NoteTable.getInstance().TABLE_NAME,
                        contentValues, selection, selectionArgs);
                break;
            }
            case NOTE_CODE: {
                String id = uri.getPathSegments().get(ID_POSITION);
                selection = selection + ((id == null) ? "" : (" AND (" + NoteTable.getInstance().PRIMARY_KEY + " = " + id + ")"));
                result = this.sqLiteDatabase.update(NoteTable.getInstance().TABLE_NAME,
                        contentValues, selection, selectionArgs);
                break;
            }
            default:
                throw new IllegalArgumentException(uri.toString() + URI_EXCEPTION_MESSAGE_1);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int result = 0;
        switch (uriMatcher.match(uri)) {
            case NOTES_CODE: {
                result = this.sqLiteDatabase.delete(NoteTable.getInstance().TABLE_NAME, selection, selectionArgs);
                String update0 = UPDATE_SEQ_0 + "'" + NoteTable.getInstance().TABLE_NAME + "'";
                this.sqLiteDatabase.execSQL(update0);
                break;
            }
            case NOTE_CODE: {
                String id = uri.getPathSegments().get(ID_POSITION);
                selection = selection + ((id == null) ? "" : ("AND (" + NoteTable.getInstance().PRIMARY_KEY + " = " + id + ")"));
                result = this.sqLiteDatabase.delete(NoteTable.getInstance().TABLE_NAME, selection, selectionArgs);

                String update1 = "UPDATE SQLITE_SEQUENCE SET " + NoteTable.getInstance().PRIMARY_KEY +
                        " = (" + NoteTable.getInstance().PRIMARY_KEY + "-1) WHERE " + NoteTable.getInstance().PRIMARY_KEY + " > " + id;
                this.sqLiteDatabase.execSQL(update1);

                String update2 = UPDATE_SEQ_1 + "'" + NoteTable.getInstance().TABLE_NAME + "'";
                this.sqLiteDatabase.execSQL(update2);
                break;
            }
            default:
                throw new IllegalArgumentException(uri.toString() + URI_EXCEPTION_MESSAGE_1);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        String result = null;
        switch (uriMatcher.match(uri)) {
            case NOTES_CODE: {
                result = NOTES_TYPE;
                break;
            }
            case NOTE_CODE: {
                result = NOTE_TYPE;
                break;
            }
            default:
                throw new IllegalArgumentException(uri.toString() + URI_EXCPETION_MESSAGE_2);
        }
        return result;
    }
}

