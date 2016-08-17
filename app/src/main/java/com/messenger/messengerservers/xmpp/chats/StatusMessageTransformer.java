package com.messenger.messengerservers.xmpp.chats;

import com.messenger.messengerservers.ConnectionException;
import com.messenger.messengerservers.xmpp.stanzas.outgoing.StatusMessageStanza;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;

import rx.Observable;

class StatusMessageTransformer implements Observable.Transformer<XMPPConnection, String> {
   private final StatusMessageStanza statusMessageStanza;

   StatusMessageTransformer(StatusMessageStanza statusMessageStanza) {
      this.statusMessageStanza = statusMessageStanza;
   }

   @Override
   public Observable<String> call(Observable<XMPPConnection> connectionObservable) {
      return connectionObservable.flatMap(connection -> Observable.create(subscriber -> {
         try {
            connection.sendStanza(statusMessageStanza);
            subscriber.onNext(statusMessageStanza.messageId);
            subscriber.onCompleted();
         } catch (SmackException.NotConnectedException e) {
            subscriber.onError(new ConnectionException(e));
         } catch (Throwable throwable) {
            subscriber.onError(throwable);
         }
      }));
   }
}
