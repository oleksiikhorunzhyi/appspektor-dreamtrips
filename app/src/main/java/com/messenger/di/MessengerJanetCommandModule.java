package com.messenger.di;

import com.messenger.delegate.chat.attachment.SendImageAttachmentCommand;
import com.messenger.delegate.chat.attachment.SendLocationAttachmentCommand;
import com.messenger.delegate.chat.flagging.FlagMessageAction;
import com.messenger.delegate.chat.message.ChatSendMessageAction;
import com.messenger.delegate.chat.typing.ChatStateAction;
import com.messenger.delegate.command.ChangeAvatarCommand;
import com.messenger.delegate.command.RemoveAvatarCommand;
import com.messenger.delegate.command.SetAvatarUploadCommand;
import com.messenger.delegate.conversation.command.LoadConversationCommand;
import com.messenger.delegate.conversation.command.LoadConversationsCommand;
import com.messenger.delegate.conversation.command.SyncConversationCommand;
import com.messenger.delegate.conversation.command.SyncConversationsCommand;
import com.messenger.delegate.user.FetchUsersDataCommand;
import com.messenger.delegate.roster.LoadContactsCommand;

import dagger.Module;

@Module(injects = {
        SendImageAttachmentCommand.class,
        SendLocationAttachmentCommand.class,
        ChangeAvatarCommand.class,
        RemoveAvatarCommand.class,
        FlagMessageAction.class,
        ChatSendMessageAction.class,
        LoadConversationCommand.class,
        LoadConversationsCommand.class,
        SyncConversationCommand.class,
        SyncConversationsCommand.class,
        FetchUsersDataCommand.class,
        LoadContactsCommand.class,
        ChatStateAction.class,
        SetAvatarUploadCommand.class},
        complete = false, library = true)
public class MessengerJanetCommandModule {

}
