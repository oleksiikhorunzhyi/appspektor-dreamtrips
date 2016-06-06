package com.messenger.di;

import com.messenger.delegate.chat.attachment.SendImageAttachmentCommand;
import com.messenger.delegate.chat.attachment.SendLocationAttachmentCommand;
import com.messenger.delegate.chat.command.KickChatCommand;
import com.messenger.delegate.chat.command.LeaveChatCommand;
import com.messenger.delegate.chat.command.LoadChatMessagesCommand;
import com.messenger.delegate.chat.flagging.FlagMessageCommand;
import com.messenger.delegate.chat.message.ChatSendMessageCommand;
import com.messenger.delegate.chat.message.RetrySendMessageCommand;
import com.messenger.delegate.chat.typing.ChatStateCommand;
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
        FlagMessageCommand.class,
        ChatSendMessageCommand.class,
        RetrySendMessageCommand.class,
        LoadConversationCommand.class,
        LoadConversationsCommand.class,
        LeaveChatCommand.class,
        KickChatCommand.class,
        SyncConversationCommand.class,
        SyncConversationsCommand.class,
        FetchUsersDataCommand.class,
        LoadContactsCommand.class,
        ChatStateCommand.class,
        LoadChatMessagesCommand.class,
        SetAvatarUploadCommand.class},
        complete = false, library = true)
public class MessengerJanetCommandModule {

}
