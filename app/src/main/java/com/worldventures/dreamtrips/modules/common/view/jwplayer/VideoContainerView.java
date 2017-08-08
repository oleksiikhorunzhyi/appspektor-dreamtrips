package com.worldventures.dreamtrips.modules.common.view.jwplayer;

import android.view.ViewGroup;

public interface VideoContainerView {

   ViewGroup getJwPlayerViewContainer();

   ViewGroup getRootContainerForFullscreen();

   ViewGroup getRootContainerWhenWindowed();
}
