package com.messenger.messengerservers.xmpp.loaders;

import android.text.TextUtils;

import com.messenger.delegate.chat.flagging.FlagMessageDTO;
import com.messenger.delegate.chat.flagging.FlagMessageException;
import com.messenger.delegate.chat.flagging.ImmutableFlagMessageDTO;
import com.messenger.messengerservers.ConnectionException;
import com.messenger.messengerservers.loaders.FlagMessageLoader;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.providers.FlaggingProvider;
import com.messenger.messengerservers.xmpp.stanzas.FlagMessageIQ;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;

import rx.Observable;
import rx.Subscriber;

public class XmppFlagMessageLoader implements FlagMessageLoader {

    private final XmppServerFacade xmppServerFacade;

    public XmppFlagMessageLoader(XmppServerFacade facade) {
        this.xmppServerFacade = facade;
        ProviderManager.addIQProvider(
                FlagMessageIQ.ELEMENT_QUERY, FlagMessageIQ.NAMESPACE,
                new FlaggingProvider());
    }

    @Override
    public Observable<FlagMessageDTO> flagMessage(FlagMessageDTO flagMessageDTO) {
        return xmppServerFacade.getConnectionObservable()
                .take(1)
                .flatMap(connection -> flagMessageInternal(flagMessageDTO, connection));
    }

    private Observable<FlagMessageDTO> flagMessageInternal(FlagMessageDTO flagMessageDTO, XMPPConnection xmppConnection) {
        return Observable.<FlagMessageDTO>create(subscriber -> {
            FlagMessageIQ flagMessageIQ = new FlagMessageIQ(flagMessageDTO.messageId(),
                    flagMessageDTO.reasonId(), flagMessageDTO.reasonDescription());
            flagMessageIQ.setTo(JidCreatorHelper.obtainGroupJid(flagMessageDTO.groupId()));

            try {
                xmppConnection.sendIqWithResponseCallback(flagMessageIQ,
                        packet -> processResponse(packet, subscriber, flagMessageDTO),
                        exception -> subscriber.onError(new ConnectionException()));
            } catch (SmackException.NotConnectedException e) {
                subscriber.onError(e);
            }
        });
    }

    private void processResponse(Stanza packet, Subscriber<? super FlagMessageDTO> subscriber,
                                 FlagMessageDTO flagMessageDTO) {
        FlagMessageIQ responseIq = (FlagMessageIQ) packet;
        if (subscriber.isUnsubscribed()) return;
        if (TextUtils.equals(responseIq.getResult(), "success")) {
            subscriber.onNext(ImmutableFlagMessageDTO
                    .copyOf(flagMessageDTO)
                    .withMessageId(responseIq.getMessageId())
                    .withResult(responseIq.getResult())
            );
            subscriber.onCompleted();
        } else {
            subscriber.onError(new FlagMessageException(responseIq.getMessageId(),
                    responseIq.getResult()));
        }

    }


}
