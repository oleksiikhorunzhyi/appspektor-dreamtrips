package com.messenger.storage.helper;

import android.text.TextUtils;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataPhotoAttachment;
import com.messenger.entities.DataUser;
import com.messenger.entities.PhotoAttachment;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.storage.dao.PhotoDAO;
import com.messenger.storage.dao.UsersDAO;
import com.messenger.ui.helper.ConversationHelper;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.tripsimages.model.Image;

import javax.inject.Inject;

import rx.Observable;

public class PhotoAttachmentHelper {

    private final PhotoDAO photoDAO;
    private final MessageDAO messageDAO;
    private final UsersDAO usersDAO;
    private final ConversationsDAO conversationsDAO;

    @Inject
    public PhotoAttachmentHelper(PhotoDAO photoDAO, MessageDAO messageDAO,
                                 UsersDAO usersDAO, ConversationsDAO conversationsDAO) {
        this.photoDAO = photoDAO;
        this.messageDAO = messageDAO;
        this.usersDAO = usersDAO;
        this.conversationsDAO = conversationsDAO;
    }

    public Observable<PhotoAttachment> obtainPhotoAttachment(String attachmentImageId, DataUser currentUser) {
        return Observable.just(new PhotoAttachment.Builder())
                .map(builder -> {
                    DataPhotoAttachment dataAttachment = photoDAO.getAttachmentById(attachmentImageId)
                            .toBlocking().first();
                    DataMessage dataMessage = messageDAO.getMessageByAttachmentId(dataAttachment.getPhotoAttachmentId())
                            .toBlocking().first();
                    DataUser dataUser = usersDAO.getUserById(dataMessage.getFromId())
                            .toBlocking().first();
                    DataConversation dataConversation = conversationsDAO.getConversation(dataMessage.getConversationId())
                            .toBlocking().first();

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
                    builder.conversationId(dataMessage.getConversationId());
                    builder.messageId(dataMessage.getId());
                    builder.flaggingEnabled(!TextUtils.equals(currentUser.getId(), dataUser.getId())
                            && ConversationHelper.isTripChat(dataConversation));

                    if (dataMessage.getStatus() != MessageStatus.ERROR)
                        builder.date(dataMessage.getDate());

                    return builder.build();
                });
    }

}
