package com.messenger.di;

import android.content.Context;

import com.messenger.api.ErrorParser;
import com.messenger.delegate.chat.flagging.FlagMessageAction;
import com.messenger.delegate.chat.typing.ChatStateAction;
import com.messenger.delegate.command.ChangeAvatarCommand;
import com.messenger.delegate.command.RemoveAvatarCommand;
import com.messenger.delegate.command.SendImageAttachmentCommand;
import com.messenger.delegate.command.SendLocationAttachmentCommand;
import com.messenger.delegate.command.SetAvatarUploadCommand;
import com.techery.spares.module.qualifier.ForApplication;

import dagger.Module;
import dagger.Provides;

@Module(injects = {
        SendImageAttachmentCommand.class,
        SendLocationAttachmentCommand.class,
        ChangeAvatarCommand.class,
        RemoveAvatarCommand.class,
        FlagMessageAction.class,
        ChatStateAction.class,
        SetAvatarUploadCommand.class},
        complete = false, library = true)
public class MessengerJanetCommandModule {

}
