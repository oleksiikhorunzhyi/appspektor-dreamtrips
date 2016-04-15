package com.messenger.ui.adapter.holder.chat;

import android.graphics.drawable.Animatable;
import android.support.annotation.DrawableRes;
import android.view.View;

import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.util.Utils;
import com.worldventures.dreamtrips.R;

import butterknife.OnClick;

public class OwnImageMessageViewHolder extends ImageMessageViewHolder {

    public OwnImageMessageViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    @DrawableRes
    protected int provideBackgroundForFollowing() {
        return selected ? R.drawable.dark_blue_bubble_image_post : R.drawable.blue_bubble_image_post;
    }

    @Override
    @DrawableRes
    protected int provideBackgroundForInitial() {
        return selected ? R.drawable.dark_blue_bubble_comics_image_post : R.drawable.blue_bubble_comics_image_post;
    }

    @Override
    public void updateMessageStatusUi() {
        super.updateMessageStatusUi();
        if (dataMessage.getStatus() == MessageStatus.SENDING) {
            applyLoadingStatusUi();
            imagePostView.setAlpha(ALPHA_MESSAGE_SENDING);
        }

        boolean isError = dataMessage.getStatus() == MessageStatus.ERROR;
        int viewVisible = isError ? View.VISIBLE : View.GONE;
        if (viewVisible != retrySwitcher.getVisibility()) {
            retrySwitcher.setVisibility(viewVisible);
        }
        if (isError && retrySwitcher.getCurrentView().getId() == R.id.progress_bar) {
            retrySwitcher.showPrevious();
        }
    }


    @OnClick(R.id.chat_image_error)
    void onMessageErrorClicked() {
        if (dataMessage.getStatus() == MessageStatus.ERROR) {
            cellDelegate.onRetryClicked(dataMessage);
        } else loadImage();
    }

    @Override
    protected BaseControllerListener<ImageInfo> getLoadingListener() {
        return new BaseControllerListener<ImageInfo>() {
            @Override
            public void onSubmit(String id, Object callerContext) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                if (dataMessage.getStatus() != MessageStatus.SENDING) {
                    progressBar.setVisibility(View.GONE);
                }

                //TODO should be refactored
                if (dataMessage.getStatus() == MessageStatus.ERROR &&
                        Utils.isFileUri(imagePostUri)) {
                    errorView.setVisibility(View.VISIBLE);
                    retrySwitcher.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                applyErrorStatusUi();
            }
        };
    }

}