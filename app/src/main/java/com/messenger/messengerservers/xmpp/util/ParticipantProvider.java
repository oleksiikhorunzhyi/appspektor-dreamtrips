package com.messenger.messengerservers.xmpp.util;

import com.innahema.collections.query.queriables.Queryable;

import com.messenger.messengerservers.model.Conversation;
import com.messenger.messengerservers.model.Participant;
import com.messenger.messengerservers.xmpp.packets.ConversationParticipants;
import com.messenger.messengerservers.xmpp.packets.ObtainConversationParticipants;
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
                ConversationParticipants.ELEMENT_QUERY, ConversationParticipants.NAMESPACE,
                new ConversationParticipantsProvider(connection.getUser())
        );
    }

    public List<Participant> getSingleChatParticipants(Conversation conversation) {
        ArrayList<Participant> participants = new ArrayList<>();
        String companionJid = ThreadCreatorHelper.obtainCompanionFromSingleChat(conversation.getId(), connection.getUser());
        Participant companion = new Participant(JidCreatorHelper.obtainId(companionJid), Participant.Affiliation.MEMBER, conversation.getId());
        participants.add(companion);
        return participants;
    }

    public void loadMultiUserChatParticipants(Conversation conversation, @NotNull OnGroupChatParticipantsLoaded listener) {
        ObtainConversationParticipants participantsPacket = new ObtainConversationParticipants();
        participantsPacket.setTo(JidCreatorHelper.obtainGroupJid(conversation.getId()));
        participantsPacket.setFrom(connection.getUser());

        try {
            connection.sendStanzaWithResponseCallback(participantsPacket,
                    stanza -> stanza instanceof ConversationParticipants
                            && conversation.getId().equals(JidCreatorHelper.obtainId(stanza.getFrom())),
                    packet -> {
                        ConversationParticipants conversationParticipants = (ConversationParticipants) packet;
                        //
                        Participant owner = conversationParticipants.getOwner();
                        if (owner != null) {
                            owner = new Participant(owner, conversation.getId());
                        }
                        List<Participant> participants = conversationParticipants.getParticipants();
                        if (participants != null) {
                            participants = Queryable.from(participants).map(p -> new Participant(p, conversation.getId())).toList();
                        }
                        boolean abandoned = conversationParticipants.isAbandoned();
                        //
                        listener.onLoaded(owner, participants, abandoned);
                    },
                    exception -> {
                        Timber.w(exception, "Can't get participants for conversation: %s", conversation);
                        listener.onLoaded(null, Collections.emptyList(), false);
                    }
            );
        } catch (SmackException.NotConnectedException e) {
            Timber.e(e, "XMPP Exception");
        }
    }

}