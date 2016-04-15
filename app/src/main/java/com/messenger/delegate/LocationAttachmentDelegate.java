package com.messenger.delegate;

import android.location.Location;

import com.messenger.delegate.command.SendLocationAttachmentCommand;
import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataLocationAttachment;
import com.messenger.entities.DataMessage;
import com.messenger.messengerservers.constant.AttachmentType;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;

public class LocationAttachmentDelegate {

    private final ActionPipe<SendLocationAttachmentCommand> sendLocationPipe;

    private final SessionHolder<UserSession> sessionHolder;
    private final AttachmentDelegateHelper attachmentDelegateHelper;

    @Inject
    public LocationAttachmentDelegate(SessionHolder<UserSession> sessionHolder, Janet janet) {
        this.sessionHolder = sessionHolder;

        this.sendLocationPipe = janet.createPipe(SendLocationAttachmentCommand.class);
        this.attachmentDelegateHelper = new AttachmentDelegateHelper();
    }

    public void retry(DataConversation conversation, DataMessage message, DataAttachment dataAttachment, DataLocationAttachment dataLocationAttachment) {
        sendLocationPipe.send(new SendLocationAttachmentCommand(conversation, message, dataAttachment, dataLocationAttachment));
    }

    public void send(DataConversation conversation, Location location) {
        String userId = sessionHolder.get().get().getUsername();
        DataMessage emptyMessage = attachmentDelegateHelper.createEmptyMessage(userId, conversation.getId());
        DataAttachment attachment = attachmentDelegateHelper.createDataAttachment(emptyMessage, AttachmentType.LOCATION);
        DataLocationAttachment dataLocationAttachment = attachmentDelegateHelper.createLocationAttachment(attachment, location.getLatitude(), location.getLongitude());
        sendLocationPipe.send(new SendLocationAttachmentCommand(conversation, emptyMessage, attachment, dataLocationAttachment));
    }
}