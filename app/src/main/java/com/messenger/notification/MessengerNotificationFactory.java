package com.messenger.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;

import com.messenger.ui.activity.ChatActivity;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationFactory;
import com.worldventures.dreamtrips.modules.gcm.model.NewMessagePushMessage;

public class MessengerNotificationFactory extends NotificationFactory {

    public static final String MESSENGER_TAG = "messenger_tag";

    public MessengerNotificationFactory(Context context) {
        super(context);
    }

    public Notification createNewMessage(NewMessagePushMessage data) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder
                .append(TextUtils.join(" ", data.alertWrapper.alert.locArgs.subList(0, 2))) // user
                .append(": ")
                .append(data.alertWrapper.alert.locArgs.get(2)); // message text

        NotificationCompat.Builder notification = createNewMessageNotification(
                data.conversationId, data.unreadConversationsCount, messageBuilder.toString()
        );
        return notification.build();
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

    private PendingIntent createMessengerIntent(String conversationId, boolean forAction) {
        Intent resultIntent = new Intent(context, ChatActivity.class);
        //set args to pending intent
        resultIntent.putExtra(ChatActivity.EXTRA_CHAT_CONVERSATION_ID, conversationId);
        //
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(ChatActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        //
        return stackBuilder.getPendingIntent(0, forAction ? PendingIntent.FLAG_ONE_SHOT : PendingIntent.FLAG_CANCEL_CURRENT);
    }

}
