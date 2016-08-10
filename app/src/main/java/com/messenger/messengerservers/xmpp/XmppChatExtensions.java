package com.messenger.messengerservers.xmpp;

import com.messenger.messengerservers.ChatExtensions;
import com.messenger.messengerservers.event.ClearChatEvent;
import com.messenger.messengerservers.event.ImmutableClearChatEvent;
import com.messenger.messengerservers.event.ImmutableRevertClearingEvent;
import com.messenger.messengerservers.event.RevertClearingEvent;
import com.messenger.messengerservers.xmpp.stanzas.ClearChatIQ;
import com.messenger.messengerservers.xmpp.stanzas.RevertClearChatIQ;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;

import rx.Observable;

import static com.messenger.messengerservers.xmpp.util.JidCreatorHelper.obtainUserJid;

public class XmppChatExtensions implements ChatExtensions {

    private final XmppServerFacade facade;

    public XmppChatExtensions(XmppServerFacade facade) {
        this.facade = facade;
    }

    @Override
    public Observable<ClearChatEvent> clearChat(String chatId, long clearToDate) {
        return facade
                .getConnectionObservable()
                .take(1)
                .flatMap(connection ->
                        XmppSendWithResponseObservable.send(connection, new ClearChatIQ(chatId,
                                obtainUserJid(facade.getUsername()), clearToDate)))
                .map(stanza -> (ClearChatIQ) stanza)
                .map(clearIq -> ImmutableClearChatEvent.builder()
                        .conversationId(clearIq.getChatId())
                        .clearTime(clearIq.getClearDate())
                        .build());
    }

    @Override
    public Observable<RevertClearingEvent> revertChatClearing(String chatId) {
        return facade
                .getConnectionObservable()
                .take(1)
                .flatMap(connection ->
                        XmppSendWithResponseObservable.send(connection, new RevertClearChatIQ(chatId,
                                obtainUserJid(facade.getUsername()))))
                .map(stanza -> (RevertClearChatIQ) stanza)
                .map(revertIq -> ImmutableRevertClearingEvent.builder()
                        .conversationId(revertIq.getChatId())
                        .build());
    }
}
