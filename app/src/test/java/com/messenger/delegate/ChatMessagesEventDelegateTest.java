package com.messenger.delegate;

import com.messenger.delegate.chat.ChatMessagesEventDelegate;
import com.messenger.delegate.conversation.LoadConversationDelegate;
import com.messenger.messengerservers.model.DeletedMessage;
import com.messenger.messengerservers.model.ImmutableDeletedMessage;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.util.BaseTest;
import com.messenger.util.DecomposeMessagesHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ChatMessagesEventDelegateTest extends BaseTest {

    @Mock
    ConversationsDAO conversationsDAO;
    @Mock
    MessageDAO messageDAO;
    @Mock
    LoadConversationDelegate loadConversationDelegate;
    @Mock
    DecomposeMessagesHelper decomposeMessagesHelper;

    ChatMessagesEventDelegate chatMessagesEventDelegate;

    @Before
    public void setup() {
        chatMessagesEventDelegate = new ChatMessagesEventDelegate(conversationsDAO, messageDAO,
                loadConversationDelegate, decomposeMessagesHelper);
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

        verify(messageDAO, times(1)).deleteMessageByIds(any());
    }

}
