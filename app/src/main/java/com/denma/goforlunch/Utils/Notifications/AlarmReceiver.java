package com.denma.goforlunch.Utils.Notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.denma.goforlunch.Controllers.Activities.MainActivity;
import com.denma.goforlunch.Models.GoogleAPI.Nearby.Result;
import com.denma.goforlunch.R;

public class AlarmReceiver extends BroadcastReceiver {

    private Context context;
    private String restName;
    private String restVicinity;
    private String restCoworker;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Broadcast", "RECEIVED");
        this.context = context;
        this.restName = intent.getStringExtra("restName");
        this.restVicinity = intent.getStringExtra("restVicinity");
        this.restCoworker = intent.getStringExtra("restCoworker");
        if(this.restName != ""){
            sendNotification(true);
            Log.e("Notif", "SEND_VALID");
        } else {
            sendNotification(false);
            Log.e("Notif", "SEND_NOT_VALID");
        }
    }

    // Send simple notification without any action on click, just informative
    private void sendNotification(boolean answer) {
        // Create an explicit intent for an MainActivity in your app
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        String NOTIFICATION_ID = "channel_id_01";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground);

        // - Notification with a valid Restaurant object
        if (answer) {

            // - Set the notification Text
            String text = "You choose to lunch at " + restName + " (" + restVicinity + "), with " + restCoworker;
            builder.setContentTitle(context.getResources().getString(R.string.title_notif))
                    .setContentText(text)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);
        }
        //- Notification with an null Restaurant object
        else {
            builder.setContentTitle(context.getResources().getString(R.string.title_notif))
                    .setContentText(context.getResources().getString(R.string.text_notif_unvalid))
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);
        }

        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(1, builder.build());
    }
}
