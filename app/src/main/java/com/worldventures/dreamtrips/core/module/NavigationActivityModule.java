package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.messenger.di.MessengerModule;
import com.messenger.util.UnreadConversationObservable;
import com.worldventures.core.component.ComponentDescription;
import com.worldventures.core.component.ComponentsConfig;
import com.worldventures.core.component.RootComponentsProvider;
import com.worldventures.core.di.qualifier.ForActivity;
import com.worldventures.core.model.session.Feature;
import com.worldventures.core.model.session.FeatureManager;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.modules.auth.service.AuthInteractor;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.core.ui.view.activity.BaseActivity;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.navigation.router.RouterImpl;
import com.worldventures.dreamtrips.modules.common.service.UserNotificationInteractor;
import com.worldventures.dreamtrips.modules.dtl_flow.di.DtlModule;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerPresenter;
import com.worldventures.dreamtrips.modules.trips.TripsAppModule;
import com.worldventures.dreamtrips.social.di.SocialAppModule;
import com.worldventures.wallet.di.SmartCardModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class NavigationActivityModule {

   private final BaseActivity activity;

   public NavigationActivityModule(BaseActivity activity) {
      this.activity = activity;
   }

   @Provides
   public Router provideRouter() {
      return new RouterImpl(activity);
   }

   @Provides
   ActivityRouter provideActivityCompass() {
      return new ActivityRouter(activity);
   }

   @Provides
   @Singleton
   NavigationDrawerPresenter provideNavDrawerPresenter(SessionHolder sessionHolder, UnreadConversationObservable unreadObservable,
         AuthInteractor authInteractor, UserNotificationInteractor userNotificationInteractor) {
      return new NavigationDrawerPresenter(sessionHolder, unreadObservable, authInteractor, userNotificationInteractor);
   }

   @Provides
   RootComponentsProvider provideRootComponentsProvider(Set<ComponentDescription> descriptions, ComponentsConfig config) {
      return new RootComponentsProvider(descriptions, config);
   }

   @Provides
   ComponentsConfig provideComponentsConfig(FeatureManager featureManager, @ForActivity Context context) {
      List<String> activeComponents = new ArrayList<>();
      featureManager.with(Feature.SOCIAL, () -> activeComponents.add(SocialAppModule.FEED));
      featureManager.with(Feature.TRIPS, () -> activeComponents.add(TripsAppModule.TRIPS));
      if (!ViewUtils.isTablet(context)) {
         featureManager.with(Feature.WALLET, () -> activeComponents.add(SmartCardModule.WALLET));
      }
      featureManager.with(Feature.DTL, () -> activeComponents.add(DtlModule.DTL));
      featureManager.with(Feature.SOCIAL, () -> activeComponents.add(SocialAppModule.NOTIFICATIONS));
      featureManager.with(Feature.SOCIAL, () -> activeComponents.add(MessengerModule.MESSENGER));
      featureManager.with(Feature.BOOK_TRAVEL, () -> activeComponents.add(SocialAppModule.OTA));
      activeComponents.add(SocialAppModule.TRIP_IMAGES);
      featureManager.with(Feature.MEMBERSHIP, () -> activeComponents.add(SocialAppModule.MEMBERSHIP));
      activeComponents.add(SocialAppModule.BUCKETLIST);
      activeComponents.add(SocialAppModule.ACCOUNT_PROFILE);
      featureManager.with(Feature.REP_TOOLS, () -> activeComponents.add(SocialAppModule.REP_TOOLS));
      activeComponents.add(SocialAppModule.SETTINGS);
      activeComponents.add(SocialAppModule.SEND_FEEDBACK);
      activeComponents.add(SocialAppModule.HELP);
      activeComponents.add(SocialAppModule.TERMS);
      activeComponents.add(TripsAppModule.MAP_TRIPS);
      activeComponents.add(SocialAppModule.LOGOUT);
      return new ComponentsConfig(activeComponents);
   }
}
