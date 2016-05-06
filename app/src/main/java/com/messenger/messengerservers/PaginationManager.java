package com.messenger.messengerservers;

import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.paginations.PagePagination;

public interface PaginationManager {

    PagePagination<Message> getConversationHistoryPagination();
}
