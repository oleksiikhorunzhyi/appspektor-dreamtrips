package com.messenger.storage;

import android.database.sqlite.SQLiteDatabase;

import com.messenger.entities.DataAttachment$Adapter;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataConversation$Adapter;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataMessage$Adapter;
import com.messenger.entities.DataParticipant$Adapter;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;

@Migration(version = 2, databaseName = MessengerDatabase.NAME)
public class MigrationToVersion2 extends BaseMigration {

    @Override
    public void migrate(SQLiteDatabase database) {
        String dropTable = "DROP TABLE ";
        database.execSQL(dropTable + "ParticipantsRelationship");
        database.execSQL(dropTable + DataMessage.TABLE_NAME);
        database.execSQL(dropTable + DataConversation.TABLE_NAME);

        DataParticipant$Adapter participantAdapter = new DataParticipant$Adapter();
        DataAttachment$Adapter attachmentAdapter = new DataAttachment$Adapter();
        DataMessage$Adapter dataMessageAdapter = new DataMessage$Adapter();
        DataConversation$Adapter dataConversationAdapter = new DataConversation$Adapter();

        database.execSQL(dataMessageAdapter.getCreationQuery());
        database.execSQL(participantAdapter.getCreationQuery());
        database.execSQL(attachmentAdapter.getCreationQuery());
        database.execSQL(dataConversationAdapter.getCreationQuery());

    }
}
