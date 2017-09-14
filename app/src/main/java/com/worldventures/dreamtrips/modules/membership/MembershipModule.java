package com.worldventures.dreamtrips.modules.membership;

import com.worldventures.dreamtrips.social.ui.activity.presenter.PlayerPresenter;
import com.worldventures.dreamtrips.social.ui.activity.PlayerActivity;
import com.worldventures.dreamtrips.modules.infopages.presenter.EnrollMemberPresenter;
import com.worldventures.dreamtrips.modules.infopages.presenter.EnrollMerchantPresenter;
import com.worldventures.dreamtrips.modules.infopages.presenter.EnrollRepPresenter;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.EnrollMemberFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.EnrollMerchantFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.WVAdvantageFragment;
import com.worldventures.dreamtrips.modules.membership.presenter.EditTemplatePresenter;
import com.worldventures.dreamtrips.modules.membership.presenter.InvitePresenter;
import com.worldventures.dreamtrips.modules.membership.presenter.MembershipPresenter;
import com.worldventures.dreamtrips.modules.membership.presenter.PodcastsPresenter;
import com.worldventures.dreamtrips.modules.membership.presenter.SelectTemplatePresenter;
import com.worldventures.dreamtrips.modules.membership.view.cell.PodcastCell;
import com.worldventures.dreamtrips.modules.membership.view.dialog.FilterLanguageDialogFragment;
import com.worldventures.dreamtrips.modules.membership.view.fragment.EditTemplateFragment;
import com.worldventures.dreamtrips.modules.membership.view.fragment.InviteFragment;
import com.worldventures.dreamtrips.modules.membership.view.fragment.MembershipFragment;
import com.worldventures.dreamtrips.modules.membership.view.fragment.PodcastsFragment;
import com.worldventures.dreamtrips.modules.membership.view.fragment.PreviewTemplateFragment;
import com.worldventures.dreamtrips.modules.membership.view.fragment.SelectTemplateFragment;
import com.worldventures.dreamtrips.modules.player.PodcastPlayerActivity;
import com.worldventures.dreamtrips.modules.player.presenter.PodcastPlayerPresenter;
import com.worldventures.dreamtrips.modules.player.presenter.PodcastPresenterImpl;
import com.worldventures.dreamtrips.modules.player.view.PodcastPlayerScreenImpl;
import com.worldventures.dreamtrips.modules.reptools.presenter.TrainingVideosPresenter;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.TrainingVideosFragment;
import com.worldventures.dreamtrips.social.ui.activity.presenter.VideoPlayerPresenter;
import com.worldventures.dreamtrips.modules.video.cell.Video360Cell;
import com.worldventures.dreamtrips.modules.video.cell.Video360SmallCell;
import com.worldventures.dreamtrips.modules.video.cell.VideoCell;
import com.worldventures.dreamtrips.modules.video.presenter.PresentationVideosPresenter;
import com.worldventures.dreamtrips.modules.video.presenter.ThreeSixtyVideosPresenter;
import com.worldventures.dreamtrips.modules.video.view.PresentationVideosFragment;
import com.worldventures.dreamtrips.modules.video.view.ThreeSixtyVideosFragment;

import dagger.Module;

@Module(
      injects = {
            PodcastPlayerActivity.class,
            PodcastPlayerPresenter.class,
            MembershipPresenter.class,
            MembershipFragment.class,
            EnrollMemberFragment.class,
            EnrollMerchantFragment.class,
            EnrollMerchantPresenter.class,
            InviteFragment.class,
            InvitePresenter.class,
            PodcastsFragment.class,
            PodcastsPresenter.class,
            PodcastCell.class,
            VideoCell.class,
            SelectTemplateFragment.class,
            SelectTemplatePresenter.class,
            EditTemplateFragment.class,
            EditTemplatePresenter.class,
            EnrollMemberPresenter.class,
            EnrollRepPresenter.class,
            PreviewTemplateFragment.class,
            FilterLanguageDialogFragment.class,
            WVAdvantageFragment.class,
            Video360Cell.class,
            Video360SmallCell.class,
            ThreeSixtyVideosFragment.class,
            ThreeSixtyVideosPresenter.class,
            VideoPlayerPresenter.class,
            TrainingVideosFragment.class,
            TrainingVideosPresenter.class,
            PresentationVideosPresenter.class,
            PresentationVideosFragment.class,
            PlayerActivity.class,
            PlayerPresenter.class,
            PodcastPresenterImpl.class,
            PodcastPlayerScreenImpl.class
      },
      complete = false,
      library = true)
public class MembershipModule {
}
