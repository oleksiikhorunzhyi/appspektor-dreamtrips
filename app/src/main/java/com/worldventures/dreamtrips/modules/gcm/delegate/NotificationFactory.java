package com.worldventures.dreamtrips.modules.gcm.delegate;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import com.worldventures.dreamtrips.R;

public class NotificationFactory {

    protected final Context context;

    public NotificationFactory(Context context) {
        this.context = context;
    }

    protected NotificationCompat.Builder createNotification() {
        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.dt_push_icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_dt_launcher))
                .setContentTitle(context.getString(R.string.app_name))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true);
    }
}
