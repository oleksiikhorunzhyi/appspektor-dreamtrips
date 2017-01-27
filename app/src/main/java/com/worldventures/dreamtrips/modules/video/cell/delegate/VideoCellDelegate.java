package com.worldventures.dreamtrips.modules.video.cell.delegate;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.modules.video.model.Video;

public interface VideoCellDelegate extends CellDelegate<Video> {

   void sendAnalytic(String action, String name);

   void onDownloadVideo(CachedEntity entity);

   void onDeleteVideo(CachedEntity entity);

   void onCancelCachingVideo(CachedEntity entity);

   void onPlayVideoClicked(Video entity);
}
