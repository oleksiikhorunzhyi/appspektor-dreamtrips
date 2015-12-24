package com.messenger.messengerservers.xmpp.loaders;

import android.util.Log;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.ConversationWithParticipants;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.loaders.Loader;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.entities.ConversationWithLastMessage;
import com.messenger.messengerservers.xmpp.packets.ConversationsPacket;
import com.messenger.messengerservers.xmpp.packets.ObtainConversationListPacket;
import com.messenger.messengerservers.xmpp.providers.ConversationProvider;
import com.messenger.messengerservers.xmpp.util.ParticipantProvider;
import com.messenger.messengerservers.xmpp.util.ThreadCreatorHelper;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.ProviderManager;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.messenger.messengerservers.entities.Conversation.Type.CHAT;

public class XmppConversationLoader extends Loader<ConversationWithParticipants> {
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

        ProviderManager.addIQProvider(ConversationsPacket.ELEMENT_LIST, ConversationsPacket.NAMESPACE, new ConversationProvider());
        try {
            facade.getConnection().sendStanzaWithResponseCallback(packet,
                    (stanza) -> stanza instanceof ConversationsPacket,
                    (stanzaPacket) -> {
                        List<ConversationWithLastMessage> conversations = ((ConversationsPacket) stanzaPacket).getConversations();
                        obtainConversationsWithParticipants(conversations)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(conversationWithParticipants -> {
                                    Log.e("Loaded", "size: " + conversationWithParticipants.size());
                                    notifyListeners(conversationWithParticipants);
                                    ProviderManager.removeIQProvider(ConversationsPacket.ELEMENT_LIST, ConversationsPacket.NAMESPACE);
                                });
                    });
        } catch (SmackException.NotConnectedException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    private Observable<List<ConversationWithParticipants>> obtainConversationsWithParticipants(List<ConversationWithLastMessage> conversations) {
        ParticipantProvider provider = new ParticipantProvider(facade.getConnection());
        return Observable.from(conversations)
                .filter(c -> !hasNoOtherUsers(c.conversation))
                .flatMap(conversationWithLastMessage -> Observable.<ConversationWithParticipants>create(subscriber -> {
                    Conversation conversation = conversationWithLastMessage.conversation;
                    if (conversation.getType().equals(CHAT)) {
                        List<User> users = provider.getSingleChatParticipants(conversation);
                        if (subscriber.isUnsubscribed()) return;
                        //
                        subscriber.onNext(new ConversationWithParticipants(conversationWithLastMessage.lastMessage, conversation, users));
                        subscriber.onCompleted();
                    } else {
                        provider.loadMultiUserChatParticipants(conversation, (owner, members) -> {
                            if (subscriber.isUnsubscribed()) return;
                            if (owner == null) {
                                subscriber.onCompleted();
                                return;
                            }
                            //
                            conversation.setOwnerId(owner.getId());
                            members.add(0, owner);
                            subscriber.onNext(new ConversationWithParticipants(conversationWithLastMessage.lastMessage, conversation, members));
                            subscriber.onCompleted();
                        });
                    }
                }))
                .toList();
    }

    private boolean hasNoOtherUsers(Conversation conversation) {
        String companion = ThreadCreatorHelper.obtainCompanionFromSingleChat(conversation, facade.getConnection().getUser());
        return companion == null;
    }

    public void notifyListeners(List<ConversationWithParticipants> conversations) {
        if (persister != null) {
            persister.save(conversations);
        }
        if (onEntityLoadedListener != null) {
            onEntityLoadedListener.onLoaded(conversations);
        }
    }
}
