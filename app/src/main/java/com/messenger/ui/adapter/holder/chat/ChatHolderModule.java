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

    @Provides(type = Provides.Type.SET)
    ChatViewHolderInfo provideOwnTextMessageViewHolderInfo() {
        return new ChatViewHolderInfo(VIEW_TYPE_OWN_TEXT_MESSAGE, OwnTextMessageViewHolder.class,
                true);
    }

    @Provides(type = Provides.Type.SET)
    ChatViewHolderInfo provideUserTextMessageViewHolderInfo() {
        return new ChatViewHolderInfo(VIEW_TYPE_USER_TEXT_MESSAGE, UserTextMessageViewHolder.class,
                false);
    }

    @Provides(type = Provides.Type.SET)
    ChatViewHolderInfo provideOwnUnsupportedMessageViewHolderInfo() {
        return new ChatViewHolderInfo(VIEW_TYPE_OWN_TEXT_MESSAGE, OwnTextMessageViewHolder.class,
                true, AttachmentType.UNSUPPORTED);
    }

    @Provides(type = Provides.Type.SET)
    ChatViewHolderInfo provideUserUnsupportedMessageViewHolderInfo() {
        return new ChatViewHolderInfo(VIEW_TYPE_USER_TEXT_MESSAGE, UserTextMessageViewHolder.class,
                false, AttachmentType.UNSUPPORTED);
    }

    @Provides(type = Provides.Type.SET)
    ChatViewHolderInfo provideOwnImageMessageViewHolderInfo() {
        return new ChatViewHolderInfo(VIEW_TYPE_OWN_IMAGE_MESSAGE, OwnImageMessageViewHolder.class,
                true, AttachmentType.IMAGE);
    }

    @Provides(type = Provides.Type.SET)
    ChatViewHolderInfo provideUserImageMessageViewHolderInfo() {
        return new ChatViewHolderInfo(VIEW_TYPE_USER_IMAGE_MESSAGE, UserImageMessageViewHolder.class,
                false, AttachmentType.IMAGE);
    }

    @Provides(type = Provides.Type.SET)
    ChatViewHolderInfo provideOwnLocationMessageHolderInfo() {
        return new ChatViewHolderInfo(VIEW_TYPE_OWN_LOCATION_MESSAGE, OwnLocationMessageHolder.class,
                true, AttachmentType.LOCATION);
    }

    @Provides(type = Provides.Type.SET)
    ChatViewHolderInfo provideUserLocationMessageHolderInfo() {
        return new ChatViewHolderInfo(VIEW_TYPE_USER_LOCATION_MESSAGE, UserLocationMessageHolder.class,
                false, AttachmentType.LOCATION);
    }
}
