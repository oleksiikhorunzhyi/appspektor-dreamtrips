package com.messenger.delegate;

import com.messenger.delegate.chat.CreateChatHelper;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.messengerservers.chat.ChatState;
import com.messenger.messengerservers.model.Message;
import com.messenger.util.MessengerBaseTest;
import com.messenger.util.MockDaggerActionService;

import org.junit.Before;
import org.mockito.Mock;

import io.techery.janet.CommandActionService;
import io.techery.janet.Janet;
import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;

public abstract class BaseChatActionDelegateTest extends MessengerBaseTest {

    @Mock
    CreateChatHelper createChatHelper;

    protected Chat chat;

    @Before
    public void setup() {
        chat = new Chat() {
            @Override
            public Observable<Message> send(Message message) {
                return Observable.just(message);
            }

            @Override
            public Observable<String> sendReadStatus(String messageId) {
                return Observable.just(messageId);
            }

            @Override
            public Observable<String> setCurrentState(@ChatState.State String state) {
                return Observable.just(state);
            }
        };

        doReturn(Observable.just(chat)).when(createChatHelper).createChat(any());
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    //////// Mock Objects
    //////////////////////////////////////////////////////////////////////////////////////////////

    protected Janet mockJanet() {
        return new Janet.Builder()
                .addService(provideMockActionService())
                .build();
    }

    protected MockDaggerActionService provideMockActionService() {
        MockDaggerActionService daggerActionService;

        daggerActionService = new MockDaggerActionService(new CommandActionService());
        daggerActionService.registerProvider(CreateChatHelper.class, () -> createChatHelper);

        return daggerActionService;
    }

}
