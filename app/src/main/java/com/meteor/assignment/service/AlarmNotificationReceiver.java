package com.meteor.assignment.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.meteor.assignment.activity.EditingActivity;
import com.meteor.assignment.activity.R;
import com.meteor.assignment.model.Note;

public class AlarmNotificationReceiver extends BroadcastReceiver {
    public static final String ACCEPTED_ACTION="com.meteor.assignment.CREATE_NOTIFICATION";

    @Override
    public void onReceive(Context context, Intent intent) {
        Note note = intent.getParcelableExtra(context.getString(R.string.broadcast_note_key));
        if (note != null) {
            intent.setClass(context, EditingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent lauchingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder builder = new Notification.Builder(context);
            builder.setSmallIcon(R.drawable.ic_notification_icon)
                    .setContentTitle(note.getTitle())
                    .setContentText(note.getContent())
                    .setContentIntent(lauchingIntent)
                    .setAutoCancel(false);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, builder.build());
        }
    }
}
