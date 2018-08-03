package com.meteor.assignment.model;

public class Note {
    private String title, content, imageUrl, birthTime;
    Long alarmTime;

    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public String getBirthTime() {
        return this.birthTime;
    }

    public Long getAlarmTime() {
        return this.alarmTime;
    }
}
