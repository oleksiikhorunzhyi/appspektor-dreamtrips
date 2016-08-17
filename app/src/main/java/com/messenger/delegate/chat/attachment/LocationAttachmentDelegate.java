package com.messenger.delegate.chat.attachment;

import android.location.Location;

import com.messenger.delegate.AttachmentDelegateHelper;
import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataLocationAttachment;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.AttachmentType;
import com.messenger.storage.dao.LocationDAO;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import timber.log.Timber;

public class LocationAttachmentDelegate {

   private final ActionPipe<SendLocationAttachmentCommand> sendLocationPipe;

   private final DataUser currentUser;
   private final AttachmentDelegateHelper attachmentDelegateHelper;
   private final LocationDAO locationDAO;

   @Inject
   public LocationAttachmentDelegate(LocationDAO locationDAO, DataUser currentUser, Janet janet) {
      this.currentUser = currentUser;
      this.locationDAO = locationDAO;

      this.sendLocationPipe = janet.createPipe(SendLocationAttachmentCommand.class);
      this.attachmentDelegateHelper = new AttachmentDelegateHelper();
   }

   public void retry(String conversationId, DataMessage dataMessage, DataAttachment dataAttachment) {
      locationDAO.getAttachmentById(dataAttachment.getId()).take(1).subscribe(dataLocationAttachment -> sendLocationPipe
            .send(new SendLocationAttachmentCommand(conversationId, dataMessage, dataAttachment, dataLocationAttachment)), throwable -> Timber
            .e(throwable, ""));
   }

   public void send(String conversationId, Location location) {
      DataMessage emptyMessage = attachmentDelegateHelper.createEmptyMessage(currentUser.getId(), conversationId);
      DataAttachment attachment = attachmentDelegateHelper.createDataAttachment(emptyMessage, AttachmentType.LOCATION);
      DataLocationAttachment dataLocationAttachment = attachmentDelegateHelper.createLocationAttachment(attachment, location
            .getLatitude(), location.getLongitude());
      sendLocationPipe.send(new SendLocationAttachmentCommand(conversationId, emptyMessage, attachment, dataLocationAttachment));
   }
}