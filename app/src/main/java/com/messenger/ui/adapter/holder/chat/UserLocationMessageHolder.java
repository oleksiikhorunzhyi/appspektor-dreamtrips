package com.messenger.ui.adapter.holder.chat;

import android.support.annotation.DrawableRes;
import android.view.View;

import com.worldventures.dreamtrips.R;

public class UserLocationMessageHolder extends LocationMessageHolder {
    public UserLocationMessageHolder(View itemView) {
        super(itemView);
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
}
