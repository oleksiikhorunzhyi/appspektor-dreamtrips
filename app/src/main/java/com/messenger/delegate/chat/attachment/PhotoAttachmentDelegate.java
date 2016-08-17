package com.messenger.delegate.chat.attachment;

import android.support.annotation.NonNull;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.delegate.AttachmentDelegateHelper;
import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataPhotoAttachment;
import com.messenger.messengerservers.constant.AttachmentType;
import com.messenger.storage.dao.PhotoDAO;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@Singleton
public class PhotoAttachmentDelegate {
   private final ActionPipe<SendImageAttachmentCommand> sendImagePipe;

   private final PhotoDAO photoDAO;
   private final SessionHolder<UserSession> sessionHolder;
   private final AttachmentDelegateHelper attachmentDelegateHelper;

   @Inject
   public PhotoAttachmentDelegate(PhotoDAO photoDAO, SessionHolder<UserSession> sessionHolder, Janet janet) {
      this.sessionHolder = sessionHolder;
      this.photoDAO = photoDAO;
      this.sendImagePipe = janet.createPipe(SendImageAttachmentCommand.class, Schedulers.io());
      this.attachmentDelegateHelper = new AttachmentDelegateHelper();
   }

   public void retry(String conversationId, DataMessage dataMessage, DataAttachment dataAttachment) {
      photoDAO.getAttachmentById(dataAttachment.getId())
            .take(1)
            .subscribe(photoAttachment -> sendImageInternally(conversationId, dataMessage, dataAttachment, photoAttachment), throwable -> Timber
                  .e(throwable, ""));
   }

   public void sendImages(String conversationId, List<String> filePaths) {
      Queryable.from(filePaths).forEachR(path -> send(conversationId, path));
   }

   public void send(@NonNull String conversationId, @NonNull String filePath) {
      String userId = sessionHolder.get().get().getUsername();
      DataMessage emptyMessage = attachmentDelegateHelper.createEmptyMessage(userId, conversationId);
      DataAttachment dataAttachment = attachmentDelegateHelper.createDataAttachment(emptyMessage, AttachmentType.IMAGE);
      DataPhotoAttachment dataPhotoAttachment = attachmentDelegateHelper.createEmptyPhotoAttachment(dataAttachment);
      dataPhotoAttachment.setLocalPath(rectificationScheme(filePath));
      sendImageInternally(conversationId, emptyMessage, dataAttachment, dataPhotoAttachment);
   }

   private void sendImageInternally(String conversationId, DataMessage dataMessage, DataAttachment dataAttachment, DataPhotoAttachment dataPhotoAttachment) {
      sendImagePipe.send(new SendImageAttachmentCommand(conversationId, dataMessage, dataAttachment, dataPhotoAttachment));
   }


   private String rectificationScheme(@NonNull String filePath) {
      return filePath.contains("://") ? filePath : "file://" + filePath;
   }

}
