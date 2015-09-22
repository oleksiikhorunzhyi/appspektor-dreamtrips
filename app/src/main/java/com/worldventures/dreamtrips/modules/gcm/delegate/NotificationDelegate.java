package com.worldventures.dreamtrips.modules.gcm.delegate;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.view.activity.ComponentActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.Module;

public class NotificationDelegate {

    private Context context;

    @Inject
    @Named(RouteCreatorModule.PROFILE)
    RouteCreator<Integer> routeCreator;

    public NotificationDelegate(Context context) {
        this.context = context;
    }

    /**
     * Shows a push notification with ability to open user profile
     *
     * @param message actual message that will be shown in notification
     * @param userId userId to get Route for navigation, and also unique id for the push
     * @param notificationId UserPresenter need notification to mark actual notification
     *                       as read, if it does not equal -1
     */
    public void sendFriendNotification(String message, int userId, int notificationId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Intent resultIntent = new Intent(context, ComponentActivity.class);

        //set args to pending intent
        Bundle args = new Bundle();
        args.putSerializable(ComponentPresenter.ROUTE, routeCreator.createRoute(userId));
        args.putSerializable(ComponentPresenter.COMPONENT_TOOLBAR_CONFIG,
                ToolbarConfig.Builder.create().visible(false).build());
        args.putParcelable(ComponentPresenter.EXTRA_DATA,
                new UserBundle(new User(userId), notificationId));

        resultIntent.putExtra(ComponentPresenter.COMPONENT_EXTRA, args);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack
        stackBuilder.addParentStack(ComponentActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        // Gets a PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);

        notificationBuilder.setSmallIcon(R.drawable.dt_push_icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_dt_launcher))
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(userId, notificationBuilder.build());
    }

}
