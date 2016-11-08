package com.worldventures.dreamtrips.modules.gcm.delegate;


import android.app.Notification;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.gcm.model.PushMessage;

public class MerchantNotficationFactory extends NotificationFactory {

   public MerchantNotficationFactory(Context context) {
      super(context);
   }

   Notification createMerchantNotification(PushMessage data) {
      String message = context.getString(R.string.merchant_message_text, data.alertWrapper.alert.locArgs.get(0),
            data.alertWrapper.alert.locArgs.get(1), data.alertWrapper.alert.locArgs.get(2));
      Notification notification = createNotification()
            .setContentText(message)
            .setStyle(new NotificationCompat.BigTextStyle()
                  .bigText(message)).build();
      notification.flags = Notification.FLAG_AUTO_CANCEL;
      return notification;
   }

}
