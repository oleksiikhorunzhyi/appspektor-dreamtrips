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
        switch (dataMessage.getStatus()) {
            case MessageStatus.SENDING:
                mapView.setAlpha(ALPHA_MESSAGE_SENDING);
                break;
            case MessageStatus.ERROR:
                mapView.setAlpha(ALPHA_MESSAGE_NORMAL);
                break;
        }

        boolean isError = dataMessage.getStatus() == MessageStatus.ERROR;
        int viewVisibility = isError ? View.VISIBLE : View.GONE;
        if (viewVisibility != retrySwitcher.getVisibility()) {
            retrySwitcher.setVisibility(viewVisibility);
        }
        if (isError && retrySwitcher.getCurrentView().getId() == R.id.progress_bar) {
            retrySwitcher.showPrevious();
        }
    }
}
