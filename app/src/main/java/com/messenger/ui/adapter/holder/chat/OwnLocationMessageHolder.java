package com.messenger.ui.adapter.holder.chat;

import android.support.annotation.DrawableRes;
import android.view.View;

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
}
