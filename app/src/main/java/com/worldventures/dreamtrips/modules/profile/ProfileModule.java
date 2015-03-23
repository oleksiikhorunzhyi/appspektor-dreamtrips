package com.worldventures.dreamtrips.modules.profile;

import com.worldventures.dreamtrips.modules.profile.presenter.ProfilePresenter;
import com.worldventures.dreamtrips.modules.profile.view.fragment.ProfileFragment;

import dagger.Module;

/**
 * Created by 1 on 23.03.15.
 */
@Module(
        injects = {
                ProfilePresenter.class,
                ProfileFragment.class,
        },
        complete = false,
        library = true
)
public class ProfileModule {
}
