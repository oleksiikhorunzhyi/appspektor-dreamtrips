package com.messenger.notification;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.constant.AttachmentType;
import com.messenger.messengerservers.constant.ConversationStatus;
import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.messengerservers.listeners.GlobalMessageListener;
import com.messenger.messengerservers.model.Message;
import com.messenger.storage.dao.AttachmentDAO;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.UsersDAO;
import com.messenger.ui.activity.MessengerActivity;
import com.messenger.ui.inappnotifications.AppNotification;
import com.messenger.ui.inappnotifications.MessengerInAppNotificationListener;
import com.messenger.ui.widget.inappnotification.messanger.InAppMessengerNotificationView;
import com.messenger.ui.widget.inappnotification.messanger.InAppNotificationViewChat;
import com.messenger.ui.widget.inappnotification.messanger.InAppNotificationViewGroup;
import com.messenger.util.MessageVersionHelper;
import com.messenger.util.OpenedConversationTracker;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import timber.log.Timber;

import static com.messenger.messengerservers.listeners.GlobalMessageListener.SimpleGlobalMessageListener;

public class UnhandledMessageWatcher {

    /**
     * New Activity onStart() is called before previous Activity onStop()
     * Callback flow example:
     * onStart FirstActivity -> we start SecondActivity
     * onStart SecondActivity
     * onStop FirstActivity
     */
    private Activity currentActivity;
    private MessengerServerFacade messengerServerFacade;
    private AppNotification appNotification;
    private DreamSpiceManager spiceManager;
    private OpenedConversationTracker openedConversationTracker;
    private ConversationsDAO conversationsDAO;
    private ParticipantsDAO participantsDAO;
    private UsersDAO usersDAO;
    private AttachmentDAO attachmentDAO;

    public UnhandledMessageWatcher(MessengerServerFacade messengerServerFacade,
                                   AppNotification appNotification,
                                   DreamSpiceManager spiceManager,
                                   OpenedConversationTracker openedConversationTracker,
                                   ConversationsDAO conversationsDAO,
                                   ParticipantsDAO participantsDAO,
                                   UsersDAO usersDAO,
                                   AttachmentDAO attachmentDAO) {
        this.messengerServerFacade = messengerServerFacade;
        this.appNotification = appNotification;
        this.spiceManager = spiceManager;
        this.openedConversationTracker = openedConversationTracker;
        this.conversationsDAO = conversationsDAO;
        this.participantsDAO = participantsDAO;
        this.usersDAO = usersDAO;
        this.attachmentDAO = attachmentDAO;
    }

    private GlobalMessageListener messageListener = new SimpleGlobalMessageListener() {
        @Override
        public void onReceiveMessage(Message message) {
            onUnhandledMessage(UnhandledMessageWatcher.this.currentActivity,
                    new DataMessage(message));
        }
    };

    public void start(Activity activity) {
        if (currentActivity == activity) return;
        //
        messengerServerFacade.getGlobalEventEmitter().removeGlobalMessageListener(messageListener);
        this.currentActivity = activity;
        messengerServerFacade.getGlobalEventEmitter().addGlobalMessageListener(messageListener);
    }

    public void stop(Activity activity) {
        if (currentActivity == activity) {
            dismissAppNotification(currentActivity);
            messengerServerFacade.getGlobalEventEmitter().removeGlobalMessageListener(messageListener);
            currentActivity = null;
        } else {
            dismissAppNotification(activity);
        }
    }

    private void onUnhandledMessage(Activity activity, DataMessage message) {
        if (isOpenedConversation(message)) return;
        //
        WeakReference<Activity> activityRef = new WeakReference<>(activity);
        conversationsDAO.getConversation(message.getConversationId())
                .compose(new NonNullFilter<>())
                .filter(conversation -> TextUtils.equals(conversation.getStatus(), ConversationStatus.PRESENT))
                .first()
                .flatMap(conversation -> {
                    DataAttachment attachment = attachmentDAO.getAttachmentByMessageId(message.getId()).toBlocking().first();
                    String attachmentType = attachment != null ? attachment.getType() : null;

                    if (isSingleChat(conversation))
                        return composeSingleChatNotification(conversation, message, attachmentType);
                    else return composeGroupChatNotification(conversation, message, attachmentType);
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

    private boolean isSingleChat(@NonNull DataConversation conversation) {
        return conversation.getType().equalsIgnoreCase(ConversationType.CHAT);
    }

    private String getImagePostMessage(DataUser user) {
        return user.getName() + " " + currentActivity.getString(R.string.sent_photo);
    }

    //single ava + sender name + sender text
    private Observable<NotificationData> composeSingleChatNotification(DataConversation conversation, DataMessage message, @Nullable String attachmentType) {
        return usersDAO.getUserById(message.getFromId())
                .compose(new NonNullFilter<>()).first()
                .map(user -> {
                    String messageText = TextUtils.equals(attachmentType, AttachmentType.IMAGE) ?
                            getImagePostMessage(user) :
                            getMessageText(message, attachmentType);
                    return new NotificationData(user.getName(), user.getName(), Collections.singletonList(user), messageText, conversation, false);
                });
    }

    //group 4 avas + group name/user names + last name : last message
    private Observable<NotificationData> composeGroupChatNotification(DataConversation conversation, DataMessage message, @Nullable String attachmentType) {
        return Observable.zip(
                participantsDAO.getParticipantsEntities(conversation.getId()).first(),
                usersDAO.getUserById(message.getFromId()).compose(new NonNullFilter<>()).first(),
                (users, fromUser) -> {
                    String lastName = fromUser.getName();
                    String messageText;
                    if (!TextUtils.equals(attachmentType, AttachmentType.IMAGE)) {
                        messageText = lastName + ": " + getMessageText(message, attachmentType);
                    } else {
                        messageText = getImagePostMessage(fromUser);
                    }

                    String groupName = TextUtils.isEmpty(conversation.getSubject()) ?
                            TextUtils.join(", ", Queryable.from(users).map(DataUser::getName).toList()) :
                            conversation.getSubject();
                    return new NotificationData(groupName, lastName, users, messageText, conversation, true);
                }).first();
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
