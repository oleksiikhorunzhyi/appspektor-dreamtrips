package com.worldventures.dreamtrips.wallet.service.command;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.util.SmartCardAvatarHelper;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class LoadImageForSmartCardCommand extends SmartCardAvatarCommand implements InjectableAction {

    private final String photoUrl;
    private final int imageSize;

    @Inject
    SmartCardAvatarHelper smartCardAvatarHelper;

    public LoadImageForSmartCardCommand(@NonNull String photoUrl) {
        this(photoUrl, DEFAULT_IMAGE_SIZE);
    }

    public LoadImageForSmartCardCommand(@NonNull String photoUrl, int imageSize) {
        this.imageSize = imageSize;
        this.photoUrl = photoUrl;
    }

    @Override
    protected void run(CommandCallback<File> callback) throws Throwable {
        smartCardAvatarHelper.compressPhotoFromUrl(photoUrl, imageSize)
                .subscribe(callback::onSuccess, callback::onFail);
    }
}
