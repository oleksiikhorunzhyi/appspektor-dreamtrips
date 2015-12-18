package com.messenger.messengerservers.entities;

import com.messenger.storege.MessengerDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(databaseName = MessengerDatabase.NAME)
public class ParticipantsRelationship extends BaseModel {
    public static final String COLUMN_CONVERSATION = "conversationId";
    public static final String COLUMN_USER = "userId";

    @Unique(unique = true, onUniqueConflict = ConflictAction.IGNORE)
    @PrimaryKey @Column() String id;

    @Column String conversationId;
    @Column String userId;

    public ParticipantsRelationship(String conversationId, User user) {
        id = String.format("%s_%s", conversationId, user.getId());

        this.conversationId = conversationId;
        this.userId = user.getId();
    }

    public ParticipantsRelationship() {
    }
}
