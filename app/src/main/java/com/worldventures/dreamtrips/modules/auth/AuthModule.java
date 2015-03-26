package com.worldventures.dreamtrips.modules.auth;

import com.worldventures.dreamtrips.modules.auth.presenter.LoginPresenter;
import com.worldventures.dreamtrips.modules.auth.view.LoginActivity;
import com.worldventures.dreamtrips.modules.auth.view.LoginFragment;

import dagger.Module;

@Module(
        injects = {
                LoginPresenter.class,
                LoginActivity.class,
                LoginFragment.class
        },
        complete = false,
        library = true
)
public class AuthModule {
}