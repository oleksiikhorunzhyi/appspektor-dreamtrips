package com.messenger.util;

import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;

import java.util.List;

public class UiUtils {

    public static String getGroupConversationName(Conversation conversation, List<User> participants) {
        if (TextUtils.isEmpty(conversation.getSubject())) {
            return TextUtils.join(", ", Queryable.from(participants).map(User::getName).toList());
        } else {
            return conversation.getSubject();
        }
    }
}
