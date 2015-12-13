package com.messenger.messengerservers;

import android.support.annotation.Nullable;

import com.messenger.messengerservers.chat.MultiUserChat;
import com.messenger.messengerservers.chat.SingleUserChat;
import com.messenger.messengerservers.entities.User;


public interface ChatManager {

    SingleUserChat createSingleUserChat(User companion);

    MultiUserChat createMultiUserChat(User owner, @Nullable String roomId);
}
