package com.messenger.ui.adapter.holder.chat;

import com.messenger.messengerservers.constant.AttachmentType;

import dagger.Module;
import dagger.Provides;

@Module(
        complete = false, library = true
)
public class ChatHolderModule {

    private static final int VIEW_TYPE_OWN_TEXT_MESSAGE = 1;
    private static final int VIEW_TYPE_USER_TEXT_MESSAGE = 2;
    private static final int VIEW_TYPE_OWN_IMAGE_MESSAGE = 3;
    private static final int VIEW_TYPE_USER_IMAGE_MESSAGE = 4;
    private static final int VIEW_TYPE_OWN_LOCATION_MESSAGE = 5;
    private static final int VIEW_TYPE_USER_LOCATION_MESSAGE = 6;
    private static final int VIEW_TYPE_SYSTEM_MESSAGE = 7;

    @Provides(type = Provides.Type.SET)
    ChatViewHolderInfo provideOwnTextMessageViewHolderInfo() {
        return new ChatViewHolderInfo.Builder()
                .viewType(VIEW_TYPE_OWN_TEXT_MESSAGE)
                .viewHolderClass(OwnTextMessageViewHolder.class)
                .own(true)
                .build();
    }

    @Provides(type = Provides.Type.SET)
    ChatViewHolderInfo provideUserTextMessageViewHolderInfo() {
        return new ChatViewHolderInfo.Builder()
                .viewType(VIEW_TYPE_USER_TEXT_MESSAGE)
                .viewHolderClass(UserTextMessageViewHolder.class)
                .own(false)
                .build();
    }

    @Provides(type = Provides.Type.SET)
    ChatViewHolderInfo provideOwnUnsupportedMessageViewHolderInfo() {
        return new ChatViewHolderInfo.Builder()
                .viewType(VIEW_TYPE_OWN_TEXT_MESSAGE)
                .viewHolderClass(OwnTextMessageViewHolder.class)
                .own(true)
                .attachmentType(AttachmentType.UNSUPPORTED)
                .build();
    }

    @Provides(type = Provides.Type.SET)
    ChatViewHolderInfo provideUserUnsupportedMessageViewHolderInfo() {
        return new ChatViewHolderInfo.Builder()
                .viewType(VIEW_TYPE_USER_TEXT_MESSAGE)
                .viewHolderClass(UserTextMessageViewHolder.class)
                .own(false)
                .attachmentType(AttachmentType.UNSUPPORTED)
                .build();
    }

    @Provides(type = Provides.Type.SET)
    ChatViewHolderInfo provideOwnImageMessageViewHolderInfo() {
        return new ChatViewHolderInfo.Builder()
                .viewType(VIEW_TYPE_OWN_IMAGE_MESSAGE)
                .viewHolderClass(OwnImageMessageViewHolder.class)
                .own(true)
                .attachmentType(AttachmentType.IMAGE)
                .build();
    }

    @Provides(type = Provides.Type.SET)
    ChatViewHolderInfo provideUserImageMessageViewHolderInfo() {
        return new ChatViewHolderInfo.Builder()
                .viewType(VIEW_TYPE_USER_IMAGE_MESSAGE)
                .viewHolderClass(UserImageMessageViewHolder.class)
                .own(false)
                .attachmentType(AttachmentType.IMAGE)
                .build();
    }

    @Provides(type = Provides.Type.SET)
    ChatViewHolderInfo provideOwnLocationMessageHolderInfo() {
        return new ChatViewHolderInfo.Builder()
                .viewType(VIEW_TYPE_OWN_LOCATION_MESSAGE)
                .viewHolderClass(OwnLocationMessageHolder.class)
                .own(true)
                .attachmentType(AttachmentType.LOCATION)
                .build();
    }

    @Provides(type = Provides.Type.SET)
    ChatViewHolderInfo provideUserLocationMessageHolderInfo() {
        return new ChatViewHolderInfo.Builder()
                .viewType(VIEW_TYPE_USER_LOCATION_MESSAGE)
                .viewHolderClass(UserLocationMessageHolder.class)
                .own(false)
                .attachmentType(AttachmentType.LOCATION)
                .build();
    }

    @Provides(type = Provides.Type.SET)
    ChatViewHolderInfo provideSystemMessageHolderInfo() {
        return new ChatViewHolderInfo.Builder()
                .viewType(VIEW_TYPE_SYSTEM_MESSAGE)
                .viewHolderClass(SystemMessageViewHolder.class)
                .systemMessage(true)
                .build();
    }
}
