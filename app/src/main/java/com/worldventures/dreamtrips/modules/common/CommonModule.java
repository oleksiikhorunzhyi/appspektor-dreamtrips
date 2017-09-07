package com.worldventures.dreamtrips.modules.common;

import android.content.Context;

import com.messenger.di.MessengerModule;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.component.ComponentsConfig;
import com.worldventures.dreamtrips.core.component.RootComponentsProvider;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.LaunchActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.activity.LaunchActivity;
import com.worldventures.dreamtrips.modules.dtl_flow.di.DtlModule;
import com.worldventures.dreamtrips.modules.media_picker.OldMediaPickerActivityModule;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerPresenter;
import com.worldventures.dreamtrips.modules.picker.MediaPickerModule;
import com.worldventures.dreamtrips.wallet.di.WalletAppModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      includes = {
            MediaPickerModule.class,
            OldMediaPickerActivityModule.class
      },
      injects = {
            LaunchActivity.class,
            LaunchActivityPresenter.class,

            ActivityPresenter.class,
            Presenter.class,
      },
      complete = false,
      library = true)
public class CommonModule {

   @Provides
   @Singleton
   NavigationDrawerPresenter provideNavDrawerPresenter(@ForApplication Injector injector) {
      return new NavigationDrawerPresenter(injector);
   }

   @Provides
   RootComponentsProvider provideRootComponentsProvider(Set<ComponentDescription> descriptions, ComponentsConfig config) {
      return new RootComponentsProvider(descriptions, config);
   }

   @Provides
   ComponentsConfig provideComponentsConfig(FeatureManager featureManager, @ForActivity Context context) {
      List<String> activeComponents = new ArrayList<>();
      featureManager.with(Feature.SOCIAL, () -> activeComponents.add(SocialAppModule.FEED));
      featureManager.with(Feature.TRIPS, () -> activeComponents.add(SocialAppModule.TRIPS));
      if (!ViewUtils.isTablet(context)) {
         featureManager.with(Feature.WALLET, () -> activeComponents.add(WalletAppModule.WALLET));
      }
      featureManager.with(Feature.SOCIAL, () -> activeComponents.add(SocialAppModule.NOTIFICATIONS));
      featureManager.with(Feature.SOCIAL, () -> activeComponents.add(MessengerModule.MESSENGER));
      featureManager.with(Feature.DTL, () -> activeComponents.add(DtlModule.DTL));
      featureManager.with(Feature.BOOK_TRAVEL, () -> activeComponents.add(SocialAppModule.OTA));
      activeComponents.add(SocialAppModule.TRIP_IMAGES);
      featureManager.with(Feature.MEMBERSHIP, () -> activeComponents.add(SocialAppModule.MEMBERSHIP));
      activeComponents.add(SocialAppModule.BUCKETLIST);
      activeComponents.add(SocialAppModule.ACCOUNT_PROFILE);
      featureManager.with(Feature.REP_TOOLS, () -> activeComponents.add(SocialAppModule.REP_TOOLS));
      activeComponents.add(SocialAppModule.SEND_FEEDBACK);
      activeComponents.add(SocialAppModule.SETTINGS);
      activeComponents.add(SocialAppModule.HELP);
      activeComponents.add(SocialAppModule.TERMS);
      activeComponents.add(SocialAppModule.MAP_TRIPS);
      activeComponents.add(SocialAppModule.LOGOUT);
      return new ComponentsConfig(activeComponents);
   }
}
