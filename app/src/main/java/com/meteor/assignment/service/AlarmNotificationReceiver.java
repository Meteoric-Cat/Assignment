package com.meteor.assignment.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.meteor.assignment.activity.EditingActivity;
import com.meteor.assignment.activity.R;
import com.meteor.assignment.model.Note;

public class AlarmNotificationReceiver extends BroadcastReceiver {
    public static final String ACCEPTED_ACTION = "com.meteor.assignment.CREATE_NOTIFICATION";
    public static final String NOTE_KEY="com.meteor.assignment.note_key";
    public static final String NOTE_ID_KEY="com.meteor.assignment.note_id_key";
    public static final String MAX_NOTE_ID_KEY="com.meteor.assignment.max_note_id_key";
    public static final String BUNDLE_KEY="com.meteor.assignment.hello_world";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Note note = intent.getBundleExtra(BUNDLE_KEY).getParcelable(NOTE_KEY);
            int noteID=intent.getBundleExtra(BUNDLE_KEY).getInt(NOTE_ID_KEY);
            //Log.d("RECEIVER", "received message");
            if (note != null) {
                Log.d("RECEIVER:", "received intent");
                intent.setClass(context, EditingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent lauchingIntent = PendingIntent.getActivity(context, noteID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification.Builder builder = new Notification.Builder(context);
                builder.setSmallIcon(R.drawable.ic_notification_icon)
                        .setContentTitle(note.getTitle())
                        .setContentText(note.getContent())
                        .setContentIntent(lauchingIntent)
                        .setAutoCancel(false);                                                      //or auto

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, builder.build());
                Log.d("RECEIVER:", "start notification");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
