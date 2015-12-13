package com.messenger.messengerservers;


import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.paginations.PagePagination;

public interface PaginationManager {

    PagePagination<Message> getConversationHistoryPagination(Conversation conversation);
}
