package com.worldventures.dreamtrips.modules.gcm.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;

public class PushListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");

        sendNotification(message);
    }

    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification.Builder notificationBuilder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_dt_launcher)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // TODO : update once requirements for notifications are ready
        notificationManager.notify(424242, notificationBuilder.build());
    }
}
