package com.messenger.delegate;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.delegate.command.SendImageAttachmentCommand;
import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataPhotoAttachment;
import com.messenger.messengerservers.constant.AttachmentType;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import io.techery.janet.ReadOnlyActionPipe;

@Singleton
public class PhotoAttachmentDelegate {
    private final ActionPipe<SendImageAttachmentCommand> sendImagePipe;
    private final ReadOnlyActionPipe<SendImageAttachmentCommand> readSendImagePipe;

    private final SessionHolder<UserSession> sessionHolder;
    private final AttachmentDelegateHelper attachmentDelegateHelper;

    @Inject
    public PhotoAttachmentDelegate(SessionHolder<UserSession> sessionHolder, Janet janet) {
        this.sessionHolder = sessionHolder;

        this.sendImagePipe = janet.createPipe(SendImageAttachmentCommand.class);
        this.readSendImagePipe = sendImagePipe.asReadOnly();
        this.attachmentDelegateHelper = new AttachmentDelegateHelper();
    }

    public void retry(DataConversation conversation, DataMessage message, DataAttachment dataAttachment, DataPhotoAttachment photoAttachment) {
        sendImagePipe.send(new SendImageAttachmentCommand(conversation, photoAttachment.getUrl(), message, dataAttachment, photoAttachment));
    }

    public void send(DataConversation conversation, String filePath) {
        String userId = sessionHolder.get().get().getUsername();
        DataMessage emptyMessage = attachmentDelegateHelper.createEmptyMessage(userId, conversation.getId());
        DataAttachment attachment = attachmentDelegateHelper.createDataAttachment(emptyMessage, AttachmentType.IMAGE);
        DataPhotoAttachment dataPhotoAttachment = attachmentDelegateHelper.createEmptyPhotoAttachment(attachment);
        sendImagePipe.send(new SendImageAttachmentCommand(conversation, filePath, emptyMessage, attachment, dataPhotoAttachment));
    }

    public ReadOnlyActionPipe<SendImageAttachmentCommand> getReadSendImagePipe() {
        return readSendImagePipe;
    }

    public void sendImages(DataConversation conversation, List<String> filePaths) {
        Queryable.from(filePaths).forEachR(path -> send(conversation, path));
    }
}
