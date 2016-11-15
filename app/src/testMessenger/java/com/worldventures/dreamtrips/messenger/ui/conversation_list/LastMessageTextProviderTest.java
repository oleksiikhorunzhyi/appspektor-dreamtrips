package com.worldventures.dreamtrips.messenger.ui.conversation_list;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataTranslation;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.AttachmentType;
import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.messengerservers.constant.MessageType;
import com.messenger.messengerservers.constant.TranslationStatus;
import com.messenger.ui.adapter.inflater.conversation.LastMessageTextProvider;
import com.worldventures.dreamtrips.BaseRoboelectricTest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Class to test last message text in conversation list.
 * System messages are not tested since they are tested in SystemMessagesTextProvider test.
 */
@Ignore
public class LastMessageTextProviderTest extends BaseRoboelectricTest {

    private final static String CURRENT_USER_ID = "6566375";
    private static final String TEST_MESSAGE_ID = "1223";

    private LastMessageTextProvider provider;

    private DataUser currentUser;

    private DataUser otherUser;

    @Before
    public void setup() {

        currentUser = new DataUser(CURRENT_USER_ID);
        currentUser.setFirstName("Joe");
        currentUser.setLastName("Smith");

        otherUser = new DataUser(TEST_MESSAGE_ID + "2345");
        otherUser.setFirstName("Techery");
        otherUser.setLastName("Test");

        provider = new LastMessageTextProvider(getContext(), currentUser);
    }

    @Test
    public void textMessage() {
        // current user is sender
        String expectedText = "You: hello";
        String text = getSampleTextMessageText(getSingleConversation(), currentUser, otherUser);
        assertThat(text).isEqualTo(expectedText);
        text = getSampleTextMessageText(getGroupConversation(), currentUser, otherUser);
        assertThat(text).isEqualTo(expectedText);

        // current user is recipient
        expectedText = "hello";
        text = getSampleTextMessageText(getSingleConversation(), otherUser, currentUser);
        assertThat(text).isEqualTo(expectedText);
        expectedText = "Techery Test: hello";
        text = getSampleTextMessageText(getGroupConversation(), otherUser, currentUser);
        assertThat(text).isEqualTo(expectedText);

        // translated text message
        expectedText = "Techery Test: gracias";
        DataTranslation translation = new DataTranslation(TEST_MESSAGE_ID, "gracias", TranslationStatus.TRANSLATED);
        text = provider.getLastMessageText(getGroupConversation(), getTextMessage("hello"),
                otherUser, currentUser, null, translation);
        assertThat(text).isEqualTo(expectedText);
    }

    public DataMessage getTextMessage(String text) {
        return new DataMessage.Builder()
                .id(TEST_MESSAGE_ID)
                .text(text)
                .type(MessageType.MESSAGE)
                .build();
    }

    private String getSampleTextMessageText(DataConversation conversation, DataUser sender, DataUser recipient) {
        return provider.getLastMessageText(conversation, getTextMessage("hello"),
                sender, recipient, null, null);
    }

    @Test
    public void imageMessage() {
        // current user is sender
        String expectedText = "You sent photo";
        String text = getImageMessageText(getSingleConversation(), currentUser, otherUser);
        assertThat(text).isEqualTo(expectedText);
        text = getImageMessageText(getGroupConversation(), currentUser, otherUser);
        assertThat(text).isEqualTo(expectedText);

        // current user is recipient
        expectedText = "Techery Test sent photo";
        text = getImageMessageText(getSingleConversation(), otherUser, currentUser);
        assertThat(text).isEqualTo(expectedText);
        text = getImageMessageText(getGroupConversation(), otherUser, currentUser);
        assertThat(text).isEqualTo(expectedText);
    }

    private String getImageMessageText(DataConversation conversation, DataUser sender, DataUser recipient) {
        return provider.getLastMessageText(conversation, getEmptyMessage(),
                sender, recipient, AttachmentType.IMAGE, null);
    }

    @Test
    public void locationMessage() {
        // current user is sender
        String expectedText = "You sent location";
        String text = getLocationMessageText(getSingleConversation(), currentUser, otherUser);
        assertThat(text).isEqualTo(expectedText);
        text = getLocationMessageText(getGroupConversation(), currentUser, otherUser);
        assertThat(text).isEqualTo(expectedText);

        // current user is recipient
        expectedText = "Techery Test sent location";
        text = getLocationMessageText(getSingleConversation(), otherUser, currentUser);
        assertThat(text).isEqualTo(expectedText);
        text = getLocationMessageText(getGroupConversation(), otherUser, currentUser);
        assertThat(text).isEqualTo(expectedText);
    }

    private String getLocationMessageText(DataConversation conversation, DataUser sender, DataUser recipient) {
        return provider.getLastMessageText(conversation, getEmptyMessage(),
                sender, recipient, AttachmentType.LOCATION, null);
    }

    @Test
    public void unsupportedMessage() {
        String expectedText = "To see attachment, update to latest version of DreamTrips App here";
        String text = provider.getLastMessageText(getSingleConversation(),
                getEmptyMessage(), otherUser, currentUser, AttachmentType.UNSUPPORTED, null);
        assertThat(text).isEqualTo(expectedText);
    }

    @Test
    public void clearedConversation() {
        String expectedText = "Your chat history was cleared. You can get it back by tapping “Reload”";
        DataConversation conversation = new DataConversation.Builder()
                .id(TEST_MESSAGE_ID)
                .type(ConversationType.CHAT)
                .clearDate(System.currentTimeMillis())
                .build();
        String text = provider.getLastMessageText(conversation,
                new DataMessage(), otherUser, currentUser, null, null);
        assertThat(text).isEqualTo(expectedText);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helpers
    ///////////////////////////////////////////////////////////////////////////

    public DataConversation getSingleConversation() {
        return new DataConversation.Builder()
                .id(TEST_MESSAGE_ID)
                .type(ConversationType.CHAT)
                .build();
    }

    public DataConversation getGroupConversation() {
        return new DataConversation.Builder()
                .id(TEST_MESSAGE_ID)
                .type(ConversationType.GROUP)
                .build();
    }

    public DataMessage getEmptyMessage() {
        return new DataMessage.Builder()
                .id(TEST_MESSAGE_ID)
                .type(MessageType.MESSAGE)
                .build();
    }
}
