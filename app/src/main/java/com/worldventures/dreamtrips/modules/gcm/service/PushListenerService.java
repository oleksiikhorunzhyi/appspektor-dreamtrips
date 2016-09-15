package com.worldventures.dreamtrips.modules.gcm.service;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;
import com.techery.spares.application.BaseApplicationWithInjector;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDataParser;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;
import com.worldventures.dreamtrips.modules.gcm.model.NewImagePushMessage;
import com.worldventures.dreamtrips.modules.gcm.model.NewLocationPushMessage;
import com.worldventures.dreamtrips.modules.gcm.model.NewMessagePushMessage;
import com.worldventures.dreamtrips.modules.gcm.model.NewUnsupportedMessage;
import com.worldventures.dreamtrips.modules.gcm.model.PushMessage;
import com.worldventures.dreamtrips.modules.gcm.model.TaggedOnPhotoPushMessage;
import com.worldventures.dreamtrips.modules.gcm.model.UserPushMessage;

import javax.inject.Inject;

import timber.log.Timber;

public class PushListenerService extends GcmListenerService {

   @Inject NotificationDelegate delegate;
   @Inject NotificationDataParser parser;

   @Override
   public void onCreate() {
      super.onCreate();
      ((BaseApplicationWithInjector) getApplication()).inject(this);
   }

   @Override
   public void onMessageReceived(String from, Bundle data) {
      Timber.i("Push message received: " + data);
      PushMessage message = parser.parseMessage(data, PushMessage.class);
      if (message == null) return;
      //
      switch (message.type) {
         case ACCEPT_REQUEST:
            delegate.notifyFriendRequestAccepted(parser.parseMessage(data, UserPushMessage.class));
            break;
         case SEND_REQUEST:
            delegate.notifyFriendRequestReceived(parser.parseMessage(data, UserPushMessage.class));
            break;
         case TAGGED_ON_PHOTO:
            delegate.notifyTaggedOnPhoto(parser.parseMessage(data, TaggedOnPhotoPushMessage.class));
            break;
         case NEW_MESSAGE:
            delegate.notifyNewMessageReceived(parser.parseMessage(data, NewMessagePushMessage.class));
            break;
         case NEW_IMG_MESSAGE:
            delegate.notifyNewImageMessageReceived(parser.parseMessage(data, NewImagePushMessage.class));
            break;
         case NEW_LOC_MESSAGE:
            delegate.notifyNewLocationMessageReceived(parser.parseMessage(data, NewLocationPushMessage.class));
            break;
         case UNSUPPORTED_MESSAGE:
            delegate.notifyUnsupportedMessageReceived(parser.parseMessage(data, NewUnsupportedMessage.class));
            break;
         case BADGE_UPDATE:
            break;
         default:
            Timber.w("Unknown message type: %s", message.type);
            break;
      }
      //
      delegate.updateNotificationCount(message);
   }

}
