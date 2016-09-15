package com.worldventures.dreamtrips.messenger.util.serverfacade;

import com.messenger.messengerservers.LoaderManager;
import com.messenger.messengerservers.loaders.ContactsLoader;
import com.messenger.messengerservers.loaders.ConversationLoader;
import com.messenger.messengerservers.loaders.ConversationsLoader;
import com.messenger.messengerservers.loaders.FlagMessageLoader;

public abstract class BaseLoaderManager implements LoaderManager {
   @Override
   public FlagMessageLoader createFlaggingLoader() {
      return null;
   }

   @Override
   public ConversationsLoader createConversationsLoader() {
      return null;
   }

   @Override
   public ConversationLoader createConversationLoader(String conversationId) {
      return null;
   }

   @Override
   public ContactsLoader createContactLoader() {
      return null;
   }
}
