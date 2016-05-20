package com.messenger.delegate;

import com.messenger.delegate.chat.typing.ChatStateDelegate;
import com.messenger.delegate.command.BaseChatAction;
import com.messenger.entities.DataConversation;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.chat.ChatManager;
import com.messenger.messengerservers.chat.ChatState;
import com.messenger.messengerservers.chat.GroupChat;
import com.messenger.messengerservers.chat.SingleUserChat;
import com.messenger.storage.MessengerDatabase;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.util.BaseTest;
import com.messenger.util.janet.BaseCommandActionServiceWrapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.techery.janet.ActionHolder;
import io.techery.janet.Janet;
import io.techery.janet.JanetException;
import rx.Observable;
import rx.Subscriber;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MessengerDatabase.class)
public class ChatStateDelegateTest extends BaseTest {

    @Mock MessengerServerFacade messengerServerFacade;
    @Mock ChatManager chatManager;
    @Mock ConversationsDAO conversationsDAO;
    @Mock GroupChat groupChat;
    @Mock SingleUserChat singleUserChat;

    private ChatStateDelegate chatStateDelegate;

    @Before
    public void setup() {
        mockMessengerDataBase();

        doReturn(Observable.just(new DataConversation())).when(conversationsDAO).getConversation(any());
        doReturn(chatManager).when(messengerServerFacade).getChatManager();
        doReturn(groupChat).when(chatManager).createGroupChat(any(), any());
        doReturn(singleUserChat).when(chatManager).createSingleUserChat(any(), any());

        chatStateDelegate = new ChatStateDelegate(mockJanet(), conversationsDAO);
    }

    @Test
    public void testChatDelegate_TypingStarted() {
        chatStateDelegate.init("");
        chatStateDelegate.connectTypingStopAction(Observable.create(new Observable.OnSubscribe<CharSequence>() {
            @Override
            public void call(Subscriber<? super CharSequence> subscriber) {
                subscriber.onNext("h");
                subscriber.onNext("hi");
            }
        }))
        .subscribe(state -> {
            assertEquals(state, ChatState.COMPOSING);
        });
    }

    @Test
    public void testChatDelegate_TypingStopped() {
        chatStateDelegate.init("");
        chatStateDelegate.connectTypingStopAction(Observable.create(new Observable.OnSubscribe<CharSequence>() {
            @Override
            public void call(Subscriber<? super CharSequence> subscriber) {
                subscriber.onNext("h");
                subscriber.onNext("hi");
                try {
                    Thread.sleep(ChatStateDelegate.STOP_TYPING_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }))
        .subscribe(state -> {
            assertEquals(state, ChatState.PAUSE);
        });
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //////// Mock Objects
    //////////////////////////////////////////////////////////////////////////////////////////////

    private void mockMessengerDataBase() {
        PowerMockito.mockStatic(MessengerDatabase.class);
        when(MessengerDatabase.buildUri(any())).thenReturn(null);
    }

    private Janet mockJanet() {
        return new Janet.Builder()
                .addService(new BaseCommandActionServiceWrapper() {
                    @Override
                    protected <A> boolean onInterceptSend(ActionHolder<A> holder) throws JanetException {
                        BaseChatAction chatAction = (BaseChatAction) holder.action();
                        chatAction.setMessengerServerFacade(messengerServerFacade);
                        return false;
                    }
                })
                .build();
    }


}
