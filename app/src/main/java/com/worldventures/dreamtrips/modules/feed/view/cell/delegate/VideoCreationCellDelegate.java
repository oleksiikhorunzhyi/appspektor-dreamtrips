package com.worldventures.dreamtrips.modules.feed.view.cell.delegate;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.feed.model.VideoCreationModel;

public interface VideoCreationCellDelegate extends CellDelegate<VideoCreationModel> {
   void onRemoveClicked(VideoCreationModel model);
}