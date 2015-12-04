package com.messenger.messengerservers.xmpp;

import org.jivesoftware.smack.AbstractXMPPConnection;

import com.messenger.messengerservers.PaginationManager;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.paginations.PagePagination;
import com.messenger.messengerservers.xmpp.paginations.XmppConversationHistoryPaginator;

public class XmppPaginationManager implements PaginationManager {

    private AbstractXMPPConnection connection;

    public XmppPaginationManager(AbstractXMPPConnection connection) {
        this.connection = connection;
    }

    @Override
    public PagePagination<Message> getConversationHistoryPagination(Conversation conversation) {
        return new XmppConversationHistoryPaginator(connection, conversation.getId());
    }
}
