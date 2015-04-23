package com.worldventures.dreamtrips.modules.membership;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;
import com.worldventures.dreamtrips.modules.membership.presenter.InvitePresenter;
import com.worldventures.dreamtrips.modules.membership.presenter.MembershipPresenter;
import com.worldventures.dreamtrips.modules.membership.presenter.PresentationsPresenter;
import com.worldventures.dreamtrips.modules.membership.view.InviteFragment;
import com.worldventures.dreamtrips.modules.membership.view.MembershipFragment;
import com.worldventures.dreamtrips.modules.membership.view.PresentationsFragment;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                MembershipPresenter.class,
                PresentationsPresenter.class,
                MembershipFragment.class,
                PresentationsFragment.class,
                StaticInfoFragment.EnrollFragment.class,
                InviteFragment.class,
                InvitePresenter.class
        },
        complete = false,
        library = true
)
public class MembershipModule {
    @Provides(type = Provides.Type.SET)
    ComponentDescription provideMembershipComponent() {
        return new ComponentDescription(Route.MEMBERSHIP.name(), R.string.membership, R.drawable.ic_membership, MembershipFragment.class);
    }
}
