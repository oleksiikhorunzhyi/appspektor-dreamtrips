package com.worldventures.dreamtrips.core.module;

import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class NavigationModule {

    @Provides
    @Singleton
    public BackStackDelegate backStackDelegate() {
        return new BackStackDelegate();
    }

}
