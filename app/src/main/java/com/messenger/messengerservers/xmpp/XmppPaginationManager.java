package com.messenger.messengerservers.xmpp;


import com.messenger.messengerservers.PaginationManager;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.paginations.PagePagination;
import com.messenger.messengerservers.xmpp.paginations.XmppConversationHistoryPaginator;

public class XmppPaginationManager implements PaginationManager {

    private final XmppServerFacade facade;

    public XmppPaginationManager(XmppServerFacade facade) {
        this.facade = facade;
    }

    @Override
    public PagePagination<Message> getConversationHistoryPagination(Conversation conversation, int pageSize) {
        return new XmppConversationHistoryPaginator(facade.getConnection(), conversation.getId(), pageSize);
    }
}
