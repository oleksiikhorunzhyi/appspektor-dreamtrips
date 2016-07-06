package com.messenger.notification;

import android.app.Activity;
import android.content.Context;

import com.messenger.delegate.chat.ChatMessagesEventDelegate;
import com.messenger.entities.DataMessage;
import com.messenger.messengerservers.constant.MessageType;
import com.messenger.notification.model.GroupNotificationData;
import com.messenger.notification.model.NotificationData;
import com.messenger.notification.model.SingleChatNotificationData;
import com.messenger.ui.activity.MessengerActivity;
import com.messenger.ui.inappnotifications.AppNotification;
import com.messenger.ui.inappnotifications.SimpleInAppNotificationListener;
import com.messenger.ui.widget.inappnotification.messanger.InAppMessengerNotificationView;
import com.messenger.ui.widget.inappnotification.messanger.InAppNotificationViewChat;
import com.messenger.ui.widget.inappnotification.messanger.InAppNotificationViewGroup;
import com.messenger.util.OpenedConversationTracker;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;

import javax.inject.Inject;

import rx.Notification;
import rx.Subscription;
import rx.subjects.PublishSubject;
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
    private Subscription notificationSubscription;

    private final PublishSubject<Void> unsubscribeSubject = PublishSubject.create();
    private final ChatMessagesEventDelegate chatMessagesEventDelegate;
    private final OpenedConversationTracker openedConversationTracker;
    private final NotificationDataFactory notificationDataFactory;
    private final AppNotification appNotification;

    @Inject UnhandledMessageWatcher(AppNotification appNotification,
                                   ChatMessagesEventDelegate chatMessagesEventDelegate,
                                   OpenedConversationTracker openedConversationTracker,
                                    NotificationDataFactory notificationDataFactory) {
        this.appNotification = appNotification;
        this.chatMessagesEventDelegate = chatMessagesEventDelegate;
        this.openedConversationTracker = openedConversationTracker;
        this.notificationDataFactory = notificationDataFactory;
    }

    public void start(Activity activity) {
        if (currentActivity == activity) return;
        this.currentActivity = activity;
        subscribeToMessagesUpdates();
    }

    public void stop(Activity activity) {
        dismissAppNotification(activity);
        if (currentActivity == activity) {
            unsubscribeSubject.onNext(null);
            currentActivity = null;
        }
    }

    private void subscribeToMessagesUpdates() {
        if (notificationSubscription == null || notificationSubscription.isUnsubscribed()) {
            bindToMessageObservable();
        }
    }

    private void bindToMessageObservable() {
        notificationSubscription = chatMessagesEventDelegate.getReceivedSavedMessageStream()
                .filter(Notification::isOnNext)
                .map(Notification::getValue)
                .filter(message -> !isOpenedConversation(message)
                        && MessageType.MESSAGE.equals(message.getType()))
                .flatMap(message -> notificationDataFactory.createNotificationData(message))
                .compose(new IoToMainComposer<>())
                .takeUntil(unsubscribeSubject)
                .subscribe(this::showNotification,
                        t -> Timber.e(t, "Can't show inner notification"));
    }

    private void showNotification(NotificationData notification) {
        InAppMessengerNotificationView view;
        Activity activity = currentActivity;
        if (notification instanceof GroupNotificationData) {
            view = createGroupChatCrouton(activity, (GroupNotificationData) notification);
        } else {
            view = createSingleChatCrouton(activity, (SingleChatNotificationData) notification);
        }
        final String conversationId = notification.getConversation().getId();
        appNotification.show(activity, view, new SimpleInAppNotificationListener() {
            @Override
            public void onClick() {
                MessengerActivity.startMessengerWithConversation(activity, conversationId);
            }
        });
    }

    private InAppMessengerNotificationView createSingleChatCrouton(Context context, SingleChatNotificationData notification) {
        InAppNotificationViewChat view = new InAppNotificationViewChat(context);
        view.bindNotification(notification);
        return view;
    }

    private InAppMessengerNotificationView createGroupChatCrouton(Context context, GroupNotificationData notification) {
        InAppNotificationViewGroup view = new InAppNotificationViewGroup(context);
        view.bindNotification(notification);
        return view;
    }

    private void dismissAppNotification(Activity activity) {
        appNotification.dismissForActivity(activity);
    }

    private boolean isOpenedConversation(DataMessage message) {
        return openedConversationTracker.containsOpenedConversationId(message.getConversationId());
    }
}
