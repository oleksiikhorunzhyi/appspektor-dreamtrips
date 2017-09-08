package com.worldventures.dreamtrips.modules.infopages;

import com.worldventures.dreamtrips.modules.common.presenter.TermsConditionsDialogPresenter;
import com.worldventures.dreamtrips.modules.common.view.dialog.TermsConditionsDialog;
import com.worldventures.dreamtrips.modules.infopages.presenter.AuthorizedStaticInfoPresenter;
import com.worldventures.dreamtrips.modules.infopages.presenter.DocumentListPresenter;
import com.worldventures.dreamtrips.modules.infopages.presenter.DocumentPresenter;
import com.worldventures.dreamtrips.modules.infopages.presenter.FeedbackImageAttachmentsPresenter;
import com.worldventures.dreamtrips.modules.infopages.presenter.HelpDocumentListPresenter;
import com.worldventures.dreamtrips.modules.infopages.presenter.HelpTabPresenter;
import com.worldventures.dreamtrips.modules.infopages.presenter.LegalTermsPresenter;
import com.worldventures.dreamtrips.modules.infopages.presenter.OtaPresenter;
import com.worldventures.dreamtrips.modules.infopages.presenter.SendFeedbackPresenter;
import com.worldventures.dreamtrips.modules.infopages.presenter.WebViewFragmentPresenter;
import com.worldventures.dreamtrips.modules.infopages.view.cell.DocumentCell;
import com.worldventures.dreamtrips.modules.infopages.view.custom.AttachmentImagesHorizontalView;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.DocumentListFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.FeedbackImageAttachmentFullscreenFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.FeedbackImageAttachmentsFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.HelpDocumentListFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.HelpFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.LegalTermsFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.SendFeedbackFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.AuthorizedStaticInfoFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.DocumentFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.EnrollRepFragment;
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

@Module(
      injects = {
            AuthorizedStaticInfoFragment.class,
            AuthorizedStaticInfoPresenter.class,
            StaticInfoFragment.class,
            StaticInfoFragment.BookItFragment.class,
            StaticInfoFragment.BundleUrlFragment.class,
            StaticInfoFragment.EnrollUpgradeFragment.class,
            EnrollRepFragment.class,
            DocumentFragment.class,
            HelpVideosFragment.class,
            HelpVideosPresenter.class,
            HelpFragment.class,
            HelpTabPresenter.class,
            LegalTermsFragment.class,
            LegalTermsPresenter.class,
            WebViewFragmentPresenter.class,
            SendFeedbackFragment.class,
            SendFeedbackPresenter.class,
            FeedbackImageAttachmentsPresenter.class,
            FeedbackImageAttachmentFullscreenFragment.class,
            FeedbackImageAttachmentsFragment.class,
            DocumentListFragment.class,
            DocumentListPresenter.class,
            DocumentPresenter.class,
            HelpDocumentListFragment.class,
            HelpDocumentListPresenter.class,
            TermsConditionsDialogPresenter.class,
            TermsConditionsDialog.class,
      },
      complete = false,
      library = true)
public class InfoActivityModule {
}
