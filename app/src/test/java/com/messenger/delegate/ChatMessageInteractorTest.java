package com.messenger.delegate;

import com.messenger.delegate.chat.message.ChatMessageInteractor;
import com.messenger.delegate.chat.message.ChatSendMessageCommand;
import com.messenger.delegate.chat.message.RetrySendMessageCommand;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.model.Message;
import com.messenger.storage.MessengerDatabase;
import com.messenger.util.MockDaggerActionService;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.modules.common.model.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Locale;

import rx.observers.TestSubscriber;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MessengerDatabase.class)
public class ChatMessageInteractorTest extends BaseChatActionDelegateTest {

    @Mock
    LocaleHelper localeHelper;

    private MessageBodyCreator messageBodyCreator;
    private DataUser currentUser;
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
        currentUser = new DataUser(testDataUserId);

        chatMessageInteractor = new ChatMessageInteractor(mockJanet());
    }

    @Test
    public void checkSendMessage() {
        TestSubscriber<ChatSendMessageCommand> subscriber = new TestSubscriber<>();
        chatMessageInteractor.getMessageActionPipe().
                createObservableSuccess(new ChatSendMessageCommand(testConversationId, testText))
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
                createObservableSuccess(new RetrySendMessageCommand(failedMessage))
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
        actionService.registerProvider(DataUser.class, () -> currentUser);
        return actionService;
    }
}
