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
import org.jivesoftware.smack.provider.ProviderManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

public class ParticipantProvider {

    private AbstractXMPPConnection connection;

    public interface OnGroupChatParticipantsLoaded {
        void onLoaded(Participant owner, List<Participant> participants, boolean abandoned);
    }

    public ParticipantProvider(AbstractXMPPConnection connection) {
        this.connection = connection;
        ProviderManager.addIQProvider(
                ConversationParticipantsIQ.ELEMENT_QUERY, ConversationParticipantsIQ.NAMESPACE,
                new ConversationParticipantsProvider(connection.getUser())
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
                        //
                        Participant owner = conversationParticipantsIQ.getOwner();
                        if (owner != null) {
                            owner = new Participant(owner, conversationId);
                        }
                        List<Participant> participants = conversationParticipantsIQ.getParticipants();
                        if (participants != null) {
                            participants = Queryable.from(participants).map(p -> new Participant(p, conversationId)).toList();
                        }
                        boolean abandoned = conversationParticipantsIQ.isAbandoned();
                        //
                        listener.onLoaded(owner, participants, abandoned);
                    },
                    exception -> {
                        Timber.w(exception, "Can't get participants for conversation: %s", conversationId);
                        listener.onLoaded(null, Collections.emptyList(), false);
                    }
            );
        } catch (SmackException.NotConnectedException e) {
            Timber.e(e, "XMPP Exception");
        }
    }

}
