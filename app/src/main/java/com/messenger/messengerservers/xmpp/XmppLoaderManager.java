package com.messenger.messengerservers.xmpp;

import com.messenger.messengerservers.LoaderManager;
import com.messenger.messengerservers.loaders.ContactsLoader;
import com.messenger.messengerservers.loaders.ConversationLoader;
import com.messenger.messengerservers.loaders.ConversationsLoader;
import com.messenger.messengerservers.loaders.FlagMessageLoader;
import com.messenger.messengerservers.xmpp.loaders.XmppContactLoader;
import com.messenger.messengerservers.xmpp.loaders.XmppConversationLoader;
import com.messenger.messengerservers.xmpp.loaders.XmppConversationsListLoader;
import com.messenger.messengerservers.xmpp.loaders.XmppFlagMessageLoader;

public class XmppLoaderManager implements LoaderManager {
   private final XmppServerFacade facade;


   public XmppLoaderManager(XmppServerFacade facade) {
      this.facade = facade;
   }

   @Override
   public ContactsLoader createContactLoader() {
      return new XmppContactLoader(facade);
   }

   @Override
   public ConversationLoader createConversationLoader(String conversationId) {
      return new XmppConversationLoader(facade, conversationId);
   }

   @Override
   public ConversationsLoader createConversationsLoader() {
      return new XmppConversationsListLoader(facade);
   }

   @Override
   public FlagMessageLoader createFlaggingLoader() {
      return new XmppFlagMessageLoader(facade);
   }
}
