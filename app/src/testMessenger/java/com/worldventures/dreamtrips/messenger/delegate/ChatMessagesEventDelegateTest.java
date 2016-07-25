package com.worldventures.dreamtrips.messenger.delegate;

import com.messenger.delegate.chat.ChatMessagesEventDelegate;
import com.messenger.delegate.conversation.LoadConversationDelegate;
import com.messenger.delegate.user.UsersDelegate;
import com.messenger.messengerservers.model.DeletedMessage;
import com.messenger.messengerservers.model.ImmutableDeletedMessage;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.util.DecomposeMessagesHelper;
import com.worldventures.dreamtrips.messenger.util.MessengerBaseTest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ChatMessagesEventDelegateTest extends MessengerBaseTest {

    @Mock
    ConversationsDAO conversationsDAO;
    @Mock
    MessageDAO messageDAO;
    @Mock
    LoadConversationDelegate loadConversationDelegate;
    @Mock
    DecomposeMessagesHelper decomposeMessagesHelper;
    @Mock
    UsersDelegate usersDelegate;


    ChatMessagesEventDelegate chatMessagesEventDelegate;

    @Before
    public void setup() {
        chatMessagesEventDelegate = new ChatMessagesEventDelegate(conversationsDAO, messageDAO,
                loadConversationDelegate, decomposeMessagesHelper, usersDelegate);
    }

    @Test
    public void onMessagesDeleted_success() {
        List<DeletedMessage> deletedMessages = Collections.singletonList(ImmutableDeletedMessage
                .builder()
                .messageId("asdfasdf")
                .source("admin")
                .build());

        chatMessagesEventDelegate.onMessagesDeleted(deletedMessages)
                .subscribe(messageIds -> {
                    assertEquals(deletedMessages.get(0).messageId(), messageIds.get(0));
                }, e -> fail());

        List<String> messageIds = Collections.singletonList(deletedMessages.get(0).messageId());
        verify(messageDAO, times(1)).deleteMessageByIds(messageIds);
    }

}
