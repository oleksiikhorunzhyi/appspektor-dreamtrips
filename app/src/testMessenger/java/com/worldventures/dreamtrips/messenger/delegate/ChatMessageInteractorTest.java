package com.worldventures.dreamtrips.messenger.delegate;

import com.messenger.delegate.MessageBodyCreator;
import com.messenger.delegate.chat.message.ChatMessageInteractor;
import com.messenger.delegate.chat.message.ChatSendMessageCommand;
import com.messenger.delegate.chat.message.RetrySendMessageCommand;
import com.messenger.entities.DataMessage;
import com.messenger.messengerservers.model.Message;
import com.messenger.storage.MessengerDatabase;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.janet.MockDaggerActionService;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.modules.common.model.User;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.Locale;

import rx.observers.TestSubscriber;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;

@PrepareForTest(MessengerDatabase.class)
public class ChatMessageInteractorTest extends BaseChatActionDelegateTest {

    @Mock
    LocaleHelper localeHelper;
    @Mock
    SessionHolder<UserSession> sessionHolder;
    @Mock
    Optional<UserSession> userSessionOptional;

    private MessageBodyCreator messageBodyCreator;
    private ChatMessageInteractor chatMessageInteractor;

    private String testText = "dummy_text";
    private String testConversationId = "chat123412";
    private String testMessageId = "mesagee14123";
    private String testDataUserId = "134124123";

    @Before
    public void setup() {
        super.setup();
        messageBodyCreator = new MessageBodyCreator(localeHelper, new User());
        doReturn(Locale.getDefault()).when(localeHelper).getAccountLocale(any());

        UserSession userSession = new UserSession();
        userSession.setUsername(testDataUserId);

        doReturn(userSessionOptional).when(sessionHolder).get();
        doReturn(userSession).when(userSessionOptional).get();

        chatMessageInteractor = new ChatMessageInteractor(mockJanet());
    }

    @Test
    public void checkSendMessage() {
        TestSubscriber<ChatSendMessageCommand> subscriber = new TestSubscriber<>();
        chatMessageInteractor.getMessageActionPipe().
                createObservableResult(new ChatSendMessageCommand(testConversationId, testText))
                .subscribe(subscriber);

        subscriber.assertNoErrors();
        Message message = subscriber.getOnNextEvents().get(0).getResult();

        assertEquals(message.getConversationId(), testConversationId);
        assertEquals(message.getFromId(), testDataUserId);
        assertEquals(message.getMessageBody().getText(), testText);
    }

    @Test
    public void checkResendMessage() {
        DataMessage failedMessage = new DataMessage(new Message.Builder()
                .id(testMessageId)
                .conversationId(testConversationId)
                .messageBody(messageBodyCreator.provideForText(testText))
                .fromId(testDataUserId)
                .build());

        TestSubscriber<RetrySendMessageCommand> subscriber = new TestSubscriber<>();
        chatMessageInteractor.getResendMessagePipe().
                createObservableResult(new RetrySendMessageCommand(failedMessage))
                .subscribe(subscriber);

        subscriber.assertNoErrors();

        Message message = subscriber.getOnNextEvents().get(0).getResult();

        assertEquals(message.getConversationId(), testConversationId);
        assertEquals(message.getId(), testMessageId);
        assertEquals(message.getFromId(), testDataUserId);
        assertEquals(message.getMessageBody().getText(), testText);
    }


    @Override
    protected MockDaggerActionService provideMockActionService() {
        MockDaggerActionService actionService = super.provideMockActionService();
        actionService.registerProvider(MessageBodyCreator.class, () -> messageBodyCreator);
        actionService.registerProvider(SessionHolder.class, () -> sessionHolder);
        return actionService;
    }
}
