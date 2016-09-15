package com.worldventures.dreamtrips.modules.common.view.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForFragment;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import dagger.Module;
import dagger.Provides;

@Module(
      complete = false,
      library = true)
public class BaseFragmentModule {

   private final Injector injector;
   private final Fragment fragment;
   private final Presenter.TabletAnalytic tableAnalytic;

   public BaseFragmentModule(Injector injector, Fragment fragment, Presenter.TabletAnalytic tableAnalytic) {
      this.injector = injector;
      this.fragment = fragment;
      this.tableAnalytic = tableAnalytic;
   }

   @ForFragment
   @Provides
   Injector activityFragmentInjector() {
      return injector;
   }

   @Provides
   FragmentManager childManager() {
      return fragment.getChildFragmentManager();
   }

   @Provides
   Presenter.TabletAnalytic tabletAnalytic() {
      return tableAnalytic;
   }
}
