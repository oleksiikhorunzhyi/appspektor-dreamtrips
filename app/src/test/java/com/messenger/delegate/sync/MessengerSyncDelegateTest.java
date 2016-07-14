package com.messenger.delegate.sync;

import android.support.annotation.CallSuper;

import com.messenger.delegate.conversation.command.SyncConversationsCommand;
import com.messenger.delegate.conversation.helper.ConversationSyncHelper;
import com.messenger.delegate.roster.LoadContactsCommand;
import com.messenger.delegate.user.UsersDelegate;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.constant.Affiliation;
import com.messenger.messengerservers.constant.ConversationStatus;
import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.messengerservers.loaders.ContactsLoader;
import com.messenger.messengerservers.loaders.ConversationsLoader;
import com.messenger.messengerservers.model.Conversation;
import com.messenger.messengerservers.model.ImmutableConversation;
import com.messenger.messengerservers.model.ImmutableParticipant;
import com.messenger.messengerservers.model.MessengerUser;
import com.messenger.storage.MessengerDatabase;
import com.messenger.storage.dao.UsersDAO;
import com.messenger.synchmechanism.MessengerSyncDelegate;
import com.messenger.util.BaseTest;
import com.messenger.util.MockDaggerActionService;
import com.messenger.util.serverfacade.BaseLoaderManager;
import com.messenger.util.serverfacade.MockContactLoader;
import com.messenger.util.serverfacade.MockConversationsLoader;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.List;

import io.techery.janet.ActionState;
import io.techery.janet.CommandActionService;
import io.techery.janet.Janet;
import rx.Observable;
import rx.observers.TestSubscriber;

import static com.messenger.util.AssertUtil.assertActionSuccess;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MessengerDatabase.class)
public class MessengerSyncDelegateTest extends BaseTest {

    private static final List<Conversation> testConversations;
    private static final String testConversationId = "1234123412";

    static {
        testConversations = Collections.singletonList(ImmutableConversation.builder().id(testConversationId)
                .type(ConversationType.GROUP)
                .unreadMessageCount(0)
                .leftTime(0)
                .participants(Collections.singletonList(ImmutableParticipant
                        .builder()
                        .userId("userId")
                        .affiliation(Affiliation.MEMBER)
                        .conversationId(testConversationId)
                        .build()))
                .lastActiveDate(0)
                .status(ConversationStatus.PRESENT)
                .build());
    }

    static {
        PowerMockito.mockStatic(MessengerDatabase.class);
        when(MessengerDatabase.buildUri(any())).thenReturn(null);
    }

    @Mock List<MessengerUser> testUsers;
    @Mock ConversationSyncHelper conversationSyncHelper;
    @Mock MessengerServerFacade messengerServerFacade;
    @Mock UsersDelegate usersDelegate;
    @Mock UsersDAO usersDAO;

    private MessengerSyncDelegate messengerSyncDelegate;

    @CallSuper
    @Before
    public void setup() {
        Mockito.doReturn(new BaseLoaderManager() {
            @Override
            public ConversationsLoader createConversationsLoader() {
                return new MockConversationsLoader(testConversations);
            }

            @Override
            public ContactsLoader createContactLoader() {
                return new MockContactLoader(testUsers);
            }
        }).when(messengerServerFacade).getLoaderManager();
        Mockito.doReturn(Observable.just(Collections.emptyList()))
                .when(usersDelegate)
                .loadUsers(any());
        Mockito.doReturn(Observable.just(Collections.emptyList()))
                .when(usersDelegate)
                .loadAndSaveUsers(any());


        MockDaggerActionService daggerActionService;
        Janet janet = new Janet.Builder()
                .addService(daggerActionService = new MockDaggerActionService(new CommandActionService()))
                .build();

        daggerActionService.registerProvider(Janet.class, () -> janet);
        daggerActionService.registerProvider(ConversationSyncHelper.class, () -> conversationSyncHelper);
        daggerActionService.registerProvider(MessengerServerFacade.class, () -> messengerServerFacade);
        daggerActionService.registerProvider(UsersDelegate.class, () -> usersDelegate);
        daggerActionService.registerProvider(UsersDAO.class, () -> usersDAO);

        messengerSyncDelegate = new MessengerSyncDelegate(janet);
    }

    @Test
    public void testLoadContacts() {
        TestSubscriber<ActionState<LoadContactsCommand>> testSubscriber = new TestSubscriber<>();
        messengerSyncDelegate.getContactsPipe().createObservable(new LoadContactsCommand())
                .subscribe(testSubscriber);

        verify(usersDelegate, times(1)).loadUsers(testUsers);
        verify(usersDAO, times(1)).unfriendAll();
        verify(usersDAO, times(1)).save(anyList());
        assertActionSuccess(testSubscriber, action -> action.getResult() != null);
    }

    @Test
    public void testLoadConversations() {
        TestSubscriber<ActionState<SyncConversationsCommand>> testSubscriber = new TestSubscriber<>();
        messengerSyncDelegate.getConversationsPipe().createObservable(new SyncConversationsCommand())
                .subscribe(testSubscriber);

        verify(usersDelegate, times(1)).loadAndSaveUsers(anyList());
        verify(conversationSyncHelper).process(anyList());
        assertActionSuccess(testSubscriber, action -> action.getResult() != null);
    }

    @Test
    public void testFullSync() {
        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        messengerSyncDelegate.sync().subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(true);
    }

}
