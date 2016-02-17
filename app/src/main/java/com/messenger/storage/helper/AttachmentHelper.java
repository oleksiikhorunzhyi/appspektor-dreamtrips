package com.messenger.storage.helper;

import android.util.Pair;

import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataUser;
import com.messenger.entities.PhotoAttachment;
import com.messenger.storage.dao.AttachmentDAO;
import com.messenger.storage.dao.UsersDAO;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.tripsimages.model.Image;

import rx.Observable;

public class AttachmentHelper {

    private final AttachmentDAO attachmentDAO;
    private final UsersDAO usersDAO;

    public AttachmentHelper(AttachmentDAO attachmentDAO, UsersDAO usersDAO) {
        this.attachmentDAO = attachmentDAO;
        this.usersDAO = usersDAO;
    }

    public Observable<PhotoAttachment> obtainPhotoAttachment(String attachmentImageId, String userSenderId) {
        return Observable.combineLatest(attachmentDAO.getAttachmentById(attachmentImageId).first(),
                usersDAO.getUserById(userSenderId).first(),
                (dataAttachment, dataUser) -> new Pair<>(dataAttachment, dataUser))
                .map(attachmentPairInfo -> {
                    DataAttachment dataAttachment = attachmentPairInfo.first;
                    DataUser dataUser = attachmentPairInfo.second;

                    Image image = new Image();
                    image.setUrl(dataAttachment.getUrl());
                    image.setFromFile(false);

                    User user = new User(dataUser.getSocialId());
                    user.setUsername(dataUser.getId()); // cause "username" in core entity "User" is like 65663832

                    return new PhotoAttachment(image, user);
                });
    }

}
