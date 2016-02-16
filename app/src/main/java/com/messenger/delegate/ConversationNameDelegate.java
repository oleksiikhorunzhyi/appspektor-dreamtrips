package com.messenger.delegate;

import android.text.TextUtils;

import com.messenger.entities.DataUser;
import com.messenger.entities.DataUser$Table;
import com.messenger.storage.dao.ParticipantsDAO;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import timber.log.Timber;

public class ConversationNameDelegate {
    private final ParticipantsDAO participantsDAO;

    public ConversationNameDelegate(ParticipantsDAO participantsDAO) {
        this.participantsDAO = participantsDAO;
    }

    public Observable<String> obtainGroupConversationName(String conversationId) {
        return participantsDAO.getParticipants(conversationId)
                .first()
                .map(participantsCursor -> {
                    List<String> names = new ArrayList<>(participantsCursor.getCount());
                    while (participantsCursor.moveToNext()) {
                        names.add(participantsCursor.getString(participantsCursor.getColumnIndex(DataUser$Table.USERNAME)));
                    }
                    participantsCursor.close();
                    return TextUtils.join(", ", names);
                })
                .doOnNext(defaultName -> Timber.d("Obtain name  %s for conversation %s", defaultName, conversationId));
    }


    public String obtainGroupConversationName(List<DataUser> dataUsers) {
        StringBuilder builder = new StringBuilder();
        for (DataUser user : dataUsers) {
            if (builder.length() > 0) builder.append(", ");
            builder.append(user.getUserName());
        }
        return builder.toString();
    }
}
