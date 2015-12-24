package com.messenger.messengerservers.xmpp.util;


import android.util.Log;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.xmpp.packets.ConversationParticipants;
import com.messenger.messengerservers.xmpp.packets.ObtainConversationParticipants;
import com.messenger.messengerservers.xmpp.providers.ConversationParticipantsProvider;
import com.raizlabs.android.dbflow.annotation.NotNull;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.ProviderManager;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class ParticipantProvider {

    private AbstractXMPPConnection connection;

    public interface OnGroupChatParticipantsLoaded {
        void onLoaded(User user, List<User> participants);
    }

    public ParticipantProvider(AbstractXMPPConnection connection) {
        this.connection = connection;
    }

    public List<User> getSingleChatParticipants(Conversation conversation) {
        ArrayList<User> participants = new ArrayList<>();
        String companionJid = ThreadCreatorHelper.obtainCompanionFromSingleChat(conversation, connection.getUser());
        participants.add(JidCreatorHelper.obtainUser(companionJid));
        return participants;
    }

    public void loadMultiUserChatParticipants(Conversation conversation, @NotNull OnGroupChatParticipantsLoaded listener) {
        ObtainConversationParticipants participantsPacket = new ObtainConversationParticipants();
        participantsPacket.setTo(JidCreatorHelper.obtainGroupJid(conversation.getId()));
        participantsPacket.setFrom(connection.getUser());
        ProviderManager.addIQProvider(ConversationParticipants.ELEMENT_QUERY, ConversationParticipants.NAMESPACE,
                new ConversationParticipantsProvider());

        try {
            connection.sendStanzaWithResponseCallback(participantsPacket,
                    stanza -> stanza instanceof ConversationParticipants
                            && conversation.getId().equals(JidCreatorHelper.obtainId(stanza.getFrom())),
                    packet -> {
                        if (packet == null) {
                            // TODO should we throw exception?
                            listener.onLoaded(null, null);
                            return;
                        }
                        Timber.d("HANDLE PACKAGE + " + packet.hashCode());
                        ConversationParticipants conversationParticipants = (ConversationParticipants) packet;
                        listener.onLoaded(conversationParticipants.getOwner(), conversationParticipants.getParticipants());
                    }
                    , exception -> listener.onLoaded(null, null)
            );
        } catch (SmackException.NotConnectedException e) {
            Log.e("Xmpp", "Exception", e);
        }
    }

}
