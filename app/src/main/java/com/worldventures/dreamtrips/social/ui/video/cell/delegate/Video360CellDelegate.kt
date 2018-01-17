package com.worldventures.dreamtrips.social.ui.video.cell.delegate

import com.worldventures.core.modules.video.model.Video

interface Video360CellDelegate : VideoCellDelegate {
   fun onOpen360Video(video: Video)
}
