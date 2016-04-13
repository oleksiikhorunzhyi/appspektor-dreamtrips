package com.messenger.di;

import com.messenger.delegate.command.ChangeAvatarCommand;
import com.messenger.delegate.command.RemoveAvatarCommand;
import com.messenger.delegate.command.SendImageAttachmentCommand;
import com.messenger.delegate.command.SetAvatarUploadCommand;

import dagger.Module;

@Module(injects = {
        SendImageAttachmentCommand.class,
        ChangeAvatarCommand.class,
        RemoveAvatarCommand.class,
        SetAvatarUploadCommand.class},
        complete = false, library = true)
public class MessengerJanetCommandModule {
}
