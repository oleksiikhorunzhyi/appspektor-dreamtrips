package com.messenger.ui.adapter.holder.chat;

import android.graphics.drawable.Animatable;
import android.support.annotation.DrawableRes;
import android.view.View;

import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.worldventures.dreamtrips.R;

import butterknife.OnClick;

public class UserImageMessageViewHolder extends ImageMessageViewHolder {

    public UserImageMessageViewHolder(View itemView) {
        super(itemView);
    }

    @OnClick(R.id.chat_image_error)
    void onMessageErrorClicked() {
        loadImage();
    }

    @Override
    @DrawableRes
    protected int provideBackgroundForFollowing() {
        return selected ? R.drawable.dark_grey_bubble_image_post : R.drawable.grey_bubble_image_post;
    }

    @Override
    @DrawableRes
    protected int provideBackgroundForInitial() {
        return selected ? R.drawable.dark_grey_bubble_comics_image_post : R.drawable.grey_bubble_comics_image_post;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Image message logic
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected BaseControllerListener<ImageInfo> getLoadingListener() {
        return new BaseControllerListener<ImageInfo>() {
            @Override
            public void onSubmit(String id, Object callerContext) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                applyErrorStatusUi();
            }
        };
    }
}