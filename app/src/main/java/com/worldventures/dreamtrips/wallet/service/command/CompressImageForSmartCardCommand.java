package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.util.SmartCardAvatarHelper;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class CompressImageForSmartCardCommand extends SmartCardAvatarCommand implements InjectableAction {

    @Inject
    SmartCardAvatarHelper smartCardAvatarHelper;

    private final String filePath;
    private final int imageSize;

    public CompressImageForSmartCardCommand(String filePath) {
        this(filePath, DEFAULT_IMAGE_SIZE);
    }

    public CompressImageForSmartCardCommand(String filePath, int imageSize) {
        this.filePath = filePath;
        this.imageSize = imageSize;
    }


    @Override
    protected void run(CommandCallback<File> callback) throws Throwable {
        try {
            callback.onSuccess(smartCardAvatarHelper.compressPhotoFromFile(filePath, imageSize));
        } catch (Throwable throwable) {
            callback.onFail(throwable);
        }
    }
}
