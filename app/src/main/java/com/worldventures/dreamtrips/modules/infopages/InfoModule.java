package com.worldventures.dreamtrips.modules.infopages;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.preference.StaticPageHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.infopages.presenter.AuthorizedStaticInfoPresenter;
import com.worldventures.dreamtrips.modules.infopages.presenter.WebViewFragmentPresenter;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.TermsTabFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.Video360Fragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.AuthorizedStaticInfoFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.OtaFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;
import com.worldventures.dreamtrips.modules.membership.presenter.PresentationVideosPresenter;
import com.worldventures.dreamtrips.modules.video.cell.Video360Cell;
import com.worldventures.dreamtrips.modules.video.cell.Video360SmallCell;
import com.worldventures.dreamtrips.modules.video.cell.VideoCell;
import com.worldventures.dreamtrips.modules.video.presenter.Video360Presenter;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                OtaFragment.class,
                AuthorizedStaticInfoFragment.class,
                AuthorizedStaticInfoPresenter.class,
                StaticInfoFragment.TrainingVideosFragment.class,
                Video360Presenter.class,
                PresentationVideosPresenter.class,
                Video360Fragment.class,
                StaticInfoFragment.class,
                StaticInfoFragment.BookItFragment.class,
                StaticInfoFragment.BundleUrlFragment.class,
                StaticInfoFragment.TermsOfServiceFragment.class,
                StaticInfoFragment.PrivacyPolicyFragment.class,
                StaticInfoFragment.CookiePolicyFragment.class,
                StaticInfoFragment.FAQFragment.class,
                StaticInfoFragment.EnrollRepFragment.class,

                VideoCell.class,
                Video360Cell.class,
                Video360SmallCell.class,

                TermsTabFragment.class,
                WebViewFragmentPresenter.class,
        },
        complete = false,
        library = true
)
public class InfoModule {

    public static final String MEMBERSHIP = Route.MEMBERSHIP.name();
    public static final String FAQ = Route.FAQ.name();
    public static final String TERMS = Route.TERMS.name();

    @Provides
    StaticPageProvider provideStaticPageProvider(SessionHolder<UserSession> session, StaticPageHolder holder) {
        return new StaticPageProvider(session, holder);
    }

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideTermsOfServiceComponent() {
        return new ComponentDescription(TERMS, R.string.terms, R.drawable.ic_termsconditions, TermsTabFragment.class);
    }

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideFAQComponent() {
        return new ComponentDescription(FAQ, R.string.faq, R.drawable.ic_faq, StaticInfoFragment.FAQFragment.class);
    }
}
