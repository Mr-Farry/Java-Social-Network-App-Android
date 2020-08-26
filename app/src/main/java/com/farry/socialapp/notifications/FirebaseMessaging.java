package com.farry.socialapp.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.farry.socialapp.ActiveVoiceCall;
import com.farry.socialapp.ChatActivity;
import com.farry.socialapp.VoiceCallActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessaging extends FirebaseMessagingService {

    String uid="";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        Log.d("remoteMessage", "onrecieved");
        if (remoteMessage.getData().get("type").equalsIgnoreCase("1"))
        {
            // means it is call notification ..
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Log.d("OreoNotification", "Send the Oreo notification ");
                //  sendImageNotification(bitmap);
                sendOAndAboveCallNotification(remoteMessage.getData().get("user"));
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

             //   sendLolipopCallNotification(remoteMessage.getData().get("user"));
            } else {
                // Log.d("Normal Notification", "Send the normal notification ");

           //     sendNormalCallNotification(remoteMessage.getData().get("user"));
            }


        }
        else {
        String sent = remoteMessage.getData().get("sent");
        String user = remoteMessage.getData().get("user");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();

        if (firebaseUser != null && sent.equals(firebaseUser.getUid())) {
            if (!uid.equals(user)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Log.d("OreoNotification", "Send the Oreo notification ");

                    sendOAndAboveNotifications(remoteMessage);

                } else {
                    Log.d("Normal Notification", "Send the normal notification ");

                    sendNormalNotifications(remoteMessage);
                }
            }
            else {
                Log.d("Else of second if .. ", "executing second else .. ");


            }
        } else {
            Log.d("Else of first if .. ", "executing first else .. ");


        }
        }
    }

    private void sendOAndAboveCallNotification(String user)
    {
        Intent intent=new Intent(this, ActiveVoiceCall.class);
        Bundle bundle=new Bundle();
        bundle.putString("Uid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoAndAboveNotifications notification1=new OreoAndAboveNotifications(this);
        Notification.Builder builder=notification1.getONotifications("","",pendingIntent,defSoundUri);

        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);


        notification1.getManager().notify(1,builder.build());
        Log.d("OreoNotification","Oreo notification sent");

    }

    private void sendNormalNotifications(RemoteMessage remoteMessage) {

        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent=new Intent(this, ChatActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("Uid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this).setSmallIcon(Integer.parseInt(icon)).setContentText(body)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int j=0;
        if(i>0)
        {
            j=i;
        }
        notificationManager.notify(j,builder.build());
        Log.d("normalNotification","normal notification sent");

    }

    private void sendOAndAboveNotifications(RemoteMessage remoteMessage) {
        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent=new Intent(this, ChatActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("Uid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoAndAboveNotifications notification1=new OreoAndAboveNotifications(this);
        Notification.Builder builder=notification1.getONotifications(title,body,pendingIntent,defSoundUri);

        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int j=0;
        if(i>0)
        {
            j=i;
        }

        notification1.getManager().notify(j,builder.build());
        Log.d("OreoNotification","Oreo notification sent");

    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null)

        {
            updateToken(s);
        }
    }

    private void updateToken(String tokenRefresh) {

    FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Tokens");
        Token token=new Token(tokenRefresh);
        databaseReference.child(user.getUid()).setValue(token);

    }
}
