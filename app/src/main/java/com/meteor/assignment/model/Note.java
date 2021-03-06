package com.meteor.assignment.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Note implements Parcelable {
    private String title, content, birthTime, imageUrl, alarmTime;

    public Note() {
        String initialValue = "NULL";
        this.title = initialValue;
        this.content = initialValue;
        this.birthTime = initialValue;
        this.imageUrl = initialValue;
        this.alarmTime = initialValue;
    }

    public Note(Note note) {
        this.title = note.getTitle();
        this.content = note.getContent();
        this.birthTime = note.getBirthTime();
        this.imageUrl = note.getImageUrl();
        this.alarmTime = note.getAlarmTime();
    }

    public Note(String title, String content, String birthTime, String imageUrl, String alarmTime) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.birthTime = birthTime;
        this.alarmTime = alarmTime;
    }

    public Note(Parcel parcel) {
        String[] temp = new String[5];
        parcel.readStringArray(temp);

        this.title = temp[0];
        this.content = temp[1];
        this.imageUrl = temp[2];
        this.birthTime = temp[3];
        this.alarmTime = temp[4];
    }

    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel parcel) {
            return new Note(parcel);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        String[] temp = new String[5];
        temp[0] = title;
        temp[1] = content;
        temp[2] = imageUrl;
        temp[3] = birthTime;
        temp[4] = alarmTime;

        parcel.writeStringArray(temp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBirthTime() {
        return this.birthTime;
    }

    public void setBirthTime(String birthTime) {
        this.birthTime = birthTime;
    }

    public String getAlarmTime() {
        return this.alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

//    public void displayInformation() {
//        Log.d("NOTE:",this.title+" ; "+this.content+" ; "
//        +this.birthTime+" ; "+this.imageUrl+" ; "+this.alarmTime);
//    }
}
