package com.worldventures.dreamtrips.modules.profile;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.modules.profile.presenter.ProfilePresenter;
import com.worldventures.dreamtrips.modules.profile.view.fragment.ProfileFragment;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                ProfilePresenter.class,
                ProfileFragment.class,
        },
        complete = false,
        library = true
)
public class ProfileModule {

    public static final String MY_PROFILE = "my_profile";

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideProfileComponent() {
        return new ComponentDescription(MY_PROFILE, R.string.my_profile, R.drawable.ic_profile, ProfileFragment.class);
    }
}
