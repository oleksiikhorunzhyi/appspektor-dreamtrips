package com.messenger.loader;

import com.messenger.app.Environment;
import com.messenger.model.ChatUser;
import com.messenger.model.MockChatUser;

import java.util.ArrayList;
import java.util.List;

public class MockChatContactsLoader extends MockLoader<List<ChatUser>> {

    @Override public List<ChatUser> provideData() {
        return provideChatContacts(100);
    }

    public static List<ChatUser> provideChatContacts(int count) {
        return provideChatUsers(false, count);
    }

    public static List<ChatUser> provideChatUsers(boolean includeCurrentUser, int count) {
        ArrayList<ChatUser> users = new ArrayList<>();
        // start from to 1 to exclude current user
        if (includeCurrentUser) {
            users.add(Environment.getCurrentUser());
        }
        for (int i = 1; i < count; i++) {
            ChatUser chatUser = createMockUser(i + 1, i % 2 == 1);
            // first two users are close friends
            if (i < 3) {
                chatUser.setCloseFriend(true);
            }
            users.add(chatUser);
        }
        return users;
    }

    private static ChatUser createMockUser(long id, boolean isOnline) {
        // A ASCII code is 65 + 25 to get to Z
        char randomFirstLetter = (char)((int)(Math.random() * 25) + 65);
        ChatUser user = new MockChatUser(id, randomFirstLetter + "Name Surname " + String.valueOf(id),
                "http://www.skivecore.com/members/0/Default.jpg");
        user.setOnline(isOnline);
        return user;
    }
}
