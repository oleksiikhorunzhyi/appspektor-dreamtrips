package com.messenger.util;

import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataLocationAttachment;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataPhotoAttachment;
import com.messenger.messengerservers.model.Message;

import java.util.ArrayList;
import java.util.List;

import static com.innahema.collections.query.queriables.Queryable.from;


public class DecomposeMessagesHelper {

    public static DecomposedMessagesResult decomposeMessages(List<Message> serverMessages){
        List<DataMessage> messages = from(serverMessages).map(DataMessage::new).toList();
        List<DataAttachment> attachments = new ArrayList<>(serverMessages.size());
        List<DataPhotoAttachment> photoAttachments = new ArrayList<>();
        List<DataLocationAttachment> locationAttachments = new ArrayList<>();

        from(serverMessages).forEachR(serverMessage -> {
            attachments.addAll(DataAttachment.fromMessage(serverMessage));
            photoAttachments.addAll(DataPhotoAttachment.fromMessage(serverMessage));
            locationAttachments.addAll(DataLocationAttachment.fromMessage(serverMessage));
        });

        return new DecomposedMessagesResult(messages, attachments, photoAttachments, locationAttachments);
    }

    public static class DecomposedMessagesResult {
        public final List<DataMessage> messages;
        public final List<DataAttachment> attachments;
        public final List<DataPhotoAttachment> photoAttachments;
        public final List<DataLocationAttachment> locationAttachments;

        public DecomposedMessagesResult(List<DataMessage> messages, List<DataAttachment> attachments,
                                        List<DataPhotoAttachment> photoAttachments, List<DataLocationAttachment> locationAttachments) {
            this.messages = messages;
            this.attachments = attachments;
            this.photoAttachments = photoAttachments;
            this.locationAttachments = locationAttachments;
        }
    }

}
