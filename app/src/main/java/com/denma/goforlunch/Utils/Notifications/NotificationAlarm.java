package com.denma.goforlunch.Utils.Notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.denma.goforlunch.Models.GoogleAPI.Nearby.Result;
import com.denma.goforlunch.R;
import com.denma.goforlunch.Utils.RestaurantHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NotificationAlarm {

    private Context mContext;
    private List<String> luncherId = new ArrayList<>();
    private String TAG = "Notification_Alarm_Class";
    private PendingIntent pendingIntent;

    public NotificationAlarm(Context context){
        this.mContext = context;
    }

    // - Update the lunching restaurant of current user and update the corresponding pending intent for the notification
    public void updateLunchingRestaurant(final Result rest){
        if(rest != null){
            // - Init the list of luncher id
            luncherId = new ArrayList<>();
            rest.setLuncherId(luncherId);

            // - Get luncherId collection of the current user's lunching restaurant in FireBase
            RestaurantHelper.getRestaurantsCollection().document(rest.getPlaceId())
                    .collection("luncherId").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    // - Set the list of luncher id
                    for (DocumentSnapshot docSnap : task.getResult()) {
                        luncherId.add(docSnap.getId());
                    }
                    rest.setLuncherId(luncherId);

                    // - Just to test the process
                    for(int i =0; i < rest.getLuncherId().size(); i++){
                        Log.e(TAG, rest.getLuncherId().get(i));
                    }

                    // - Update the pending intent with a valid Restaurant object
                    configureAlarmManager(rest);
                    Log.e("NotifAlarm", "configureREST");
                }
            });
        } else {
            // - Update the pending intent with a null Restaurant object
            configureAlarmManager(null);
            Log.e("NotifAlarm", "configureNULL");
        }

    }

    // - Configuring the AlarmManager
    public void configureAlarmManager(Result rest){
        Intent alarmIntent = new Intent(mContext, AlarmReceiver.class);
        if(rest != null){
            alarmIntent.putExtra("restName", rest.getName());
            alarmIntent.putExtra("restVicinity", rest.getVicinity());
            // - Set co-worker list into a string
            String coworker = "";
            for(int i = 0; i < rest.getLuncherId().size(); i++){
                if(i != rest.getLuncherId().size()-1){
                    coworker += rest.getLuncherId().get(i) + ", ";
                } else {
                    coworker += rest.getLuncherId().get(i) + ".";
                }
            }
            alarmIntent.putExtra("restCoworker", coworker);
        } else {
            alarmIntent.putExtra("restName", "");
        }
        this.pendingIntent = PendingIntent.getBroadcast(mContext, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.e("NotifAlarm", "configureOk");
    }

    // - Start Alarm
    public void startAlarm(Result result) {
        if(this.pendingIntent == null){
            configureAlarmManager(result);
        }
        // - Define when the alarm will be firing
        long alarmUp = 0;
        Calendar midday = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        midday.set(Calendar.HOUR_OF_DAY, 12);
        midday.set(Calendar.MINUTE, 0);
        midday.set(Calendar.SECOND, 0);

        // - Allow us to know if alarm time is past or not -> don't start it if it's past
        if(midday.getTimeInMillis() <= now.getTimeInMillis())
            alarmUp = midday.getTimeInMillis() + (AlarmManager.INTERVAL_DAY+1);
        else
            alarmUp = midday.getTimeInMillis();

        AlarmManager manager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, alarmUp, AlarmManager.INTERVAL_DAY, this.pendingIntent);
        Toast.makeText(mContext, R.string.set_notif, Toast.LENGTH_SHORT).show();
        Log.e("NotifAlarm", "StartOk");
    }

    // - Stop Alarm
    public void stopAlarm(Result result) {
        if(this.pendingIntent == null){
            configureAlarmManager(result);
        }
        AlarmManager manager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(this.pendingIntent);
        Toast.makeText(mContext, R.string.cancel_notif, Toast.LENGTH_SHORT).show();
        Log.e("NotifAlarm", "StopOk");
    }

}
