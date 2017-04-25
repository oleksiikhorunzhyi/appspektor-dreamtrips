package com.worldventures.dreamtrips.modules.video.cell.delegate;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.video.model.CachedModel;
import com.worldventures.dreamtrips.modules.video.model.Video;

public interface VideoCellDelegate extends CellDelegate<Video> {

   void sendAnalytic(String action, String name);

   void onDownloadVideo(CachedModel entity);

   void onDeleteVideo(CachedModel entity);

   void onCancelCachingVideo(CachedModel entity);

   void onPlayVideoClicked(Video entity);
}
