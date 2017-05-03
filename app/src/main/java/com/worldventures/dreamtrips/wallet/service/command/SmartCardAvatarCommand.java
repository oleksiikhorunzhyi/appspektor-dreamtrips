package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;

import io.techery.janet.Command;

public abstract class SmartCardAvatarCommand extends Command<SmartCardUserPhoto> {
   public static final int DEFAULT_IMAGE_SIZE = 256;
}
