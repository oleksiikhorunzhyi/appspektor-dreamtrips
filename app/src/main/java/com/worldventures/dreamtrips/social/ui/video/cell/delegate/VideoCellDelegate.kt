package com.worldventures.dreamtrips.social.ui.video.cell.delegate

import com.worldventures.core.modules.video.cell.ProgressMediaButtonActions
import com.worldventures.core.modules.video.model.Video
import com.worldventures.core.ui.view.cell.CellDelegate

interface VideoCellDelegate : CellDelegate<Video>, ProgressMediaButtonActions<Video> {

   fun onPlayVideoClicked(video: Video)
}
