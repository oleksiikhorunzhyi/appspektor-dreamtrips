package com.messenger.storage.helper;

import com.messenger.entities.DataMessage;
import com.messenger.entities.DataPhotoAttachment;
import com.messenger.entities.DataUser;
import com.messenger.entities.PhotoAttachment;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.storage.dao.PhotoDAO;
import com.messenger.storage.dao.UsersDAO;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.tripsimages.model.Image;

import javax.inject.Inject;

import rx.Observable;

public class AttachmentHelper {

    private final PhotoDAO photoDAO;
    private final MessageDAO messageDAO;
    private final UsersDAO usersDAO;

    @Inject
    public AttachmentHelper(PhotoDAO photoDAO, MessageDAO messageDAO, UsersDAO usersDAO) {
        this.photoDAO = photoDAO;
        this.messageDAO = messageDAO;
        this.usersDAO = usersDAO;
    }

    public Observable<PhotoAttachment> obtainPhotoAttachment(String attachmentImageId) {
        return Observable.just(new PhotoAttachment.Builder())
                .map(builder -> {
                    DataPhotoAttachment dataAttachment = photoDAO.getAttachmentById(attachmentImageId).toBlocking().first();
                    DataMessage dataMessage = messageDAO.getMessageByAttachmentId(dataAttachment.getPhotoAttachmentId()).toBlocking().first();
                    DataUser dataUser = usersDAO.getUserById(dataMessage.getFromId()).toBlocking().first();

                    String uri = dataAttachment.getUrl() == null ? dataAttachment.getLocalPath() : dataAttachment.getUrl();
                    //noinspection ConstantConditions
                    String url = uri.replace(" ", "%20");
                    Image image = new Image();
                    image.setUrl(url);
                    image.setFromFile(false);

                    User user = new User(dataUser.getSocialId());
                    user.setUsername(dataUser.getId()); // cause "username" in core entity "User" is like 65663832

                    builder.image(image);
                    builder.user(user);

                    if (dataMessage.getStatus() != MessageStatus.ERROR)
                        builder.date(dataMessage.getDate());

                    return builder.build();
                });
    }

}
