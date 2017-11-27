package com.worldventures.dreamtrips.social.ui.membership.view.cell.delegate;

import com.worldventures.core.modules.video.cell.ProgressMediaButtonActions;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.social.ui.membership.model.Podcast;

public interface PodcastCellDelegate extends CellDelegate<Podcast>, ProgressMediaButtonActions<Podcast> {

   void play(Podcast podcast);

}
