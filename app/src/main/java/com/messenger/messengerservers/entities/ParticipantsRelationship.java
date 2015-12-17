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

    @Unique(unique = true, onUniqueConflict = ConflictAction.REPLACE)
    @PrimaryKey @Column() String id;

    @Column String conversationId;

    @ForeignKey(
            references = {@ForeignKeyReference(
                    columnName = COLUMN_USER,
                    columnType = String.class,
                    foreignColumnName = User.COLUMN_ID)},
            saveForeignKeyModel = true)
    @Column User user;

    public ParticipantsRelationship(String conversationId, User user) {
        id = String.format("%s_%s", conversationId, user.getId());

        this.conversationId = conversationId;
        this.user = user;
    }

    public ParticipantsRelationship() {
    }
}
