package com.worldventures.dreamtrips.social.ui.membership.view.cell.delegate;

import com.worldventures.core.model.CachedModel;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.social.ui.membership.model.Podcast;

public interface PodcastCellDelegate extends CellDelegate<Podcast> {
   void onDownloadPodcast(CachedModel entity);

   void onDeletePodcast(CachedModel entity);

   void onCancelCachingPodcast(CachedModel entity);

   void play(Podcast podcast);
}
