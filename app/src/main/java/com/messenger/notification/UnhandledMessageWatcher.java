package com.messenger.notification;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.delegate.chat.ChatMessagesEventDelegate;
import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.AttachmentType;
import com.messenger.messengerservers.constant.ConversationStatus;
import com.messenger.storage.dao.AttachmentDAO;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.UsersDAO;
import com.messenger.ui.activity.MessengerActivity;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.ui.inappnotifications.AppNotification;
import com.messenger.ui.inappnotifications.MessengerInAppNotificationListener;
import com.messenger.ui.widget.inappnotification.messanger.InAppMessengerNotificationView;
import com.messenger.ui.widget.inappnotification.messanger.InAppNotificationViewChat;
import com.messenger.ui.widget.inappnotification.messanger.InAppNotificationViewGroup;
import com.messenger.util.MessageVersionHelper;
import com.messenger.util.OpenedConversationTracker;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

public class UnhandledMessageWatcher {

    /**
     * New Activity onStart() is called before previous Activity onStop()
     * Callback flow example:
     * onStart FirstActivity -> we start SecondActivity
     * onStart SecondActivity
     * onStop FirstActivity
     */
    private Activity currentActivity;
    private AppNotification appNotification;
    private ChatMessagesEventDelegate chatMessagesEventDelegate;
    private OpenedConversationTracker openedConversationTracker;
    private ConversationsDAO conversationsDAO;
    private ParticipantsDAO participantsDAO;
    private UsersDAO usersDAO;
    private AttachmentDAO attachmentDAO;

    private Subscription processedMessagesSubscription;

    @Inject
    public UnhandledMessageWatcher(AppNotification appNotification,
                                   ChatMessagesEventDelegate chatMessagesEventDelegate,
                                   OpenedConversationTracker openedConversationTracker,
                                   ConversationsDAO conversationsDAO,
                                   ParticipantsDAO participantsDAO,
                                   UsersDAO usersDAO,
                                   AttachmentDAO attachmentDAO) {
        this.appNotification = appNotification;
        this.chatMessagesEventDelegate = chatMessagesEventDelegate;
        this.openedConversationTracker = openedConversationTracker;
        this.conversationsDAO = conversationsDAO;
        this.participantsDAO = participantsDAO;
        this.usersDAO = usersDAO;
        this.attachmentDAO = attachmentDAO;
    }

    public void start(Activity activity) {
        if (currentActivity == activity) return;
        //
        unsubscribeFromMessagesUpdates();
        this.currentActivity = activity;
        subscribeToMessagesUpdates();
    }

    public void stop(Activity activity) {
        if (currentActivity == activity) {
            dismissAppNotification(currentActivity);
            unsubscribeFromMessagesUpdates();
            currentActivity = null;
        } else {
            dismissAppNotification(activity);
        }
    }

    private void subscribeToMessagesUpdates() {
        processedMessagesSubscription = chatMessagesEventDelegate
                .getReceivedSavedMessageStream()
                    .subscribe(notification -> {
                        if (notification.isOnNext()) onUnhandledMessage(notification.getValue());
                });
    }

    private void unsubscribeFromMessagesUpdates() {
        if (processedMessagesSubscription != null) processedMessagesSubscription.unsubscribe();
        processedMessagesSubscription = null;
    }

    private void onUnhandledMessage(DataMessage message) {
        if (isOpenedConversation(message)) return;
        //
        WeakReference<Activity> activityRef = new WeakReference<>(currentActivity);
        conversationsDAO.getConversation(message.getConversationId())
                .compose(new NonNullFilter<>())
                .filter(conversation -> TextUtils.equals(conversation.getStatus(), ConversationStatus.PRESENT))
                .first()
                .flatMap(conversation -> {
                    DataAttachment attachment = attachmentDAO.getAttachmentByMessageId(message.getId()).toBlocking().first();
                    String attachmentType = attachment != null ? attachment.getType() : null;

                    if (ConversationHelper.isSingleChat(conversation)) {
                        return composeSingleChatNotification(conversation, message, attachmentType);
                    } else {
                        return composeGroupChatNotification(conversation, message, attachmentType);
                    }
                })
                .compose(new IoToMainComposer<>())
                .filter(data -> {
                    return activityRef.get() != null; // TODO use ActivityEvent (lifecycle) instead
                })
                .subscribe(data -> showNotification(activityRef.get(), data),
                        t -> Timber.e(t, "Can't show inner notification")
                );
    }

    private boolean isOpenedConversation(DataMessage message) {
        return openedConversationTracker.containsOpenedConversationId(message.getConversationId());
    }

    private void showNotification(Activity activity, NotificationData data) {
        InAppMessengerNotificationView view;
        if (data.isGroup) {
            view = createGroupChatCrouton(activity, data.conversation, data.title, data.messageText);
        } else {
            view = createSingleChatCrouton(activity, data.participants.get(0).getAvatarUrl(), data.title, data.messageText);
        }
        appNotification.show(activity, view, new MessengerInAppNotificationListener(data.conversation.getId()) {
            @Override
            public void openConversation(String conversationId) {
                MessengerActivity.startMessengerWithConversation(activity, data.conversation.getId());
            }
        });
    }

    private String getImagePostMessage(DataUser user) {
        return user.getName() + " " + currentActivity.getString(R.string.sent_photo);
    }

    private String getLocationPostMessage(DataUser user) {
        return user.getName() + " " + currentActivity.getString(R.string.sent_location);
    }

    //single ava + sender name + sender text
    private Observable<NotificationData> composeSingleChatNotification(DataConversation conversation, DataMessage message, @Nullable String attachmentType) {
        return usersDAO.getUserById(message.getFromId())
                .compose(new NonNullFilter<>()).first()
                .map(user -> {
                    String messageText = getSingleChatNotificationText(message, attachmentType, user);
                    return new NotificationData(user.getName(), user.getName(), Collections.singletonList(user), messageText, conversation, false);
                });
    }

    private String getSingleChatNotificationText(DataMessage message, @Nullable String attachmentType,
                                                 DataUser user) {
        if (attachmentType == null) {
            return getMessageText(message, attachmentType);
        }
        switch (attachmentType) {
            case AttachmentType.IMAGE:
                return getImagePostMessage(user);
            case AttachmentType.LOCATION:
                return getLocationPostMessage(user);
            default:
                return getMessageText(message, attachmentType);
        }
    }

    //group name/user names + last name : last message
    private Observable<NotificationData> composeGroupChatNotification(DataConversation conversation, DataMessage message, @Nullable String attachmentType) {
        return Observable.zip(
                participantsDAO.getParticipantsEntities(conversation.getId()).take(1),
                usersDAO.getUserById(message.getFromId()).compose(new NonNullFilter<>()).take(1),
                (users, fromUser) -> {
                    String lastName = fromUser.getName();
                    String messageText = getGroupChatNotificationText(message, attachmentType, fromUser);
                    String groupName = TextUtils.isEmpty(conversation.getSubject()) ?
                            TextUtils.join(", ", Queryable.from(users).map(DataUser::getName).toList()) :
                            conversation.getSubject();
                    return new NotificationData(groupName, lastName, users, messageText, conversation, true);
                }).take(1);
    }

    private String getGroupChatNotificationText(DataMessage message, @Nullable String attachmentType,
                                                DataUser fromUser) {
        if (attachmentType == null) {
            return getGroupNotificationMessage(fromUser, message, attachmentType);
        }
        switch (attachmentType) {
            case AttachmentType.IMAGE:
                return getImagePostMessage(fromUser);
            case AttachmentType.LOCATION:
                return getLocationPostMessage(fromUser);
            default:
                return getGroupNotificationMessage(fromUser, message, attachmentType);
        }
    }

    private String getGroupNotificationMessage(DataUser dataUser, DataMessage dataMessage,
                                               String attachmentType) {
        return dataUser.getName() + ": " + getMessageText(dataMessage, attachmentType);
    }

    private String getMessageText(DataMessage dataMessage, String attachmentType) {
        return MessageVersionHelper.isUnsupported(dataMessage.getVersion(), attachmentType) ?
                Html.fromHtml(currentActivity.getString(R.string.chat_update_proposition)).toString() :
                dataMessage.getText();
    }

    private InAppMessengerNotificationView createSingleChatCrouton(Context context, String avaUrl, String title, String text) {
        InAppNotificationViewChat view = new InAppNotificationViewChat(context);
        view.setAvatarUrl(avaUrl);
        view.setTitle(title);
        view.setText(text);
        return view;
    }

    private InAppMessengerNotificationView createGroupChatCrouton(Context context, DataConversation conversation, String title, String text) {
        InAppNotificationViewGroup view = new InAppNotificationViewGroup(context);
        view.setConversation(conversation);
        view.setTitle(title);
        view.setText(text);
        return view;
    }

    private void dismissAppNotification(Activity activity) {
        appNotification.dismissForActivity(activity);
    }

    static class NotificationData {

        final String title;
        final String fromUserName;
        final List<DataUser> participants;
        final String messageText;
        final DataConversation conversation;
        final boolean isGroup;

        public NotificationData(String title, String fromUserName, List<DataUser> participants, String messageText, DataConversation conversation, boolean isGroup) {
            this.title = title;
            this.fromUserName = fromUserName;
            this.participants = participants;
            this.messageText = messageText;
            this.conversation = conversation;
            this.isGroup = isGroup;
        }
    }
}
