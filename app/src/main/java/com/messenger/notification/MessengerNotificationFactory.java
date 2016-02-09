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
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationFactory;
import com.worldventures.dreamtrips.modules.gcm.model.NewMessagePushMessage;

import rx.Observable;
import timber.log.Timber;

public class MessengerNotificationFactory extends NotificationFactory {

    public static final String MESSENGER_TAG = "messenger_tag";

    public MessengerNotificationFactory(Context context) {
        super(context);
    }

    @NonNull
    private String createNewMessageText(NewMessagePushMessage data) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder
                .append(TextUtils.join(" ", data.alertWrapper.alert.locArgs.subList(0, 2))) // user
                .append(": ")
                .append(data.alertWrapper.alert.locArgs.get(2)); // message text
        return messageBuilder.toString();
    }

    public Notification createNewMessage(NewMessagePushMessage data) {
        NotificationCompat.Builder notification = createNewMessageNotification(
                data.conversationId, data.unreadConversationsCount, createNewMessageText(data)
        );
        return notification.build();
    }

    public Observable<Notification> createNewImageMessage(NewMessagePushMessage data) {
        return createNewImageMessageNotification(
                data.conversationId, data.unreadConversationsCount, createNewMessageText(data),
                "http://www.alleycat.org/view.image?Id=2320"
                //"http://cdn.playbuzz.com/cdn/0079c830-3406-4c05-a5c1-bc43e8f01479/7dd84d70-768b-492b-88f7-a6c70f2db2e9.jpg"
        ).map(builder -> builder.build());
    }

    /**
     * Shows a push notification with ability to open conversation
     *
     * @param unreadConversations
     * @param message actual message that will be shown in notification
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
                Timber.d("Loading bitmap on %s", Thread.currentThread().getName());
                // resize in case server sends too big pic, aspect ratio is 16:9
                int width = context.getResources().getDisplayMetrics().widthPixels;
                int height = width * 9 / 16;
                Bitmap imagePreview = Picasso.with(context).load(Uri.parse(url))
                        .resize(width, height)
                        .centerCrop()
                        .onlyScaleDown()
                        .get();
                NotificationCompat.BigPictureStyle s = new NotificationCompat.BigPictureStyle()
                        .setSummaryText(message)
                        .bigPicture(imagePreview);

                NotificationCompat.Builder notification = super.createNotification()
                        .setContentIntent(intent)
                        .setStyle(s)
                        .setContentText(message)
                        .setNumber(unreadConversations);
                Timber.d("Loading bitmap FINISHED on %s", Thread.currentThread().getName());
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(notification);
                }
            } catch (Throwable e) {
                subscriber.onError(e);
            }
        });
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
