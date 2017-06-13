package com.worldventures.dreamtrips.modules.membership.view.cell.delegate;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.membership.model.Podcast;
import com.worldventures.dreamtrips.modules.video.model.CachedModel;

public interface PodcastCellDelegate extends CellDelegate<Podcast> {
   void onDownloadPodcast(CachedModel entity);

   void onDeletePodcast(CachedModel entity);

   void onCancelCachingPodcast(CachedModel entity);

   void play(Podcast podcast);
}
