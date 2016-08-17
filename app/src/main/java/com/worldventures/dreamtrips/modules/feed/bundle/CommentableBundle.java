package com.worldventures.dreamtrips.modules.feed.bundle;

import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;

public interface CommentableBundle extends Parcelable {

   FeedEntity getFeedEntity();

   boolean shouldOpenKeyboard();

   boolean shouldShowLikersPanel();
}
