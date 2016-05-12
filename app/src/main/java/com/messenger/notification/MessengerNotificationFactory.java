package com.messenger.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;

import com.messenger.ui.activity.MessengerActivity;
import com.squareup.picasso.Picasso;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationFactory;
import com.worldventures.dreamtrips.modules.gcm.model.NewImagePushMessage;
import com.worldventures.dreamtrips.modules.gcm.model.NewLocationPushMessage;
import com.worldventures.dreamtrips.modules.gcm.model.NewMessagePushMessage;
import com.worldventures.dreamtrips.modules.gcm.model.NewUnsupportedMessage;

import java.io.IOException;

import rx.Observable;

public class MessengerNotificationFactory extends NotificationFactory {

    public static final String MESSENGER_TAG = "messenger_tag";

    public static final int MESSAGE_NOTIFICATION_ID = 0x55a;

    public MessengerNotificationFactory(Context context) {
        super(context);
    }

    public Observable<Notification> createNewImageMessage(NewImagePushMessage data) {
        return createNewImageMessageNotification(
                data.conversationId, data.unreadConversationsCount, createNewMessageImageText(data),
                data.getImageUrl()
        ).map(NotificationCompat.Builder::build);
    }

    @NonNull
    private String createNewMessageImageText(NewMessagePushMessage data) {
        return String.format("%s %s",
                TextUtils.join(" ", data.alertWrapper.alert.locArgs.subList(0, 2)),
                context.getString(R.string.sent_photo));
    }

    public Notification createNewMessage(NewMessagePushMessage data) {
        return createNewMessageNotification(
                data.conversationId, data.unreadConversationsCount, createNewMessageText(data)).build();
    }

    @NonNull
    private String createNewMessageText(NewMessagePushMessage data) {
        return String.format("%s: %s",
                TextUtils.join(" ", data.alertWrapper.alert.locArgs.subList(0, 2)),
                data.alertWrapper.alert.locArgs.get(2));
    }

    public Notification createUnsupportedMessage(NewUnsupportedMessage unsupportedMessage){
        return createUnsupportedMessageNotification(
                unsupportedMessage.conversationId, unsupportedMessage.notificationsCount)
                .build();
    }

    public Notification createNewLocationMessage(NewLocationPushMessage locationMessage) {
        return createNewMessageNotification(locationMessage.conversationId,
                locationMessage.unreadConversationsCount,
                createNewLocationMessageText(locationMessage)).build();
    }

    @NonNull
    private String createNewLocationMessageText(NewLocationPushMessage data) {
        return String.format("%s %s",
                TextUtils.join(" ", data.alertWrapper.alert.locArgs.subList(0, 2)),
                context.getString(R.string.sent_location));
    }

    /**
     * Shows a push notification with ability to open conversation
     *
     * @param unreadConversations
     * @param message             actual message that will be shown in notification
     */
    private NotificationCompat.Builder createNewMessageNotification(String conversationId, int unreadConversations, String message) {
        PendingIntent intent = createMessengerIntent(conversationId, false);
        return super.createNotification()
                .setContentIntent(intent)
                .setContentText(message)
                .setNumber(unreadConversations);
    }

    private Observable<NotificationCompat.Builder> createNewImageMessageNotification(String conversationId, int unreadConversations, String message, String url) {
        PendingIntent intent = createMessengerIntent(conversationId, false);
        return Observable.<NotificationCompat.Builder>create((subscriber) -> {
            try {
                NotificationCompat.BigPictureStyle pictureStyle = new NotificationCompat.BigPictureStyle()
                        .setSummaryText(message)
                        .bigPicture(provideResizedBitmap(url));

                NotificationCompat.Builder notification = super.createNotification()
                        .setContentIntent(intent)
                        .setStyle(pictureStyle)
                        .setContentText(message)
                        .setNumber(unreadConversations);

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(notification);
                }
            } catch (Throwable e) {
                subscriber.onError(e);
            }
        });
    }

    private NotificationCompat.Builder createUnsupportedMessageNotification(String conversationId, int unreadConversations) {
        PendingIntent intent = createMessengerIntent(conversationId, false);
        return super.createNotification()
                .setContentText(context.getString(R.string.push_unsupported_message))
                .setContentIntent(intent)
                .setNumber(unreadConversations);
    }

    private Bitmap provideResizedBitmap(String url) throws IOException{
        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = width * 9 / 16;
        return Picasso.with(context).load(Uri.parse(url))
                .resize(width, height)
                .centerCrop()
                .onlyScaleDown()
                .get();
    }

    private PendingIntent createMessengerIntent(String conversationId, boolean forAction) {
        Intent resultIntent = new Intent(context, MessengerActivity.class);
        //set args to pending intent
        resultIntent.putExtra(MessengerActivity.EXTRA_CHAT_CONVERSATION_ID, conversationId);
        //
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MessengerActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        //
        return stackBuilder.getPendingIntent(0, forAction ? PendingIntent.FLAG_ONE_SHOT : PendingIntent.FLAG_CANCEL_CURRENT);
    }

}
