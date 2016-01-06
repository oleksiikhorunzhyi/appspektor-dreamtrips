package com.messenger.di;

import android.content.Context;
import android.text.TextUtils;

import com.messenger.delegate.ChatDelegate;
import com.messenger.delegate.LeaveChatDelegate;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.XmppServerParams;
import com.messenger.service.MessengerNotificationPreSyncService;
import com.messenger.storege.dao.ConversationsDAO;
import com.messenger.synchmechanism.ActivityWatcher;
import com.messenger.ui.activity.ChatActivity;
import com.messenger.ui.presenter.AddChatMembersScreenPresenterImpl;
import com.messenger.ui.presenter.BaseNewChatMembersScreenPresenter;
import com.messenger.ui.presenter.ChatScreenPresenterImpl;
import com.messenger.ui.presenter.ChatSettingsScreenPresenterImpl;
import com.messenger.ui.presenter.ConversationListScreenPresenterImpl;
import com.messenger.ui.presenter.EditChatMembersScreenPresenterImpl;
import com.messenger.ui.presenter.NewChatScreenPresenterImpl;
import com.messenger.ui.view.EditChatMembersScreenImpl;
import com.messenger.util.OpenedConversationTracker;
import com.messenger.util.RxContentResolver;
import com.messenger.util.UnreadConversationObservable;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.session.UserSession;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true,
        complete = false,
        injects = {
                ChatScreenPresenterImpl.class,

                ChatFacadeInitializer.class,

                BaseNewChatMembersScreenPresenter.class,
                NewChatScreenPresenterImpl.class,
                AddChatMembersScreenPresenterImpl.class,

                LeaveChatDelegate.class,

                ChatSettingsScreenPresenterImpl.class,
                ConversationListScreenPresenterImpl.class,
                EditChatMembersScreenPresenterImpl.class,
                EditChatMembersScreenImpl.class,
                ChatActivity.class,

                MessengerNotificationPreSyncService.class,

                UnhandledMessageWatcher.class,
        }
)
public class XmppServerModule {

    @Singleton
    @Provides
    MessengerServerFacade provideXmppServerFacade(@ForApplication Context context, DreamSpiceManager requester) {
        return new XmppServerFacade(
                new XmppServerParams(BuildConfig.MESSENGER_API_URL, BuildConfig.MESSENGER_API_PORT),
                context, requester
        );
    }

    @Provides
    User provideUser(SessionHolder<UserSession> appSessionHolder) {
        return new User(appSessionHolder.get().get().getUser().getUsername());
    }

    @Provides
    ChatDelegate provideChatDelegate(User user, MessengerServerFacade messengerServerFacade) {
        return new ChatDelegate(user, messengerServerFacade);
    }

    @Singleton
    @Provides
    UnreadConversationObservable provideUnreadConversationObservable(Context context) {
        return new UnreadConversationObservable(context);
    }

    @Singleton
    @Provides
    ActivityWatcher provideActivityWatcher(@ForApplication Context context) {
        return new ActivityWatcher(context);
    }

    @Singleton
    @Provides
    OpenedConversationTracker providedOpenedConversationTracker() {
        return new OpenedConversationTracker();
    }

    @Singleton
    @Provides
    RxContentResolver providedRxContentResolver(@ForApplication Context context) {
        return new RxContentResolver(context.getContentResolver(),
                query -> {
                    StringBuilder builder = new StringBuilder(query.selection);
                    if (!TextUtils.isEmpty(query.sortOrder)) {
                        builder.append(" ").append(query.sortOrder);
                    }
                    return FlowManager.getDatabaseForTable(User.class).getWritableDatabase()
                            .rawQuery(builder.toString(), query.selectionArgs);
                });
    }

    @Singleton
    @Provides
    ConversationsDAO provideConversationsDAO(@ForApplication Context context, RxContentResolver rxContentResolver) {
        return new ConversationsDAO(context, rxContentResolver);
    }


}
