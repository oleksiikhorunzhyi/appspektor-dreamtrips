package com.messenger.messengerservers.xmpp.paginations;

import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.paginations.ImmutablePaginationResult;
import com.messenger.messengerservers.paginations.PagePagination;
import com.messenger.messengerservers.paginations.PaginationResult;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.stanzas.incoming.MessagePageIQ;
import com.messenger.messengerservers.xmpp.stanzas.outgoing.ObtainMessageListIQ;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Stanza;

import java.util.List;

import rx.Observable;
import timber.log.Timber;

public class XmppConversationHistoryPaginator extends PagePagination<Message> {
   private final Observable<XMPPConnection> connectionObservable;

   public XmppConversationHistoryPaginator(XmppServerFacade facade, int pageSize) {
      super(pageSize);
      this.connectionObservable = facade.getConnectionObservable();
   }

   @Override
   public Observable<PaginationResult<Message>> loadPage(String conversationId, int page, long sinceSecs) {
      ObtainMessageListIQ packet = new ObtainMessageListIQ();
      packet.setMax(getPageSize());
      packet.setConversationId(conversationId);
      packet.setPage(page);
      packet.setSinceSec(sinceSecs);
      Timber.i("Send XMPP Packet: %s", packet.toString());

      connectionObservable.take(1).subscribe(connection -> connectionPrepared(connection, packet), this::notifyError);

      return paginationPublishSubject.asObservable();
   }

   public void connectionPrepared(XMPPConnection connection, IQ packet) {
      try {
         connection.sendStanzaWithResponseCallback(packet, this::stanzaFilter, this::stanzaCallback, this::notifyError);
      } catch (Throwable e) {
         notifyError(e);
      }
   }

   private boolean stanzaFilter(Stanza stanza) {
      return stanza instanceof MessagePageIQ || (stanza.getStanzaId() != null && stanza.getStanzaId()
            .startsWith("page"));
   }

   private void stanzaCallback(Stanza stanza) {
      MessagePageIQ messagePageIQ = (MessagePageIQ) stanza;
      notifyLoaded(messagePageIQ.getMessages(), messagePageIQ.getLoadedCount());
   }

   private void notifyLoaded(List<Message> messages, int loadedCount) {
      paginationPublishSubject.onNext(ImmutablePaginationResult.<Message>builder().result(messages)
            .loadedCount(loadedCount)
            .build());
   }

   private void notifyError(Throwable throwable) {
      paginationPublishSubject.onError(throwable);
   }
}
