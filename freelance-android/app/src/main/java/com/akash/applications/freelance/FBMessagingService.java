package com.akash.applications.freelance;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class FBMessagingService extends FirebaseMessagingService
{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String body = remoteMessage.getData().get("name");
        //String body = remoteMessage.getData().toString();
        Log.e("checking",body);
        String id = remoteMessage.getData().get("id");


        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(this,ViewProject.class);
        intent.putExtra("id",id);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("Freelance");
        builder.setContentText("New Job Posted by "+body);
        builder.setAutoCancel(false);
        builder.setSound(soundUri);
        builder.setSmallIcon(R.drawable.appicon);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,builder.build());



        Log.d("checking", remoteMessage.getData().keySet().toString()+" ");
        Log.d("checking", remoteMessage.getNotification().getBody()+" ");

    }


}
