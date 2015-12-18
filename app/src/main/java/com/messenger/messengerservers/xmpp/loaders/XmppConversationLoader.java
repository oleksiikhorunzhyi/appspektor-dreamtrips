package com.messenger.messengerservers.xmpp.loaders;

import android.util.Log;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.loaders.Loader;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.packets.ConversationsPacket;
import com.messenger.messengerservers.xmpp.packets.ObtainConversationListPacket;
import com.messenger.messengerservers.xmpp.providers.ConversationProvider;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;
import com.messenger.messengerservers.xmpp.util.ThreadCreatorHelper;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import java.util.ArrayList;
import java.util.List;

public class XmppConversationLoader extends Loader<Conversation> {
    private static final String TAG = "XMPP CONTACT LOADER";

    private static final int MAX_CONVERSATIONS = 30;
    private final XmppServerFacade facade;

    public XmppConversationLoader(XmppServerFacade facade) {
        this.facade = facade;
    }

    @Override
    public void load() {
        ObtainConversationListPacket packet = new ObtainConversationListPacket();
        packet.setMax(MAX_CONVERSATIONS);
        packet.setType(IQ.Type.get);
        Log.i("Send XMPP Packet: ", packet.toString());

        try {
            ProviderManager.addIQProvider(ConversationsPacket.ELEMENT_LIST, ConversationsPacket.NAMESPACE, new ConversationProvider());
            facade.getConnection().sendStanzaWithResponseCallback(packet,
                    (stanza) -> stanza instanceof ConversationsPacket,
                    (stanzaPacket) -> {
                        List<Conversation> conversations = ((ConversationsPacket) stanzaPacket).getConversations();
                        conversations = filterConversations(conversations);
                        conversations = obtainConversationsWithParticipants(conversations);
                        notifyListeners(conversations);
                        ProviderManager.removeIQProvider(ConversationsPacket.ELEMENT_LIST, ConversationsPacket.NAMESPACE);
                    });
        } catch (SmackException.NotConnectedException e) {
            Log.i("XmppConversationLoader", "Loading error", e);
        }
    }

    private List<Conversation> filterConversations(List<Conversation> conversations ){
        List<Conversation> filteredConversations = new ArrayList<>();
        for (Conversation conversation: conversations) {
            if (!(conversation.getType().equals(Conversation.Type.CHAT)) || !isConversationMyself(conversation)){
                filteredConversations.add(conversation);
            }
        }
        return filteredConversations;
    }

    private boolean isConversationMyself(Conversation conversation){
        String companion = ThreadCreatorHelper.obtainCompanionFromSingleChat(conversation, facade.getConnection().getUser());
        return companion == null;
    }

    private List<Conversation> obtainConversationsWithParticipants(List<Conversation> conversations){
        List<Conversation> conversationsWithParticipants = new ArrayList<>(conversations.size());
        List<User> participants;

        for (Conversation conversation: conversations) {
            if(conversation.getType().equals(Conversation.Type.CHAT)) {
                participants = getSingleChatParticipants(conversation);
            } else {
                participants = getMultiUserChat(conversation);
            }

            if (participants != null) {
                conversation.setParticipants(participants);
                conversationsWithParticipants.add(conversation);
            }
        }

        return conversationsWithParticipants;
    }

    private List<User> getSingleChatParticipants(Conversation conversation){
        ArrayList<User> participants = new ArrayList<>();
        String companionJid = ThreadCreatorHelper.obtainCompanionFromSingleChat(conversation, facade.getConnection().getUser());
        participants.add(JidCreatorHelper.obtainUser(companionJid));
        conversation.setParticipants(participants);
        return participants;
    }

    private List<User> getMultiUserChat(Conversation conversation) {
        ArrayList<User> participants = new ArrayList<>();
        AbstractXMPPConnection connection = facade.getConnection();
        MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
        MultiUserChat multiUserChat = multiUserChatManager.getMultiUserChat(JidCreatorHelper.obtainGroupJid(conversation.getId()));

        try {
            if (!multiUserChat.isJoined()){
                multiUserChat.join(JidCreatorHelper.obtainUser(connection.getUser()).getUserName());
            }

            List<Affiliate> affiliates = multiUserChat.getOwners();
            affiliates.addAll(multiUserChat.getMembers());
            for (Affiliate affiliate: affiliates){
                String participantJid = affiliate.getJid();
                participants.add(JidCreatorHelper.obtainUser(participantJid));
            }
            conversation.setParticipants(participants);
        } catch (SmackException | XMPPException.XMPPErrorException e) {
            Log.i(TAG, Log.getStackTraceString(e));
            return null;
        }

        return participants;
    }

    public void notifyListeners(List<Conversation> conversations) {
        if(persister != null){
            persister.save(conversations);
        }
        if (onEntityLoadedListener != null) {
            onEntityLoadedListener.onLoaded(conversations);
        }
    }
}
