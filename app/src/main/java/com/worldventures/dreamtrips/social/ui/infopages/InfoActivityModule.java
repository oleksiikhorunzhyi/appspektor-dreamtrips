package com.worldventures.dreamtrips.social.ui.infopages;

import com.worldventures.dreamtrips.modules.common.presenter.TermsConditionsDialogPresenter;
import com.worldventures.dreamtrips.modules.common.view.dialog.TermsConditionsDialog;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.AuthorizedStaticInfoPresenter;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.BookItPresenter;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.DocumentListPresenter;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.DocumentPresenter;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.DreamLifeClubPresenter;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.EnrollUpgradePresenter;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.FeedbackImageAttachmentsPresenter;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.HelpDocumentListPresenter;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.HelpTabPresenter;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.LegalTermsPresenter;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.SendFeedbackPresenter;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.WebViewFragmentPresenter;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.DocumentListFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.FeedbackImageAttachmentFullscreenFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.FeedbackImageAttachmentsFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.HelpDocumentListFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.HelpFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.LegalTermsFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.SendFeedbackFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.AuthorizedStaticInfoFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.BookItFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.BundleUrlFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.DocumentFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.DreamLifeClubFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.EnrollRepFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.EnrollUpgradeFragment;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.StaticInfoFragment;
import com.worldventures.dreamtrips.social.ui.video.presenter.HelpVideosPresenter;
import com.worldventures.dreamtrips.social.ui.video.view.HelpVideosFragment;

import dagger.Module;

@Module(
      injects = {
            AuthorizedStaticInfoFragment.class,
            AuthorizedStaticInfoPresenter.class,
            StaticInfoFragment.class,
            BookItFragment.class,
            BookItPresenter.class,
            BundleUrlFragment.class,
            EnrollUpgradeFragment.class,
            EnrollRepFragment.class,
            EnrollUpgradePresenter.class,
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
            DreamLifeClubFragment.class,
            DreamLifeClubPresenter.class,
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
