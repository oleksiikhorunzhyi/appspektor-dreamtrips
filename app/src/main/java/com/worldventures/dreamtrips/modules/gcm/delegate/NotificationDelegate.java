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
import android.text.Html;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.view.activity.ComponentActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

import javax.inject.Inject;
import javax.inject.Named;

public class NotificationDelegate {

    private Context context;

    @Inject
    @Named(RouteCreatorModule.PROFILE)
    RouteCreator<Integer> routeCreator;

    public NotificationDelegate(Context context) {
        this.context = context;
    }

    public void sendFriendNotification(BaseEventModel eventModel) {
        User user = eventModel.getLinks().getUsers().get(0);

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Intent resultIntent = new Intent(context, ComponentActivity.class);

        //set args to pending intent
        Bundle args = new Bundle();
        args.putSerializable(ComponentPresenter.ROUTE, routeCreator.createRoute(user.getId()));
        args.putSerializable(ComponentPresenter.COMPONENT_TOOLBAR_CONFIG,
                ToolbarConfig.Builder.create().visible(false).build());
        args.putParcelable(ComponentPresenter.EXTRA_DATA,
                new UserBundle(user));

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
                .setContentText(Html.fromHtml(eventModel.infoText(context.getResources())))
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(user.getId(), notificationBuilder.build());
     }

}
