package com.worldventures.dreamtrips.modules.video.cell.delegate;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.video.cell.ProgressVideoButtonActions;
import com.worldventures.dreamtrips.modules.video.model.Video;

public interface VideoCellDelegate extends CellDelegate<Video>, ProgressVideoButtonActions {

   void sendAnalytic(String action, String name);

   void onPlayVideoClicked(Video entity);
}
