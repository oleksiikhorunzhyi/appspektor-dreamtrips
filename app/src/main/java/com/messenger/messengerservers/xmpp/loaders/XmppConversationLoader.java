package com.messenger.messengerservers.xmpp.loaders;

import android.os.Looper;
import android.util.Log;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.loaders.Loader;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.packets.ConversationsPacket;
import com.messenger.messengerservers.xmpp.packets.ObtainConversationListPacket;
import com.messenger.messengerservers.xmpp.providers.ConversationProvider;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
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
                        Log.e("isMainThread", ""+(Looper.myLooper() == Looper.getMainLooper()));

                        List<Conversation> conversations = ((ConversationsPacket) stanzaPacket).getConversations();
                        loadParticipants(conversations);
                        notifyListeners(conversations);
                        ProviderManager.removeIQProvider(ConversationsPacket.ELEMENT_LIST, ConversationsPacket.NAMESPACE);
                    });
        } catch (SmackException.NotConnectedException e) {
            Log.i("XmppConversationLoader", "Loading error", e);
        }
    }

    private void loadParticipants(List<Conversation> conversations){
        for (Conversation conversation: conversations) {
            if(conversation.getType().equals(Conversation.Type.CHAT)) {
                conversation.setParticipants(getSingleChatParticipants(conversation));
            } else {
                conversation.setParticipants(getMultiUserChat(conversation));
            }
        }
    }

    private List<User> getSingleChatParticipants(Conversation conversation){
        ArrayList<User> participants = new ArrayList<>();
        AbstractXMPPConnection connection = facade.getConnection();
        ChatManager chatManager = ChatManager.getInstanceFor(connection);

        Chat chat = chatManager.getThreadChat(conversation.getId());
        if (chat == null) {
            chat = chatManager.createChat(connection.getUser(), conversation.getId(), null);
        }

        String participantJid = chat.getParticipant();
        participants.add(JidCreatorHelper.obtainUser(participantJid));
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
