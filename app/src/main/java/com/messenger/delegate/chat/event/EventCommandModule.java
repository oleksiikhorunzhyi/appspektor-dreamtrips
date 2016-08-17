package com.messenger.delegate.chat.event;

import dagger.Module;

@Module(injects = {ClearChatCommand.class, RevertClearingChatCommand.class}, complete = false, library = true)
public class EventCommandModule {}
