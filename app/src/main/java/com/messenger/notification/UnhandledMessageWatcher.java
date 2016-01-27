package com.messenger.notification;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.listeners.GlobalMessageListener;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.UsersDAO;
import com.messenger.ui.activity.MessengerActivity;
import com.messenger.ui.inappnotifications.AppNotification;
import com.messenger.ui.inappnotifications.MessengerInAppNotificationListener;
import com.messenger.ui.widget.inappnotification.messanger.InAppMessengerNotificationView;
import com.messenger.ui.widget.inappnotification.messanger.InAppNotificationViewChat;
import com.messenger.ui.widget.inappnotification.messanger.InAppNotificationViewGroup;
import com.messenger.util.OpenedConversationTracker;
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

    public UnhandledMessageWatcher(MessengerServerFacade messengerServerFacade,
                                   AppNotification appNotification,
                                   DreamSpiceManager spiceManager,
                                   OpenedConversationTracker openedConversationTracker,
                                   ConversationsDAO conversationsDAO,
                                   ParticipantsDAO participantsDAO,
                                   UsersDAO usersDAO) {
        this.messengerServerFacade = messengerServerFacade;
        this.appNotification = appNotification;
        this.spiceManager = spiceManager;
        this.openedConversationTracker = openedConversationTracker;
        this.conversationsDAO = conversationsDAO;
        this.participantsDAO = participantsDAO;
        this.usersDAO = usersDAO;
    }

    private GlobalMessageListener messageListener = new SimpleGlobalMessageListener() {
        @Override
        public void onReceiveMessage(Message message) {
            onUnhandledMessage(UnhandledMessageWatcher.this.currentActivity, message);
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

    private void onUnhandledMessage(Activity activity, Message message) {
        if (isOpenedConversation(message)) return;
        //
        WeakReference<Activity> activityRef = new WeakReference<>(activity);
        conversationsDAO.getConversation(message.getConversationId())
                .compose(new NonNullFilter<>())
                .first()
                .flatMap(conversation -> {
                    if (isSingleChat(conversation)) return composeSingleChatNotification(conversation, message);
                    else return composeGroupChatNotification(conversation, message);
                })
                .compose(new IoToMainComposer<>())
                .filter(data -> {
                    return activityRef.get() != null; // TODO use ActivityEvent (lifecycle) instead
                })
                .subscribe(data -> showNotification(activityRef.get(), data),
                        t -> Timber.e(t, "Can't show inner notification")
                );
    }

    private boolean isOpenedConversation(Message message) {
        return message.getConversationId().equals(openedConversationTracker.getOpenedConversationId());
    }

    private void showNotification(Activity activity, NotificationData data) {
        InAppMessengerNotificationView view;
        if (data.isGroup) {
            view = createGroupChatCrouton(activity, data.participants, data.title, data.fromUserName + ": " + data.messageText);
        } else {
            view = createSingleChatCrouton(activity, data.participants.get(0).getAvatarUrl(), data.title, data.messageText);
        }
        appNotification.show(activity, view, new MessengerInAppNotificationListener(data.conversation.getId()) {
            @Override
            public void openConversation(String conversationId) {
                MessengerActivity.startMessengerWithConversation(activity, data.conversation.getId());
                //ChatActivity.startChat(activity, data.conversation);
            }
        });
    }

    private boolean isSingleChat(@NonNull Conversation conversation) {
        return conversation.getType().equalsIgnoreCase(Conversation.Type.CHAT);
    }

    //single ava + sender name + sender text
    private  Observable<NotificationData> composeSingleChatNotification(Conversation conversation, Message message) {
        return usersDAO.getUserById(message.getFromId())
                .compose(new NonNullFilter<>()).first()
                .map(user -> new NotificationData(user.getName(), user.getName(), Collections.singletonList(user), message.getText(), conversation, false));
    }

    //group 4 avas + group name/user names + last name : last message
    private Observable<NotificationData> composeGroupChatNotification(Conversation conversation, Message message) {
        return Observable.zip(
                participantsDAO.getParticipantsEntities(conversation.getId()),
                usersDAO.getUserById(message.getFromId()).compose(new NonNullFilter<>()),
                (users, fromUser) -> {
                    String lastName = fromUser.getName();
                    String lastMessage = message.getText();

                    String groupName = TextUtils.isEmpty(conversation.getSubject()) ?
                            TextUtils.join(", ", Queryable.from(users).map(User::getName).toList()) :
                            conversation.getSubject();
                    return new NotificationData(groupName, lastName, users, lastMessage, conversation, true);
        }).first();
    }

    private InAppMessengerNotificationView createSingleChatCrouton(Activity activity, String avaUrl, String title, String text) {
        InAppNotificationViewChat view = new InAppNotificationViewChat(activity);
        view.setAvatarUrl(avaUrl);
        view.setTitle(title);
        view.setText(text);
        return view;
    }

    private InAppMessengerNotificationView createGroupChatCrouton(Activity activity, List<User> chatParticipants, String title, String text) {
        InAppNotificationViewGroup view = new InAppNotificationViewGroup(activity);
        view.setChatParticipants(chatParticipants);
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
        final List<User> participants;
        final String messageText;
        final Conversation conversation;
        final boolean isGroup;

        public NotificationData(String title, String fromUserName, List<User> participants, String messageText, Conversation conversation, boolean isGroup) {
            this.title = title;
            this.fromUserName = fromUserName;
            this.participants = participants;
            this.messageText = messageText;
            this.conversation = conversation;
            this.isGroup = isGroup;
        }
    }
}
