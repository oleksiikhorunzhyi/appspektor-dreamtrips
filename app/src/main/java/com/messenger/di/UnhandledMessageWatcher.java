package com.messenger.di;

import android.app.Activity;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.ParticipantsRelationship;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.xmpp.UnhandledMessageListener;
import com.messenger.ui.activity.ChatActivity;
import com.messenger.ui.inappnotifications.AppNotification;
import com.messenger.ui.inappnotifications.MessengerInAppNotificationListener;
import com.messenger.ui.widget.inappnotification.messanger.InAppNotificationViewChat;
import com.messenger.ui.widget.inappnotification.messanger.InAppNotificationViewGroup;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
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
    private MessengerServerFacade messengerServerFacade;
    private AppNotification appNotification;

    private UnhandledMessageListener currentUnhandledMessageListener;

    public UnhandledMessageWatcher(MessengerServerFacade messengerServerFacade, AppNotification appNotification) {
        this.messengerServerFacade = messengerServerFacade;
        this.appNotification = appNotification;
    }

    public void start(Activity activity) {
        if (currentActivity == activity) return;

        messengerServerFacade.getGlobalEventEmitter().removeUnhandledMessageListener(currentUnhandledMessageListener);
        this.currentActivity = activity;
        currentUnhandledMessageListener = message -> showInAppNotification(UnhandledMessageWatcher.this.currentActivity, message);
        messengerServerFacade.getGlobalEventEmitter().addUnhandledMessageListener(currentUnhandledMessageListener);
    }

    public void stop(Activity activity) {
        if (currentActivity == activity) {
            dismissAppNotification(currentActivity);
            messengerServerFacade.getGlobalEventEmitter().removeUnhandledMessageListener(currentUnhandledMessageListener);
            currentActivity = null;
        } else {
            dismissAppNotification(activity);
        }
    }

    private void showInAppNotification(final Activity activity, Message message) {
        Conversation conversation = new Select()
                .from(Conversation.class)
                .byIds(message.getConversationId())
                .querySingle();
        //isGroup
        boolean isSingleChat = isSingleChat(conversation);
        if (isSingleChat) {
            //single ava + sender name + sender text
            User fromUser = new Select()
                    .from(User.class)
                    .byIds(message.getFromId())
                    .querySingle();

            String avatarUrl = fromUser.getAvatarUrl();
            String title = fromUser.getName();
            String text = message.getText();

            activity.runOnUiThread(() -> showSingleChatCrouton(conversation, activity, avatarUrl, title, text));
        } else {
            //group 4 avas + group name/user names + last name : last message
            RxContentResolver contentResolver = new RxContentResolver(activity.getContentResolver(),
                    query -> FlowManager.getDatabaseForTable(User.class).getWritableDatabase()
                            .rawQuery(query.selection, query.selectionArgs));

            RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                    .withSelection("SELECT * FROM Users u " +
                                    "JOIN ParticipantsRelationship p " +
                                    "ON p.userId = u._id " +
                                    "WHERE p.conversationId = ?"
                    ).withSelectionArgs(new String[]{conversation.getId()}).build();

            contentResolver.query(q, User.CONTENT_URI,
                    ParticipantsRelationship.CONTENT_URI)
                    .map(c -> SqlUtils.convertToList(User.class, c))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .first()
                    .subscribe(users -> {
                        User fromUser = new Select()
                                .from(User.class)
                                .byIds(message.getFromId())
                                .querySingle();
                        String lastName = fromUser.getName();
                        String lastMessage = message.getText();

                        final List<User> participantsList = new ArrayList<>();
                        String groupName = TextUtils.isEmpty(conversation.getSubject()) ?
                                TextUtils.join(", ", Queryable.from(participantsList).map(User::getName).toList()) :
                                conversation.getSubject();

                        showGroupChatCrouton(conversation, activity, participantsList, groupName, lastName + ": " + lastMessage);
                    }, throwable -> Timber.e(throwable, "Error"));
        }
    }

    private boolean isSingleChat(Conversation conversation) {
        return conversation.getType().equalsIgnoreCase(Conversation.Type.CHAT);
    }

    private MessengerInAppNotificationListener notificationEventListener;

    private void showSingleChatCrouton(Conversation conversation, Activity activity, String avaUrl, String title, String text) {
        notificationEventListener = new MessengerInAppNotificationListener(conversation.getId()) {
            @Override
            public void openConversation(String conversationId) {
                ChatActivity.startChat(activity, conversation);
            }
        };
        InAppNotificationViewChat view = new InAppNotificationViewChat(activity);
        view.setAvatarUrl(avaUrl);
        view.setTitle(title);
        view.setText(text);
        appNotification.show(activity, view, notificationEventListener);
    }

    private void showGroupChatCrouton(Conversation conversation, Activity activity, List<User> participantsList, String title, String text) {
        notificationEventListener = new MessengerInAppNotificationListener(conversation.getId()) {
            @Override
            public void openConversation(String conversationId) {
                ChatActivity.startChat(activity, conversation);
            }
        };
        InAppNotificationViewGroup view = new InAppNotificationViewGroup(activity);
        view.setChatParticipantsList(participantsList);
        view.setTitle(title);
        view.setText(text);
        appNotification.show(activity, view, notificationEventListener);
    }

    private void dismissAppNotification(Activity activity) {
        appNotification.dismissForActivity(activity);
    }
}