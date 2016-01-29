package com.messenger.delegate;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.converter.UserConverter;
import com.messenger.entities.Conversation;
import com.messenger.messengerservers.model.Participant;
import com.messenger.entities.ParticipantsRelationship;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.UsersDAO;
import com.worldventures.dreamtrips.modules.common.model.User;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import rx.functions.Action1;

public class StartChatDelegate {

    private final UsersDAO usersDAO;
    private final ParticipantsDAO participantsDAO;
    private final ConversationsDAO conversationsDAO;
    private final ChatDelegate chatDelegate;

    public StartChatDelegate(UsersDAO usersDAO, ParticipantsDAO participantsDAO, ConversationsDAO conversationsDAO,
                             ChatDelegate chatDelegate) {
        this.usersDAO = usersDAO;
        this.participantsDAO = participantsDAO;
        this.conversationsDAO = conversationsDAO;
        this.chatDelegate = chatDelegate;
    }

    public void startSingleChat(User user, @NotNull Action1<Conversation> crossingAction){
        if (user.getUsername() == null) return;

        usersDAO.getUserById(user.getUsername())
                .first()
                .map(participant -> {
                    if (participant == null) {
                        participant = UserConverter.convert(user);
                        usersDAO.save(Collections.singletonList(participant));
                    }
                    return participant;
                }).subscribe(participant -> startSingleChat(participant.getId(), crossingAction));
    }

    public void startSingleChat(String participantId, @NotNull Action1<Conversation> crossingAction){
        Conversation conversation = chatDelegate.getExistingSingleConverastion(participantId);
        if (conversation == null){
            conversation = chatDelegate.createNewConversation(Collections.singletonList(participantId), "");
            //there is no owners in single chat
            ParticipantsRelationship relationship = new ParticipantsRelationship(conversation.getId(), participantId, Participant.Affiliation.MEMBER);

            participantsDAO.save(Collections.singletonList(relationship));
            conversationsDAO.save(Collections.singletonList(conversation));
        }
        crossingAction.call(conversation);
    }

    public void startNewGroupChat(String ownerId,
                                  List<String> participantIds,
                                  @Nullable String subject, @NotNull Action1<Conversation> crossingAction) {
        Conversation conversation = chatDelegate.createNewConversation(participantIds, subject);
        conversation.setOwnerId(ownerId);

        List<ParticipantsRelationship> relationships = Queryable.from(participantIds).map(userId ->
                new ParticipantsRelationship(conversation.getId(), userId, Participant.Affiliation.MEMBER)).toList();
        // we are participants too and if conversation is group then we're owner otherwise we're member
        relationships.add(new ParticipantsRelationship(conversation.getId(), ownerId, Participant.Affiliation.OWNER));

        participantsDAO.save(relationships);
        conversationsDAO.save(Collections.singletonList(conversation));

        crossingAction.call(conversation);
    }

}
