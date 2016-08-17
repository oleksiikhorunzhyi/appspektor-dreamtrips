package com.messenger.di;

import com.messenger.delegate.chat.attachment.SendImageAttachmentCommand;
import com.messenger.delegate.chat.attachment.SendLocationAttachmentCommand;
import com.messenger.delegate.chat.command.ClearChatServerCommand;
import com.messenger.delegate.chat.command.KickChatCommand;
import com.messenger.delegate.chat.command.LeaveChatCommand;
import com.messenger.delegate.chat.command.LoadChatMessagesCommand;
import com.messenger.delegate.chat.command.RevertClearingChatServerCommand;
import com.messenger.delegate.chat.event.EventCommandModule;
import com.messenger.delegate.chat.flagging.FlagMessageCommand;
import com.messenger.delegate.chat.message.ChatSendMessageCommand;
import com.messenger.delegate.chat.message.MarkMessageAsReadCommand;
import com.messenger.delegate.chat.message.RetrySendMessageCommand;
import com.messenger.delegate.chat.typing.SendChatStateCommand;
import com.messenger.delegate.command.avatar.RemoveChatAvatarCommand;
import com.messenger.delegate.command.avatar.SendChatAvatarCommand;
import com.messenger.delegate.command.avatar.SetChatAvatarCommand;
import com.messenger.delegate.command.avatar.UploadChatAvatarCommand;
import com.messenger.delegate.conversation.command.LoadConversationCommand;
import com.messenger.delegate.conversation.command.LoadConversationsCommand;
import com.messenger.delegate.conversation.command.SyncConversationCommand;
import com.messenger.delegate.conversation.command.SyncConversationsCommand;
import com.messenger.delegate.roster.LoadContactsCommand;
import com.messenger.delegate.user.FetchUsersDataCommand;

import dagger.Module;

@Module(includes = {EventCommandModule.class},
        injects = {SendImageAttachmentCommand.class, SendLocationAttachmentCommand.class, RemoveChatAvatarCommand.class, FlagMessageCommand.class, ChatSendMessageCommand.class, RetrySendMessageCommand.class, LoadConversationCommand.class, LoadConversationsCommand.class, LeaveChatCommand.class, KickChatCommand.class, SyncConversationCommand.class, SyncConversationsCommand.class, FetchUsersDataCommand.class, LoadContactsCommand.class, SendChatStateCommand.class, LoadChatMessagesCommand.class, UploadChatAvatarCommand.class, SendChatAvatarCommand.class, SetChatAvatarCommand.class, RemoveChatAvatarCommand.class, MarkMessageAsReadCommand.class, ClearChatServerCommand.class, RevertClearingChatServerCommand.class},
        complete = false, library = true)
public class MessengerJanetCommandModule {

}
