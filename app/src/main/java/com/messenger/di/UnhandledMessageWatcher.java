package com.messenger.di;

import android.app.Activity;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.xmpp.UnhandledMessageListener;
import com.messenger.ui.inappnotifications.AppNotification;
import com.messenger.ui.inappnotifications.InAppNotificationEventListener;
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

public class UnhandledMessageWatcher {

    private Activity activity;
    private MessengerServerFacade messengerServerFacade;
    private AppNotification appNotification;

    private UnhandledMessageListener unhandledMessageListener;

    public UnhandledMessageWatcher(MessengerServerFacade messengerServerFacade, AppNotification appNotification) {
        this.messengerServerFacade = messengerServerFacade;
        this.appNotification = appNotification;
    }

    public void start(Activity activity) {
        this.activity = activity;
        unhandledMessageListener = message -> showInAppNotification(this.activity, message);
        messengerServerFacade.getGlobalEventEmitter().addUnhandledMessageListener(unhandledMessageListener);
    }

    public void stop() {
        dismissAppNotification(activity);
        messengerServerFacade.getGlobalEventEmitter().removeUnhandledMessageListener(unhandledMessageListener);
        this.activity = null;
    }

    private void showInAppNotification(Activity activity, Message message) {
        Conversation conversation = new Select()
                .from(Conversation.class)
                .byIds(message.getConversationId())
                .querySingle();
        //isGroup
        boolean isGroup = isSingleChat(conversation);
        if (!isGroup) {
            //single ava + sender name + sender text
            User fromUser = new Select()
                    .from(User.class)
                    .byIds(message.getFromId())
                    .querySingle();

            String avatarUrl = fromUser.getAvatarUrl();
            String title = fromUser.getName();
            String text = message.getText();

            showSingleChatCrouton(activity, avatarUrl, title, text);

        } else {
            //group 4 avas + group name/user names + last name : last message

            RxContentResolver contentResolver = new RxContentResolver(activity.getContentResolver(), query -> {
                return FlowManager.getDatabaseForTable(User.class).getWritableDatabase()
                        .rawQuery(query.selection, query.selectionArgs);
            });

            RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                    .withSelection("SELECT * FROM Users u " +
                            "JOIN ParticipantsRelationship p " +
                            "ON p.userId = u._id " +
                            "WHERE p.conversationId = ?"
                    ).withSelectionArgs(new String[]{conversation.getId()}).build();

            contentResolver.query(q, null)
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

                        showGroupChatCrouton(activity, participantsList, groupName, lastName + ": " + lastMessage);
                    });

        }
    }

    private boolean isSingleChat(Conversation conversation) {
        return conversation.getType().equalsIgnoreCase(Conversation.Type.CHAT);
    }

    private InAppNotificationEventListener notificationEventListener = new InAppNotificationEventListener() {
        @Override
        public void onClick() {
        }

        @Override
        public void onClose() {
        }
    };

    private void showSingleChatCrouton(Activity activity, String avaUrl, String title, String text) {
        InAppNotificationViewChat view = new InAppNotificationViewChat(activity);
        view.setAvatarUrl(avaUrl);
        view.setTitle(title);
        view.setText(text);
        appNotification.show(view, notificationEventListener);
    }

    private void showGroupChatCrouton(Activity activity, List<User> participantsList, String title, String text) {
        InAppNotificationViewGroup view = new InAppNotificationViewGroup(activity);
        view.setChatParticipantsList(participantsList);
        view.setTitle(title);
        view.setText(text);
        appNotification.show(view, notificationEventListener);
    }

    private void dismissAppNotification(Activity activity) {
        appNotification.dismissForActivity(activity);
    }
}