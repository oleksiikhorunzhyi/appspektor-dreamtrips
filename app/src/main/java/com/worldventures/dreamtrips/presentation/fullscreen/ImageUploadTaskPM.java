package com.worldventures.dreamtrips.presentation.fullscreen;

public class ImageUploadTaskPM extends BaseFSViewPM {
    public ImageUploadTaskPM(View view) {
        super(view);
    }

    @Override
    protected boolean isLiked() {
        return false;
    }

    @Override
    protected boolean isFlagVisible() {
        return false;
    }

    @Override
    protected boolean isDeleteVisible() {
        return false;
    }

    @Override
    protected boolean isLikeVisible() {
        return false;
    }
}
