package com.messenger.ui.adapter.holder.chat;

import android.support.annotation.DrawableRes;
import android.view.View;

import com.messenger.messengerservers.constant.MessageStatus;
import com.worldventures.dreamtrips.R;

public class OwnLocationMessageHolder extends LocationMessageHolder {

    public OwnLocationMessageHolder(View itemView) {
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
            mapView.setAlpha(ALPHA_IMAGE_POST_SENDING);
        } else if (dataMessage.getStatus() == MessageStatus.ERROR) {
            mapView.setAlpha(ALPHA_IMAGE_POST_NORMAL);
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
}
