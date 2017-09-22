package com.worldventures.dreamtrips.social.ui.membership.view.cell.delegate;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.social.ui.membership.model.Podcast;
import com.worldventures.dreamtrips.social.ui.video.model.CachedModel;

public interface PodcastCellDelegate extends CellDelegate<Podcast> {
   void onDownloadPodcast(CachedModel entity);

   void onDeletePodcast(CachedModel entity);

   void onCancelCachingPodcast(CachedModel entity);

   void play(Podcast podcast);
}
