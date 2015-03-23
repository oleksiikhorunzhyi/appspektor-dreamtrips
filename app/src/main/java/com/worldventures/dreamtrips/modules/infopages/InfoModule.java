package com.worldventures.dreamtrips.modules.infopages;

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

/**
 * Created by 1 on 23.03.15.
 */
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
}
