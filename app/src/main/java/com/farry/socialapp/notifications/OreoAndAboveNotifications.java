package com.farry.socialapp.notifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.farry.socialapp.R;

public class OreoAndAboveNotifications extends ContextWrapper {

    private static final String ID="some_id";
    private static final String NAME="SocialApp";

    private NotificationManager notificationManager;

    public OreoAndAboveNotifications(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {

        NotificationChannel notificationChannel=new NotificationChannel(ID,NAME,NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

getManager().createNotificationChannel(notificationChannel);

    }

    public NotificationManager getManager()
    {
        if(notificationManager==null)
        {
            notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        }
        return notificationManager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getONotifications(String title,
                                                  String body,
                                                  PendingIntent pendingIntent,
                                                  Uri soundUri)
    {

        return new Notification.Builder(getApplicationContext(),ID)
                .setContentIntent(pendingIntent).
                        setContentTitle(title).
                        setContentText(body).
                        setSound(soundUri).
                        setAutoCancel(true).
                setSmallIcon(R.drawable.icon_chat);
    }


}
