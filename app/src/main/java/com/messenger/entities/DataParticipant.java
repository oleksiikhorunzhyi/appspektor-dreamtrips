package com.messenger.entities;

import android.net.Uri;

import com.messenger.messengerservers.constant.Affiliation;
import com.messenger.messengerservers.model.Participant;
import com.messenger.storage.MessengerDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;
import com.raizlabs.android.dbflow.structure.provider.BaseProviderModel;

@Table(tableName = DataParticipant.TABLE_NAME, databaseName = MessengerDatabase.NAME, insertConflict = ConflictAction.REPLACE)
@TableEndpoint(name = DataParticipant.TABLE_NAME, contentProviderName = MessengerDatabase.NAME)
public class DataParticipant extends BaseProviderModel<DataParticipant> {
   public static final String TABLE_NAME = "Participants";

   @ContentUri(path = TABLE_NAME, type = ContentUri.ContentType.VND_MULTIPLE + TABLE_NAME) public static final Uri CONTENT_URI = MessengerDatabase
         .buildUri(TABLE_NAME);

   @Unique(unique = true, onUniqueConflict = ConflictAction.REPLACE) @PrimaryKey @Column() String id;

   @Column String conversationId;
   @Column String userId;
   @Column long syncTime;
   @Column String affiliation;

   public DataParticipant(Participant participant) {
      affiliation = participant.getAffiliation();
      String conversationId = this.conversationId = participant.getConversationId();
      String userId = this.userId = participant.getUserId();
      id = createId(conversationId, userId);
   }

   public DataParticipant(String conversationId, String userId, @Affiliation String affiliation) {
      this.id = createId(conversationId, userId);
      this.conversationId = conversationId;
      this.userId = userId;
      this.affiliation = affiliation;
   }

   private String createId(String conversationId, String userId) {
      return String.format("%s_%s", conversationId, userId);
   }

   public DataParticipant() {
   }

   public String getId() {
      return id;
   }

   public String getConversationId() {
      return conversationId;
   }

   public String getUserId() {
      return userId;
   }

   public long getSyncTime() {
      return syncTime;
   }

   public void setSyncTime(long syncTime) {
      this.syncTime = syncTime;
   }

   @Affiliation
   public String getAffiliation() {
      return affiliation;
   }

   @Override
   public Uri getDeleteUri() {
      return CONTENT_URI;
   }

   @Override
   public Uri getInsertUri() {
      return CONTENT_URI;
   }

   @Override
   public Uri getUpdateUri() {
      return CONTENT_URI;
   }

   @Override
   public Uri getQueryUri() {
      return CONTENT_URI;
   }

   @Override
   public String toString() {
      return id;
   }
}
