package com.worldventures.dreamtrips.modules.player.playback;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.player.PodcastPlayerActivity;

public class PodcastServiceNotificationFactory {

    private static final int REQUEST_CODE_OPEN_PODCAST_NOTIFICATION = 33;

    private Context context;
    private Resources res;
    private Uri uri;

    public PodcastServiceNotificationFactory(Context context, Uri uri) {
        this.context = context;
        this.res = context.getResources();
        this.uri = uri;
    }

    private NotificationCompat.Builder getDefaultNotification() {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.dt_push_icon)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.podcast_placeholder));

        Intent launchIntent = new Intent(context, PodcastPlayerActivity.class);
        launchIntent.setData(uri);

        launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intent = PendingIntent.getActivity(context, REQUEST_CODE_OPEN_PODCAST_NOTIFICATION,launchIntent, 0);
        notification.setContentIntent(intent);

        notification.setContentTitle(res.getString(R.string.app_name));
        notification.setPriority(Notification.PRIORITY_MAX);
        notification.setCategory(NotificationCompat.CATEGORY_SERVICE);
        return notification;
    }

    public Notification getPlayingNotification() {
        return getDefaultNotification()
                .addAction(getPauseAction())
                .addAction(getStopAction())
                .build();
    }

    public Notification getPausedNotification() {
        return getDefaultNotification()
                .addAction(getPlayAction())
                .addAction(getStopAction())
                .build();
    }

    @NonNull
    private NotificationCompat.Action getPauseAction() {
        Intent intent = new Intent(context, PodcastService.class);
        intent.setAction(PodcastService.NOTIFICATION_ACTION_PAUSE);
        return new NotificationCompat.Action.Builder(
                R.drawable.notification_player_icon_pause,
                res.getString(R.string.podcasts_button_pause),
                PendingIntent.getService(context, 0, intent, 0))
                .build();
    }

    private NotificationCompat.Action getPlayAction() {
        Intent intent = new Intent(context, PodcastService.class);
        intent.setAction(PodcastService.NOTIFICATION_ACTION_PLAY);
        return new NotificationCompat.Action.Builder(
                R.drawable.notification_player_icon_play,
                res.getString(R.string.podcasts_button_resume),
                PendingIntent.getService(context, 0, intent, 0))
                .build();
    }

    private NotificationCompat.Action getStopAction() {
        Intent intent = new Intent(context, PodcastService.class);
        intent.setAction(PodcastService.NOTIFICATION_ACTION_STOP);
        return new NotificationCompat.Action.Builder(
                R.drawable.notification_player_icon_stop,
                res.getString(R.string.podcasts_button_stop),
                PendingIntent.getService(context, 0, intent, 0))
                .build();
    }
}
