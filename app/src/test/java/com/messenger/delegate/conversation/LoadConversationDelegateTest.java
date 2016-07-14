package com.messenger.delegate.conversation;

import android.support.annotation.CallSuper;

import com.messenger.delegate.conversation.helper.ConversationSyncHelper;
import com.messenger.delegate.user.UsersDelegate;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.constant.Affiliation;
import com.messenger.messengerservers.constant.ConversationStatus;
import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.messengerservers.loaders.ConversationLoader;
import com.messenger.messengerservers.model.Conversation;
import com.messenger.messengerservers.model.ImmutableConversation;
import com.messenger.messengerservers.model.ImmutableParticipant;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.util.BaseTest;
import com.messenger.util.MockDaggerActionService;
import com.messenger.util.serverfacade.BaseLoaderManager;
import com.messenger.util.serverfacade.MockConversationLoader;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Collections;

import io.techery.janet.CommandActionService;
import io.techery.janet.Janet;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class LoadConversationDelegateTest extends BaseTest {

    private static final Conversation testConversation;
    private static final String testConversationId = "1234123412";

    static {
        testConversation = ImmutableConversation.builder().id(testConversationId)
                .type(ConversationType.GROUP)
                .unreadMessageCount(0)
                .leftTime(0)
                .lastActiveDate(0)
                .participants(Collections.singletonList(ImmutableParticipant
                        .builder()
                        .userId("userId")
                        .affiliation(Affiliation.MEMBER)
                        .conversationId(testConversationId)
                        .build()))
                .status(ConversationStatus.PRESENT)
                .build();
    }

    @Mock ConversationSyncHelper conversationSyncHelper;
    @Mock MessengerServerFacade messengerServerFacade;
    @Mock UsersDelegate usersDelegate;
    @Mock ConversationsDAO conversationsDAO;

    private LoadConversationDelegate loadConversationDelegate;

    @CallSuper
    @Before
    public void setup() {
        Mockito.doReturn(new BaseLoaderManager() {
            @Override
            public ConversationLoader createConversationLoader(String conversationId) {
                return new MockConversationLoader(testConversation);
            }
        }).when(messengerServerFacade).getLoaderManager();
        Mockito.doReturn(Observable.just(Collections.emptyList()))
                .when(usersDelegate)
                .loadAndSaveUsers(Mockito.any());

        MockDaggerActionService daggerActionService;
        Janet janet = new Janet.Builder()
                .addService(daggerActionService = new MockDaggerActionService(new CommandActionService()))
                .build();

        daggerActionService.registerProvider(Janet.class, () -> janet);
        daggerActionService.registerProvider(ConversationSyncHelper.class, () -> conversationSyncHelper);
        daggerActionService.registerProvider(MessengerServerFacade.class, () -> messengerServerFacade);
        daggerActionService.registerProvider(UsersDelegate.class, () -> usersDelegate);

        loadConversationDelegate = new LoadConversationDelegate(janet, conversationsDAO);
    }

    @Test
    public void testLoadConversationCommand() {
        TestSubscriber<Conversation> subscriber = new TestSubscriber<>();

        loadConversationDelegate.loadConversationFromNetwork(testConversationId)
                .subscribe(subscriber);

        verify(conversationSyncHelper, times(1)).process(testConversation);
        verify(usersDelegate, times(1)).loadAndSaveUsers(anyList());

        subscriber.unsubscribe();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
        subscriber.assertUnsubscribed();
    }
}
