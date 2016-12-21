package com.worldventures.dreamtrips.modules.infopages;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.infopages.presenter.AuthorizedStaticInfoPresenter;
import com.worldventures.dreamtrips.modules.infopages.presenter.DocumentListPresenter;
import com.worldventures.dreamtrips.modules.infopages.presenter.FeedbackImageAttachmentFullscreenPresenter;
import com.worldventures.dreamtrips.modules.infopages.presenter.FeedbackImageAttachmentsPresenter;
import com.worldventures.dreamtrips.modules.infopages.presenter.HelpTabPresenter;
import com.worldventures.dreamtrips.modules.infopages.presenter.SendFeedbackPresenter;
import com.worldventures.dreamtrips.modules.infopages.presenter.TermsTabPresenter;
import com.worldventures.dreamtrips.modules.infopages.presenter.WebViewFragmentPresenter;
import com.worldventures.dreamtrips.modules.infopages.view.cell.DocumentCell;
import com.worldventures.dreamtrips.modules.infopages.view.custom.AttachmentImagesHorizontalView;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.DocumentListFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.FeedbackImageAttachmentFullscreenFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.FeedbackImageAttachmentsFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.HelpFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.SendFeedbackFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.TermsTabFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.AuthorizedStaticInfoFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.DocumentFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.OtaFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;
import com.worldventures.dreamtrips.modules.video.cell.MediaHeaderCell;
import com.worldventures.dreamtrips.modules.video.cell.MediaHeaderLightCell;
import com.worldventures.dreamtrips.modules.video.cell.VideoCell;
import com.worldventures.dreamtrips.modules.video.presenter.HelpVideosPresenter;
import com.worldventures.dreamtrips.modules.video.presenter.PresentationVideosPresenter;
import com.worldventures.dreamtrips.modules.video.presenter.ThreeSixtyVideosPresenter;
import com.worldventures.dreamtrips.modules.video.view.HelpVideosFragment;
import com.worldventures.dreamtrips.modules.video.view.ThreeSixtyVideosFragment;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {OtaFragment.class,
            AuthorizedStaticInfoFragment.class,
            AuthorizedStaticInfoPresenter.class,
            PresentationVideosPresenter.class,
            StaticInfoFragment.class,
            StaticInfoFragment.BookItFragment.class,
            StaticInfoFragment.BundleUrlFragment.class,
            StaticInfoFragment.TermsOfServiceFragment.class,
            StaticInfoFragment.PrivacyPolicyFragment.class,
            StaticInfoFragment.CookiePolicyFragment.class,
            StaticInfoFragment.FAQFragment.class,
            StaticInfoFragment.EnrollRepFragment.class,
            StaticInfoFragment.EnrollUpgradeFragment.class,
            DocumentFragment.class,
            VideoCell.class,
            MediaHeaderLightCell.class,
            MediaHeaderCell.class,
            ThreeSixtyVideosFragment.class,
            ThreeSixtyVideosPresenter.class,
            HelpVideosFragment.class,
            HelpVideosPresenter.class,
            HelpFragment.class,
            HelpTabPresenter.class,
            TermsTabFragment.class,
            TermsTabPresenter.class,
            WebViewFragmentPresenter.class,
            SendFeedbackFragment.class,
            SendFeedbackPresenter.class,
            AttachmentImagesHorizontalView.class,
            FeedbackImageAttachmentFullscreenPresenter.class,
            FeedbackImageAttachmentsPresenter.class,
            FeedbackImageAttachmentFullscreenFragment.class,
            FeedbackImageAttachmentsFragment.class,
            DocumentListFragment.class,
            DocumentListPresenter.class,
            DocumentCell.class},
      complete = false,
      library = true)
public class InfoModule {

   public static final String HELP = Route.HELP.name();
   public static final String TERMS = Route.TERMS.name();
   public static final String SEND_FEEDBACK = Route.SEND_FEEDBACK.name();

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideTermsOfServiceComponent() {
      return new ComponentDescription(TERMS, R.string.legal_terms, R.string.legal_terms, R.drawable.ic_termsconditions, TermsTabFragment.class);
   }

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideHelpComponent() {
      return new ComponentDescription(HELP, R.string.help, R.string.help, R.drawable.ic_help, HelpFragment.class);
   }

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideFeedbackComponent() {
      return new ComponentDescription(SEND_FEEDBACK, R.string.send_feedback, R.string.send_feedback, R.drawable.ic_send_feedback, SendFeedbackFragment.class);
   }
}
