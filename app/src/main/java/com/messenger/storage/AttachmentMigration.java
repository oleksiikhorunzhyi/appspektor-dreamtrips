package com.messenger.storage;

import android.database.sqlite.SQLiteDatabase;

import com.messenger.entities.DataAttachment$Adapter;
import com.messenger.entities.DataParticipant$Adapter;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;

@Migration(version = 2, databaseName = MessengerDatabase.NAME)
public class AttachmentMigration extends BaseMigration {

    @Override
    public void migrate(SQLiteDatabase database) {
        database.execSQL("DROP TABLE ParticipantsRelationship");

        DataParticipant$Adapter participantAdapterAdapter = new DataParticipant$Adapter();
        DataAttachment$Adapter attachmentAdapter = new DataAttachment$Adapter();
        database.execSQL(participantAdapterAdapter.getCreationQuery());
        database.execSQL(attachmentAdapter.getCreationQuery());

    }
}
