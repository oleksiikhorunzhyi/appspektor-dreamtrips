package com.messenger.entities;

import android.net.Uri;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.messengerservers.constant.AttachmentType;
import com.messenger.messengerservers.model.AttachmentHolder;
import com.messenger.messengerservers.model.ImageAttachment;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.model.MessageBody;
import com.messenger.storage.MessengerDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;
import com.raizlabs.android.dbflow.structure.provider.BaseProviderModel;

import java.util.Collections;
import java.util.List;

@Table(tableName = DataPhotoAttachment.TABLE_NAME, databaseName = MessengerDatabase.NAME, insertConflict = ConflictAction.REPLACE)
@TableEndpoint(name = DataPhotoAttachment.TABLE_NAME, contentProviderName = MessengerDatabase.NAME)
public class DataPhotoAttachment extends BaseProviderModel<DataAttachment> {
   public static final String TABLE_NAME = "Photos";

   @ContentUri(path = TABLE_NAME, type = ContentUri.ContentType.VND_MULTIPLE + TABLE_NAME) public static final Uri CONTENT_URI = MessengerDatabase
         .buildUri(TABLE_NAME);

   @Unique(unique = true, onUniqueConflict = ConflictAction.REPLACE) @PrimaryKey @Column String photoAttachmentId;
   @Column String url;
   @Column String localPath;
   @Column @PhotoAttachmentStatus int uploadState;

   @SuppressWarnings("PMD.UnnecessaryConstructor")
   public DataPhotoAttachment() {
      //do nothing
   }

   private DataPhotoAttachment(Builder builder) {
      setPhotoAttachmentId(builder.id);
      setUrl(builder.url);
      setLocalPath(builder.localPath);
      setUploadState(builder.state);
   }

   public DataPhotoAttachment(@NonNull ImageAttachment attachment, Message message, int index) {
      this.photoAttachmentId = createId(message.getId(), index);
      url = attachment.getOriginUrl();
   }

   private String createId(String messageId, int index) {
      return String.format("%s__%s", messageId, index);
   }

   public String getPhotoAttachmentId() {
      return photoAttachmentId;
   }

   public void setPhotoAttachmentId(String id) {
      this.photoAttachmentId = id;
   }

   @Nullable
   public String getUrl() {
      return url;
   }

   public void setUrl(@Nullable String url) {
      this.url = url;
   }

   @Nullable
   public String getLocalPath() {
      return localPath;
   }

   public void setLocalPath(@Nullable String localPath) {
      this.localPath = localPath;
   }

   @PhotoAttachmentStatus
   public int getUploadState() {
      return uploadState;
   }

   public void setUploadState(@PhotoAttachmentStatus int uploadState) {
      this.uploadState = uploadState;
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

   @NonNull
   public static List<DataPhotoAttachment> fromMessage(@NonNull Message message) {
      MessageBody body = message.getMessageBody();
      List<AttachmentHolder> attachmentHolders;
      if (body == null || (attachmentHolders = body.getAttachments()) == null || attachmentHolders.isEmpty()) {
         return Collections.emptyList();
      }

      return Queryable.from(attachmentHolders)
            .filter(attachmentHolder -> attachmentHolder != null)
            .filter(attachmentHolder -> TextUtils.equals(attachmentHolder.getType(), AttachmentType.IMAGE))
            .map((elem, idx) -> new DataPhotoAttachment((ImageAttachment) elem.getItem(), message, idx))
            .toList();
   }

   public static final class Builder {
      private String id;
      private String url;
      private int state = PhotoAttachmentStatus.UPLOADED;
      private String localPath;

      public Builder id(String var) {
         id = var;
         return this;
      }

      public Builder url(String val) {
         url = val;
         return this;
      }

      public Builder state(@PhotoAttachmentStatus int val) {
         state = val;
         return this;
      }

      public Builder localPath(String val) {
         localPath = val;
         return this;
      }

      public DataPhotoAttachment build() {
         return new DataPhotoAttachment(this);
      }

   }

   @IntDef({PhotoAttachmentStatus.FAILED, PhotoAttachmentStatus.UPLOADED, PhotoAttachmentStatus.UPLOADING, PhotoAttachmentStatus.NONE})
   public @interface PhotoAttachmentStatus {
      int NONE = 0x74829;
      int FAILED = 0x74830;
      int UPLOADED = 0x74831;
      int UPLOADING = 0x74832;
   }
}
