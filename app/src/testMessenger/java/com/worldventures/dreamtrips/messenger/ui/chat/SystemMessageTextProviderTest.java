package com.worldventures.dreamtrips.messenger.ui.chat;

import com.messenger.entities.DataMessage;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.messengerservers.constant.MessageType;
import com.messenger.ui.util.chat.SystemMessageTextProvider;
import com.worldventures.dreamtrips.BaseRoboelectricTest;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SystemMessageTextProviderTest extends BaseRoboelectricTest {

    private final static String CURRENT_USER_ID = "65663751";

    private SystemMessageTextProvider provider;

    private DataUser currentUser;
    private DataUser user1;
    private DataUser user2;

    @Before
    public void setup() {
        provider = new SystemMessageTextProvider(getContext(), CURRENT_USER_ID);

        currentUser = new DataUser(CURRENT_USER_ID);
        currentUser.setFirstName("John");
        currentUser.setLastName("Smith");

        user1 = new DataUser(UUID.randomUUID().toString());
        user1.setFirstName("Techery");
        user1.setLastName("Test");

        user2 = new DataUser(UUID.randomUUID().toString());
        user2.setFirstName("Techery");
        user2.setLastName("Test");
    }

    ///////////////////////////////////////////////////////////////////////////
    // Group chat Join message
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void group_currentUser_added_other_user() {
        String systemMessageText = getSystemMessageForGroup(getJoinMessage(), currentUser, user1);
        assertThat(systemMessageText).isEqualTo("You added Techery Test");
    }

    @Test
    public void group_otherUser_added_currentUser() {
        String systemMessageText = getSystemMessageForGroup(getJoinMessage(), user1, currentUser);
        assertThat(systemMessageText).isEqualTo("Techery Test(Admin) added you");
    }

    @Test
    public void group_otherUser_added_otherUser() {
        String systemMessageText = getSystemMessageForGroup(getJoinMessage(), user1, user2);
        assertThat(systemMessageText).isEqualTo("Techery Test(Admin) added Techery Test");
    }

    ///////////////////////////////////////////////////////////////////////////
    // Trip chat Join message
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void trip_otherUser_added_currentUser() {
        String systemMessageText = getSystemMessageForTrip(getJoinMessage(), user1, currentUser);
        assertThat(systemMessageText).isEqualTo("You are added");
    }

    @Test
    public void trip_anyUser_added_otherUser() {
        final String expectedText = "Techery Test is added";
        assertThat(getSystemMessageForTrip(getJoinMessage(), currentUser, user2)).isEqualTo(expectedText);
        assertThat(getSystemMessageForTrip(getJoinMessage(), user1, user2)).isEqualTo(expectedText);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Group chat Kick message
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void group_currentUser_kicked_other_user() {
        String systemMessageText = getSystemMessageForGroup(getKickMessage(), currentUser, user1);
        assertThat(systemMessageText).isEqualTo("You removed Techery Test from the chat");
    }

    @Test
    public void group_otherUser_kicked_currentUser() {
        String systemMessageText = getSystemMessageForGroup(getKickMessage(), user1, currentUser);
        assertThat(systemMessageText).isEqualTo("Techery Test(Admin) removed you from the chat");
    }

    @Test
    public void group_otherUser_kicked_otherUser() {
        String systemMessageText = getSystemMessageForGroup(getKickMessage(), user1, user2);
        assertThat(systemMessageText).isEqualTo("Techery Test(Admin) removed Techery Test from the chat");
    }

    ///////////////////////////////////////////////////////////////////////////
    // Trip chat Kick message
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void trip_otherUser_kicked_currentUser() {
        String systemMessageText = getSystemMessageForTrip(getKickMessage(), user1, currentUser);
        assertThat(systemMessageText).isEqualTo("You are removed");
    }

    @Test
    public void trip_anyUser_kicked_otherUser() {
        final String expectedText = "Techery Test is removed";
        assertThat(getSystemMessageForTrip(getKickMessage(), currentUser, user2)).isEqualTo(expectedText);
        assertThat(getSystemMessageForTrip(getKickMessage(), user1, user2)).isEqualTo(expectedText);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Group and Trip chats Leave message
    ///////////////////////////////////////////////////////////////////////////

    @Test
    public void currentUser_left() {
        final String expectedText = "You left the chat";
        assertThat(getSystemMessageForGroup(getLeaveMessage(), currentUser, null)).isEqualTo(expectedText);
        assertThat(getSystemMessageForGroup(getLeaveMessage(), currentUser, user1)).isEqualTo(expectedText);
    }

    @Test
    public void otherUser_left() {
        final String expectedText = "Techery Test left";
        assertThat(getSystemMessageForGroup(getLeaveMessage(), user1, null)).isEqualTo(expectedText);
        assertThat(getSystemMessageForGroup(getLeaveMessage(), user1, user2)).isEqualTo(expectedText);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helpers
    ///////////////////////////////////////////////////////////////////////////

    private DataMessage getLeaveMessage() {
        return getMessageOfType(MessageType.SYSTEM_LEAVE);
    }

    private DataMessage getKickMessage() {
        return getMessageOfType(MessageType.SYSTEM_KICK);
    }

    private DataMessage getJoinMessage() {
        return getMessageOfType(MessageType.SYSTEM_JOIN);
    }

    private DataMessage getMessageOfType(String type) {
        return new DataMessage.Builder()
                .type(type)
                .build();
    }

    private String getSystemMessageForGroup(DataMessage message, DataUser sender, DataUser recipient) {
        return provider.getSystemMessageText(ConversationType.GROUP,
                message, sender, recipient).toString();
    }

    private String getSystemMessageForTrip(DataMessage message, DataUser sender, DataUser recipient) {
        return provider.getSystemMessageText(ConversationType.TRIP,
                message, sender, recipient).toString();
    }
}
