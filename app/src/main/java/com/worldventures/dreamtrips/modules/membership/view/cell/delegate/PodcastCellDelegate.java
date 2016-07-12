package com.worldventures.dreamtrips.modules.membership.view.cell.delegate;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.membership.model.Podcast;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

public interface PodcastCellDelegate extends CellDelegate<Podcast> {
    void onDownloadPodcast(CachedEntity entity);

    void onDeletePodcast(CachedEntity entity);

    void onCancelCachingPodcast(CachedEntity entity);

    void play(Podcast podcast);
}
