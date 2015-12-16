package com.messenger.messengerservers.entities;

import com.messenger.storege.MessengerDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.container.ForeignKeyContainer;

@Table(databaseName = MessengerDatabase.NAME)
public class ParticipantsRelationship extends BaseModel {
    public static final String COLUMN_CONVERSATION = "conversationId";

    @Unique(unique = true, onUniqueConflict = ConflictAction.IGNORE)
    @PrimaryKey @Column() String id;

    @Column
    @ForeignKey(references = {
            @ForeignKeyReference(columnName = COLUMN_CONVERSATION,
                    columnType = String.class,
                    foreignColumnName = Conversation.COLUMN_ID)},
            saveForeignKeyModel = false,
            onDelete = ForeignKeyAction.CASCADE,
            onUpdate = ForeignKeyAction.CASCADE)
    protected ForeignKeyContainer<Conversation> conversation;

    @Column
    @ForeignKey(references = {
            @ForeignKeyReference(columnName = "userId",
                    columnType = String.class,
                    foreignColumnName = User.COLUMN_ID)},
            saveForeignKeyModel = false,
            onDelete = ForeignKeyAction.CASCADE,
            onUpdate = ForeignKeyAction.CASCADE)
    protected User user;

    public ParticipantsRelationship(Conversation conversation, User user) {
        this.conversation = new ForeignKeyContainer<>(Conversation.class);
        this.conversation.setModel(conversation);

        this.user = user;
    }

    public ParticipantsRelationship() {

    }
}
