package com.messenger.messengerservers.xmpp.loaders;

import com.messenger.messengerservers.ConnectionException;
import com.messenger.messengerservers.loaders.ConversationsLoader;
import com.messenger.messengerservers.model.Conversation;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.providers.ConversationListProvider;
import com.messenger.messengerservers.xmpp.stanzas.incoming.ConversationListIQ;
import com.messenger.messengerservers.xmpp.stanzas.outgoing.ObtainConversationListIQ;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;

import java.util.List;

import rx.Observable;

public class XmppConversationsListLoader extends XmppBaseConversationsLoader implements ConversationsLoader {

   private static final int MAX_CONVERSATIONS = 512;

   public XmppConversationsListLoader(XmppServerFacade facade) {
      super(facade);
      ConversationListProvider provider = new ConversationListProvider(facade.getGson());
      ProviderManager.addIQProvider(ConversationListIQ.ELEMENT_LIST, ConversationListIQ.NAMESPACE, provider);
   }

   @Override
   public Observable<List<Conversation>> load() {
      return facade.getConnectionObservable().take(1).flatMap(this::loadConversations).flatMap(this::loadParticipants);
   }

   private Observable<List<Conversation>> loadConversations(XMPPConnection connection) {
      return Observable.<List<Conversation>>create(subscriber -> {
         try {
            connection.sendStanzaWithResponseCallback(createConversationStanza(), this::stanzaFilter, stanza -> {
               List<Conversation> data = ((ConversationListIQ) stanza).getConversations();
               subscriber.onNext(data);
               subscriber.onCompleted();
            }, subscriber::onError);
         } catch (SmackException.NotConnectedException e) {
            subscriber.onError(new ConnectionException(e));
         }
      }).doOnUnsubscribe(() -> ProviderManager.removeIQProvider(ConversationListIQ.ELEMENT_LIST, ConversationListIQ.NAMESPACE));
   }

   private boolean stanzaFilter(Stanza stanza) {
      return stanza instanceof ConversationListIQ;
   }

   private Stanza createConversationStanza() {
      ObtainConversationListIQ packet = new ObtainConversationListIQ();
      packet.setMax(MAX_CONVERSATIONS);
      packet.setType(IQ.Type.get);
      return packet;
   }

   private Observable<List<Conversation>> loadParticipants(List<Conversation> conversations) {
      return obtainParticipants(new XmppParticipantsLoader(facade), conversations);
   }
}
