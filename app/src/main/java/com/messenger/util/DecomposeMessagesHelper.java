package com.messenger.util;

import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataLocationAttachment;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataPhotoAttachment;
import com.messenger.messengerservers.model.Message;
import com.messenger.storage.dao.AttachmentDAO;
import com.messenger.storage.dao.LocationDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.storage.dao.PhotoDAO;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.innahema.collections.query.queriables.Queryable.from;


public class DecomposeMessagesHelper {

    MessageDAO messageDAO;
    AttachmentDAO attachmentDAO;
    PhotoDAO photoDAO;
    LocationDAO locationDAO;

    @Inject
    public DecomposeMessagesHelper(MessageDAO messageDAO, AttachmentDAO attachmentDAO, PhotoDAO photoDAO, LocationDAO locationDAO) {
        this.messageDAO = messageDAO;
        this.attachmentDAO = attachmentDAO;
        this.photoDAO = photoDAO;
        this.locationDAO = locationDAO;
    }

    public Result decomposeMessages(List<Message> serverMessages){
        List<DataMessage> messages = from(serverMessages).map(DataMessage::new).toList();
        List<DataAttachment> attachments = new ArrayList<>(serverMessages.size());
        List<DataPhotoAttachment> photoAttachments = new ArrayList<>();
        List<DataLocationAttachment> locationAttachments = new ArrayList<>();

        from(serverMessages).forEachR(serverMessage -> {
            attachments.addAll(DataAttachment.fromMessage(serverMessage));
            photoAttachments.addAll(DataPhotoAttachment.fromMessage(serverMessage));
            locationAttachments.addAll(DataLocationAttachment.fromMessage(serverMessage));
        });

        return new Result(messages, attachments, photoAttachments, locationAttachments);
    }

    public void saveDecomposeMessage(Result result) {
        messageDAO.save(result.messages);
        photoDAO.save(result.photoAttachments);
        locationDAO.save(result.locationAttachments);
        attachmentDAO.save(result.attachments);
    }

    public static class Result {
        public final List<DataMessage> messages;
        public final List<DataAttachment> attachments;
        public final List<DataPhotoAttachment> photoAttachments;
        public final List<DataLocationAttachment> locationAttachments;

        public Result(List<DataMessage> messages, List<DataAttachment> attachments,
                      List<DataPhotoAttachment> photoAttachments, List<DataLocationAttachment> locationAttachments) {
            this.messages = messages;
            this.attachments = attachments;
            this.photoAttachments = photoAttachments;
            this.locationAttachments = locationAttachments;
        }
    }

}
