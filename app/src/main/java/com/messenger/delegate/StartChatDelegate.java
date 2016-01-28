package com.messenger.delegate;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.converter.UserConverter;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Participant;
import com.messenger.messengerservers.entities.ParticipantsRelationship;
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
                }).subscribe(participant -> startSingleChat(participant, crossingAction));
    }

    public void startSingleChat(com.messenger.messengerservers.entities.User participant, @NotNull Action1<Conversation> crossingAction){
        Conversation conversation = chatDelegate.getExistingSingleConverastion(participant);
        if (conversation == null){
            conversation = chatDelegate.createNewConversation(Collections.singletonList(participant), "");
            //there is no owners in single chat
            ParticipantsRelationship relationship = new ParticipantsRelationship(conversation.getId(), participant, Participant.Affiliation.MEMBER);

            participantsDAO.save(Collections.singletonList(relationship));
            conversationsDAO.save(Collections.singletonList(conversation));
        }
        crossingAction.call(conversation);
    }

    public void startNewGroupChat(com.messenger.messengerservers.entities.User owner,
                                  List<com.messenger.messengerservers.entities.User> participants,
                                  @Nullable String subject, @NotNull Action1<Conversation> crossingAction) {
        Conversation conversation = chatDelegate.createNewConversation(participants, subject);
        conversation.setOwnerId(owner.getId());

        List<ParticipantsRelationship> relationships = Queryable.from(participants).map(u ->
                new ParticipantsRelationship(conversation.getId(), u, Participant.Affiliation.MEMBER)).toList();
        // we are participants too and if conversation is group then we're owner otherwise we're member
        relationships.add(new ParticipantsRelationship(conversation.getId(), owner, Participant.Affiliation.OWNER));

        participantsDAO.save(relationships);
        conversationsDAO.save(Collections.singletonList(conversation));

        crossingAction.call(conversation);
    }

}
