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
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;

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
    private DreamSpiceManager spiceManager;

    private UnhandledMessageListener currentUnhandledMessageListener;
    private MessengerInAppNotificationListener notificationEventListener;

    public UnhandledMessageWatcher(MessengerServerFacade messengerServerFacade,
                                   AppNotification appNotification,
                                   DreamSpiceManager spiceManager) {
        this.messengerServerFacade = messengerServerFacade;
        this.appNotification = appNotification;
        this.spiceManager = spiceManager;
    }

    public void start(Activity activity) {
        if (currentActivity == activity) return;

        messengerServerFacade.getGlobalEventEmitter().removeUnhandledMessageListener(currentUnhandledMessageListener);
        this.currentActivity = activity;
        currentUnhandledMessageListener = message -> onUnhandledMessage(UnhandledMessageWatcher.this.currentActivity, message);
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

    private void onUnhandledMessage(final Activity activity, Message message) {
        Conversation conversation = new Select()
                .from(Conversation.class)
                .byIds(message.getConversationId())
                .querySingle();

        boolean isSingleChat = isSingleChat(conversation);
        if (isSingleChat) {
            composeSingleChatNotification(activity, conversation, message);
        } else {
            composeGroupChatNotification(activity, conversation, message);
        }
    }

    private boolean isSingleChat(Conversation conversation) {
        return conversation.getType().equalsIgnoreCase(Conversation.Type.CHAT);
    }

    //single ava + sender name + sender text
    private void composeSingleChatNotification(Activity activity, Conversation conversation, Message message) {
        User fromUser = new Select()
                .from(User.class)
                .byIds(message.getFromId())
                .querySingle();

        String avatarUrl = fromUser.getAvatarUrl();
        String title = fromUser.getName();
        String text = message.getText();

        activity.runOnUiThread(() -> showSingleChatCrouton(activity, conversation, avatarUrl, title, text));
    }

    //group 4 avas + group name/user names + last name : last message
    private void composeGroupChatNotification(Activity activity, Conversation conversation, Message message) {
        RxContentResolver contentResolver = new RxContentResolver(activity.getContentResolver(),
                query -> FlowManager.getDatabaseForTable(User.class).getWritableDatabase()
                        .rawQuery(query.selection, query.selectionArgs));

        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM Users u " +
                                "JOIN ParticipantsRelationship p " +
                                "ON p.userId = u._id " +
                                "WHERE p.conversationId = ?"
                ).withSelectionArgs(new String[]{conversation.getId()}).build();

        contentResolver.query(q, User.CONTENT_URI, ParticipantsRelationship.CONTENT_URI)
                .map(c -> SqlUtils.convertToList(User.class, c))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .first()
                .subscribe(users -> {
                    String lastName = "";
                    String lastMessage = "";

                    User fromUser = new Select()
                            .from(User.class)
                            .byIds(message.getFromId())
                            .querySingle();
                    if (fromUser != null) {
                        lastName = fromUser.getName();
                        lastMessage = message.getText();
                    } else {
                        //download non friend user using spiceManager
                    }

                    String groupName = TextUtils.isEmpty(conversation.getSubject()) ?
                            TextUtils.join(", ", Queryable.from(users).map(User::getName).toList()) :
                            conversation.getSubject();

                    showGroupChatCrouton(activity, conversation, users, groupName, lastName + ": " + lastMessage);
                }, throwable -> Timber.e(throwable, "Error"));
    }

    private void showSingleChatCrouton(Activity activity, Conversation conversation, String avaUrl, String title, String text) {
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

    private void showGroupChatCrouton(Activity activity, Conversation conversation, List<User> chatParticipants, String title, String text) {
        notificationEventListener = new MessengerInAppNotificationListener(conversation.getId()) {
            @Override
            public void openConversation(String conversationId) {
                ChatActivity.startChat(activity, conversation);
            }
        };
        InAppNotificationViewGroup view = new InAppNotificationViewGroup(activity);
        view.setChatParticipants(chatParticipants);
        view.setTitle(title);
        view.setText(text);
        appNotification.show(activity, view, notificationEventListener);
    }

    private void dismissAppNotification(Activity activity) {
        appNotification.dismissForActivity(activity);
    }
}
