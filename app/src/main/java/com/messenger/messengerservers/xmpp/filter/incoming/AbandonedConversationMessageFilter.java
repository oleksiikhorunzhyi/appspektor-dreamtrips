package com.messenger.messengerservers.xmpp.filter.incoming;

import com.messenger.delegate.conversation.LoadConversationDelegate;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;
import com.messenger.messengerservers.xmpp.util.XmppPacketDetector;
import com.messenger.ui.helper.ConversationHelper;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;

import org.jivesoftware.smack.packet.Message;

import rx.Observable;

public class AbandonedConversationMessageFilter extends BaseIncomingMessageFilter {

   private LoadConversationDelegate loadConversationDelegate;

   public AbandonedConversationMessageFilter(LoadConversationDelegate loadConversationDelegate) {
      this.loadConversationDelegate = loadConversationDelegate;
   }

   @Override
   public Observable<Boolean> skipMessage(int type, Message message) {
      switch (type) {
         case XmppPacketDetector.EXTENTION_STATUS:
            if (message.getType() != org.jivesoftware.smack.packet.Message.Type.chat) {
               String conversationId = JidCreatorHelper.obtainId(message.getFrom());
               return checkIfAbandonedConversation(conversationId);
            }
            break;
         case XmppPacketDetector.MESSAGE:
            return checkIfAbandonedConversation(message.getThread());
      }
      return Observable.just(false);
   }

   public Observable<Boolean> checkIfAbandonedConversation(String conversationId) {
      return loadConversationDelegate.loadConversationFromDb(conversationId)
            .compose(new NonNullFilter<>())
            .switchIfEmpty(loadConversationDelegate.loadConversationFromNetworkAndRefreshFromDb(conversationId))
            .map(ConversationHelper::isAbandoned);
   }
}
