package com.worldventures.dreamtrips.modules.infopages;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.preference.StaticPageHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.modules.infopages.presenter.AuthorizedStaticInfoPresenter;
import com.worldventures.dreamtrips.modules.infopages.presenter.SendFeedbackPresenter;
import com.worldventures.dreamtrips.modules.infopages.presenter.WebViewFragmentPresenter;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.HelpFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.SendFeedbackFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.TermsTabFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.AuthorizedStaticInfoFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.OtaFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;
import com.worldventures.dreamtrips.modules.video.cell.VideoCell;
import com.worldventures.dreamtrips.modules.video.cell.VideoHeaderCell;
import com.worldventures.dreamtrips.modules.video.cell.VideoHeaderLightCell;
import com.worldventures.dreamtrips.modules.video.presenter.HelpVideosPresenter;
import com.worldventures.dreamtrips.modules.video.presenter.PresentationVideosPresenter;
import com.worldventures.dreamtrips.modules.video.presenter.ThreeSixtyVideosPresenter;
import com.worldventures.dreamtrips.modules.video.view.HelpVideosFragment;
import com.worldventures.dreamtrips.modules.video.view.ThreeSixtyVideosFragment;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                OtaFragment.class,
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

                VideoCell.class,
                VideoHeaderLightCell.class,
                VideoHeaderCell.class,

                ThreeSixtyVideosFragment.class,
                ThreeSixtyVideosPresenter.class,

                HelpVideosFragment.class,
                HelpVideosPresenter.class,

                HelpFragment.class,
                TermsTabFragment.class,
                WebViewFragmentPresenter.class,

                SendFeedbackFragment.class,
                SendFeedbackPresenter.class
        },
        complete = false,
        library = true
)
public class InfoModule {

    public static final String HELP = Route.HELP.name();
    public static final String TERMS = Route.TERMS.name();
    public static final String SEND_FEEDBACK = Route.SEND_FEEDBACK.name();

    @Provides
    StaticPageProvider provideStaticPageProvider(SessionHolder<UserSession> session, StaticPageHolder holder, LocaleHelper localeHelper) {
        return new StaticPageProvider(holder, session, localeHelper);
    }

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideTermsOfServiceComponent() {
        return new ComponentDescription(TERMS, R.string.legal_terms, R.string.legal_terms, R.drawable.ic_termsconditions, TermsTabFragment.class);
    }

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideHelpComponent() {
        return new ComponentDescription(HELP, R.string.help, R.string.help, R.drawable.ic_faq, HelpFragment.class);
    }

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideFeedbackComponent() {
        return new ComponentDescription(SEND_FEEDBACK, R.string.send_feedback, R.string.send_feedback, R.drawable.ic_send_feedback, SendFeedbackFragment.class);
    }
}
