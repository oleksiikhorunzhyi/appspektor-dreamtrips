package com.messenger.delegate.conversation.helper;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataParticipant;
import com.messenger.messengerservers.model.Conversation;
import com.messenger.messengerservers.model.Message;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.util.DecomposeMessagesHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import static com.innahema.collections.query.queriables.Queryable.from;

public class ConversationSyncHelper {

    private final DecomposeMessagesHelper decomposeMessagesHelper;
    private final ConversationsDAO conversationsDAO;
    private final ParticipantsDAO participantsDAO;

    @Inject
    public ConversationSyncHelper(DecomposeMessagesHelper decomposeMessagesHelper,
                                  ConversationsDAO conversationsDAO, ParticipantsDAO participantsDAO) {
        this.decomposeMessagesHelper = decomposeMessagesHelper;
        this.conversationsDAO = conversationsDAO;
        this.participantsDAO = participantsDAO;
    }

    public void process(Conversation conversation) {
        final long syncTime = System.currentTimeMillis();
        List<Conversation> conversations = Collections.singletonList(conversation);

        saveConversations(conversations, syncTime);
        saveLastMessages(conversations, syncTime);
        saveParticipants(conversations, syncTime);
        participantsDAO.deleteBySyncTime(syncTime, conversations.get(0).getId());
    }

    public void process(List<Conversation> conversations) {
        final long syncTime = System.currentTimeMillis();

        saveConversations(conversations, syncTime);
        conversationsDAO.deleteBySyncTime(syncTime);
        saveLastMessages(conversations, syncTime);
        saveParticipants(conversations, syncTime);
        participantsDAO.deleteBySyncTime(syncTime);
    }

    private void saveConversations(List<Conversation> conversations, long syncTime) {
        List<DataConversation> dataConversations = from(conversations).map(DataConversation::new).toList();
        from(dataConversations).forEachR(conversation -> conversation.setSyncTime(syncTime));
        conversationsDAO.save(dataConversations);
    }

    private void saveLastMessages(List<Conversation> conversations, long syncTime) {
        DecomposeMessagesHelper.Result result = decomposeMessagesHelper.decomposeMessages(from(conversations)
                .map(Conversation::getLastMessage)
                .notNulls()
                .toList());
        from(result.messages).forEachR(msg -> msg.setSyncTime(syncTime));
        decomposeMessagesHelper.saveDecomposeMessage(result);
    }

    private void saveParticipants(List<Conversation> conversations, long syncTime) {
        List<DataParticipant> relationships = new ArrayList<>();
        if (!conversations.isEmpty()) {
            from(conversations)
                    .filter(conversation -> conversation.getParticipants() != null)
                    .map(Conversation::getParticipants)
                    .map(participants -> from(participants)
                            .map(DataParticipant::new)
                            .map(participant -> {
                                participant.setSyncTime(syncTime);
                                return participant;
                            })
                            .toList()
                    )
                    .forEachR(relationships::addAll);
        }

        participantsDAO.save(relationships);
    }

}
