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
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import java.util.ArrayList;

public class PhotoNotificationFactory extends NotificationFactory {

   public PhotoNotificationFactory(Context context) {
      super(context);
   }

   public Notification createTaggedOnPhoto(TaggedOnPhotoPushMessage data) {
      String message = context.getString(R.string.notification_message_tagged_on_photo, TextUtils.join(" ", data.alertWrapper.alert.locArgs));
      NotificationCompat.Builder notification = createFriendNotification(data.photoUid, data.userId, data.notificationId, message);
      return notification.build();
   }

   /**
    * Shows a push notification with ability to open full screen photo
    *
    * @param uid            photoUid to create Photo object to put it to bundle
    * @param userId         userId for FullScreenImagesBundle
    * @param notificationId SocialFullScreenPresenter need notification to mark actual notification
    * @param message        actual message that will be shown in notification
    */
   private NotificationCompat.Builder createFriendNotification(String uid, int userId, int notificationId, String message) {
      PendingIntent intent = createPhotoIntent(createPhotoBundle(uid, userId, notificationId));
      return super.createNotification().setContentIntent(intent).setContentText(message);
   }

   private PendingIntent createPhotoIntent(FullScreenImagesBundle bundle) {
      Intent resultIntent = new Intent(context, ComponentActivity.class);
      //set args to pending intent
      Bundle args = new Bundle();
      args.putSerializable(ComponentPresenter.ROUTE, Route.FULLSCREEN_PHOTO_LIST);
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

   private FullScreenImagesBundle createPhotoBundle(String uid, int userId, int notificationId) {
      ArrayList<IFullScreenObject> fixedList = new ArrayList<>();
      fixedList.add(new Photo(uid));
      //
      return new FullScreenImagesBundle.Builder().position(0)
            .userId(userId)
            .type(TripImagesType.FIXED)
            .route(Route.SOCIAL_IMAGE_FULLSCREEN)
            .fixedList(fixedList)
            .notificationId(notificationId)
            .build();
   }
}
