package com.worldventures.dreamtrips.social.ui.feed.bundle;

import android.os.Parcelable;

import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity;

public interface CommentableBundle extends Parcelable {

   FeedEntity getFeedEntity();

   boolean shouldOpenKeyboard();

   boolean shouldShowLikersPanel();
}
