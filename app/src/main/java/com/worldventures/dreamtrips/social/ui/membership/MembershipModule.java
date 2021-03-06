package com.worldventures.dreamtrips.social.ui.membership;

import com.worldventures.core.modules.infopages.StaticPageProviderConfig;
import com.worldventures.dreamtrips.modules.dtl.helper.MerchantStaticPageProvider;
import com.worldventures.dreamtrips.social.ui.activity.PlayerActivity;
import com.worldventures.dreamtrips.social.ui.activity.presenter.PlayerPresenter;
import com.worldventures.dreamtrips.social.ui.activity.presenter.VideoPlayerPresenter;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.EnrollMemberPresenter;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.EnrollMerchantPresenter;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.EnrollRepPresenter;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.PreviewTemplatePresenter;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.WVAdvantagePresenter;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.EnrollMemberFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.EnrollMerchantFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.WVAdvantageFragment;
import com.worldventures.dreamtrips.social.ui.membership.presenter.EditTemplatePresenter;
import com.worldventures.dreamtrips.social.ui.membership.presenter.InvitePresenter;
import com.worldventures.dreamtrips.social.ui.membership.presenter.MembershipPresenter;
import com.worldventures.dreamtrips.social.ui.membership.presenter.PodcastsPresenter;
import com.worldventures.dreamtrips.social.ui.membership.presenter.SelectTemplatePresenter;
import com.worldventures.dreamtrips.social.ui.membership.view.cell.PodcastCell;
import com.worldventures.dreamtrips.social.ui.membership.view.dialog.FilterLanguageDialogFragment;
import com.worldventures.dreamtrips.social.ui.membership.view.fragment.EditTemplateFragment;
import com.worldventures.dreamtrips.social.ui.membership.view.fragment.InviteFragment;
import com.worldventures.dreamtrips.social.ui.membership.view.fragment.MembershipFragment;
import com.worldventures.dreamtrips.social.ui.membership.view.fragment.PodcastsFragment;
import com.worldventures.dreamtrips.social.ui.membership.view.fragment.PreviewTemplateFragment;
import com.worldventures.dreamtrips.social.ui.membership.view.fragment.SelectTemplateFragment;
import com.worldventures.dreamtrips.social.ui.podcast_player.PodcastPlayerActivity;
import com.worldventures.dreamtrips.social.ui.podcast_player.presenter.PodcastPlayerPresenter;
import com.worldventures.dreamtrips.social.ui.podcast_player.presenter.PodcastPresenterImpl;
import com.worldventures.dreamtrips.social.ui.podcast_player.view.PodcastPlayerScreenImpl;
import com.worldventures.dreamtrips.social.ui.reptools.presenter.TrainingVideosPresenter;
import com.worldventures.dreamtrips.social.ui.reptools.view.fragment.TrainingVideosFragment;
import com.worldventures.dreamtrips.social.ui.video.cell.Video360Cell;
import com.worldventures.dreamtrips.social.ui.video.cell.Video360SmallCell;
import com.worldventures.dreamtrips.social.ui.video.cell.VideoCell;
import com.worldventures.dreamtrips.social.ui.video.presenter.PresentationVideosPresenter;
import com.worldventures.dreamtrips.social.ui.video.presenter.ThreeSixtyVideosPresenter;
import com.worldventures.dreamtrips.social.ui.video.view.PresentationVideosFragment;
import com.worldventures.dreamtrips.social.ui.video.view.ThreeSixtyVideosFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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
            EnrollMerchantPresenter.class,
            EnrollRepPresenter.class,
            PreviewTemplateFragment.class,
            PreviewTemplatePresenter.class,
            FilterLanguageDialogFragment.class,
            WVAdvantageFragment.class,
            WVAdvantagePresenter.class,
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

   @Provides
   @Singleton
   MerchantStaticPageProvider merchantStaticPageProvider(StaticPageProviderConfig config) {
      return new MerchantStaticPageProvider(config);
   }
}
