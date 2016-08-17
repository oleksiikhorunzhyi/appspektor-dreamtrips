package com.messenger.messengerservers.xmpp;

import com.messenger.messengerservers.PaginationManager;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.paginations.PagePagination;
import com.messenger.messengerservers.xmpp.paginations.XmppConversationHistoryPaginator;
import com.messenger.messengerservers.xmpp.providers.MessagePageProvider;
import com.messenger.messengerservers.xmpp.stanzas.incoming.MessagePageIQ;

import org.jivesoftware.smack.provider.ProviderManager;

public class XmppPaginationManager implements PaginationManager {
   public static final int DEFAULT_PAGE_SIZE = 20;

   private final XmppServerFacade facade;

   public XmppPaginationManager(XmppServerFacade facade) {
      ProviderManager.addIQProvider(MessagePageIQ.ELEMENT_CHAT, MessagePageIQ.NAMESPACE, new MessagePageProvider(facade.getGson()));
      this.facade = facade;
   }

   @Override
   public PagePagination<Message> getConversationHistoryPagination() {
      return new XmppConversationHistoryPaginator(facade, DEFAULT_PAGE_SIZE);
   }
}
