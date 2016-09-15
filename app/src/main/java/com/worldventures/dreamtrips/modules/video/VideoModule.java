package com.worldventures.dreamtrips.modules.video;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.reptools.presenter.TrainingVideosPresenter;
import com.worldventures.dreamtrips.modules.reptools.view.cell.VideoLanguageCell;
import com.worldventures.dreamtrips.modules.reptools.view.cell.VideoLocaleCell;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.TrainingVideosFragment;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.VideoPlayerPresenter;
import com.worldventures.dreamtrips.modules.video.cell.MediaHeaderCell;
import com.worldventures.dreamtrips.modules.video.cell.MediaHeaderLightCell;
import com.worldventures.dreamtrips.modules.video.cell.Video360Cell;
import com.worldventures.dreamtrips.modules.video.cell.Video360SmallCell;
import com.worldventures.dreamtrips.modules.video.cell.VideoCell;
import com.worldventures.dreamtrips.modules.video.presenter.PresentationVideosPresenter;
import com.worldventures.dreamtrips.modules.video.presenter.ThreeSixtyVideosPresenter;
import com.worldventures.dreamtrips.modules.video.view.PresentationVideosFragment;
import com.worldventures.dreamtrips.modules.video.view.ThreeSixtyVideosFragment;

import dagger.Module;

@Module(
      injects = {VideoCell.class, MediaHeaderLightCell.class, Video360Cell.class, Video360SmallCell.class, MediaHeaderCell.class, VideoLocaleCell.class, VideoLanguageCell.class,

            ThreeSixtyVideosFragment.class, ThreeSixtyVideosPresenter.class, VideoPlayerPresenter.class,

            TrainingVideosFragment.class, TrainingVideosPresenter.class,

            PresentationVideosPresenter.class, PresentationVideosFragment.class,},
      complete = false,
      library = true)
public class VideoModule {

   public static final String MEMBERSHIP = Route.MEMBERSHIP.name();

}
