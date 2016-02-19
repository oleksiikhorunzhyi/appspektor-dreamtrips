package com.worldventures.dreamtrips.modules.auth;

import com.worldventures.dreamtrips.modules.auth.presenter.LoginPresenter;
import com.worldventures.dreamtrips.modules.auth.view.LoginFragment;
import com.worldventures.dreamtrips.modules.common.api.GetLocaleQuery;

import dagger.Module;

@Module(
        injects = {
                LoginPresenter.class,
                LoginFragment.class,
        },
        complete = false,
        library = true
)
public class AuthModule {
}
