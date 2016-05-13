package com.messenger.messengerservers.listeners;

import com.messenger.messengerservers.model.DeletedMessage;

import java.util.List;

public interface MessagesDeletedListener {

    void onMessagesDeleted(List<DeletedMessage> deletedMessages);
}
