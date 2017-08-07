package com.worldventures.dreamtrips.modules.gcm.delegate;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.view.activity.ComponentActivity;
import com.worldventures.dreamtrips.modules.gcm.model.TaggedOnPhotoPushMessage;
import com.worldventures.dreamtrips.modules.tripsimages.model.BaseMediaEntity;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoMediaEntity;
import com.worldventures.dreamtrips.modules.tripsimages.view.args.TripImagesFullscreenArgs;

import java.util.ArrayList;
import java.util.List;

public class PhotoNotificationFactory extends NotificationFactory {

   public PhotoNotificationFactory(Context context) {
      super(context);
   }

   public Notification createTaggedOnPhoto(TaggedOnPhotoPushMessage data) {
      String message = context.getString(R.string.notification_message_tagged_on_photo, TextUtils.join(" ", data.alertWrapper.alert.locArgs));
      NotificationCompat.Builder notification = createFriendNotification(data.photoUid, data.notificationId, message);
      return notification.build();
   }

   /**
    * Shows a push notification with ability to open full screen photo
    *
    * @param uid            photoUid to create Photo object to put it to bundle
    * @param notificationId SocialFullScreenPresenter need notification to mark actual notification
    * @param message        actual message that will be shown in notification
    */
   private NotificationCompat.Builder createFriendNotification(String uid, int notificationId, String message) {
      PendingIntent intent = createPhotoIntent(createPhotoBundle(uid, notificationId));
      return super.createNotification().setContentIntent(intent).setContentText(message);
   }

   private PendingIntent createPhotoIntent(TripImagesFullscreenArgs bundle) {
      Intent resultIntent = new Intent(context, ComponentActivity.class);
      //set args to pending intent
      Bundle args = new Bundle();
      args.putSerializable(ComponentPresenter.ROUTE, Route.TRIP_IMAGES_FULLSCREEN);
      args.putSerializable(ComponentPresenter.COMPONENT_TOOLBAR_CONFIG, ToolbarConfig.Builder.create()
            .visible(false)
            .build());
      args.putParcelable(ComponentPresenter.EXTRA_DATA, bundle);
      resultIntent.putExtra(ComponentPresenter.COMPONENT_EXTRA, args);
      //
      TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
      stackBuilder.addParentStack(ComponentActivity.class);
      stackBuilder.addNextIntent(resultIntent);
      //
      return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
   }

   private TripImagesFullscreenArgs createPhotoBundle(String uid, int notificationId) {
      List<BaseMediaEntity> items = new ArrayList<>();
      PhotoMediaEntity baseMediaEntity = new PhotoMediaEntity();
      baseMediaEntity.setUid(uid);
      baseMediaEntity.setPhoto(new Photo(uid));
      items.add(baseMediaEntity);
      return TripImagesFullscreenArgs.builder()
            .mediaEntityList(items)
            .notificationId(notificationId)
            .build();
   }
}
