package com.messenger.delegate;

import android.location.Location;

import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.messengerservers.constant.AttachmentType;
import com.messenger.storage.dao.LocationDAO;
import com.messenger.storage.dao.PhotoDAO;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class AttachmentManager {

    PhotoDAO photoDAO;
    LocationDAO locationDAO;

    PhotoAttachmentDelegate photoAttachmentDelegate;
    LocationAttachmentDelegate locationAttachmentDelegate;

    @Inject
    public AttachmentManager(PhotoDAO photoDAO, LocationDAO locationDAO,
                             PhotoAttachmentDelegate photoAttachmentDelegate, LocationAttachmentDelegate locationAttachmentDelegate) {
        this.photoDAO = photoDAO;
        this.locationDAO = locationDAO;
        this.photoAttachmentDelegate = photoAttachmentDelegate;
        this.locationAttachmentDelegate = locationAttachmentDelegate;
    }

    public void sendImages(DataConversation conversation, List<String> filePaths) {
        photoAttachmentDelegate.sendImages(conversation, filePaths);
    }

    public void sendLocation(DataConversation conversation, Location location) {
        locationAttachmentDelegate.send(conversation, location);
    }

    public void retrySendAttachment(DataConversation conversation, DataMessage message, DataAttachment attachment) {
        switch (attachment.getType()) {
            case AttachmentType.IMAGE:
                retrySendPhotoAttachment(conversation, message, attachment);
                break;
            case AttachmentType.LOCATION:
                retrySendLocationAttachment(conversation, message, attachment);
                break;
        }
    }

    private void retrySendPhotoAttachment(DataConversation conversation, DataMessage message, DataAttachment dataAttachment) {
        photoDAO.getAttachmentById(dataAttachment.getId())
                .take(1)
                .subscribe(photoAttachment -> photoAttachmentDelegate.retry(conversation, message, dataAttachment, photoAttachment),
                           throwable -> Timber.e(throwable, ""));
    }

    private void retrySendLocationAttachment(DataConversation conversation, DataMessage message, DataAttachment dataAttachment) {
        locationDAO.getAttachmentById(dataAttachment.getId())
                   .take(1)
                   .subscribe(locationAttachment -> locationAttachmentDelegate.retry(conversation, message, dataAttachment, locationAttachment),
                           throwable -> Timber.e(throwable, ""));
    }

}
