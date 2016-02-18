package com.messenger.storage.helper;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.ui.helper.ConversationHelper;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import java.util.Collections;
import java.util.List;

import rx.Observable;

public class ParticipantsDaoHelper {

    private ParticipantsDAO participantsDAO;

    public ParticipantsDaoHelper(ParticipantsDAO participantsDAO) {
        this.participantsDAO = participantsDAO;
    }

    public Observable<List<DataUser>> obtainParticipantsStream(DataConversation dataConversation, DataUser currentUser) {
        String conversationId = dataConversation.getId();

        if (ConversationHelper.isGroup(dataConversation)) {
            return participantsDAO.getParticipantsEntities(conversationId);
        } else {
            return participantsDAO.getParticipant(conversationId, currentUser.getId())
                    .map(dataUser -> dataUser != null ? Collections.singletonList(dataUser) : Collections.<DataUser>emptyList());
        }
    }
}
