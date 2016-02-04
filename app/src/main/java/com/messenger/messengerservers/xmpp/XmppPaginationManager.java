package com.messenger.messengerservers.xmpp;

import com.messenger.messengerservers.PaginationManager;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.paginations.PagePagination;
import com.messenger.messengerservers.xmpp.paginations.XmppConversationHistoryPaginator;

public class XmppPaginationManager implements PaginationManager {

    private final XmppServerFacade facade;

    public XmppPaginationManager(XmppServerFacade facade) {
        this.facade = facade;
    }

    @Override
    public PagePagination<Message> getConversationHistoryPagination(String conversationId, int pageSize) {
        return new XmppConversationHistoryPaginator(facade, conversationId, pageSize);
    }
}
