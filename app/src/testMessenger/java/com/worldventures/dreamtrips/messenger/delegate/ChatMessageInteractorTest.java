package com.worldventures.dreamtrips.messenger.delegate;

import com.messenger.delegate.MessageBodyCreator;
import com.messenger.delegate.chat.message.ChatMessageInteractor;
import com.messenger.delegate.chat.message.ChatSendMessageCommand;
import com.messenger.delegate.chat.message.RetrySendMessageCommand;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.messengerservers.constant.ConversationStatus;
import com.messenger.messengerservers.constant.MessageType;
import com.messenger.messengerservers.model.Message;
import com.messenger.storage.MessengerDatabase;
import com.messenger.storage.dao.ConversationsDAO;
import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.model.User;
import com.worldventures.core.model.session.ImmutableUserSession;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.model.session.UserSession;
import com.worldventures.core.storage.complex_objects.Optional;
import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.model.session.UserSession;
import com.worldventures.core.test.janet.MockDaggerActionService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.Collections;

import rx.Observable;
import rx.observers.TestSubscriber;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@PrepareForTest(MessengerDatabase.class)
public class ChatMessageInteractorTest extends BaseChatActionDelegateTest {

   @Mock SessionHolder sessionHolder;
   @Mock Optional<UserSession> userSessionOptional;
   @Mock ConversationsDAO conversationsDAO;

   private MessageBodyCreator messageBodyCreator;
   private ChatMessageInteractor chatMessageInteractor;

   private String testText = "dummy_text";
   private String testConversationId = "chat123412";
   private String testMessageId = "mesagee14123";
   private String testDataUserId = "134124123";

   @Before
   public void setup() {
      super.setup();
      messageBodyCreator = new MessageBodyCreator();

      UserSession userSession = ImmutableUserSession.builder()
            .user(mock(User.class))
            .locale("mock-locale")
            .apiToken("mock-token")
            .legacyApiToken("mock-legacy-token")
            .username(testDataUserId)
            .userPassword("mock-password")
            .lastUpdate(0L)
            .permissions(Collections.emptyList())
            .build();

      doReturn(userSessionOptional).when(sessionHolder).get();
      doReturn(userSession).when(userSessionOptional).get();

      DataConversation conversationFromBd = new DataConversation.Builder().id(testConversationId)
            .status(ConversationStatus.PRESENT)
            .build();
      doReturn(Observable.just(conversationFromBd)).when(conversationsDAO).getConversation(testConversationId);

      chatMessageInteractor = new ChatMessageInteractor(new SessionActionPipeCreator(mockJanet()));
   }

   @Test
   public void checkSendMessage() {
      TestSubscriber<ChatSendMessageCommand> subscriber = new TestSubscriber<>();
      chatMessageInteractor.getMessageActionPipe().
            createObservableResult(new ChatSendMessageCommand(testConversationId, testText)).subscribe(subscriber);

      subscriber.assertNoErrors();
      Message message = subscriber.getOnNextEvents().get(0).getResult();

      checkMessageData(message);
   }

   @Test
   public void checkResendMessage() {
      DataMessage failedMessage = new DataMessage(new Message.Builder().id(testMessageId)
            .conversationId(testConversationId)
            .messageBody(messageBodyCreator.provideForText(testText))
            .fromId(testDataUserId)
            .type(MessageType.MESSAGE)
            .build());

      TestSubscriber<RetrySendMessageCommand> subscriber = new TestSubscriber<>();
      chatMessageInteractor.getResendMessagePipe().
            createObservableResult(new RetrySendMessageCommand(failedMessage)).subscribe(subscriber);

      subscriber.assertNoErrors();

      Message message = subscriber.getOnNextEvents().get(0).getResult();

      assertEquals(message.getId(), testMessageId);
      checkMessageData(message);
   }

   private void checkMessageData(Message message) {
      assertEquals(message.getConversationId(), testConversationId);
      assertEquals(message.getFromId(), testDataUserId);
      assertEquals(message.getMessageBody().getText(), testText);
   }

   @Override
   protected MockDaggerActionService provideMockActionService() {
      MockDaggerActionService actionService = super.provideMockActionService();
      actionService.registerProvider(MessageBodyCreator.class, () -> messageBodyCreator);
      actionService.registerProvider(SessionHolder.class, () -> sessionHolder);
      actionService.registerProvider(ConversationsDAO.class, () -> conversationsDAO);
      return actionService;
   }
}
