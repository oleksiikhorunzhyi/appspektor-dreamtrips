package com.messenger.messengerservers.xmpp.loaders;

import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.messengerservers.constant.Affiliation;
import com.messenger.messengerservers.model.ImmutableParticipant;
import com.messenger.messengerservers.model.Participant;
import com.messenger.messengerservers.model.ParticipantItem;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.providers.ConversationParticipantsProvider;
import com.messenger.messengerservers.xmpp.stanzas.incoming.ConversationParticipantsIQ;
import com.messenger.messengerservers.xmpp.stanzas.outgoing.ObtainConversationParticipantsIQ;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;
import com.messenger.messengerservers.xmpp.util.ThreadCreatorHelper;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class XmppParticipantsLoader {

   private final XmppServerFacade facade;

   public XmppParticipantsLoader(XmppServerFacade facade) {
      this.facade = facade;
      ProviderManager.addIQProvider(ConversationParticipantsIQ.ELEMENT_QUERY, ConversationParticipantsIQ.NAMESPACE, new ConversationParticipantsProvider());
   }

   public Observable<Participant> getSingleChatParticipants(String conversationId) {
      String companionId = ThreadCreatorHelper.obtainCompanionIdFromSingleChatId(conversationId, facade.getUsername());
      Participant companion = ImmutableParticipant.builder()
            .userId(companionId)
            .affiliation(Affiliation.MEMBER)
            .conversationId(conversationId)
            .build();
      return Observable.just(companion);
   }

   public Observable<List<Participant>> loadMultiUserChatParticipants(String conversationId) {
      return facade.getConnectionObservable()
            .take(1)
            .flatMap(xmppConnection -> loadMultiUserChatParticipants(conversationId, xmppConnection));
   }

   private Observable<List<Participant>> loadMultiUserChatParticipants(String conversationId, XMPPConnection connection) {
      return Observable.<List<Participant>>create(subscriber -> {
         ObtainConversationParticipantsIQ participantsPacket = new ObtainConversationParticipantsIQ();
         participantsPacket.setTo(JidCreatorHelper.obtainGroupJid(conversationId));
         participantsPacket.setFrom(connection.getUser());

         try {
            connection.sendStanzaWithResponseCallback(participantsPacket, stanza -> stanzaFilter(stanza, conversationId), packet -> processPacket((ConversationParticipantsIQ) packet, conversationId, subscriber), exception -> pushParticipantsListToSubscriber(Collections
                  .emptyList(), subscriber));
         } catch (SmackException.NotConnectedException e) {
            subscriber.onError(e);
         }
      });
   }

   private boolean stanzaFilter(Stanza stanza, String conversationId) {
      return stanza instanceof ConversationParticipantsIQ && TextUtils.equals(conversationId, JidCreatorHelper.obtainId(stanza
            .getFrom()));
   }

   private void processPacket(ConversationParticipantsIQ participantsIQPacket, String conversationId, Subscriber<? super List<Participant>> subscriber) {
      List<ParticipantItem> participantItems = participantsIQPacket.getParticipantItems();
      if (participantItems.isEmpty()) {
         pushParticipantsListToSubscriber(Collections.emptyList(), subscriber);
         return;
      }
      List<Participant> participants = Queryable.from(participantItems)
            .map(item -> (Participant) ImmutableParticipant.builder()
                  .conversationId(conversationId)
                  .userId(item.getUserId())
                  .affiliation(item.getAffiliation())
                  .build())
            .toList();

      pushParticipantsListToSubscriber(participants, subscriber);
   }

   private void pushParticipantsListToSubscriber(List<Participant> participants, Subscriber<? super List<Participant>> subscriber) {
      if (subscriber.isUnsubscribed()) return;

      subscriber.onNext(participants);
      subscriber.onCompleted();
   }

}