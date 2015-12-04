package com.messenger.loader;

import java.util.ArrayList;

import com.messenger.model.ChatContacts;
import com.messenger.model.ChatUser;
import com.messenger.model.MockChatContacts;
import com.messenger.model.MockChatUser;

public class MockChatContactsLoader extends MockLoader<ChatContacts> {

    @Override public ChatContacts provideData() {
        ArrayList<ChatUser> users = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            ChatUser user = new MockChatUser("Name Surname " + String.valueOf(i + 1),
                    "http://www.skivecore.com/members/0/Default.jpg");
            users.add(user);
            user.setOnline(i % 2 == 0);
        }
        return new MockChatContacts(users);
    }
}
