package com.worldventures.dreamtrips.core.module;


import android.app.FragmentManager;
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
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.LaunchActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.service.UserNotificationInteractor;
import com.worldventures.dreamtrips.modules.common.view.activity.LaunchActivity;
import com.worldventures.dreamtrips.modules.common.view.jwplayer.VideoPlayerHolder;
import com.worldventures.dreamtrips.modules.dtl_flow.di.DtlModule;
import com.worldventures.dreamtrips.modules.facebook.FacebookModule;
import com.worldventures.dreamtrips.modules.media_picker.OldMediaPickerActivityModule;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerPresenter;
import com.worldventures.dreamtrips.social.di.SocialAppModule;
import com.worldventures.wallet.di.SmartCardModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      includes = {
            FacebookModule.class,
            OldMediaPickerActivityModule.class,
            UiUtilModule.class,
      },
      injects = {
            LaunchActivity.class,
            LaunchActivityPresenter.class,
            ActivityPresenter.class,
            Presenter.class,
      },
      complete = false,
      library = true)
public class LegacyActivityModule {

   protected BaseActivity baseActivity;

   public LegacyActivityModule(BaseActivity baseActivity) {
      this.baseActivity = baseActivity;
   }

   @Provides
   @ForActivity
   Context provideContext() {
      return baseActivity;
   }

   @Provides
   public ActivityRouter provideActivityCompass() {
      return new ActivityRouter(baseActivity);
   }

   @Provides
   public android.support.v4.app.FragmentManager provideSupportFragmentManager() {
      return baseActivity.getSupportFragmentManager();
   }

   @Provides
   public FragmentManager provideFragmentManager() {
      return baseActivity.getFragmentManager();
   }

   @Provides
   public Router provideRouter() {
      return new RouterImpl(baseActivity);
   }

   @Provides
   @Singleton
   public VideoPlayerHolder provideVideoPlayerHolder() {
      return new VideoPlayerHolder();
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
      featureManager.with(Feature.TRIPS, () -> activeComponents.add(SocialAppModule.TRIPS));
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
      activeComponents.add(SocialAppModule.MAP_TRIPS);
      activeComponents.add(SocialAppModule.LOGOUT);
      return new ComponentsConfig(activeComponents);
   }
}
