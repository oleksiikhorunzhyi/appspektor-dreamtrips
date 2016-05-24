package com.messenger.messengerservers.xmpp.util;

import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.messengerservers.model.Participant;
import com.messenger.messengerservers.xmpp.stanzas.incoming.ConversationParticipantsIQ;
import com.messenger.messengerservers.xmpp.stanzas.outgoing.ObtainConversationParticipantsIQ;
import com.messenger.messengerservers.xmpp.providers.ConversationParticipantsProvider;
import com.raizlabs.android.dbflow.annotation.NotNull;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.provider.ProviderManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

public class ParticipantProvider {

    private XMPPConnection connection;

    public interface OnGroupChatParticipantsLoaded {
        void onLoaded(List<Participant> participants);
    }

    public ParticipantProvider(XMPPConnection connection) {
        this.connection = connection;
        ProviderManager.addIQProvider(
                ConversationParticipantsIQ.ELEMENT_QUERY, ConversationParticipantsIQ.NAMESPACE,
                new ConversationParticipantsProvider()
        );
    }

    public List<Participant> getSingleChatParticipants(String conversationId) {
        ArrayList<Participant> participants = new ArrayList<>();
        String companionJid = ThreadCreatorHelper.obtainCompanionFromSingleChat(conversationId, connection.getUser());
        Participant companion = new Participant(JidCreatorHelper.obtainId(companionJid), Participant.Affiliation.MEMBER, conversationId);
        participants.add(companion);
        return participants;
    }

    public void loadMultiUserChatParticipants(String conversationId, @NotNull OnGroupChatParticipantsLoaded listener) {
        ObtainConversationParticipantsIQ participantsPacket = new ObtainConversationParticipantsIQ();
        participantsPacket.setTo(JidCreatorHelper.obtainGroupJid(conversationId));
        participantsPacket.setFrom(connection.getUser());

        try {
            connection.sendStanzaWithResponseCallback(participantsPacket,
                    stanza -> stanza instanceof ConversationParticipantsIQ
                            && TextUtils.equals(conversationId, JidCreatorHelper.obtainId(stanza.getFrom())),
                    packet -> {
                        ConversationParticipantsIQ conversationParticipantsIQ = (ConversationParticipantsIQ) packet;
                        List<Participant> participants = conversationParticipantsIQ.getParticipants();
                        if (!participants.isEmpty()) {
                            participants = Queryable.from(participants).map(p -> new Participant(p, conversationId)).toList();
                        }
                        //
                        listener.onLoaded(participants);
                    },
                    exception -> {
                        Timber.w(exception, "Can't get participants for conversation: %s", conversationId);
                        listener.onLoaded(Collections.emptyList());
                    }
            );
        } catch (SmackException.NotConnectedException e) {
            Timber.e(e, "XMPP Exception");
        }
    }

}
