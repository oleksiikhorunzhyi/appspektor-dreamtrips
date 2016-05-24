package com.messenger.delegate;

import com.messenger.delegate.chat.message.ChatMessageDelegate;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.model.Message;
import com.messenger.storage.MessengerDatabase;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.modules.common.model.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Locale;

import rx.Observable;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MessengerDatabase.class)
public class ChatMessageDelegateTest extends BaseChatActionDelegateTest {

    @Mock LocaleHelper localeHelper;

    private ChatMessageDelegate chatMessageDelegate;

    private String testText = "dummy_text";
    private String testConversationId = "chat123412";
    private String testDataUserId = "134124123";

    @Before
    public void setup() {
        super.setup();
        MessageBodyCreator messageBodyCreator = new MessageBodyCreator(localeHelper, new User());
        doReturn(Locale.getDefault()).when(localeHelper).getAccountLocale(any());

        DataUser currentUser = new DataUser(testDataUserId);
        chatMessageDelegate = new ChatMessageDelegate(mockJanet(), currentUser, messageBodyCreator);
    }

    @Test
    public void checkChatMessageDelegate_sendMessage() {
        chatMessageDelegate.sendMessage(testConversationId, testText)
                .subscribe(message -> {
                    assertEquals(message.getConversationId(), testConversationId);
                    assertEquals(message.getFromId(), testDataUserId);
                    assertEquals(message.getMessageBody().getText(), testText);
                });
    }

}
