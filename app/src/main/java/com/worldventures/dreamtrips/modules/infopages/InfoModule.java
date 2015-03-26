package com.worldventures.dreamtrips.modules.infopages;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.modules.infopages.presenter.ActualTokenStaticInfoFragmentPM;
import com.worldventures.dreamtrips.modules.infopages.presenter.EnrollActivityPresenter;
import com.worldventures.dreamtrips.modules.infopages.presenter.MembershipVideosPresenter;
import com.worldventures.dreamtrips.modules.infopages.presenter.Video360FragmentPM;
import com.worldventures.dreamtrips.modules.infopages.presenter.WebViewFragmentPresenter;
import com.worldventures.dreamtrips.modules.infopages.view.activity.EnrollActivity;
import com.worldventures.dreamtrips.modules.infopages.view.cell.Video360Cell;
import com.worldventures.dreamtrips.modules.infopages.view.cell.Video360SmallCell;
import com.worldventures.dreamtrips.modules.infopages.view.cell.VideoCell;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.MemberShipFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.Video360Fragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.ActualTokenStaticInfoFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.OtaFragment;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                OtaFragment.class,
                ActualTokenStaticInfoFragment.class,
                ActualTokenStaticInfoFragmentPM.class,
                StaticInfoFragment.TrainingVideosFragment.class,
                Video360FragmentPM.class,
                MembershipVideosPresenter.class,
                Video360Fragment.class,
                StaticInfoFragment.class,
                StaticInfoFragment.BookIt.class,
                StaticInfoFragment.BundleUrlFragment.class,
                StaticInfoFragment.TermsOfServiceFragment.class,
                StaticInfoFragment.PrivacyPolicyFragment.class,
                StaticInfoFragment.CookiePolicyFragment.class,
                StaticInfoFragment.FAQFragment.class,

                VideoCell.class,
                Video360Cell.class,
                Video360SmallCell.class,
                EnrollActivity.class,
                EnrollActivityPresenter.class,

                WebViewFragmentPresenter.class,
                MemberShipFragment.class,
                StaticInfoFragment.EnrollFragment.class,
        },
        complete = false,
        library = true
)
public class InfoModule {

    public static final String MEMBERSHIP = "membership";
    public static final String TERMS_OF_SERVICE = "terms_of_service";
    public static final String FAQ = "faq";
    public static final String PRIVACY_POLICY = "privacy_policy";
    public static final String COOKIE_POLICY = "cookie_policy";

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideMembershipComponent() {
        return new ComponentDescription(MEMBERSHIP, R.string.membership, R.drawable.ic_terms, MemberShipFragment.class);
    }

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideTermsOfServiceComponent() {
        return new ComponentDescription(TERMS_OF_SERVICE, R.string.terms_of_service, R.drawable.ic_membership, StaticInfoFragment.TermsOfServiceFragment.class);
    }

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideFAQComponent() {
        return new ComponentDescription(FAQ, R.string.faq, R.drawable.ic_faq, StaticInfoFragment.FAQFragment.class);
    }

    @Provides(type = Provides.Type.SET)
    ComponentDescription providePrivacyPolicyComponent() {
        return new ComponentDescription(PRIVACY_POLICY, R.string.privacy, R.drawable.ic_termsconditions, StaticInfoFragment.PrivacyPolicyFragment.class);
    }

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideCookiePolicyComponent() {
        return new ComponentDescription(COOKIE_POLICY, R.string.cookie, R.drawable.ic_cookie, StaticInfoFragment.CookiePolicyFragment.class);
    }
}