package com.worldventures.dreamtrips.social.ui.podcast_player.presenter;

import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.social.ui.podcast_player.view.PodcastPlayerScreen;

public interface PodcastPresenter extends DtlPresenter<PodcastPlayerScreen, ViewState.EMPTY> {
   void seekTo(int position);

   void playPause();

   void onBackPressed();
}
