package com.worldventures.dreamtrips.social.ui.video.cell.delegate;

import com.worldventures.core.modules.video.cell.ProgressMediaButtonActions;
import com.worldventures.core.modules.video.model.Video;
import com.worldventures.core.ui.view.cell.CellDelegate;

public interface VideoCellDelegate extends CellDelegate<Video>, ProgressMediaButtonActions<Video> {

   void onPlayVideoClicked(Video video);
}
