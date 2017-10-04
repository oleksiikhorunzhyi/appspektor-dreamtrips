package com.worldventures.dreamtrips.social.ui.video.cell.delegate;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.social.ui.video.cell.ProgressVideoButtonActions;
import com.worldventures.dreamtrips.social.ui.video.model.Video;

public interface VideoCellDelegate extends CellDelegate<Video>, ProgressVideoButtonActions<Video> {

   void onPlayVideoClicked(Video video);
}
